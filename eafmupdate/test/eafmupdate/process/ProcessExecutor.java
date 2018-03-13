package eafmupdate.process;

import java.util.Random;

import org.sat4j.specs.TimeoutException;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.selection.TruncationSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import eafmupdate.MutatedModel;
import eafmupdate.process.Models;
import eafmupdate.process.usingwatchmaker.FMEvolutionaryOperator;
import eafmupdate.process.usingwatchmaker.FeatureModelEvaluator;
import eafmupdate.process.usingwatchmaker.FeatureModelFactory;

public class ProcessExecutor {

	public static void main(String[] args) throws TimeoutException {
		
		Models model = Models.EXAMPLE;

		MutatedModel fm = new MutatedModel(model.getFM1());
		
		CandidateFactory<MutatedModel> candidateFactory = new FeatureModelFactory(fm);

		EvolutionaryOperator<MutatedModel> evolutionaryOperator = new FMEvolutionaryOperator();

		FitnessEvaluator<MutatedModel> fitnessEvaluator = new FeatureModelEvaluator(model.getOracle(), model.getFM1());

		SelectionStrategy<Object> selectionStrategy = new TruncationSelection(.2);
		
		Random rng = new Random();
		
		GenerationalEvolutionEngine<MutatedModel> engine = new GenerationalEvolutionEngine<MutatedModel>(candidateFactory, evolutionaryOperator,
				fitnessEvaluator, selectionStrategy, rng);
		engine.setSingleThreaded(true);
		
		
		TerminationCondition tc = new GenerationCount(20);
		TerminationCondition tc2 = new TargetFitness(1.0, true);
		
		MutatedModel best = engine.evolve(1000, 10, new TerminationCondition[] {tc, tc2});
		
		System.out.println(best);
	
	}

}
