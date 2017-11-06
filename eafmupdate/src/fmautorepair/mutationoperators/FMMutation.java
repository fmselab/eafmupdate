package fmautorepair.mutationoperators;

import java.io.IOException;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.model.Oracle;
import eafmupdate.model.Util;
import fmautorepair.utils.Pair;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class FMMutation extends Pair<IFeatureModel, String> {

	private Class<? extends FMMutator> mutationClass = null;
	
	private IFeatureModel previous;
	
	private int generation;
		
	/**
	 * 
	 * @param s the feature model after the mutation has been applied
	 * @param mutationClass the type of mutation
	 * @param a a string describing the mutation
	 */
	public FMMutation(IFeatureModel s, Class<? extends FMMutator> mutationClass, String a) {
		super(s, a);
		this.mutationClass = mutationClass;
	}
	
	public void setPreviousModel(IFeatureModel previous) { this.previous = previous; }
	public void setGeneration(int generation) { this.generation = generation; }

	public Class<? extends FMMutator> getMutationClass() {
		return mutationClass;
	}
	
	public double getActualAdequacy(Oracle oracle) throws TimeoutException, IOException, UnsupportedModelException, FeatureModelException, ConfigurationEngineException { return Util.getAdequacy(oracle, getFirst()); }
	public double getPreviousAdequacy(Oracle oracle) throws TimeoutException, IOException, UnsupportedModelException, FeatureModelException, ConfigurationEngineException { return Util.getAdequacy(oracle, previous); }
	public double getDeltaAdequacy(Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException { return getActualAdequacy(oracle)-getPreviousAdequacy(oracle); }
	
	public IFeatureModel getPreviousModel() { return previous; }
	public int getGeneration() { return generation; }
	
	public double getActualCompactness() { return Util.getCompactness(getFirst()); }
	public double getPreviousCompactness() { return Util.getCompactness(previous); }
	
	
	/**
	 * added by marcoradavelli
	 * to reduce the verbosity of the output, printing only 
	 * mutations' description, and not the models step by step
	 */
	@Override
	public String toString() {
		return getMutationClass().getSimpleName()+" "+super.getSecond();
	}
	
	/** @return some statistics in CSV format:
	 * mutationName; ADQbefore;ADQafter; deltaED;EDbefore;EDafter
	 * the editDistance is w.r.t. the target model 
	 * @throws UnsupportedModelException 
	 * @throws ConfigurationEngineException 
	 * @throws FeatureModelException 
	 * @throws IOException 
	 * @throws TimeoutException */
	public String getStatistics(Oracle oracle, IFeatureModel target, IFeatureModel initial) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		return getMutationClass().getSimpleName()
				+","+getPreviousAdequacy(oracle)
				+","+getActualAdequacy(oracle)
				+","+getPreviousCompactness()
				+","+getActualCompactness()
				;
	}
	
	public String getStatisticsWithoutCompactness(Oracle oracle, IFeatureModel target, IFeatureModel initial) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		return getMutationClass().getSimpleName()
				+","+getPreviousAdequacy(oracle)
				+","+getActualAdequacy(oracle)
				;
	}

}
