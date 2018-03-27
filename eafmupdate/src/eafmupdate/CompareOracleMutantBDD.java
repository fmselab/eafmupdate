package eafmupdate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.prop4j.FMToBDD;
import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.editing.Comparison;
import de.ovgu.featureide.fm.core.editing.ModelComparator;
import de.ovgu.featureide.fm.core.editing.NodeCreator;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.model.Oracle;
import fmmutation.utils.Utils;
import fmupdate.models.ExampleTaker;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDD.AllSatIterator;
import net.sf.javabdd.BDDFactory;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class CompareOracleMutantBDD {
	
	private static IFeatureModel readModel(String modelPath) throws FileNotFoundException, UnsupportedModelException, NoSuchExtensionException {
		try {
			return ExampleTaker.readExample(modelPath);
		}
		catch(UnsupportedModelException e) {}
		try {
			return Utils.readSPLOTModel(modelPath);
		}
		catch(UnsupportedModelException e) {
			throw e;
		}
	}

	private static Conformance getConformance(String oracle, String mutant) throws UnsupportedModelException, TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, NoSuchExtensionException {
		return getConformance(readModel(oracle), readModel(mutant));
	}

	/** added by radavelli */
	static Conformance getConformance(Oracle oracle, IFeatureModel mutant) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException {
//		int numFeatures = oracle.getFeatureNames().size();
		// all the common features
		Set<String> features = oracle.getAllFeatureNames();
		features.addAll(Util.getFeatureNames(mutant));
		int numFeatures = features.size();
		System.out.println("numF:"+numFeatures);
		
		assert numFeatures <= 128; // we use the oracle, that we assume that does not have more features than the mutant
		long numConfigurations = (long)Math.pow(2, numFeatures - 1);
		System.out.println("F1:"+Util.getFeatureNames(mutant)+" "+oracle.getAllFeatureNames());
		int confsNotJudgedCorrectly = getBddsCountDiff(oracle, mutant);
		System.out.println("F2:"+Util.getFeatureNames(mutant)+" "+oracle.getAllFeatureNames());
		return new Conformance(numConfigurations, numConfigurations - confsNotJudgedCorrectly);
	}
	
	public static Conformance getConformance(IFeatureModel oracle, IFeatureModel mutant) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException {
		// assumiamo che il mutant non abbia MAI piu' features dell'oracolo???
		// AG
//		assert Util.getFeatureNames(oracle).containsAll(Util.getFeatureNames(mutant));
		assert oracle.getNumberOfFeatures() <= 64;
		long numConfigurations = (long)Math.pow(2, oracle.getNumberOfFeatures() - 1);

		//PA 13/10/2015
		//The mutant and the oracle may have a different number of features
		/*int oracleNumFeatures = oracle.getNumberOfFeatures();
		int mutantNumFeatures = mutant.getNumberOfFeatures();
		long numConfigurations = (long)Math.pow(2, Math.max(oracleNumFeatures, mutantNumFeatures) - 1);*/

		ModelComparator comparator = new ModelComparator(1000000);
		Comparison comparison = comparator.compare(oracle, mutant);
		if(comparison == Comparison.REFACTORING) {
			return new Conformance(numConfigurations, numConfigurations);
		}
		else {
			int confsNotJudgedCorrectly;
			switch(comparison) {
				case GENERALIZATION:
					confsNotJudgedCorrectly = getGeneralizationDifference(oracle, mutant);
					break;
				case SPECIALIZATION:
					confsNotJudgedCorrectly = getSpecializationDifference(oracle, mutant);
					break;
				case ARBITRARY:
					confsNotJudgedCorrectly = getArbitraryEditDifference(oracle, mutant);
					break;
				default:
					throw new Error("Comparison failed! Result = " + comparison.toString());
			}
			/*for(MyConfiguration mc: confsNotJudgedCorrectly) {
				System.out.println(mc);
			}*/
			return new Conformance(numConfigurations, numConfigurations - confsNotJudgedCorrectly);
		}
	}

	public static void main(String[] args) throws UnsupportedModelException, IOException, TimeoutException, FeatureModelException, ConfigurationEngineException, NoSuchExtensionException {
		/*System.out.println(getConformance("toyExamples/man.xml", "toyExamples/opt.xml"));
		System.out.println(getConformance("toyExamples/opt.xml", "toyExamples/man.xml"));
		System.out.println(getConformance("toyExamples/opt.xml", "toyExamples/opt.xml"));*/
		System.out.println(getConformance("toyExamples/alt4Mutant.xml", "toyExamples/alt4.xml"));
		System.out.println(getConformance("toyExamples/alt4.xml", "toyExamples/alt4Mutant.xml"));
		System.out.println(getConformance("toyExamples/alt4Mutant.xml", "toyExamples/alt4Mutant.xml"));
		/*File[] mutants = new File("mutants_model_20091225_1547989376/depth2/").listFiles();
		for(File mutant: mutants) {
			System.out.println(mutant.getAbsolutePath());
			System.out.println(getConformance("splotmodels_new/model_20091225_1547989376.xml", mutant.getAbsolutePath()));
		}*/		
	}

	private static int getGeneralizationDifference(IFeatureModel fm1, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		return getDifference(fm1, fm2);
	}

	private static int getSpecializationDifference(IFeatureModel fm1, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		return getDifference(fm2, fm1);
	}

	private static int getDifference(IFeatureModel fm1, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		int bdd1 = getBddsCount(fm1);
		int bdd2 = getBddsCount(fm2);
		return (bdd2 - bdd1);
	}

	private static int getArbitraryEditDifference(IFeatureModel fm1, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		return getBddsCountDiff(fm1, fm2);
	}

	private static int getBddsCount(IFeatureModel fm) throws IOException, FeatureModelException, ConfigurationEngineException {
		//BDD bdd = CompareOracleMutantBDD.getBDD(fm);
		//FMToBDD f2bdd = new FMToBDD(fm.getFeatureOrderList()); // non aggiunge le features astratte
		FMToBDD f2bdd = new FMToBDD(new ArrayList<>(Util.getFeatureNames(fm))); // with abstract features
		BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
		int num = (int)bdd.satCount();
		return num;
	}
/*
	public static BDD getBDD(IFeatureModel fm) throws IOException,
			FeatureModelException, ConfigurationEngineException {
		// use splar to count the valid configurations 
		SXFMWriter sxfmWriter =  new SXFMWriter(fm);
		File temp = File.createTempFile("tempfile", ".temp");
		sxfmWriter.writeToFile(temp);
		splar.core.fm.IFeatureModel IFeatureModel = new XMLFeatureModel(temp.getAbsolutePath(), XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
		IFeatureModel.loadModel();
		BDDConfigurationEngine confEngine = new BDDConfigurationEngine(temp.getAbsolutePath());
		confEngine.reset();
		FMReasoningWithBDD reasoner = confEngine.getReasoner();
		temp.delete();
		BDD bdd = reasoner.getBDD();
		return bdd;
	}*/
	
	/** the oracle contains the delta combinations */
	@Deprecated
	public static BDD createBDDFromOracle(Oracle oracle, List<String> fmVars, FMToBDD f2bdd) {
		BDD bddOriginalModel = f2bdd.nodeToBDD(NodeCreator.createNodes(oracle.originalFM));
		BDDFactory init = f2bdd.init; //BDDFactory.init(FMToBDD.BDD_LIBRARY, 3000, 300);
		//init.setVarNum(fmVars.size());
		
		// products to add
		BDD bigBDD = null;
		for (Map<String,Boolean> values : oracle.productsToAdd) {
			//Map<String,Boolean> values = oracle.values.get(0);
			List<Entry<String, Boolean>> vals = new ArrayList<>(values.entrySet());
			BDD bdd = null;
			for (Entry<String, Boolean> v : vals) {
				int elem = fmVars.indexOf(v.getKey());
				assert elem >= 0 : "element not found " + vals.get(0).getKey();
				BDD b = v.getValue() ? init.ithVar(elem) : init.nithVar(elem);
				if (bdd==null) bdd = b;
				else bdd.andWith(b);
			}
			if (bigBDD==null) bigBDD = bdd;
			else bigBDD.orWith(bdd);
		}
		if (bigBDD!=null) bddOriginalModel.orWith(bigBDD);
		
		// products to remove
		bigBDD = null;
		for (Map<String,Boolean> values : oracle.productsToRemove) {
			//Map<String,Boolean> values = oracle.values.get(0);
			List<Entry<String, Boolean>> vals = new ArrayList<>(values.entrySet());
			BDD bdd = null;
			for (Entry<String, Boolean> v : vals) {
				int elem = fmVars.indexOf(v.getKey());
				assert elem >= 0 : "element not found " + vals.get(0).getKey();
				BDD b = v.getValue() ? init.ithVar(elem) : init.nithVar(elem);
				if (bdd==null) bdd = b;
				else bdd.andWith(b);
			}
			bdd.not();
			if (bigBDD==null) bigBDD = bdd;
			else bigBDD.andWith(bdd);
		}
		if (bigBDD!=null) bddOriginalModel.andWith(bigBDD);
		
		return bddOriginalModel;
	}
	
	//XXX: marcoradavelli: inserita cache 
	private static Oracle currentOracle;
	private static Map<String, BDD> bddCache = new HashMap<>(); // the cache for the current oracle
	
	public static BDD getBDD(Oracle o) {
		if (o.getBDDCache()==null) return generateBDD(o);
		return o.getBDDCache();
	}
	
	private static BDD generateBDD(Oracle o) {
		return generateBDD(o, null, null);
	}
	
	private static BDD generateBDD(Oracle o, List<String> fmVars, FMToBDD f2bdd) {
		if (fmVars==null) fmVars = new ArrayList<>(o.getAllFeatureNames());
		//if (fmVars==null) fmVars = new ArrayList<>(getFeatureNames());
		o.fmVars = fmVars; 
		IFeatureModel correctFM = o.oracleFM;
		assert correctFM!=null;
		if (correctFM==null) {
			correctFM = o.originalFM.clone();
			Util.addMissingFeatures(o, correctFM);
		}
		if (f2bdd==null) o.f2bdd = f2bdd = new FMToBDD(fmVars);
		BDD bddOracle = f2bdd.nodeToBDD(NodeCreator.createNodes(correctFM));
		
		// aggiunge le feature rimosse al BDD
		for (String s : o.featuresToRemove) {
			int elem = o.fmVars.indexOf(s);
			assert elem >= 0 : "element not found " + s;
			bddOracle = bddOracle.and(o.f2bdd.init.nithVar(elem));
		}
		
		if (o.oracleFM!=null) return bddOracle;
		
		BDDFactory init = f2bdd.init;
		// products to add
		BDD bigBDD = null;
		for (Map<String,Boolean> values : o.productsToAdd) {
			//Map<String,Boolean> values = oracle.values.get(0);
			List<Entry<String, Boolean>> vals = new ArrayList<>(values.entrySet());
			BDD bdd = null;
			for (Entry<String, Boolean> v : vals) {
				int elem = fmVars.indexOf(v.getKey());
				assert elem >= 0 : "element not found " + v.getKey();
				BDD b = v.getValue() ? init.ithVar(elem) : init.nithVar(elem);
				if (bdd==null) bdd = b;
				else bdd.andWith(b);
			}
			if (bigBDD==null) bigBDD = bdd;
			else bigBDD.orWith(bdd);
		}
		if (bigBDD!=null) bddOracle.orWith(bigBDD);
		
		// products to remove
		bigBDD = null;
		for (Map<String,Boolean> values : o.productsToRemove) {
			//Map<String,Boolean> values = oracle.values.get(0);
			List<Entry<String, Boolean>> vals = new ArrayList<>(values.entrySet());
			BDD bdd = null;
			for (Entry<String, Boolean> v : vals) {
				int elem = fmVars.indexOf(v.getKey());
				assert elem >= 0 : "element not found " + vals.get(0).getKey();
				BDD b = v.getValue() ? init.ithVar(elem) : init.nithVar(elem);
				if (bdd==null) bdd = b;
				else bdd.andWith(b);
			}
			bdd = bdd.not();
			if (bigBDD==null) bigBDD = bdd;
			else bigBDD.andWith(bdd);
		}
		if (bigBDD!=null) bddOracle.andWith(bigBDD);
		
		return bddOracle;
	}
	
		
	/** Radavelli. Method created to compute adequacy */
	static int getBddsCountDiff(Oracle oracle, IFeatureModel fm2) throws IOException  {
		//List<String> fmVars = new ArrayList<>(Util.getFeatureNames(oracle.originalFM));
		//fm.getFeatureOrderList();
		//FMToBDD f2bdd = new FMToBDD(fmVars);
		BDD bdd = getBDD(oracle); // createBDDFromOracle(oracle, fmVars, f2bdd);
		BDD bdd2 = oracle.f2bdd.nodeToBDD(NodeCreator.createNodes(fm2)); // without cache
				// generateBDD2(oracle, fm2); // with cache

		Set<String> varO = oracle.getAllFeatureNames(); //oracle.getFeatureNames();
		Set<String> varM = Util.getFeatureNames(fm2);
		Set<String> varONotM = new HashSet<>(), varMNotO = new HashSet<>();
		for (String s : varO) if (!varM.contains(s)) varONotM.add(s);
		for (String s : varM) if (!varO.contains(s)) varMNotO.add(s);
		
		//assert varMNotO.isEmpty() : "Strange";
		
		// normalmente non ci dovrebbero essere differenze qui
		BDD bddOracle = bdd;
		/*BDD bddOracle = null;
		for (String s : varMNotO) {
			int elem = oracle.fmVars.indexOf(s);
			assert elem >= 0 : "element not found " + s;
			BDD b = oracle.f2bdd.init.nithVar(elem);
			bddOracle = (bddOracle==null ? bdd : bddOracle).and(b);
		}
		if (bddOracle==null) bddOracle = bdd;*/
		
		BDD bddModel = null;
		for (String s : varONotM) {
			int elem = oracle.fmVars.indexOf(s);
			assert elem >= 0 : "element not found " + s;
			BDD b = oracle.f2bdd.init.nithVar(elem);
			bddModel = (bddModel==null ? bdd2 : bddModel).and(b);
		}
		if (bddModel==null) bddModel = bdd2;
		
		System.out.println("M: "+bddModel.var());
		System.out.println("O: "+bddOracle.var());
		
		BDD and = bddOracle.and(bddModel);
		System.out.println("SATCount: "+ bddOracle.satCount() + " " + bddModel.satCount() + " " + and.satCount());

		for (String s: oracle.fmVars) System.out.print(s+" ");
		System.out.println("SAT ORACLE: ");
		AllSatIterator sats = bddOracle.allsat();
		while (sats.hasNext()) System.out.println(Arrays.toString(sats.nextSat()));
		
		System.out.println("SAT MODEL: ");
		sats = bddModel.allsat();
		while (sats.hasNext()) System.out.println(Arrays.toString(sats.nextSat()));
		
		System.out.println("SAT AND: ");
		sats = and.allsat();
		while (sats.hasNext()) System.out.println(Arrays.toString(sats.nextSat()));
		
		int diff = (int)((bddOracle.satCount() - and.satCount()) +
					 (bddModel.satCount() - and.satCount()) );
		/*System.out.println("AND pathCount " + and.pathCount() + " satCount " + and.satCount());
		and.printDot();
		//printSolutions(and);
		System.out.println(diff);*/
		return diff;
	}
	
	private static BDD generateBDD2(Oracle oracle, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		BDD bdd2 = null;
		String apted = "";
		if (oracle!=currentOracle) {
			currentOracle=oracle;
			bddCache.clear();
		} 
		if ((bdd2 = bddCache.get(apted = Util.toAPTED(fm2)))==null) {
			bddCache.put(apted, bdd2 = oracle.f2bdd.nodeToBDD(NodeCreator.createNodes(fm2)));
		}
		return bdd2;
	}

	public static int getStrengtheningCountDiff(Oracle oracle, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		BDD bdd = getBDD(oracle); // createBDDFromOracle(oracle, fmVars, f2bdd);
		BDD bdd2 = generateBDD2(oracle, fm2);
		BDD and = bdd.and(bdd2);
		return (int)((bdd.satCount() - and.satCount()));
	}
	
	public static int getWeakeningCountDiff(Oracle oracle, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		BDD bdd = getBDD(oracle); // createBDDFromOracle(oracle, fmVars, f2bdd);
		BDD bdd2 = generateBDD2(oracle, fm2);
		BDD and = bdd.and(bdd2);
		return (int)((bdd2.satCount() - and.satCount()));
	}
	
	private static int getBddsCountDiff(IFeatureModel fm, IFeatureModel fm2) throws IOException, FeatureModelException, ConfigurationEngineException {
		// nuovo modo (AG)
		/*FMToBDD f2bdd = new FMToBDD(fm.getFeatureOrderList());
		BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
		BDD bdd2 = f2bdd.nodeToBDD(NodeCreator.createNodes(fm2));*/

		//OR as follows??? (PA 13/10/2015)
		//it does not work because of remove
		/*List<String> fmVars = fm.getFeatureOrderList();
		List<String> fm2Vars = fm2.getFeatureOrderList();
		//fm must be the oracle and the mutant must contain
		//all the variables of the oracle
		assert fm2Vars.containsAll(fmVars): "\nfm = " + fm + "\nfmVars = " + fmVars + "\nfm2 = " + fm2 + "\nfm2Vars = " + fm2Vars;
		FMToBDD f2bdd = new FMToBDD(fm2Vars);
		BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
		BDD bdd2 = f2bdd.nodeToBDD(NodeCreator.createNodes(fm2));*/


		//OR as follows??? (PA 14/10/2015)
//		List<String> fmVars = fm.getFeatureOrderList();
//		List<String> fm2Vars = fm2.getFeatureOrderList();
//		//fm must be the oracle and the mutant must contain
//		//all the variables of the oracle
//		assert fm2Vars.containsAll(fmVars) || fmVars.containsAll(fm2Vars): "\nfm = " + fm + "\nfmVars = " + fmVars + "\nfm2 = " + fm2 + "\nfm2Vars = " + fm2Vars;
//		FMToBDD f2bdd = null;
//		if(fm2Vars.containsAll(fmVars)) {
//			f2bdd = new FMToBDD(new ArrayList<>(Util.getFeatureNames(fm2))); //  new FMToBDD(fm2Vars);
//		}
//		else {
//			f2bdd = new FMToBDD(new ArrayList<>(Util.getFeatureNames(fm))); //new FMToBDD(fmVars);
//		}
//		BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
//		BDD bdd2 = f2bdd.nodeToBDD(NodeCreator.createNodes(fm2));


		//OR as follows??? (MR 25/11/2017)
		Set<String> fmVars = new HashSet<String>(Util.getFeatureNames(fm2));
		fmVars.addAll(Util.getFeatureNames(fm));
		FMToBDD f2bdd = new FMToBDD(new ArrayList<>(fmVars));
		BDD bdd = f2bdd.nodeToBDD(NodeCreator.createNodes(fm));
		BDD bdd2 = f2bdd.nodeToBDD(NodeCreator.createNodes(fm2));


		/*System.out.println("bdd = " + bdd + "\tsatCount = " + bdd.satCount() + "\tnodeCount = " + bdd.nodeCount() + "\tpathCount = " + bdd.pathCount());
		bdd.printDot();
		printSolutions(bdd);
		System.out.println("bdd2 = " + bdd2 + "\tsatCount = " + bdd2.satCount() + "\tnodeCount = " + bdd2.nodeCount() + "\tpathCount = " + bdd2.pathCount());
		bdd2.printDot();
		printSolutions(bdd2);*/
		
		
		/*BDD bdd = getBDD(fm);
		System.out.println("bdd = " + bdd + "\tsatCount = " + bdd.satCount() + "\tnodeCount = " + bdd.nodeCount() + "\tpathCount = " + bdd.pathCount());
		printSolutions(bdd);
		bdd.printDot();
		System.out.println();
		BDD bdd2 = getBDD(fm2);
		System.out.println("bdd2 = " + bdd2 + "\tsatCount = " + bdd2.satCount() + "\tnodeCount = " + bdd2.nodeCount() + "\tpathCount = " + bdd2.pathCount());
		printSolutions(bdd2);
		bdd2.printDot();
		System.out.println();*/
		
		//
		/*BDD firstBdd = bdd.getFactory().ithVar(bdd.var());
		BDD secondBdd = bdd2.getFactory().ithVar(bdd2.var());
		BDD and = bdd.getFactory().one();*/
		
		
		BDD and = bdd.and(bdd2);
		int diff = (int)((bdd.satCount() - and.satCount()) +
					 (bdd2.satCount() - and.satCount()) );
		/*System.out.println("AND pathCount " + and.pathCount() + " satCount " + and.satCount());
		and.printDot();
		printSolutions(and);
		System.out.println(diff);*/
		return diff;
	}

	/*private static void printSolutions(BDD bdd) {
		AllSatIterator it = bdd.allsat();
		while(it.hasNext()) {
			System.out.println(Arrays.toString(it.nextSat()));
		}
	}*/
}