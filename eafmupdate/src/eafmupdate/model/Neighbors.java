package eafmupdate.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ovgu.featureide.fm.core.base.IFeatureStructure;

/** A class to represent information about all the neighbor constraints in a FM
 * especially for variables to be added
 * @author marcoradavelli
 *
 */
public class Neighbors {
	/**
	 * A map containing, for each feature (represented by its name), a map of neighbor feature (by name) together with their role(s)
	 */
	public Map<String, Map<String, Roles>> neighbors = new HashMap<>();
	
	public static Neighbors instance = new Neighbors(new HashMap<>());
	
	public Neighbors(Map<String, Map<String, Roles>> neighbours) {
		this.neighbors = neighbours;
		instance = this;
	}
	
	@Override
	public String toString() {
		return neighbors.toString();
	}

	/** @return true if it the movement respects the neighbor constraints */
	public boolean isNeighbour(String featuretobemoved, IFeatureStructure destination) {
		Map<String, Roles> n = neighbors.get(featuretobemoved);
		if (n==null || n.isEmpty()) return true;
		List<IFeatureStructure> children = destination.getChildren();
		for (Entry<String, Roles> e : n.entrySet()) {
			if ( destination.getFeature().getName().equals(e.getKey()) && e.getValue().containsRole(Role.PARENT) ) {
				return true;
			}
			for (IFeatureStructure futureSibling : children) {
				if ( futureSibling.getFeature().getName().equals(e.getKey()) && e.getValue().containsRole(Role.SIBLING) ) {
					return true;
				}
			}
		}
		return false;
	}
}
