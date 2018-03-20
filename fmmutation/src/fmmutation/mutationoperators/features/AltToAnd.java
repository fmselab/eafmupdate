package fmmutation.mutationoperators.features;

import org.apache.log4j.Logger;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import fmmutation.mutationoperators.FMMutator;

/** transform alternative to AND */
public class AltToAnd extends FeatureMutator {

	private static Logger logger = Logger.getLogger(AltToAnd.class);

	public static FMMutator instance = new AltToAnd();
	
	protected boolean convertToOpt;
	
	protected AltToAnd(){
		convertToOpt = false;
	}
	
	@Override
	String mutate(IFeatureModel fm, IFeature feature) {
		feature.getStructure().changeToAnd();
		if (convertToOpt){
			for (IFeatureStructure child : feature.getStructure().getChildren()) {
				child.setMandatory(false);
			}
		}
		logger.info("mutating feature " + feature.getName()+ " from alternative to AND" + (convertToOpt? " to OPT" : ""));
		return feature.getName() + " from ALT TO AND" + (convertToOpt? " to OPT": "");
	}

	@Override
	boolean isMutable(IFeatureModel fm, IFeature tobemutated) {
		// alternative, but more than one child 
		// with one child it is equivalent already to And - avoid equivalent mutants
		return (tobemutated.getStructure().isAlternative()  && tobemutated.getStructure().getChildren().size()>1);
	}

}
