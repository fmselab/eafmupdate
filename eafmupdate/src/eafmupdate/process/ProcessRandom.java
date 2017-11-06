package eafmupdate.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.model.Oracle;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class ProcessRandom extends Process {

	final int maxOrder, nrand, steadySteps;
	
	/** used in the process, the starting time of the repair */
	long repairStart;
	
	public ProcessRandom(int nrand, int maxOrder, int steadySteps) {
		this.nrand=nrand;
		this.maxOrder = maxOrder;
		this.steadySteps=steadySteps;
	}

	@Override
	public Stats repair(IFeatureModel initialModel, Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		repairStart = Calendar.getInstance().getTimeInMillis();
		
		Util.addMissingFeatures(oracle, initialModel);
		List<Double> bestFitnesses = new ArrayList<>();
		
		MutatedModel fmodel = new MutatedModel(initialModel);
		
		List<MutatedModel> models = new ArrayList<>();
		MutatedModel best = fmodel.clone(), mutant = null;
		double max=0, min=1, avg=0, maxMinComplexity=-1;
		int order=0;
		int count=0;
		List<Double> values = new ArrayList<>();
		for (int j=0; j<maxOrder; j++) {
			MutatedModel orderBest = best;
			for (int i=0; i<nrand; i++) {
				if (j==0) models.add(mutant = Util.mutateRandomly(best, 1));
				else models.set(i, mutant = Util.mutateRandomly(models.get(i), 1));
				
				double n = Util.getAdequacy(oracle, mutant.model);
				values.add(n);
				//System.out.println(n);
				if (n>max) { max=n; orderBest = mutant; maxMinComplexity=-1; order = j; }
				//else if (n==max && (maxMinComplexity==-1 || Util.getComplexity(mutant)<maxMinComplexity)) { maxMinComplexity = Util.getComplexity(mutant); best = mutant; }
				//else if (n==max && (maxMinComplexity==-1 || EditDistance.instance.getDistance(mutant.model,fmodel.model)<maxMinComplexity)) { maxMinComplexity = EditDistance.instance.getDistance(mutant.model,fmodel.model); orderBest = mutant; order=j; }
				avg+=n;
				count++;
				if (n<min) min=n;
				
				//if (i%100==0) System.out.println(i+": "+Util.getAdequacy(oracle, models.get(i).model));
				if (max==1) break;
			}
			bestFitnesses.add(max);
			best = orderBest;
			if (max==1) break;
			if (j-order >= steadySteps) break; 
		}
		avg = avg/count;	
		//System.out.println(min +" < "+avg+" < "+max +" : "+(order+1));
		//printStatistics(fmodel, fmodelCorrect, best);
		return new Stats(initialModel, oracle, null, best, Calendar.getInstance().getTimeInMillis()-repairStart, count, order, min, avg, max, models, null, bestFitnesses);
	}
	
}
