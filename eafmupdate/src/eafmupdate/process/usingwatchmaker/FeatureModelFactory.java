package eafmupdate.process.usingwatchmaker;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import eafmupdate.MutatedModel;


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
