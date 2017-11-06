package fmficrepair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.prop4j.FMToBDD;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.editing.NodeCreator;
import fmficrepair.GenerateUpdateRequest;
import fmficrepair.Neighbors;
import fmficrepair.Util;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

/**
 * an oracle, made by the original fm, the set of products (combs) to be admitted, and the set of products (combs) to be removed
 * @author marcoradavelli
 */
public class Oracle {
	
	/** the original FeatureModel. Does NOT contain additional features */
	public IFeatureModel originalFM;
	
	/** if known, the oracle feature model */
	public IFeatureModel oracleFM;
	
	/** the failure inducing combinations */
	public List<Map<String,Boolean>> productsToAdd;
	
	/** the list of combinations containing the products to remove */
	public List<Map<String,Boolean>> productsToRemove;
	
	/** both existing features, and features to add, with information about their neighbors,
	 * used for the mutations: feature add and feature move */
	public Neighbors neighbors;
	
	/** the set of features that are to be removed */
	public Set<String> featuresToRemove;
	
	/** the BDD representation of the oracle */
	protected BDD bdd;
	
	/** the FMToBDD needed for initialization: to be in common for all BDDs that are to be joined together */
	public FMToBDD f2bdd;
	
	public List<String> fmVars;
	
	public Oracle(IFeatureModel original) {
		this.originalFM = original;
	}
	
	public Oracle(IFeatureModel original, Neighbors neighbors) {
		this(original);
		this.neighbors=neighbors;
	}
	
	public Oracle(IFeatureModel original, List<Map<String, Boolean>> productsToAdd, List<Map<String, Boolean>> productsToRemove) {
		this(original);
		this.productsToAdd = productsToAdd;
		this.productsToRemove = productsToRemove;
	}
	
	public Oracle(IFeatureModel original, List<Map<String, Boolean>> productsToAdd, List<Map<String, Boolean>> productsToRemove, Neighbors neighbors) {
		this(original, productsToAdd, productsToRemove);
		this.neighbors=neighbors;
	}

	public Oracle(IFeatureModel original, List<Map<String, Boolean>> productsToAdd, List<Map<String, Boolean>> productsToRemove, Neighbors neighbors, Set<String> featuresToRemove, IFeatureModel correct) {
		this(original, productsToAdd, productsToRemove, neighbors);
		this.featuresToRemove = featuresToRemove;
		this.oracleFM=correct;
	}
	
	public Set<String> getFeatureNames() {
		Set<String> features = Util.getFeatureNames(originalFM);
		if (neighbors!=null && neighbors.neighbors!=null) features.addAll(neighbors.neighbors.keySet());
		if (featuresToRemove!=null && !featuresToRemove.isEmpty()) features.removeAll(featuresToRemove);
		return features;
	}
	
	/** @return also the feature names of the original model */
	public Set<String> getAllFeatureNames() {
		Set<String> features = new HashSet<>(Util.getFeatureNames(oracleFM));
		features.addAll(featuresToRemove);
		return features;
	}
	
	public void setDeltaProducts(List<Map<String, Boolean>> productsToAdd, List<Map<String, Boolean>> productsToRemove) {
		this.productsToAdd = productsToAdd;
		this.productsToRemove = productsToRemove;
	}
	
	public BDD getBDD() {
		if (bdd==null) return generateBDD();
		return bdd;
	}
	
	public BDD generateBDD() {
		return generateBDD(null,null);
	}
	
