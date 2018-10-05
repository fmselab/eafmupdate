package eafmupdate.process;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import eafmupdate.GenerateMutants;
import eafmupdate.GenerateUpdateRequest;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.model.Oracle;
import fmmutation.utils.Utils;
import fmupdate.models.ExampleTaker;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

/**
 * A collection of models' pairs used for experiments.
 * @author mradavelli
 */
public enum ModelsPair{
	
	WRONGEXAMPLE(Model.EXAMPLE_E1, Model.EXAMPLE, true),
	EXAMPLE(Model.EXAMPLE, Model.EXAMPLE_E4),
	EXAMPLE_XOR(Model.EXAMPLE_XOR, Model.EXAMPLE_E4, true),
	REGISTER(Model.REGISTER_FAULT,Model.REGISTER),
	FIGURE4(Model.FIGURE4_MR, Model.FIGURE4_ORACLE),
	AIRCRAFT1(Model.AIRCRAFT1, Model.AIRCRAFT),
	CONNECTOR1(Model.CONNECTOR1, Model.CONNECTOR),
	
	//MOBILE_MEDIA_V3_TO_V4("models/constraintrepair/MobileMediaV3.xml", "models/constraintrepair/MobileMediaV4.xml"),  // names not compatible (different case)
	//MOBILE_MEDIA_V4_TO_V5(Model.MOBILE_MEDIA_V4, Model.MOBILE_MEDIA_V5, true),
	MOBILE_MEDIA_V5_TO_V6(Model.MOBILE_MEDIA_V5, Model.MOBILE_MEDIA_V6, "Favorites->Favorite", true),
	MOBILE_MEDIA_V6_TO_V7(Model.MOBILE_MEDIA_V6, Model.MOBILE_MEDIA_V7, "Mobilemedia->MobileMedia;Albummanagement->AlbumManagement;Createalbum->CreateAlbum;Deletealbum->DeleteAlbum;Favorite->Favorites;Setfavorites->SetFavorites;Viewfavorites->ViewFavorites;Photomanagement->MediaManagement;Receivephoto->ReceivePhoto;Sendphoto->SendPhoto;SMStranfer->SMSTransfer;Copyphoto->CopyMedia;Createphoto->CapturePhoto;Deletephoto->DeleteMedia;Editphotolabel->EditMediaLabel;Viewphoto->ViewPhoto;Photooperation->BasicMediaOperation",true),  // names not compatible (different case) 
	MOBILE_MEDIA_V7_TO_V8(Model.MOBILE_MEDIA_V7, Model.MOBILE_MEDIA_V8, "AlbumManagement->Albummanagement;SetFavorites->Setfavorites;ViewFavorites->Viewfavorites;MediaManagement->Mediamanagement;DeleteMedia->Deletemedia;EditMediaLabel->Editmedialabel;ViewPhoto->Viewphoto;CapturePhoto->Capturephoto;PlayMusic->Playmusic;MediaSelection->Mediaselection;SendPhoto->Sendphoto;ReceivePhoto->Receivephoto;SMSTransfer->SMStransfer;CopyMedia->Copymedia;BasicMediaOperation->Basicmediaoperation;MediaSelection->Mediaselection", true),
	
	MOBILE_MEDIA_V5_TO_V7(Model.MOBILE_MEDIA_V5, Model.MOBILE_MEDIA_V7, "Mobilemedia->MobileMedia;Albummanagement->AlbumManagement;Createalbum->CreateAlbum;Deletealbum->DeleteAlbum;Setfavorites->SetFavorites;Viewfavorites->ViewFavorites;Photomanagement->MediaManagement;Copyphoto->CopyMedia;Createphoto->CapturePhoto;Deletephoto->DeleteMedia;Editphotolabel->EditMediaLabel;Photooperation->BasicMediaOperation;Viewphoto->ViewPhoto",true),
	MOBILE_MEDIA_V6_TO_V8(Model.MOBILE_MEDIA_V6, Model.MOBILE_MEDIA_V8, "Mobilemedia->MobileMedia;Createalbum->CreateAlbum;Deletealbum->DeleteAlbum;Favorite->Favorites;Photomanagement->Mediamanagement;Photooperation->Basicmediaoperation;Createphoto->Createmedia;Deletephoto->Deletemedia;Editphotolabel->Editmedialabel;Copyphoto->Copymedia;SMStranfer->SMStransfer;",true),  // names not compatible (different case) 
	
	MOBILE_MEDIA_V5_TO_V8(Model.MOBILE_MEDIA_V5, Model.MOBILE_MEDIA_V8, "Mobilemedia->MobileMedia;Createalbum->CreateAlbum;Deletealbum->DeleteAlbum;Photomanagement->Mediamanagement;Photooperation->Basicmediaoperation;Createphoto->Createmedia;Deletephoto->Deletemedia;Editphotolabel->Editmedialabel;Copyphoto->Copymedia",true),
	
