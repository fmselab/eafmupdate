package fmautorepair.mutationoperators;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmautorepair.utils.Pair;

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
	
	
	public IFeatureModel getPreviousModel() { return previous; }
	public int getGeneration() { return generation; }	
	
	/**
	 * added by marcoradavelli
	 * to reduce the verbosity of the output, printing only 
	 * mutations' description, and not the models step by step
	 */
	@Override
	public String toString() {
		return getMutationClass().getSimpleName()+" "+super.getSecond();
	}
	
}
