package fmautorepair.mutationoperators.features;

import org.apache.log4j.Logger;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import fmautorepair.mutationoperators.FMMutator;

public class OrToAndOpt  extends FeatureMutator {

	private static Logger logger = Logger.getLogger(OrToAndOpt.class.getName());

	public static FMMutator instance = new OrToAndOpt();

	@Override
	String mutate(IFeatureModel fm, IFeature feature) {
			feature.getStructure().changeToAnd();
		for(IFeatureStructure child: feature.getStructure().getChildren()){
			child.setMandatory(false);
			
		}		
		logger.info("mutating feature " + feature.getName() + " from OR to AND in OPT");
		return (feature.getName() + " from OR TO AND in OPT");
	}

	@Override
	boolean isMutable(IFeatureModel fm, IFeature tobemutated) {
		// accettiamo solo con piu' di un figlio
		return (tobemutated.getStructure().isOr() && tobemutated.getStructure().getChildren().size()>1 );
	}
}