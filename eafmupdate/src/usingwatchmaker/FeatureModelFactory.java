package usingwatchmaker;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import fmficrepair.MutatedModel;


// build the initial population
public class FeatureModelFactory extends AbstractCandidateFactory<MutatedModel> {
	
	private MutatedModel initFM;

	public FeatureModelFactory(MutatedModel init){
		this.initFM = init;
	}
	
	@Override
	public MutatedModel generateRandomCandidate(Random rng) {
		// only one candidate 
		return initFM.clone();
	}


}
