package fmmutation.mutationoperators.features;

import org.apache.log4j.Logger;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmmutation.mutationoperators.FMMutator;

/** transform or to AND */
public class OrToAnd extends FeatureMutator {

	private static Logger logger = Logger.getLogger(OrToAnd.class.getName());

	public static FMMutator instance = new OrToAnd();

	@Override
	String mutate(IFeatureModel fm, IFeature feature) {
		feature.getStructure().changeToAnd();
		logger.info("mutating feature " + feature.getName() + " from OR to AND");
		return (feature.getName() + " from OR TO AND");
	}

	@Override
	boolean isMutable(IFeatureModel fm, IFeature tobemutated) {
		// accettiamo solo con piu' di un figlio
		return (tobemutated.getStructure().isOr() && tobemutated.getStructure().getChildren().size() >1);
	}

}
