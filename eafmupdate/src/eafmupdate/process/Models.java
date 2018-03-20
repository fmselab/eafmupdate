package eafmupdate.process;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import eafmupdate.GenerateUpdateRequest;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.model.Oracle;
import fmautorepair.utils.Utils;
import fmupdate.models.ExampleTaker;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

/**
 * A collection of models used for experiments.
 * @author mradavelli
 */
public enum Models {
	
	WRONGEXAMPLE("models/constraintrepair/exampleE1.xml", "models/constraintrepair/example.xml"),
	EXAMPLE("constraintrepair/example.xml", "constraintrepair/exampleE4.xml"),
	EXAMPLE_XOR("constraintrepair/exampleE4_XOR.xml", "constraintrepair/exampleE4.xml"),
	REGISTER("constraintrepair/registerFaultSeeded.xml","constraintrepair/register.xml"),
	FIGURE4("models/examples_fmsfrompreprocessor/TKESSPLC11/Figure4_mr.xml", "models/examples_fmsfrompreprocessor/TKESSPLC11/Figure4_oracle.xml"),
	AIRCRAFT1("models/constraintrepair/aircraftFault.xml", "models/splotmodels_new/featureIDE/aircraft_fm.xml"),
	CONNECTOR1("models/constraintrepair/connectorFaultSeeded.xml", "models/splotmodels_new/featureIDE/connector_fm.xml"),
	
	//MOBILE_MEDIA_V3_TO_V4("models/constraintrepair/MobileMediaV3.xml", "models/constraintrepair/MobileMediaV4.xml"),  // names not compatible (different case)
	MOBILE_MEDIA_V4_TO_V5("models/constraintrepair/MobileMediaV4.xml", "models/constraintrepair/MobileMediaV5.xml", true),
	MOBILE_MEDIA_V5_TO_V6("models/constraintrepair/MobileMediaV5.xml", "models/constraintrepair/MobileMediaV6.xml", true),
	//MOBILE_MEDIA_V6_TO_V7("models/constraintrepair/MobileMediaV6.xml", "models/constraintrepair/MobileMediaV7.xml"),  // names not compatible (different case) 
	MOBILE_MEDIA_V7_TO_V8("models/constraintrepair/MobileMediaV7.xml", "models/constraintrepair/MobileMediaV8.xml", true),
	HELP_SYSTEM("models/constraintrepair/HelpSystem1.xml", "models/constraintrepair/HelpSystem2.xml", true),
	//ELEC("models/constraintrepair/elec1.xml", "models/constraintrepair/elec2.xml", true),
	ERPSPLS("models/constraintrepair/ERP_SPL_s1.xml", "models/constraintrepair/ERP_SPL_s2.xml", true),
	SMARTHOME("models/constraintrepair/SmartHomeV2.xml", "models/constraintrepair/SmartHomeV2.2.xml", true),
	
	// other models:
	AIRCRAFT10("models/splotmodels_new/featureIDE/aircraft_fm/aircraft_fm_numMutations10.xml", "models/splotmodels_new/featureIDE/aircraft_fm.xml", true),
	CONNECTOR10("models/splotmodels_new/featureIDE/connector_fm/connector_fm_numMutations_10.xml", "models/splotmodels_new/featureIDE/connector_fm.xml", true),
	EXAMPLE_ORDER_SWITCHED("models/constraintrepair/example_featureSwitched.xml", "models/constraintrepair/example.xml"),
	//EXAMPLE_ORACLE("models/constraintrepair/example.xml", "models/constraintrepair/exampleE3.xml", getOracleExample()),
	ERPSPL("models/constraintrepair/ERP_SPL_1.xml", "models/constraintrepair/ERP_SPL_2.xml"),
	EXAMPLE_E2("models/constraintrepair/exampleE2.xml", "models/constraintrepair/example.xml"),
	EXAMPLE_E3("models/constraintrepair/exampleE3.xml", "models/constraintrepair/example.xml"),
	EASY("models/constraintrepair/easy1.xml", "models/constraintrepair/easy2.xml"),	
	SIMPLE("models/constraintrepair/simpleA.xml", "models/constraintrepair/simpleB.xml"),
	
	PPU1("examples_fmsfrompreprocessor/lochau_asej16/ppu_1.xml", "examples_fmsfrompreprocessor/lochau_asej16/ppu_2.xml"), 
	PPU2("examples_fmsfrompreprocessor/lochau_asej16/ppu_2.xml", ""),
	PPU3("examples_fmsfrompreprocessor/lochau_asej16/ppu_3.xml", ""),
	PPU4("examples_fmsfrompreprocessor/lochau_asej16/ppu_4.xml", ""),
	PPU5("examples_fmsfrompreprocessor/lochau_asej16/ppu_5.xml", ""),
	;
	
