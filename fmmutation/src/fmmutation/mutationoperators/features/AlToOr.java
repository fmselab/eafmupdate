package fmmutation.mutationoperators.features;

import org.apache.log4j.Logger;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmmutation.mutationoperators.FMMutator;

/** transform alternative to or */
public class AlToOr extends FeatureMutator {

	private static Logger logger = Logger.getLogger(AlToOr.class);

	public static FMMutator instance = new AlToOr();

	@Override
	String mutate(IFeatureModel fm, IFeature feature) {
		feature.getStructure().changeToOr();
		assert feature.getStructure().isOr();
		logger.info("mutating feature " + feature.getName() + " from ALT TO OR");
		logger.debug("mmm" + fm.toString());
		return (feature.getName() + " from ALT TO OR");
	}

	@Override
	boolean isMutable(IFeatureModel fm, IFeature tobemutated) {
		// alternative, but more than one child
		// with one child it is equivalent already to OR - avoid equivalent
		// mutants
		return (tobemutated.getStructure().isAlternative() && tobemutated.getStructure().getChildren().size()>1);
	}

}
