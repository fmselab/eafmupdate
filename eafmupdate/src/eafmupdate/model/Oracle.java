package eafmupdate.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.prop4j.FMToBDD;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import net.sf.javabdd.BDD;

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
		Set<String> features = new HashSet<>();
		for (IFeature a : originalFM.getFeatures()) features.add(a.getName());
		if (neighbors!=null && neighbors.neighbors!=null) features.addAll(neighbors.neighbors.keySet());
		if (featuresToRemove!=null && !featuresToRemove.isEmpty()) features.removeAll(featuresToRemove);
		return features;
	}
	
	/** @return also the feature names of the original model */
	public Set<String> getAllFeatureNames() {
		Set<String> features = new HashSet<>();
		for (IFeature a : oracleFM.getFeatures()) features.add(a.getName());
		features.addAll(featuresToRemove);
		return features;
	}
	
	public void setDeltaProducts(List<Map<String, Boolean>> productsToAdd, List<Map<String, Boolean>> productsToRemove) {
		this.productsToAdd = productsToAdd;
		this.productsToRemove = productsToRemove;
	}
	
	public BDD getBDDCache() {
//		if (bdd==null) return generateBDD();
		return bdd;
	}
	
	@Override
	public String toString() {
		return originalFM.toString() + "\n"
			+ getFeatureNames() + "\n"
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
	
	public List<Map<String,Boolean>> getCFrem() {
		if (productsToRemove==null) return new ArrayList<>();
		return productsToRemove;
	}
	
}
