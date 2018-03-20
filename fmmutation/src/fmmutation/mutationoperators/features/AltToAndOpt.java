package fmmutation.mutationoperators.features;

import fmmutation.mutationoperators.FMMutator;

public class AltToAndOpt extends AltToAnd {

//	private static Logger logger = Logger.getLogger(AlternativeToAndOpt.class.getName());

	public static FMMutator instance = new AltToAndOpt();

	private AltToAndOpt(){
		convertToOpt = true;
	}
}
