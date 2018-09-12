package eafmupdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prop4j.FMToBDD;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import de.ovgu.featureide.fm.core.editing.NodeCreator;
import eafmupdate.model.Neighbors;
import eafmupdate.model.Oracle;
import eafmupdate.model.Role;
import eafmupdate.model.Roles;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDD.AllSatIterator;

/**
 * util methods to generate delta information (products to add/remove and initial variables with their neighbors) from 2 distinct feature models
 * using BDDs
 * @author marcoradavelli
 *
 */
public class GenerateUpdateRequest {
	
	private static Logger logger = Logger.getLogger(GenerateUpdateRequest.class.getName());
	
	protected static BDD computeBDDDifference(IFeatureModel fm, IFeatureModel fm2, boolean add) {			
		
		fm = fm.clone();
		//Util.addMissingFeatures(oracle, fm);
		//Util.removeOverabundantFeatures(oracle.oracleFM, fm);
				
		List<String> fmVars = new ArrayList<>(Util.getAllFeatures(fm2, fm));
		//fm.getFeatureOrderList();
		FMToBDD f2bdd = new FMToBDD(fmVars);
		
		BDD bddModel = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
		BDD bddOracle = f2bdd.nodeToBDD(NodeCreator.createNodes(fm2));
		Set<String> varO = Util.getFeatureNames(fm2);  // variabili oracolo
		Set<String> varM = Util.getFeatureNames(fm);  // variabili modello
		Set<String> varONotM = new HashSet<>(), varMNotO = new HashSet<>();
		for (String s : varO) if (!varM.contains(s)) varONotM.add(s);  // variabili oracolo e non modello
		for (String s : varM) if (!varO.contains(s)) varMNotO.add(s);  // variabili modello non in oracolo
		
		// Frem semantics
		for (String s : varMNotO) {   // Frem (variabili in modello che non sono nell'oracolo)
			int elem = fmVars.indexOf(s);
			assert elem >= 0 : "element not found " + s;
			
			BDD b1 = bddModel.restrict(f2bdd.init.ithVar(elem));
			BDD b2 = bddModel.restrict(f2bdd.init.nithVar(elem));
			bddModel = b1.or(b2);
		}
		// Frem finalizing
		for (String s : varMNotO) {   // Frem (variabili in modello che non sono nell'oracolo)
			int elem = fmVars.indexOf(s);
			assert elem >= 0 : "element not found " + s;
			bddModel = bddModel.and(f2bdd.init.nithVar(elem));
			bddOracle = bddOracle.and(f2bdd.init.nithVar(elem));
		}
		// Fadd
		for (String s : varONotM) {   // Fadd (variabili in oracolo non nel modello)
			int elem = fmVars.indexOf(s);
			assert elem >= 0 : "element not found " + s;
			
			String parent = Util.findParent(fm2.getStructure().getRoot(), s).getFeature().getName();
			int elemP = fmVars.indexOf(parent);
			assert elemP >= 0 : "element not found " + parent;

			BDD b = f2bdd.init.ithVar(elem).imp(f2bdd.init.ithVar(elemP));  // add constraint: child -> parent
			
			bddModel = bddModel.and(b);
			//bddModel = (bddModel==null ? bdd2 : bddModel).and(f2bdd.init.nithVar(elem));
			
		}
		
		BDD difference = (add ? bddOracle.and(bddModel.not()) : bddModel.and(bddOracle.not()));
		
		return difference;
		/*System.out.println("Products: ");
		AllSatIterator x = difference.allsat();
		while (x.hasNext()) {
			byte[] sat = x.nextSat();
			logger.debug(Arrays.toString(sat));
			Map<String,Boolean> product = new HashMap<String, Boolean>();
			for (int i=0; i<sat.length; i++) if (sat[i]!=-1) product.put(fmVars.get(i), sat[i]==1); // if it is not a don't care value
			System.out.print(product+" ");
		}
		System.out.println();*/		
	}

	
	public static List<Map<String,Boolean>> enumerateProducts(IFeatureModel fm, IFeatureModel fm2, boolean add) {
		List<Map<String,Boolean>> products = new ArrayList<>();
		List<String> fmVars = new ArrayList<>(Util.getAllFeatures(fm, fm2));
		BDD difference = computeBDDDifference(fm, fm2, add);
		AllSatIterator configs = difference.allsat();
		while (configs.hasNext()) {
			byte[] sat = configs.nextSat();
			logger.debug(Arrays.toString(sat));
			Map<String,Boolean> product = new HashMap<String, Boolean>();
			for (int i=0; i<sat.length; i++) if (sat[i]!=-1) product.put(fmVars.get(i), sat[i]==1); // if it is not a don't care value
			products.add(product);
		}
		return products;
	}
	
	public static long getNumCFadd(Oracle oracle) {
		return GenerateUpdateRequest.computeProductsToAddOrRemove(oracle, oracle.originalFM, true);
	}

	public static long getNumCFrem(Oracle oracle) {
		return GenerateUpdateRequest.computeProductsToAddOrRemove(oracle, oracle.originalFM, false);
	}