	HELP_SYSTEM(Model.HELP_SYSTEM1, Model.HELP_SYSTEM2, true),
	//ELEC("models/constraintrepair/elec1.xml", "models/constraintrepair/elec2.xml", true),
	ERPSPLS(Model.ERPSPL_S1, Model.ERPSPL_S2, true),
	SMARTHOME(Model.SMARTHOME_V2, Model.SMARTHOME_V22, "ManualHeating->ManualHeating1;SmartHeating->AutomatedHeating;ElectronicWindows->AutomatedWindows;ManualLight->ManualIlumination;SmartLight->AutomatedIlumination;GUI->UI;PresenceSimulator->PresenceSimulation;BlindSimulation->BlindsSimulation;SmartSprinklers->AutomatedSprinklers;FingerPrintReader->FingerprintReader;HeatingManagement->HeatingControl;MusicSimulation->AVSimulation", true),
	
	// other models:
	AIRCRAFT10(Model.AIRCRAFT10, Model.AIRCRAFT, true),
	CONNECTOR10(Model.CONNECTOR10, Model.CONNECTOR, true),
	EXAMPLE_ORDER_SWITCHED(Model.EXAMPLE_ORDER_SWITCHED, Model.EXAMPLE, true),
	//EXAMPLE_ORACLE("models/constraintrepair/example.xml", "models/constraintrepair/exampleE3.xml", getOracleExample()),
	ERPSPL(Model.ERPSPL1, Model.ERPSPL2, true),
	EXAMPLE_E2(Model.EXAMPLE_E2, Model.EXAMPLE, true),
	EXAMPLE_E3(Model.EXAMPLE_E3, Model.EXAMPLE, true),
	EASY(Model.EASY1, Model.EASY2, true),	
	SIMPLE(Model.SIMPLEA, Model.SIMPLEB),
	
	PPU1_2(Model.PPU1, Model.PPU2, true), 
	PPU2_3(Model.PPU2, Model.PPU3, true), 
	PPU3_4(Model.PPU3, Model.PPU4, true), 
	PPU4_5(Model.PPU4, Model.PPU5, true), 
	PPU5_6(Model.PPU5, Model.PPU6, true), 
	PPU6_7(Model.PPU6, Model.PPU7, true), 
	PPU7_8(Model.PPU7, Model.PPU8, true), 
	PPU8_9(Model.PPU8, Model.PPU9, true), 
	PPU5_6_SIMPL(Model.PPU5S, Model.PPU6S, true), 
	PPU5_6_SIMPL2(Model.PPU5S2, Model.PPU6S2, true), 
	PPU5_6_SIMPL3(Model.PPU5S3, Model.PPU6S3, true),
	PPU5_6_SIMPL4(Model.PPU5S4, Model.PPU6S, true),
	PPU5_6_SIMPL5(Model.PPU5S5, Model.PPU6S5, true),
	PPU5_6_SIMPL5_INVERTITO(Model.PPU6S5, Model.PPU5S5, true),
	PPU_XOR(Model.PPU_XOR, Model.PPU_XOR, true),
	PPU_OPZIONALE(Model.PPU_OPZIONALE, Model.PPU_OPZIONALE, true),
	
	AIRCRAFT_EXAMPLE(Model.AIRCRAFT_EXAMPLE1, Model.AIRCRAFT_EXAMPLE2, "Matter->Materials", true),
	AIRCRAFT_EXAMPLEs(Model.AIRCRAFT_EXAMPLE1s, Model.AIRCRAFT_EXAMPLE2s, "Matter->Materials", true),
	PPU4_5_NOCONSTR(Model.PPU4, Model.PPU5_NOCONSTR, true), 
	
	// PPU d=2
	PPU1_3(Model.PPU1, Model.PPU3, true), 
	PPU2_4(Model.PPU2, Model.PPU4, true), 
	PPU3_5(Model.PPU3, Model.PPU5, true), 
	PPU4_6(Model.PPU4, Model.PPU6, true), 
	PPU5_7(Model.PPU5, Model.PPU7, true), 
	PPU6_8(Model.PPU6, Model.PPU8, true), 
	PPU7_9(Model.PPU7, Model.PPU9, true), 
	// PPU d=3
	PPU1_4(Model.PPU1, Model.PPU4, true), 
	PPU2_5(Model.PPU2, Model.PPU5, true), 
	PPU3_6(Model.PPU3, Model.PPU6, true), 
	PPU4_7(Model.PPU4, Model.PPU7, true), 
	PPU5_8(Model.PPU5, Model.PPU8, true), 
	PPU6_9(Model.PPU6, Model.PPU9, true), 
	
	// PPU d=4
	PPU1_5(Model.PPU1, Model.PPU5, true), 
	PPU2_6(Model.PPU2, Model.PPU6, true), 
	PPU3_7(Model.PPU3, Model.PPU7, true), 
	PPU4_8(Model.PPU4, Model.PPU8, true), 
	PPU5_9(Model.PPU5, Model.PPU9, true), 

	CAR_2009_2010(Model.CAR_2009, Model.CAR_2010, true), 
	CAR_2010_2011(Model.CAR_2010, Model.CAR_2011, true), 
	CAR_2011_2012(Model.CAR_2011, Model.CAR_2012, true), 
	
