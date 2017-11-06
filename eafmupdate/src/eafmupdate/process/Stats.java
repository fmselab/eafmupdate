package fmficrepair.repair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.model.Oracle;
import eafmupdate.model.Util;
import fmautorepair.mutationoperators.FMMutation;
import fmficrepair.MutatedModel;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

/**
 * A class containing information and output of the repair process
 * @author marcoradavelli
 *
 */
public class Stats {
	public IFeatureModel initial, correct;
	
	/** the repaired model. Contains information about all the applied mutations */
	public MutatedModel repaired;
	
	/** the total time for repair */
	public long time;

	public int mutations;
	
	public int order;
	
	/** the min, avg, and max adequacy achieved in the mutations */
	public double min, avg, max;
	
	/** all the mutated models obtained */
	public List<MutatedModel> models = new ArrayList<>();
	
	/** all the mutations applied */
	public List<FMMutation> allMutations = new ArrayList<>();
	
	/** the oracle, in the real, usual format */
	public Oracle oracle;
	
	double cachedAdequacy=-1;
	
	/** used for experiments */
	public RepairTag tag = new RepairTag("");
	
	/** only for experiment1, with seeded faults */
	public int seededMutationsOfTheInitialModel;
	
	/** the used repair process */
	public Processes repairProcess;
	
	public List<Double> bestFitnesses;
	
	/** used for the merge operators. Set at which stage the first repair process ends, and the second starts */
	public List<Integer> orderForStep = new ArrayList<>();
	
	public Stats(IFeatureModel initial, Oracle oracle, IFeatureModel correct, MutatedModel repaired, long time, int mutations, 
			int order, double min, double avg, double max, List<MutatedModel> models) {
		this.initial = initial;
		this.correct = correct;
		this.repaired = repaired;
		this.time = time;
		this.mutations = mutations;
		this.order = order;
		this.min = min;
		this.avg = avg;
		this.max = max;
		this.models = models;
		
		this.oracle = oracle==null ? new Oracle(correct, null, null) : oracle;
	}
	
	public Stats(IFeatureModel initial, Oracle oracle, IFeatureModel correct, MutatedModel repaired, long time, int mutations, 
			int order, double min, double avg, double max, List<MutatedModel> models, List<FMMutation> allMutations, List<Double> bestFitnesses) {
		this(initial, oracle, correct, repaired, time, mutations, order, min, avg, max, models);
		this.allMutations = allMutations;
		this.bestFitnesses = bestFitnesses;
	}
	
	@Deprecated
	public Stats(IFeatureModel initial, IFeatureModel correct, IFeatureModel repaired, long time, int mutations, 
			int order, double min, double avg, double max, List<IFeatureModel> models) {
		this(initial, null, correct, new MutatedModel(repaired), time, mutations, order, min, avg, max, toMutatedModelsForCompatibility(models));
	}
	
	protected static List<MutatedModel> toMutatedModelsForCompatibility(List<IFeatureModel> models) {
		List<MutatedModel> ms = new ArrayList<>();
		for (IFeatureModel m : models) ms.add(new MutatedModel(m));
		return ms;
	}
	
	public IFeatureModel getRepairedModel() {
		return repaired==null ? null : repaired.model;
	}
	
	/** @return a string with some statistics of the repair process */
	public String getStatistics() throws IOException, FeatureModelException, ConfigurationEngineException, TimeoutException, UnsupportedModelException {
		StringBuffer sb = new StringBuffer();
		double initialAdequacy = Util.getAdequacy(oracle, initial);
		double finalAdequacy = cachedAdequacy == -1 ? cachedAdequacy = Util.getAdequacy(getRepairedModel(), initial) : cachedAdequacy;
		sb.append("Initial configutation difference: "+initialAdequacy+"\n");
		sb.append("Initial model: "+initial+"\n");
		sb.append("Correct model: "+correct+"\n");
		sb.append("Best    model: "+repaired+"\n");
		sb.append("Correct model APTED: "+Util.toAPTED(correct.getStructure().getRoot())+"\n");
		sb.append("Repaired model APTED: "+Util.toAPTED(getRepairedModel().getStructure().getRoot())+"\n");
		sb.append("Delta percentuale in feature model configuration distance: "
				+Util.deltaPercent(finalAdequacy, initialAdequacy)+"\n");
		return sb.toString();
	}
	
	/** @return the final adequacy, of the best model */
	public double getAdequacy() throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		return Util.getAdequacy(oracle,getRepairedModel());
	}
	
	/** @return true if the model has been completely repaired, according to the oracle passed as parameter */
	public boolean isRepaired() throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		return getAdequacy()==1.0;
	}

	/**
	 * To create plots and tables, in R
	 * @param initialTag the prefix to put at the beginning of each CSV line
	 * @param bests if it has to print only the mutations that brought to the repaired model (the best one)
	 * @return a set of CSV lines, one for each mutations
	 */
	public String getMutationStatisticsAsCSV(String initialTag, boolean bests) {
		if (bests && repaired.mutations==null) return "";
		StringBuilder sb = new StringBuilder();
		int id=1;
		for (FMMutation mutation : bests ? repaired.mutations : allMutations) {
			try {
				sb.append(
						initialTag
						+","+(bests?(id):"")
						+","+(!bests ? mutation.getStatistics(oracle, correct, initial) : 
							mutation.getStatisticsWithoutCompactness(oracle, correct, initial)
							+","+(id==1 ? Util.getAdequacy(oracle, mutation.getPreviousModel()) : bestFitnesses.get(id-2))
							+","+bestFitnesses.get(id-1)
						)
						+(bests?(","+mutation):"")+"\n"
				);
			} catch (Exception e) {e.printStackTrace();}
			id++;
		}
		return sb.toString();
	}
	
	/**
	 * To create plots and tables, in R
	 * @return one CSV line, with information on the repair (time, initial and final adequacy, edit distance, and so on.
	 */
	public String getOneLineStatisticsAsCSV() {
		double initialAdq=-1, finalAdq=-1;
		try {
			initialAdq = repaired.getInitialAdequacy(oracle);
			finalAdq = getAdequacy();
		} catch (Exception e) {e.printStackTrace();}
		return seededMutationsOfTheInitialModel
				+ "," + initialAdq
				+ "," + finalAdq
				+ "," + Util.getCompactness(initial)
				+ "," + Util.getCompactness(getRepairedModel())
				+ "," + order
				+ "," + time
				+ "," + avg
				+ "," + max
				+ "," + (finalAdq==1.0 ? "1" : "0");
	}
	
	
	/** Appends a RepairStats coming from another repair process, to this one 
	 * @return */
	public Stats append(Stats s2) {
		orderForStep.add(mutations);
		this.allMutations.addAll(s2.allMutations);
		this.correct = s2.correct;
		this.models.addAll(s2.models);
		this.mutations += s2.mutations;
		this.order += s2.order; //FIXME I don't know exactly how to combine two RepairStats executed sequentially
		this.repaired = s2.repaired;
		this.time += s2.time;
		return this;
	}
	
}
