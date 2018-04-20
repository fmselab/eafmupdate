package fmmutation.mutationoperators.features;

import org.apache.log4j.Logger;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmmutation.mutationoperators.FMMutator;

/** transform AND to alternative */
public class AndToAl extends FeatureMutator {

	private static Logger logger = Logger.getLogger(AndToAl.class);

	public static FMMutator instance = new AndToAl();

	@Override
	String mutate(IFeatureModel fm, IFeature tobemutated) {
		// if has more than one child or one child but not mandatory

		logger.info("mutating tobemutated " + tobemutated.getName()
				+ " from AND to alternative");
		tobemutated.getStructure().changeToAlternative();
		return (tobemutated.getName() + " from AND TO ALT");

	}

	@Override
	boolean isMutable(IFeatureModel fm, IFeature tobemutated) {
		int size = tobemutated.getStructure().getChildren().size();
		return (tobemutated.getStructure().isAnd() && size >1);
	}
}
