package fmmutation.mutationoperators.features;

import fmmutation.mutationoperators.FMMutator;

public class AlToAndOpt extends AlToAnd {

//	private static Logger logger = Logger.getLogger(AlternativeToAndOpt.class.getName());

	public static FMMutator instance = new AlToAndOpt();

	private AlToAndOpt(){
		convertToOpt = true;
	}
}