	CAR_2009_2011(Model.CAR_2009, Model.CAR_2011, true), 
	CAR_2010_2012(Model.CAR_2010, Model.CAR_2012, true), 
	
	CAR_2009_2012(Model.CAR_2009, Model.CAR_2012, true), 
	
	CAR_EXAMPLE(Model.CAR_2009, Model.CAR_2009e, "Monochrome Radio Display->Monochrome Display;Color Radio Display->Color Display", true),
	CAR_EXAMPLE2(Model.CAR_2011, Model.CAR_2011e, "Monochrome Radio Display->Radio Display", true),
	;
	
	//private static Logger logger = Logger.getLogger(Models.class.getName());
	
	private Model m1, m2;
	private String rename; // contains a list of strings for the rename of the features
	private Oracle oracle;
	
	private Map<Integer,IFeatureModel> mutated = new LinkedHashMap<>(); // all the mutated models, used in the experiments (so we always use the same across all the process strategies)
	
	/** false if it is supposed to do mutations, true if it is a real world model */
	public boolean experimentType;

	/** the (project-relative) path of the model and of the oracle (as feature model), respectively */
	private ModelsPair(Model m1, Model m2) {
		this.m1=m1;
		this.m2=m2;
	}
	
	private ModelsPair(Model m1, Model m2, boolean experimentType) {
		this(m1,m2);
		this.experimentType=experimentType;
	}
	
	private ModelsPair(Model path1, Model path2, String rename, boolean experimentType) {
		this(path1,path2, rename);
		this.experimentType=experimentType;
	}
	
	private ModelsPair(Model path1, Model path2, Oracle oracle) {
		this(path1, path2);
		this.oracle=oracle;
	}
	
	private ModelsPair(Model path1, Model path2, String rename) {
		this(path1,path2);
		this.rename=rename;
	}
	
	/** fm1 is the initial model, usually the wrong one */
	public IFeatureModel getFM1() { 
		return Util.renameFeatures(m1.getFM(), getRenameMap());
	}
	
	/** fm2 is the final model, the one we know to be correct */
	public IFeatureModel getFM2() { 
		return m2.getFM();
	}
	
	public Map<String,String> getRenameMap() {
		if (rename==null || rename.isEmpty()) return null;
		Map<String,String> renamingMap = new HashMap<>();
		for (String s : rename.split(";")) renamingMap.put(s.split("->")[0], s.split("->")[1]);
		return renamingMap;
	}
	
	public Oracle getOracle() throws TimeoutException {return oracle==null ? oracle = generateOracle() : oracle;}
	
	/** generates the oracle from two models 
	 * @throws TimeoutException in case the iterative sat solver process times out */
	private Oracle generateOracle() throws TimeoutException {
		IFeatureModel fm = getFM1();
		IFeatureModel fmOracle = getFM2();
		//System.out.println("Non rinomia? "+fm+" "+fmOracle);
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
	
	/*
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
	*/
	
	/** for experiments, it returns the model and save them at the same time */
	public IFeatureModel getMutatedModel(int iteration) {
		if (experimentType) return getFM1(); // if real world, there are no mutations
		if (mutated.containsKey(iteration)) return mutated.get(iteration);
		IFeatureModel mutatedModel = null;
		String title = name()+"_"+iteration;
		//String path = "generated/"+title+".xml";
		try {
			//File f = new File(path);
			//if(f.exists() && !f.isDirectory()) { 
				mutatedModel = ExampleTaker.readExample("generated/"+title+".xml");			    
			//}
		} catch (Exception e) {System.out.println(iteration+" generating mutated model for "+name());}
		try {
			if (mutatedModel==null) {
				System.out.println("Genero modello "+title);
				int numMutations = (int)(Math.random()*11);
				mutatedModel = Util.mutateRandomly(new MutatedModel(getFM2()), numMutations, GenerateMutants.instance.getFmMutators(), true).model;
				Util.saveTemporary(mutatedModel, "generated/", title);				
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
	static ModelsPair[] getModelForExperiments() {
		return new ModelsPair[] { 
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
	public static HashMap<ModelsPair,Integer> getModelsForExperiments() {
		HashMap<ModelsPair, Integer> models = new LinkedHashMap<>();
		models.put(ModelsPair.EXAMPLE, 100);
		models.put(ModelsPair.REGISTER, 100);
		models.put(ModelsPair.FIGURE4, 100);
		models.put(ModelsPair.AIRCRAFT1, 100);
		models.put(ModelsPair.CONNECTOR1, 100);
		
		models.put(ModelsPair.MOBILE_MEDIA_V5_TO_V6, 10); // generates out of memory error (GC)
		models.put(ModelsPair.MOBILE_MEDIA_V7_TO_V8, 2);
		models.put(ModelsPair.HELP_SYSTEM, 2);
		models.put(ModelsPair.SMARTHOME, 1);
		models.put(ModelsPair.ERPSPLS, 2);
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
	 * type: 1->Fadd, 2->Frem, 3->CFadd, 4->CFrem, 5->FMbefore, 6->FMafter, 7->CFrename
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
				case 7: return "0";
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
		case 7: return rename==null ? "0" : "" + (rename.split("->").length-1);
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