	//private static Logger logger = Logger.getLogger(Models.class.getName());
	
	private String path1, path2;
	private Oracle oracle;
	private ModelFormat format;
	private Map<Integer,IFeatureModel> mutated = new LinkedHashMap<>(); // all the mutated models, used in the experiments (so we always use the same across all the process strategies)
	
	/** false if it is supposed to do mutations, true if it is a real world model */
	public boolean experimentType;

	private Models(String path1, String path2) {
		this.path1=path1;
		this.path2=path2;
	}
	
	private Models(String path1, String path2, boolean experimentType) {
		this(path1,path2);
		this.experimentType=experimentType;
	}
	
	private Models(String path1, String path2, Oracle oracle) {
		this(path1, path2);
		this.oracle=oracle;
	}
	
	private Models(String path1, String path2, ModelFormat format) {
		this(path1, path2);
		this.format = format;
	}
	
	/** fm1 is the initial model, usually the wrong one */
	public IFeatureModel getFM1() { 
		try {
		return ExampleTaker.readExample(path1);
		} catch (Exception e) {e.printStackTrace();}
		return load(path1, format); 
	}
	
	/** fm2 is the final model, the one we know to be correct */
	public IFeatureModel getFM2() { 
		try {
			return ExampleTaker.readExample(path2);
		} catch (Exception e) {e.printStackTrace();}
		return load(path2, format);
	}
	
	
	public Oracle getOracle() throws TimeoutException {return oracle==null ? oracle = generateOracle() : oracle;}
	
	/** generates the oracle from two models 
	 * @throws TimeoutException in case the iterative sat solver process times out */
	private Oracle generateOracle() throws TimeoutException {
		IFeatureModel fm = getFM1();
		IFeatureModel fmOracle = getFM2();
		return Util.generateOracle(fm, fmOracle);
//		return new Oracle(fm, GenerateUpdateRequest.generateProductsToAdd(fmOracle, fm), GenerateUpdateRequest.generateProductsToRemove(fmOracle, fm), neighbors, fmOracle);
	}
	
