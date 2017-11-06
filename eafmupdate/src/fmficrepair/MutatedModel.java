package fmficrepair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import eafmupdate.model.Oracle;
import eafmupdate.model.Util;
import fmautorepair.mutationoperators.FMMutation;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

/**
 * a class containing information on the provisional repaired model
 * including the mutations applied
 * @author marcoradavelli
 *
 */
public class MutatedModel {
	
	/** the last mutated model */
	public IFeatureModel model;
	
	/** the list of mutations applied, in its history */
	public List<FMMutation> mutations;
	
	public MutatedModel(IFeatureModel model) {
		this.model = model;
	}
	
	public MutatedModel(IFeatureModel model, List<FMMutation> mutations) {
		this.model = model;
		this.mutations = mutations;
	}
	
	/** create a copy of it, cloning its objects */
	public MutatedModel clone() {
		return new MutatedModel(model.clone(), mutations==null ? null : new ArrayList<>(mutations));
	}
	
	public void addMutation(FMMutation mutation) {
		if (mutations==null) mutations = new ArrayList<>();
		mutations.add(mutation);
	}
	
	public FMMutation getLastMutation() {
		return mutations==null || mutations.isEmpty() ? null : mutations.get(mutations.size()-1);
	}
	
	@Override
	public String toString() {
		return model.toString() + "\n"
				+ "Mutations: " + (mutations==null ? "null" : Arrays.toString(mutations.toArray()));
	}
	
	public IFeatureModel getInitialModel() {
		return mutations==null || mutations.isEmpty() ? model : mutations.get(0).getPreviousModel();
	}
	
	public double getInitialAdequacy(Oracle oracle) throws IOException, FeatureModelException, ConfigurationEngineException, TimeoutException {
		return Util.getAdequacy(oracle, getInitialModel());
	}
}
