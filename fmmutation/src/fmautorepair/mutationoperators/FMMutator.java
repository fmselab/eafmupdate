package fmautorepair.mutationoperators;

import java.util.Iterator;

import de.ovgu.featureide.fm.core.base.IFeatureModel;

// TODO: Auto-generated Javadoc
/**
 * given a feature model, it mutates it.
 *
 * @author garganti
 */
public abstract class FMMutator {

	/**
	 * 	
	 *
	 * @param fm the fm
	 * @return a list of mutated models together with the string describing the mutation and the target of the mutation
	 */
	public abstract Iterator<FMMutation> mutate(IFeatureModel fm);
	
}