	public static long computeProductsToAddOrRemove(Oracle oracle, IFeatureModel fm, boolean add) {
		return computeProductsToAddOrRemove(fm,oracle.oracleFM,add);
	
	}
	public static long computeProductsToAddOrRemove(IFeatureModel fm, IFeatureModel fm2) {
		return 
				computeProductsToAddOrRemove(fm,fm2,true)+
				computeProductsToAddOrRemove(fm,fm2,false);
	}
	
	public static long computeProductsToAddOrRemove(IFeatureModel fm, IFeatureModel fm2, boolean add) {						
		BDD difference = computeBDDDifference(fm, fm2, add);
		return (long)difference.satCount();
	}
	
	static Neighbors generateNeighborsForFeaturesToAdd(IFeatureModel oracle, IFeatureModel fm) {
		Map<String,Map<String,Roles>> n = new LinkedHashMap<>();
		Set<String> toAdd = Util.getFeatureNamesIn2NotIn1(fm, oracle);
		for (String f : toAdd) {
			Map<String,Roles> roles = new HashMap<>();
			IFeatureStructure parent = Util.findParent(oracle.getStructure().getRoot(), f);
			assert parent!=null : "Parent for " + f + " not found";
			if (parent!=null) {
				roles.put(parent.getFeature().getName(), new Roles(Role.PARENT));
				n.put(f, roles);
			}
		}
		return new Neighbors(n);
	}
	
}






//  ****** OBSOLETE METHODS ***************
//public static List<Map<String,Boolean>> generateProductsToAdd(IFeatureModel oracle, IFeatureModel fm) throws TimeoutException {
//	List<Map<String,Boolean>> products = new ArrayList<>();
//	fm = fm.clone();
//	Util.addMissingFeatures(oracle, fm);
//	Util.removeOverabundantFeatures(oracle, fm);
//	
//	List<String> fmVars = new ArrayList<>(Util.getAllFeatures(oracle, fm));
//	//fm.getFeatureOrderList();
//	FMToBDD f2bdd = new FMToBDD(fmVars);
//	
//	BDD bdd2 = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
//	BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(oracle));
//	
//	Set<String> varO = Util.getFeatureNames(oracle);
//	Set<String> varM = Util.getFeatureNames(fm);
//	Set<String> varONotM = new HashSet<>(), varMNotO = new HashSet<>();
//	for (String s : varO) if (!varM.contains(s)) varONotM.add(s);
//	for (String s : varM) if (!varO.contains(s)) varMNotO.add(s);
//	
//	BDD bddOracle = null;
//	for (String s : varMNotO) {
//		int elem = fmVars.indexOf(s);
//		assert elem >= 0 : "element not found " + s;
//		BDD b = f2bdd.init.nithVar(elem);
//		bddOracle = (bddOracle==null ? bdd : bddOracle).and(b);
//	}
//	if (bddOracle==null) bddOracle = bdd;
//	BDD bddModel = null;
//	for (String s : varONotM) {
//		int elem = fmVars.indexOf(s);
//		assert elem >= 0 : "element not found " + s;
//		BDD b = f2bdd.init.nithVar(elem);
//		bddModel = (bddModel==null ? bdd2 : bddModel).and(b);
//	}
//	if (bddModel==null) bddModel = bdd2;
//	
//	BDD weakening = bddOracle.and(bddModel.not());
//	
//	AllSatIterator configs = weakening.allsat();
//	while (configs.hasNext()) {
//		byte[] sat = configs.nextSat();
//		logger.debug(Arrays.toString(sat));
//		Map<String,Boolean> product = new HashMap<String, Boolean>();
//		for (int i=0; i<sat.length; i++) if (sat[i]!=-1) product.put(fmVars.get(i), sat[i]==1); // if it is not a don't care value
//		products.add(product);
//	}
//	return products;
//}
//
//public static List<Map<String,Boolean>> generateProductsToRemove(IFeatureModel oracle, IFeatureModel fm) {
//	List<Map<String,Boolean>> products = new ArrayList<>();
//	List<String> fmVars = new ArrayList<>(Util.getAllFeatures(oracle, fm));
//	logger.debug(Arrays.toString(fmVars.toArray()));
//	FMToBDD f2bdd = new FMToBDD(fmVars);
//	
//	BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
//	BDD bdd2 = f2bdd.nodeToBDD(NodeCreator.createNodes(oracle));
//	BDD strengthening = bdd.andWith(bdd2.not());
//	
//	AllSatIterator configs = strengthening.allsat();
//	while (configs.hasNext()) {
//		byte[] sat = configs.nextSat();
//		logger.debug(Arrays.toString(sat));
//		Map<String,Boolean> product = new HashMap<String, Boolean>();
//		for (int i=0; i<sat.length; i++) if (sat[i]!=-1) product.put(fmVars.get(i), sat[i]==1); // if it is not a don't care value
//		products.add(product);
//	}		
//	
//	return products;
//}