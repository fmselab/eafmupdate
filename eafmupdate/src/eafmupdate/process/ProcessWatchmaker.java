package eafmupdate.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.sat4j.specs.TimeoutException;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.model.Oracle;
import eafmupdate.process.usingwatchmaker.FMEvolutionaryOperator;
import eafmupdate.process.usingwatchmaker.FeatureModelFactory;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

/**
 * A repair process that uses an evolutionary technique, driven by a fitness function.
 * It uses Watchmaker library
 * @author marcoradavelli
 *
 */
public class ProcessWatchmaker extends Process {

	/** the size of the population */
	int nrand;
	
	/** the size of selected offspring set */
	int nbests;
	
	/** Stop condition when No improvement in last "maxOrder" generations */
	int noImprovementSteps;
	
	/** the maximum number of generations (= number of allowed mutations) */
	int maxOrder;
	
	/** the maximum repair time, in ms */
	long timeout;
	
	/** used in the process, the starting time of the repair */
	long repairStart;
		
	PopulationData<? extends MutatedModel> data;
	
	FitnessEvaluator<MutatedModel> fitnessEvaluator;
	SelectionStrategy<Object> selectionStrategy;
	
	/**
	 * For the size of population and selected offspring, if the parameter is <=0, it uses default values.
	 * If maxOrder<=0, no generation limit is set
	 * If timeout<=0, no time limit is set
	 * If noImprovementSteps<=0, no limit is set
	 * 
	 * @param nrand the size of the population
	 * @param nbests the selected offspring size
	 * @param maxOrder the maximum number of generations
	 * @param noImprovementSteps the number of generations with no improvements after which to stop
	 * @param timeout the timeout for stopping (in ms)
	 */
	public ProcessWatchmaker(int nrand, int nbests, int maxOrder, int noImprovementSteps, long timeout) {
		this.nrand = nrand;
		this.nbests = nbests;
		this.maxOrder = maxOrder;
		this.noImprovementSteps = noImprovementSteps;
		this.timeout = timeout;
		
		assert nbests <= nrand;
	}
	
	public ProcessWatchmaker(int nrand, int nbests, int maxOrder, int noImprovementSteps, long timeout, FitnessEvaluator<MutatedModel> fitnessEvaluator, SelectionStrategy<Object> selectionStrategy) {
		this(nrand,nbests,maxOrder,noImprovementSteps,timeout);
		this.fitnessEvaluator=fitnessEvaluator;
		this.selectionStrategy=selectionStrategy;
	}
	
	/** The repair method */
	@Override
	public Stats repair(IFeatureModel initialModel, Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		repairStart = Calendar.getInstance().getTimeInMillis();
		
		Util.applyAddRem(oracle, initialModel);
		
		final List<Double> bestFitnesses = new ArrayList<>();
		MutatedModel fmodel = new MutatedModel(initialModel);
		CandidateFactory<MutatedModel> candidateFactory = new FeatureModelFactory(fmodel);
		FMEvolutionaryOperator evolutionaryOperator = new FMEvolutionaryOperator();
		//FitnessEvaluator<MutatedModel> fitnessEvaluator = new FeatureModelEvaluator(oracle, initialModel);
		//SelectionStrategy<Object> selectionStrategy = new TruncationSelection(.2);
		Random rng = new Random();
		GenerationalEvolutionEngine<MutatedModel> engine = new GenerationalEvolutionEngine<MutatedModel>(candidateFactory, evolutionaryOperator, fitnessEvaluator, selectionStrategy, rng);
		engine.setSingleThreaded(true);
		
		ArrayList<TerminationCondition> tcs = new ArrayList<>();
		if (maxOrder>0) tcs.add(new GenerationCount(maxOrder));
		tcs.add(new TargetFitness(1.0, true));
		if (timeout>0) tcs.add(new ElapsedTime(timeout));
		if (noImprovementSteps>0) tcs.add(new Stagnation(noImprovementSteps, true));
		
		engine.addEvolutionObserver(new EvolutionObserver<MutatedModel>() {
			@Override
			public void populationUpdate(PopulationData<? extends MutatedModel> data) {
				ProcessWatchmaker.this.data = data;
				bestFitnesses.add(data.getBestCandidateFitness());
			}
		});
		
		MutatedModel best = engine.evolve(nrand, nbests, tcs.toArray(new TerminationCondition[0]));
		//System.out.println(best);
		
		return new Stats(initialModel, oracle, null, best, Calendar.getInstance().getTimeInMillis()-repairStart, evolutionaryOperator.count, best.mutations!=null ? best.mutations.size() : 0, -1, data.getMeanFitness(), data.getBestCandidateFitness(), evolutionaryOperator.allModels, evolutionaryOperator.allMutations, bestFitnesses);
	}
	
}
