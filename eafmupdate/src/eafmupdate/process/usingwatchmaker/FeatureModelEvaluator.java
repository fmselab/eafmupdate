package eafmupdate.process.usingwatchmaker;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.model.Oracle;

public class FeatureModelEvaluator implements FitnessEvaluator<MutatedModel> {
	
	/** the oracle to compare with */ protected Oracle oracle;
	
	/** the initial FM, to which to compute differences */ //private IFeatureModel initial;
		
	static int count=0;
	
	public FeatureModelEvaluator(Oracle oracle, IFeatureModel initial) {
		this.oracle = oracle;
		//this.initial = initial;
	}
	
	@Override
	public double getFitness(MutatedModel candidate, List<? extends MutatedModel> population) {
		double adequacy = 0;
		try {
			adequacy = Util.getAdequacy(oracle, candidate.model);
		} catch (Exception e) { 
			e.printStackTrace(); 
			System.out.println("Oracle: "+oracle);
			System.out.println("Candidate: "+candidate);
			System.out.println("Population size: "+(population==null ? "null" : population.size()));
			System.exit(0);
		}
		return adequacy;
	}

	@Override
	public boolean isNatural() {
		return true;
	}

}
