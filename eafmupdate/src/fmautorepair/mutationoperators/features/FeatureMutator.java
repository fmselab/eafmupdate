package fmautorepair.mutationoperators.features;

import java.util.Iterator;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import eafmupdate.model.Util;
import fmautorepair.mutationoperators.FMMutation;
import fmautorepair.mutationoperators.FMMutator;
import fmautorepair.utils.Filter;
import fmautorepair.utils.FilteredIterator;

/**
 * given a feature model, it mutates it
 * 
 * @author garganti
 *
 */
abstract class FeatureMutator extends FMMutator{
	
//	private static Logger logger = Logger.getLogger(FeatureMutator.class);
	
	/**
	 * 
	 * @param fm
	 *            original model
	 * @return the list of mutated models
	 */
	@Override
	public final Iterator<FMMutation> mutate(final IFeatureModel fm) {
		// filter the feature the can be mutated (by name)
		Filter<String> filterF = new Filter<String>() {			
			@Override
			public boolean matches(String featureName) {
				IFeature tobemutated = fm.getFeature(featureName);
				// do not mutate the root
				if (tobemutated == fm.getStructure().getRoot()) return false;
				return isMutable(fm, tobemutated);
			}
		};
		// get all the names
		final Iterator<String> featureNames = new FilteredIterator<String>(Util.getFeatureNames(fm).iterator(), filterF);
		//
		final Class<? extends FeatureMutator> mutationClazz = this.getClass();
		//
		return new Iterator<FMMutation>() {			
			@Override
			public FMMutation next() {
				String featureName = featureNames.next();
				// build a copy (deep) of the model to be mutated
				IFeatureModel fm2 = fm.clone();
				// get the same feature in the model
				assert Util.getFeatureNames(fm2).contains(featureName);
				IFeature tobemutated = fm2.getFeature(featureName);
				// mutate the cloned model
				String result = mutate(fm2, tobemutated);
				assert result != null;
				// System.out.println(fm2.getRoot().isOr());
				// System.out.println(fm2.toString());
				// add some description or the mutation in the model
				fm2.getProperty().addComment("mutation " + result);				
				// 
				return new FMMutation(fm2, mutationClazz, result);
			}
			@Override
			public boolean hasNext() {
				return featureNames.hasNext();				
			}
		};
	}

	abstract boolean isMutable(IFeatureModel fm, IFeature tobemutated);

	/**
	 * 
	 * @param fm
	 *            feature model to be mutated (already copied - no need to be copied again)
	 * @param tobemutated
	 *            the feature to be mutated
	 * @return the name of the mutation (null if)
	 */
	abstract String mutate(IFeatureModel fm, IFeature tobemutated);

}
