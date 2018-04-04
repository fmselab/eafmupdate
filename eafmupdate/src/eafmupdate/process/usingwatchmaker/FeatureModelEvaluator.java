package eafmupdate.process.usingwatchmaker;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.model.Oracle;

public class FeatureModelEvaluator implements FitnessEvaluator<MutatedModel> {
	
	/** the oracle to compare with */ private Oracle oracle;
	
	/** the initial FM, to which to compute differences */ private IFeatureModel initial;
		
	int numFeatures;
	static int count=0;
	
	public FeatureModelEvaluator(Oracle oracle, IFeatureModel initial) {
		this.oracle = oracle;
		this.initial = initial;
		this.numFeatures = oracle.getFeatureNames().size();
	}
	
	@Override
	public double getFitness(MutatedModel candidate, List<? extends MutatedModel> population) {
		double adequacy = 0;
		try {
			adequacy = Util.getAdequacy(oracle, candidate.model);
//			Util.saveTemporary(oracle.oracleFM, "output/temp/", "ORACLEHERE"+(count++));
//			Util.saveTemporary(candidate.model, "output/temp/", "ADQHERE"+(count++));
			System.out.println("AdequacyHere: "+adequacy);
		} catch (Exception e) { 
			e.printStackTrace(); 
			System.out.println("Oracle: "+oracle);
			System.out.println("Candidate: "+candidate);
			System.out.println("Population size: "+(population==null ? "null" : population.size()));
			System.exit(0);
		}
		/*double editDistance = 1.0 - ((double)Util.getEditDistance(initial, candidate.model) / (double)(numFeatures*2));
		double compactness = ((double) Util.getCompactness(candidate.model) / (double)numFeatures-1 );*/
		
		//return fitnessType.getFitness(adequacy, editDistance, compactness);
		return adequacy;
		//return adequacy==1.0 ? 1.0 : ( weights[0]*adequacy + weights[1]*editDistance + weights[2]*compactness);
	}

	@Override
	public boolean isNatural() {
		return true;
	}

}
