package eafmupdate.process.usingwatchmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import eafmupdate.MutatedModel;
import eafmupdate.Util;
import fmmutation.mutationoperators.FMMutation;

public class FMEvolutionaryOperator implements EvolutionaryOperator<MutatedModel> {
	
	public final List<FMMutation> allMutations = new ArrayList<>();
	public final List<MutatedModel> allModels = new ArrayList<>();
	
	public int count=0;
	
	@Override
	public List<MutatedModel> apply(List<MutatedModel> selectedCandidates, Random rng) {
		List<MutatedModel> offspring = new ArrayList<>();
		for (MutatedModel model : selectedCandidates) {
			MutatedModel m = Util.mutateRandomly(model, 1);
			offspring.add(m);
			allModels.remove(model);
			allModels.add(m);
			allMutations.add(m.getLastMutation());
			
			count++;
			
		}
		return offspring;
	}

}
