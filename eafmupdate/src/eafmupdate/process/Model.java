package eafmupdate.process;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmmutation.utils.Utils;
import fmupdate.models.ExampleTaker;

/**
 * A collection of models used as benchmarks in the experiments.
 * @author mradavelli
 */
public enum Model {
	
	// "Simple" Models
	EXAMPLE_E1("constraintrepair/exampleE1.xml"),
	EXAMPLE("constraintrepair/example.xml"),
	EXAMPLE_E4("constraintrepair/exampleE4.xml"),
	EXAMPLE_XOR("constraintrepair/exampleE4_XOR.xml"),
	REGISTER_FAULT("constraintrepair/registerFaultSeeded.xml"),
	REGISTER("constraintrepair/register.xml"),
	FIGURE4_MR("examples_fmsfrompreprocessor/TKESSPLC11/Figure4_mr.xml"),
	FIGURE4_ORACLE("examples_fmsfrompreprocessor/TKESSPLC11/Figure4_oracle.xml"),
	AIRCRAFT1("constraintrepair/aircraftFault.xml"),
	AIRCRAFT("splotmodels_new/featureIDE/aircraft_fm.xml"),
	CONNECTOR1("constraintrepair/connectorFaultSeeded.xml"),
	CONNECTOR("splotmodels_new/featureIDE/connector_fm.xml"),
	AIRCRAFT_EXAMPLE_V1("constraintrepair/aircraftFault.xml"),
	AIRCRAFT_EXAMPLE_V2("splotmodels_new/featureIDE/aircraft_fm.xml"),
	
	// Benchmarks
	MOBILE_MEDIA_V3("constraintrepair/MobileMediaV3.xml"),
	MOBILE_MEDIA_V4("constraintrepair/MobileMediaV4.xml"),
	MOBILE_MEDIA_V5("constraintrepair/MobileMediaV5.xml"),
	MOBILE_MEDIA_V6("constraintrepair/MobileMediaV6.xml"),
	MOBILE_MEDIA_V7("constraintrepair/MobileMediaV7.xml"),
	MOBILE_MEDIA_V8("constraintrepair/MobileMediaV8.xml"),
	HELP_SYSTEM1("constraintrepair/HelpSystem1.xml"),
	HELP_SYSTEM2("constraintrepair/HelpSystem2.xml"),
	//ELEC("models/constraintrepair/elec1.xml", "models/constraintrepair/elec2.xml", true),
	ERPSPL_S1("constraintrepair/ERP_SPL_s1.xml"),
	ERPSPL_S2("constraintrepair/ERP_SPL_s2.xml"),
	SMARTHOME_V2("constraintrepair/SmartHomeV2.xml"),
	SMARTHOME_V22("constraintrepair/SmartHomeV2.2.xml"),
	PPU1("examples_fmsfrompreprocessor/lochau_asej16/ppu_1.xml"), 
	PPU2("examples_fmsfrompreprocessor/lochau_asej16/ppu_2.xml"),
	PPU3("examples_fmsfrompreprocessor/lochau_asej16/ppu_3.xml"),
	PPU4("examples_fmsfrompreprocessor/lochau_asej16/ppu_4.xml"),
	PPU5("examples_fmsfrompreprocessor/lochau_asej16/ppu_5.xml"),
	PPU6("examples_fmsfrompreprocessor/lochau_asej16/ppu_6.xml"),
	PPU7("examples_fmsfrompreprocessor/lochau_asej16/ppu_7.xml"),
	PPU8("examples_fmsfrompreprocessor/lochau_asej16/ppu_8.xml"),
	PPU9("examples_fmsfrompreprocessor/lochau_asej16/ppu_9.xml"),
	
	// other models:
	AIRCRAFT10("splotmodels_new/featureIDE/aircraft_fm/aircraft_fm_numMutations10.xml"),
	CONNECTOR10("splotmodels_new/featureIDE/connector_fm/connector_fm_numMutations_10.xml"),
	EXAMPLE_ORDER_SWITCHED("constraintrepair/example_featureSwitched.xml"),
	//EXAMPLE_ORACLE("models/constraintrepair/example.xml", "models/constraintrepair/exampleE3.xml", getOracleExample()),
	ERPSPL1("constraintrepair/ERP_SPL_1.xml"),
	ERPSPL2("constraintrepair/ERP_SPL_2.xml"),
	EXAMPLE_E2("constraintrepair/exampleE2.xml"),
	EXAMPLE_E3("constraintrepair/exampleE3.xml"),
	EASY1("constraintrepair/easy1.xml"),	
	EASY2("constraintrepair/easy2.xml"),	
	SIMPLEA("constraintrepair/simpleA.xml"),
	SIMPLEB("constraintrepair/simpleA.xml"),
	
	AIRCRAFT_EXAMPLE1("constraintrepair/aircraftExample1.xml"),
	AIRCRAFT_EXAMPLE2("constraintrepair/aircraftExample2.xml"),
	;
	
	//private static Logger logger = Logger.getLogger(Models.class.getName());
	
	private String path1;
	private ModelFormat format;
	
	/** the (project-relative) path of the model and of the oracle (as feature model), respectively */
	private Model(String path1) {
		this.path1=path1;
	}
	
	/** fm1 is the initial model, usually the wrong one */
	public IFeatureModel getFM() { 
		try {
		return ExampleTaker.readExample(path1);
		} catch (Exception e) {e.printStackTrace();}
		return load(path1, format); 
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
		return "" + getFM().getNumberOfFeatures();
	}
	
}
