package fmmutation.mutationoperators.features;

import org.apache.log4j.Logger;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import fmmutation.mutationoperators.FMMutator;

/** optional to mandatory */
public class OptToMan extends FeatureMutator {

	private static Logger logger = Logger.getLogger(OptToMan.class
			.getName());

	public static FMMutator instance = new OptToMan();

	@Override
	String mutate(IFeatureModel fm, IFeature feature) {
		assert !feature.getStructure().isMandatory();
		feature.getStructure().setMandatory(true);
		logger.info("mutating feature " + feature.getName()
				+ " from OPTIONAL TO MANDATORY");
		return (feature.getName() + " from OPTIONAL TO MANDATORY");
	}

	@Override
	boolean isMutable(IFeatureModel fm, IFeature tobemutated) {
		IFeatureStructure parent= tobemutated.getStructure().getParent();
		return (parent != null &&  parent.isAnd()  && !tobemutated.getStructure().isMandatory());
	}

}