	public BDD generateBDD(List<String> fmVars, FMToBDD f2bdd) {
		if (fmVars==null) fmVars = new ArrayList<>(getAllFeatureNames());
		//if (fmVars==null) fmVars = new ArrayList<>(getFeatureNames());
		this.fmVars = fmVars;
		IFeatureModel correctFM = oracleFM;
		if (correctFM==null) {
			correctFM = originalFM.clone();
			Util.addMissingFeatures(this, correctFM);
		}
		if (f2bdd==null) this.f2bdd = f2bdd = new FMToBDD(fmVars);
		BDD bddOriginalModel = f2bdd.nodeToBDD(NodeCreator.createNodes(correctFM));
		BDDFactory init = f2bdd.init;
		
		if (oracleFM!=null) return bddOriginalModel;
		
		// products to add
		BDD bigBDD = null;
		for (Map<String,Boolean> values : productsToAdd) {
			//Map<String,Boolean> values = oracle.values.get(0);
			List<Entry<String, Boolean>> vals = new ArrayList<>(values.entrySet());
			BDD bdd = null;
			for (Entry<String, Boolean> v : vals) {
				int elem = fmVars.indexOf(v.getKey());
				assert elem >= 0 : "element not found " + v.getKey();
				BDD b = v.getValue() ? init.ithVar(elem) : init.nithVar(elem);
				if (bdd==null) bdd = b;
				else bdd.andWith(b);
			}
			if (bigBDD==null) bigBDD = bdd;
			else bigBDD.orWith(bdd);
		}
		if (bigBDD!=null) bddOriginalModel.orWith(bigBDD);
		
		// products to remove
		bigBDD = null;
		for (Map<String,Boolean> values : productsToRemove) {
			//Map<String,Boolean> values = oracle.values.get(0);
			List<Entry<String, Boolean>> vals = new ArrayList<>(values.entrySet());
			BDD bdd = null;
			for (Entry<String, Boolean> v : vals) {
				int elem = fmVars.indexOf(v.getKey());
				assert elem >= 0 : "element not found " + vals.get(0).getKey();
				BDD b = v.getValue() ? init.ithVar(elem) : init.nithVar(elem);
				if (bdd==null) bdd = b;
				else bdd.andWith(b);
			}
			bdd = bdd.not();
			if (bigBDD==null) bigBDD = bdd;
			else bigBDD.andWith(bdd);
		}
		if (bigBDD!=null) bddOriginalModel.andWith(bigBDD);
		
		return bddOriginalModel;
	}
	
	@Override
	public String toString() {
		return originalFM.toString() + "\n"
			+ Util.getFeatureNames(originalFM) + "\n"
			+ "Neighbor constraints:\n"
			+ neighbors + "\n"
			+ "Products to add:\n"
			+ toString(productsToAdd)
			+ "Products to remove:\n"
			+ toString(productsToRemove);
	}
	
	public static String toString(List<Map<String,Boolean>> products) {
		if (products==null || products.isEmpty()) return "NullProducts";
		StringBuilder sb = new StringBuilder();
		products.forEach(p -> sb.append(p+"\n"));
		return sb.toString();
	}
	
	/**
	 * @return a CSV string containing information about: numFeatures; numNewFeatures; numProductsToAdd; numProductsToRemove.
	 */
	public String getStatistics() {
		return getFeatureNames().size() + ";"
				+ (neighbors!=null && neighbors.neighbors!=null ? neighbors.neighbors.size() : 0) + ";"
				+ productsToAdd.size() + ";"
				+ productsToRemove.size();
	}
	
	public Set<String> getFadd() {
		if (neighbors==null || neighbors.neighbors==null || neighbors.neighbors.isEmpty()) return new HashSet<>();
		Set<String> fadd = new HashSet<>();
		return neighbors.neighbors.keySet();
	}
	
	public Set<String> getFrem() {
		if (featuresToRemove==null) return new HashSet<>();
		return featuresToRemove;
	}
	
	public List<Map<String,Boolean>> getCFadd() {
		if (productsToAdd==null) return new ArrayList<>();
		return productsToAdd;
	}
	
	public long getNumCFadd() {
		return GenerateUpdateRequest.computeProductsToAddOrRemove(this, originalFM, true);
	}

	public long getNumCFrem() {
		return GenerateUpdateRequest.computeProductsToAddOrRemove(this, originalFM, false);
	}
	
	public List<Map<String,Boolean>> getCFrem() {
		if (productsToRemove==null) return new ArrayList<>();
		return productsToRemove;
	}
	
}
