package eafmupdate.process.usingwatchmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import eafmupdate.GenerateMutants;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import fmmutation.mutationoperators.FMMutation;
import fmmutation.mutationoperators.FMMutator;

public class FMEvolutionaryOperator implements EvolutionaryOperator<MutatedModel> {
	
	public final List<FMMutation> allMutations = new ArrayList<>();
	public final List<MutatedModel> allModels = new ArrayList<>();
	
	public int count=0;
	
	public List<FMMutator> mutators;
	
	public FMEvolutionaryOperator() {
		mutators = GenerateMutants.instance.getFmMutators();
	}
	
	public FMEvolutionaryOperator(List<FMMutator> mutators) {
		this.mutators=mutators;
	}
	
	@Override
	public List<MutatedModel> apply(List<MutatedModel> selectedCandidates, Random rng) {
		List<MutatedModel> offspring = new ArrayList<>();
		for (MutatedModel model : selectedCandidates) {
			MutatedModel m = Util.mutateRandomly(model, 1, mutators, true);
			offspring.add(m);
			allModels.remove(model);
			allModels.add(m);
			allMutations.add(m.getLastMutation());
			
			count++;
		}
		return offspring;
	}

}
