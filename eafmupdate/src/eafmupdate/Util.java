package eafmupdate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import de.ovgu.featureide.fm.core.base.impl.Feature;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.model.Neighbors;
import eafmupdate.model.Oracle;
import eafmupdate.model.Role;
import eafmupdate.model.Roles;
import fmmutation.mutationoperators.FMMutation;
import fmmutation.mutationoperators.FMMutator;
import fmmutation.utils.CollectionsUtil;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class Util {
	public static int getComplexity(IFeatureModel fm) {
		return fm.getNumberOfFeatures() + fm.getConstraintCount();
	}
	
	public static double deltaPercent(double f, double i) {
		return (f-i)/i;
	}
	
	public static Set<String> getFeatureNames(IFeatureModel fm) {
		Set<String> fnames = new HashSet<>();
		for (IFeature a : fm.getFeatures()) fnames.add(a.getName());
		return fnames;
	}
	
	public static Set<String> getFeatureNamesIn2NotIn1(IFeatureModel fm1, IFeatureModel fm2) {
		Set<String> fnames = new HashSet<String>(getFeatureNames(fm2));
		fnames.removeAll(getFeatureNames(fm1));
		return fnames;
	}
	 
	public static Set<String> getAllFeatures(IFeatureModel m1, IFeatureModel m2) {
		Set<String> features = getFeatureNames(m1);
		features.addAll(getFeatureNames(m2));
		return features;
	}
	
	@Deprecated
	public static void addMissingFeaturesToFM1(IFeatureModel fm1, IFeatureModel fm2) {
		Set<String> featureNamesToAdd = getFeatureNamesIn2NotIn1(fm1, fm2);
		for (String fname : featureNamesToAdd) {
			Feature f = new Feature(fm1, fname);
			fm1.addFeature(f);
			fm1.getStructure().getRoot().addChild(f.getStructure());
		}
	}
	
	private static Set<String> getFeaturesToAdd(Oracle oracle, IFeatureModel fm) {
//		Set<String> fnames = new HashSet<String>(getFeatureNames(oracle.originalFM));
		Set<String> fnames = new HashSet<String>();
		if (oracle.oracleFM!=null) fnames.addAll(getFeatureNames(oracle.oracleFM));
		if (oracle.neighbors!=null && oracle.neighbors.neighbors!=null) fnames.addAll(oracle.neighbors.neighbors.keySet());
		fnames.removeAll(getFeatureNames(oracle.originalFM));
		return fnames;
	}
	
	/** @return the feature from its name, visiting the feature model. It can be expensive */
	private static IFeatureStructure findFeatureFromName(IFeatureStructure root, String fname) {
		if (root.getFeature().getName().equals(fname)) return root;
		for (IFeatureStructure child : root.getChildren()) {
			IFeatureStructure f = findFeatureFromName(child, fname);
			if (f!=null) return f;
		}
		return null;
	}
	
	private static IFeatureStructure getParentToWhichAddFeature(Neighbors neighbors, IFeatureStructure root, String fname) {
		if (neighbors==null || neighbors.neighbors==null) return root;
		Map<String, Roles> n = neighbors.neighbors.get(fname);
		if (n==null || n.isEmpty()) return root;
		for (Entry<String, Roles> e : n.entrySet()) {
			if (e.getValue().containsRole(Role.PARENT)) {
				IFeatureStructure ret = findFeatureFromName(root, e.getKey());
				return ret;
			}
		}
		IFeatureStructure ret = findFeatureFromName(root, n.keySet().iterator().next());
		return ret;
	}
	
	/** it adds missing features to fm, considering the neighbors */
	public static void addMissingFeatures(Oracle oracle, IFeatureModel fm) {
		Set<String> featureNamesToAdd = getFeaturesToAdd(oracle, fm);
//		System.out.println("Features to add: "+featureNamesToAdd);
//		System.out.println("Neighbors: "+oracle.neighbors.neighbors);
		int placed = 0;
		int i=0;
		Set<String> placedFeatures = new HashSet<>();
		System.out.println(featureNamesToAdd.size());
		while (placed < featureNamesToAdd.size()) {
			i++;
			for (String fname : featureNamesToAdd) {
				if (fm.getFeature(fname)==null 
					//&& oracle.neighbors.neighbors.get(fname)!=null
					//&& fm.getFeature(oracle.neighbors.neighbors.get(fname).keySet().iterator().next())!=null) {
					&& getParentToWhichAddFeature(oracle.neighbors, fm.getStructure().getRoot(), fname)!=null) {
					Feature f = new Feature(fm, fname);
					fm.addFeature(f);
					IFeatureStructure parent = getParentToWhichAddFeature(oracle.neighbors, fm.getStructure().getRoot(), fname);
					parent.addChild(f.getStructure());
					placed++;
					placedFeatures.add(fname);
				} // otherwise it is already placed
				//System.out.println(fm);
				if (i>100) {
					System.out.println("Place features: "+placed+" "+placedFeatures);
					featureNamesToAdd.removeAll(placedFeatures);
					System.out.println("Remaining features: "+featureNamesToAdd);
					System.out.println("Model: "+fm);
					return;
				}
			}
			
		}
		
	}
	
	/** it adds missing features to fm, considering the neighbors */
	public static void addMissingFeatures(IFeatureModel oracle, IFeatureModel fm) {
		Set<String> featureNamesToAdd = getFeatureNamesIn2NotIn1(fm, oracle);
		Neighbors n = GenerateUpdateRequest.generateNeighborsForFeaturesToAdd(oracle, fm);
		for (String fname : featureNamesToAdd) {
			Feature f = new Feature(fm, fname);
			fm.addFeature(f);
			IFeatureStructure parent = getParentToWhichAddFeature(n, fm.getStructure().getRoot(), fname);
			parent.addChild(f.getStructure());
		}
	}
	
	/** it removed the exceeding features from fm */
	public static void removeOverabundantFeatures(IFeatureModel oracle, IFeatureModel fm) {
		Set<String> featureNamesToRemove = Util.getFeatureNamesIn2NotIn1(oracle, fm);
//		System.out.println("Features to remove: "+featureNamesToRemove);
		for (String fname : featureNamesToRemove) {
			fm.deleteFeature(fm.getFeature(fname));
			//fm.getFeature(fname).getStructure().setAbstract(true);
		}
	}

	
	public static String convertToVelvet(IFeatureModel fm) {
		return new de.ovgu.featureide.fm.core.io.velvet.SimpleVelvetFeatureModelFormat().write(fm);
	}
	
	public static String convertToGUIDSL(IFeatureModel fm) {
		return new de.ovgu.featureide.fm.core.io.guidsl.GuidslWriter().writeToString(fm);
	}
	
	public static String convertToCNF(IFeatureModel fm) {
		//return new de.ovgu.featureide.fm.core.io.sxfm.SXFMWriter(fm).writeToString();
		//return new de.ovgu.featureide.fm.core.io.splconquerer.ConquererFMWriter(fm).writeToString();
		//return new de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelWriter(fm).writeToString();
		return new de.ovgu.featureide.fm.core.io.cnf.CNFFormat().write(fm);
	}
	
	private static Map<String,Integer> getListOfChildren(IFeatureStructure feature) {
		Map<String,Integer> map = new HashMap<>();
		map.put(feature.getFeature().getName(), feature.getChildrenCount());
		for (IFeatureStructure child : feature.getChildren()) {
			map.putAll(getListOfChildren(child));
		}
		return map;
	}
	
	public static double getCompactness(IFeatureModel model) {
		return getCompactness(model.getStructure().getRoot());
	}
	
	/**
	 * Compute the compactness of a model
	 * @param feature
	 * @return
	 */
	private static double getCompactness(IFeatureStructure feature) {
		Map<String,Integer> map = getListOfChildren(feature);
		int childrenCount=0, nodeCount=0;
		for (Integer children : map.values()) if (children>0){
			childrenCount += children;
			nodeCount++;
		}
		return (double)childrenCount/(double)nodeCount;
	}
	
	/**
	 * A better way to compute edit distance... outputs reduced distances
	 * @param feature the root of the model
	 * @param toPrintMandatory if it is a child of an "and" group with more than 1 children (only in that case it is needed to print information if the feature is mandatory or optional)
	 * @return the string for the APTED tool, to compute edit distances
	 */
	private static String toAPTED(IFeatureStructure feature, boolean toPrintMandatory) {
		String x = "{";
		String lastbracket="";
		if (toPrintMandatory) {
			lastbracket = "}";
			if (feature.isMandatory()) {
				x += "*{";  // mandatory
			} else {
				x += "+{";  // optional
			}
		}
		x += feature.getFeature().getName();
		int nc = feature.getChildrenCount();
		if(nc == 0) {
			return x + "}" + lastbracket;
		} else if (nc == 1) {
			x += toAPTED(feature.getFirstChild(), true);
			return x + "}" + lastbracket;
		} else {
			assert nc > 1;
			if (feature.isOr() ) {
				x += "{or";
			} else if (feature.isAlternative()) {
				x += "{alt";
			} else if (feature.isAnd()) {
				x += "{and";
			}
			// the children are put in alphabetical order
			ArrayList<IFeatureStructure> orderedChildren = new ArrayList<>(feature.getChildren());
			Collections.sort(orderedChildren, new Comparator<IFeatureStructure>() {
				@Override
				public int compare(IFeatureStructure o1, IFeatureStructure o2) {
					return o1.getFeature().getName().compareTo(o2.getFeature().getName());
				}
			});
			for (IFeatureStructure child : orderedChildren) {
				x += toAPTED(child, true);
			}
			return x + "}}" + lastbracket;			
		}
	}
	
	public static String toAPTED(IFeatureModel model) {
		return model==null ? "" : toAPTED(model.getStructure().getRoot());
	}
	
	public static String toAPTED(IFeatureStructure feature) {
		return toAPTED(feature, false);
	}
	
	public static double getAdequacy(IFeatureModel oracle, IFeatureModel mutant) throws TimeoutException, IOException,  UnsupportedModelException, FeatureModelException, ConfigurationEngineException {
		double d = CompareOracleMutantBDD.getConformance(oracle, mutant).percConfsJudgedCorrectly();
		System.out.println("Adq2:" +d);
		return d;
	}
	
	public static double getAdequacy(Oracle oracle, IFeatureModel mutant) throws IOException, TimeoutException, FeatureModelException, ConfigurationEngineException {
		double d = CompareOracleMutantBDD.getConformance(oracle, mutant).percConfsJudgedCorrectly();
		if (d<0) {
			System.out.println("Adq < 0!!!!: "+d);
			System.out.println("Oracle: "+oracle);
			System.out.println("Mutant: "+mutant);
			System.out.println("BDDs count diff: "+CompareOracleMutantBDD.getBddsCountDiff(oracle,mutant));
			System.out.println("Features Oracle: "+oracle.getFeatureNames());
			System.out.println("Features Mutant: "+Util.getFeatureNames(mutant));
		}
//		System.out.println("Adq:" +d);
		return d;
	}
	
/*	public static double getEditDistance(IFeatureModel oracle, IFeatureModel mutant) {
		return EditDistance.instance.getDistance(oracle, mutant);
	}*/
	
	@Deprecated
	public static IFeatureModel mutateRandomly(IFeatureModel model, int order) {
		IFeatureModel fmodel = model.clone();
		for (int i=0; i<order; i++) {
			fmodel = GenerateMutants.instance.generateNmutants(fmodel, 1, new ArrayList<>()).get(0);
		}
		return fmodel;
	}
	@Deprecated
	public static IFeatureModel mutateRandomlyConstraint(IFeatureModel model, int order) {
		IFeatureModel fmodel = model.clone();
		for (int i=0; i<order; i++) {
			//fmodel = GenerateMutants.generateNmutantsConstraints(fmodel, 1, new ArrayList<>()).get(0);
		}
		return fmodel;
	}
		
	/** the MutatedModel structure contains information also on the mutations applied. The model is cloned before being mutated 
	 * @param mutators TODO
	 * @param safe TODO*/
	public static MutatedModel mutateRandomly(MutatedModel model, int order, List<FMMutator> mutators, boolean safe) {
		MutatedModel fmodel = model.clone();
		for (int i=0; i<order; i++) {
			List<FMMutation> currMutants;
			do {
				FMMutator mutator = GenerateMutants.instance.getRandomMutator(mutators);  //GenerateMutants.fmMutators[GenerateMutants.rnd.nextInt(GenerateMutants.fmMutators.length)];
				currMutants = CollectionsUtil.listFromIterator(safe ? mutator.mutateSafe(fmodel.model) : mutator.mutate(fmodel.model));
			} while (currMutants.isEmpty());
			FMMutation mutation = currMutants.get(GenerateMutants.instance.rnd.nextInt(currMutants.size()));
			fmodel.model = mutation.getFirst();
			mutation.setPreviousModel(model.model);
			fmodel.addMutation(mutation);
		}
		return fmodel;
	}
	
	public static boolean equals(IFeatureModel m1, IFeatureModel m2) {
		return m1.toString().equals(m2.toString());
	}
	
	/** @return the parent of f among root and its children */
	public static IFeatureStructure findParent(IFeatureStructure root, String f) {
		if (root.getFeature().getName().equals(f)) return null; // They should have at least the same parent
		// search among the children
		for (IFeatureStructure child : root.getChildren()) {
			if (child.getFeature().getName().equals(f)) return root;
		}
		// search among the children of children, recursively
		for (IFeatureStructure child : root.getChildren()) {
			IFeatureStructure found = findParent(child, f);
			if (found!=null) return found;
		}
		return null; // if not found, returns null
	}
	
	public static void normalizeSingleChildren(IFeatureStructure root) {
		if (root!=null && root.getChildrenCount()==1 && !root.isAnd()) root.changeToAnd();
		if (root!=null && root.hasChildren()) {
			for (IFeatureStructure child : root.getChildren()) normalizeSingleChildren(child);
		}
	}
	
	/** @return a HashMap containing different feature names between m1 and m2 */
	public static HashMap<Boolean,Set<String>> getDifferentFeatures(IFeatureModel m1, IFeatureModel m2) {
		HashSet<String> add = new HashSet<>(), remove = new HashSet<>();
		Set<String> f1 = new HashSet<>(m1.getFeatureOrderList());
		Set<String> f2 = new HashSet<>(m2.getFeatureOrderList());
		for (String f : f1) if (!f2.contains(f)) { add.add(f); System.out.println("added "+f); }
		for (String f : f2) if (!f1.contains(f)) { remove.add(f);  System.out.println("removed "+f); }
		
		HashMap<Boolean,Set<String>> map = new HashMap<>();
		map.put(Boolean.TRUE, add);
		map.put(Boolean.FALSE, remove);
		return map;
	}
	
	/** converts a model to FeatureIDE format */
	private static String toFeatureIDE(IFeatureModel model) {
		return new de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat().write(model);
	}
	
	/** temporary outputs a repaired model.
	 * If directory is null, the default output folder is used. */
	public static void saveTemporary(IFeatureModel model, String directory, String title) throws Exception {
		PrintWriter fout = new PrintWriter(new FileWriter((directory==null?"output/":directory)+title+".xml"));
		fout.println(toFeatureIDE(model));
		fout.close();
	}
	
	/** temporary outputs a repaired model. The default output folder is used. */
	public static void saveTemporary(IFeatureModel model, String title) throws Exception {
		PrintWriter fout = new PrintWriter(new FileWriter("output/"+title+".xml"));
		fout.println(toFeatureIDE(model));
		fout.close();
	}
	
	/** Outputs a string to a file. If path is null, the default output folder is used. */
	public static void saveTemporary(String content, String path, String title) throws Exception {
		PrintWriter fout = new PrintWriter(new FileWriter((path==null?"output/":path)+title));
		fout.println(content);
		fout.close();
	}
	
	/** updates initialModel to create fmAR */
	public static void applyAddRem(Oracle oracle, IFeatureModel initialModel) {
		addMissingFeatures(oracle, initialModel);
		removeOverabundantFeatures(oracle.oracleFM, initialModel);  // also removes the features
	}
	
	public static Oracle generateOracle(IFeatureModel fm, IFeatureModel fmOracle) {
		Neighbors neighbors = GenerateUpdateRequest.generateNeighborsForFeaturesToAdd(fmOracle, fm);
		Set<String> featuresToRem = Util.getFeatureNamesIn2NotIn1(fmOracle, fm);
		return new Oracle(fm, null, null, neighbors, featuresToRem, fmOracle);
	} 
	
	
	// SI: added to rename features at the beginning	
	/** it renames the features in the model fm */
	public static IFeatureModel renameFeatures(IFeatureModel fm, Map<String,String> renamingMap) {
		if (renamingMap==null) return fm;
		for (Map.Entry<String,String> fname : renamingMap.entrySet()) {
			IFeature f = fm.getFeature(fname.getKey());
			if (f!=null) {
				//System.out.println("Renamed feature "+f.getName()+" into "+fname.getValue());
				f.setName(fname.getValue());
			}
		}
		return fm;
	}
	
}
