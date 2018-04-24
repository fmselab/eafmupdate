package eafmupdate;

public class Conformance {
	private long numConfigurations;
	private long numConfsJudgedCorrectly;

	public Conformance(long numConfigurations, long numConfsJudgedCorrectly) {
		this.numConfigurations = numConfigurations;
		this.numConfsJudgedCorrectly = numConfsJudgedCorrectly;
	}

	public long getNumConfigurations() {
		return numConfigurations;
	}

	public long getNumConfsJudgedCorrectly() {
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
}