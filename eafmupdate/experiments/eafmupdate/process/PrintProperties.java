package eafmupdate.process;

import org.junit.Test;

/**
 * A test class to print the properties of the benchmarks
 * @author marcoradavelli
 *
 */
public class PrintProperties {

	@Test
	public void printModelProperties() {
		System.out.println("name & sizeModel & sizeTarget & Fadd & Frem & CFadd & CFrem & CFrename \\\\");
		//for (ModelsPair model : ModelsPair.getModelForExperiments()) {
		for (ModelsPair model : ModelsPair.values()) {
			System.out.println(getProperties(model));
		}
	}
	
	public static String getProperties(ModelsPair model) {
		try {
			return model.name()
/*
					+ " & " + model.getSizeModel()
					+ " & " + model.getFM2().getNumberOfFeatures()
					
					+ " & " + model.getOracle().getFadd().size()
					+ " & " + model.getOracle().getFrem().size()
					+ " & " + model.getOracle().getNumCFadd()
					+ " & " + model.getOracle().getNumCFrem()
*/
					+ " & " + model.getSizeModel()
					+ " & " + model.getFM2().getNumberOfFeatures()
					
					+ " & " + model.getSize(7)
					
					+ " & " + model.getSize(1)
					+ " & " + model.getSize(2)
					+ " & " + model.getSize(3)
					+ " & " + model.getSize(4)
					//+ " & " + model.getSize(5)
					//+ " & " + model.getSize(6)
					
					
//					+ " & " + (long)Util.getEditDistance(model.getOracle().oracleFM, model.getOracle().originalFM)
//				+ " & " + Util.getCompactness(model.getFM1().getStructure().getRoot())
//				+ " & " + Util.getCompactness(model.getFM2().getStructure().getRoot())
					+ " \\\\";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
