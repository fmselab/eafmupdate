package org.prop4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.editing.NodeCreator;

/**
 * The Class ConfEvaluator evaulates a Node against aconfiguration
 */
public class ConfEvaluator {
	Configuration conf;
	private Set<String> selectedFeatureNames;

	public Configuration getConfiguration() {
		return conf;
	}

	public ConfEvaluator(Configuration conf) {
		this.conf = conf;
		selectedFeatureNames = conf.getSelectedFeatureNames();
	}

	public Boolean isValidForModel(IFeatureModel fm) {
		// avoid the use of eliminate which has some problems
		Node node = NodeCreator.createNodes(fm);// .eliminate(Choose.class,
												// AtMost.class,AtLeast.class);

		// System.err.println(node);
		// return nodeToBoolean(node);
		return getFeatureNames(fm).containsAll(selectedFeatureNames) && nodeToBoolean(node);
	}
	
	/** XXX: mradavelli, to maintain independence */
	public static Set<String> getFeatureNames(IFeatureModel fm) {
		Set<String> fnames = new HashSet<>();
		for (IFeature a : fm.getFeatures()) fnames.add(a.getName());
		return fnames;
	}

	private Boolean getLiteralValue(Literal n) {
		if (selectedFeatureNames != null) {
			/*
			 * for (String f : conf.getSelectedFeatureNames()) { if
			 * (f.equals(n)) return true; }
			 */
			String literalName = n.toString();
			// System.err.println(literalName);
			if (literalName.startsWith("-")) {
				return !selectedFeatureNames.contains(literalName.substring(1));
			} else {
				return selectedFeatureNames.contains(literalName);
			}
		}
		return false;
	}

	private Boolean[] nodesToBooleans(Node[] n) {
		Boolean[] bools = new Boolean[n.length];
		for (int i = 0; i < n.length; i++) {
			bools[i] = nodeToBoolean(n[i]);
		}
		return bools;
	}

	private Boolean nodeToBoolean(Node n) {
		if (n.toString().trim().equalsIgnoreCase("True"))
			return true;
		if (n.toString().trim().equalsIgnoreCase("False"))
			return false;
		if (n.toString().trim().equalsIgnoreCase("-True"))
			return false;
		if (n.toString().trim().equalsIgnoreCase("-False"))
			return true;
		// as usual
		if (n instanceof And) {
			// check that is a proper AND
			if (n.getChildren().length < 2) 
				throw new NotTranslableException(n);
			else
				return distribute(Operator.AND, nodesToBooleans(((And) n).getChildren()));
		} else if (n instanceof Or) {
			// check it is a proper OR
			if (n.getChildren().length < 2) 
				throw new NotTranslableException(n);
			else
				return distribute(Operator.OR, nodesToBooleans(((Or) n).getChildren()));
		} else if (n instanceof Implies) {
			if (n.getChildren().length != 2)
				throw new NotTranslableException(n);
			return distribute(Operator.IMPLIES, nodesToBooleans(((Implies) n).getChildren()));
		} else if (n instanceof Equals) {
			Node[] equalsChildren = n.getChildren();
			if (equalsChildren.length != 2)
				throw new NotTranslableException(n);
			Boolean a1 = nodeToBoolean(equalsChildren[0]);
			Boolean a2 = nodeToBoolean(equalsChildren[1]);
			return a1 == a2;
		} else if (n instanceof Not) {
			if (n.getChildren().length != 1)
				throw new NotTranslableException(n);
			Node[] children = ((Not) n).getChildren();
			return !nodeToBoolean(children[0]);
		} else if (n instanceof AtMost) {
			//A constraint that is true iff at most a specified number of children is  true.
			AtMost atmost = (AtMost) n;
			Node[] children = atmost.getChildren();
			// count how many children are true
			int nTrue = 0;
			for(int i= 0; i < atmost.getChildren().length; i++){
				if (nodeToBoolean(children[i])) nTrue ++;
				if (nTrue > atmost.max) return false;
			}
			// max not reached
			return true;
		} else {
			assert n instanceof Literal : n.getClass().getCanonicalName();
			return getLiteralValue((Literal) n);
		}
	}

	static enum Operator {
		AND, OR, IMPLIES
	}

	private Boolean distribute(Operator operator, Boolean[] nodes) {
		switch (operator) {
		case AND: {
			assert nodes.length >= 2;
			if (nodes.length == 2) {
				return nodes[0] & nodes[1];
			} else {
				// Boolean n = distribute("AND", Arrays.copyOfRange(nodes, 1,
				// nodes.length));
				// return nodes[0] & n;
				return nodes[0] && distribute(Operator.AND, Arrays.copyOfRange(nodes, 1, nodes.length));
			}
		}
		case OR:
			assert nodes.length >= 2;
			if (nodes.length == 2) {
				return nodes[0] || nodes[1];
			} else {
				// Boolean n = distribute("OR", Arrays.copyOfRange(nodes, 1,
				// nodes.length));
				// return nodes[0] || n;
				return nodes[0] || distribute(Operator.OR, Arrays.copyOfRange(nodes, 1, nodes.length));
			}
		case IMPLIES:
			assert nodes.length >= 2;
			if (nodes.length == 2) {
				return !nodes[0] || nodes[1];
			} else {
				Boolean n = distribute(Operator.IMPLIES, Arrays.copyOfRange(nodes, 1, nodes.length));
				return !nodes[0] || n;
			}
		default:
			assert false;
			break;
		}
		return null;
	}
}
