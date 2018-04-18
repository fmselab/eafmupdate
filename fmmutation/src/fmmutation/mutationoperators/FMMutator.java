package fmmutation.mutationoperators;

import java.util.Iterator;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmmutation.utils.Filter;
import fmmutation.utils.FilteredIterator;
import fmmutation.utils.Utils;

/**
 * given a feature model, it mutates it.
 *
 * @author garganti
 */
public abstract class FMMutator {

	/**
	 * @param fm the fm
	 * @return a list of mutated models together with the string describing the mutation and the target of the mutation
	 */
	public abstract Iterator<FMMutation> mutate(IFeatureModel fm);
	
	/**
	 * Checks if the mutation does not produce any dead features, or infeasible model, or reduntant constraints, or...
	 * @param fm the model to be mutated
	 * @return the iterator with only such "safe" mutations
	 */
	public Iterator<FMMutation> mutateSafe(IFeatureModel fm) {
		Iterator<FMMutation> iterator = mutate(fm);
		final FilteredIterator<FMMutation> safeIterator = new FilteredIterator<>(iterator, new Filter<FMMutation>() {
			@Override
			public boolean matches(FMMutation mut) {
				return Utils.isOk(mut.getFirst());
			}
		});
		return safeIterator;
	}
	
}