	public static IFeatureModel load(String path, ModelFormat modelFormat) { 
		try {
			// try loading from FeatureIDE default XML format
			if (modelFormat==null || modelFormat==ModelFormat.FEATUREIDE)
				return ExampleTaker.readExample(path);
			else return Utils.readSPLOTModel(path);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private static Oracle getOracleExample() {
		IFeatureModel original = load("models/constraintrepair/example.xml", null);
		Map<String,Boolean> fic1 = new HashMap<>(), fic2 = new HashMap<>();
		fic1.put("Root", true);
		fic1.put("C", true);
		fic1.put("A", false);
		fic1.put("B", false);
		
		fic2.put("Root", true);
		fic2.put("A", true);
		fic2.put("B", true);
		
		List<Map<String,Boolean>> fics = new ArrayList<>();
		fics.add(fic1);
		fics.add(fic2);
		return new Oracle(original, fics, new ArrayList<>());
	}
	
	/** for experiments, it returns the model and save them at the same time */
	IFeatureModel getMutatedModel(int iteration) {
		if (experimentType) return getFM1(); // if real world, there are no mutations
		if (mutated.containsKey(iteration)) return mutated.get(iteration);
		IFeatureModel mutatedModel = null;
		String title = name()+"_"+iteration;
		String path = "models/generated/"+title+".xml";
		try {
			File f = new File(path);
			if(f.exists() && !f.isDirectory()) { 
				mutatedModel = ExampleTaker.readExample("models/generated/"+title+".xml");			    
			}
		} catch (Exception e) {System.out.println(iteration+" generating mutated model for "+name());}
		try {
			if (mutatedModel==null) {
				System.out.println("Genero modello "+title);
				int numMutations = (int)(Math.random()*11);
				mutatedModel = Util.mutateRandomly(new MutatedModel(getFM2()), numMutations).model;
				Util.saveTemporary(mutatedModel, "models/generated/", title);				
			}
			mutated.put(iteration, mutatedModel);
			return mutatedModel;
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	/** just to print statistics about the models repaired n times */
	public Map<Integer,IFeatureModel> getMutatedModels(int iterations) {
		if (experimentType) return null;
		if (mutated.size()>=iterations) return mutated;
		for (int i=0; i<iterations; i++) {
			getMutatedModel(i);
		}
		return mutated;
	}
	
	/** @return an array of the only models to be used in the experiments */
	static Models[] getModelForExperiments() {
		return new Models[] { 
			//SIMPLE,
			EXAMPLE,
			REGISTER,
			FIGURE4,
			AIRCRAFT1,
			CONNECTOR1,
			
//			MOBILE_MEDIA_V4_TO_V5,
			MOBILE_MEDIA_V5_TO_V6,
			MOBILE_MEDIA_V7_TO_V8,
			HELP_SYSTEM,
			SMARTHOME,
			ERPSPLS
		};
	}
	
	/** @return an ordered HashMap containing the model and the number of iterations,
	 * to be used for experiments. */
	public static HashMap<Models,Integer> getModelsForExperiments() {
		HashMap<Models, Integer> models = new LinkedHashMap<>();
		models.put(Models.EXAMPLE, 100);
		models.put(Models.REGISTER, 100);
		models.put(Models.FIGURE4, 100);
		models.put(Models.AIRCRAFT1, 100);
		models.put(Models.CONNECTOR1, 100);
		
		models.put(Models.MOBILE_MEDIA_V5_TO_V6, 10); // generates out of memory error (GC)
		models.put(Models.MOBILE_MEDIA_V7_TO_V8, 2);
		models.put(Models.HELP_SYSTEM, 2);
		models.put(Models.SMARTHOME, 1);
		models.put(Models.ERPSPLS, 2);
		return models;
	}
	
	@Override
	public String toString() {
		String s = this.name();
		String t = "";
		boolean underscore = false;
		for (int i=0; i<s.length(); i++) {
			if (s.charAt(i)=='_') {
				underscore=true;
			} else if (underscore==true) {
				t+=s.charAt(i);
				underscore=false;
			} else t+=(""+s.charAt(i)).toLowerCase();
		}
		return t;
	}
	
	
	public String getSizeModel() {
		if (!experimentType) {
			int sum=0, count=0, min=-1, max=-1;
			for (IFeatureModel m : getMutatedModels(100).values()) {
				int n = m.getNumberOfFeatures();
				sum+=n;
				count++;
				if (min==-1 || n<min) min=n;
				if (max==-1 || n>max) max=n;
			}
			return ""+sum/count;
		}
		return "" + getFM1().getNumberOfFeatures();
	}
	
	/**
	 * type: 1->Fadd, 2->Frem, 3->CFadd, 4->CFrem, 5->FMbefore, 6->FMafter
	 * @param type
	 * @return
	 * @throws TimeoutException
	 * @throws ConfigurationEngineException 
	 * @throws FeatureModelException 
	 * @throws IOException 
	 */
	String getSize(int type) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException {
		if (!experimentType) {
			double sum=0, count=0, min=-1, max=-1;
			for (IFeatureModel m : getMutatedModels(100).values()) {
				Oracle o = Util.generateOracle(m, getFM2());
				double n = 0;
				switch (type) {
				case 1: n = o.getFadd().size(); break;
				case 2: n = o.getFrem().size(); break;
				case 3: n = GenerateUpdateRequest.getNumCFadd(o); break;
				case 4: n = GenerateUpdateRequest.getNumCFrem(o); break;
				case 5: n = 1-Util.getAdequacy(o, m); break;
				case 6: 
					IFeatureModel fm = m.clone();
					Util.applyAddRem(o, fm);
					n = 1-Util.getAdequacy(o, fm); break;
				}
				sum+=n;
				count++;
				if (min==-1 || n<min) min=n;
				if (max==-1 || n>max) max=n;
			}
			if (type==1 || type==2) return f(sum/count);
			return (f(sum/count)+ " ("+f(min)+"-"+f(max)+")").replace(".0 ", " ").replace(".0-","-").replace(".0)", ")");
		}
		switch (type) {
		case 1: return "" + getOracle().getFadd().size();
		case 2: return "" + getOracle().getFrem().size();
		case 3: return "" + f(GenerateUpdateRequest.getNumCFadd(getOracle()));
		case 4: return "" + f(GenerateUpdateRequest.getNumCFrem(getOracle()));
		case 5: return "" + f(1-Util.getAdequacy(getOracle(), getFM1()));
		case 6: 
			IFeatureModel fm = getFM1();
			Util.applyAddRem(getOracle(), fm);
			return "" + f(1-Util.getAdequacy(getOracle(), fm));
		}
		return "error";
	}
	
	public static String f(double d) {
		//return new DecimalFormat("0.##E0").format(d);
		if(d <= 9999 && d == (long) d)
	        return String.format("%d",(long)d);
	    else {
	    	if (d<0.01 || d>9999) return "$"+ new DecimalFormat("0.##E0").format(d).replace("E", " \\times 10^{").replace(",",".") + "}$";
	    	return new DecimalFormat("0.00").format(d).replace(",",".");
	    }
	       // return String.format("%f",d);
			
	}
	
}
