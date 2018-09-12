package eafmupdate;

public class Conformance {
	private double numConfigurations;
	private double numConfsJudgedCorrectly;

	public Conformance(double numConfigurations, double numConfsJudgedCorrectly) {
		this.numConfigurations = numConfigurations;
		this.numConfsJudgedCorrectly = numConfsJudgedCorrectly;
	}

	public double getNumConfigurations() {
		return numConfigurations;
	}

	public double getNumConfsJudgedCorrectly() {
		return numConfsJudgedCorrectly;
	}

	/** @return Adequacy
	 * Obs!!! It could be a problem if the different configurations are few.
	 * The number returned is, in fact, very close to 1 if the models are 
	 * very close to each other, and there may be loss of precision.
	 * Better to use the percentage of incorrect configurations, in this scenario.
	 */
	public double percConfsJudgedCorrectly() {
		return numConfsJudgedCorrectly/(double)numConfigurations;
	}
	
	/** @return FailureRate
	 * Number very close to 0 if the system fails very few configurations 
	 * (as it is the normal case for very big feature models with few differences) */
	public double percConfsJudgedIncorrectly() {
		return (double)(numConfigurations - numConfsJudgedCorrectly)/(double)numConfigurations;	
	}

	@Override
	public String toString() {
		return numConfsJudgedCorrectly + "/" + numConfigurations;  
	}
	
	public double getNumConfsJudgedIncorrectly() {
//		System.err.println("CONFIGS: "+numConfigurations+" "+numConfsJudgedCorrectly);
		return numConfigurations - numConfsJudgedCorrectly;
	}
}