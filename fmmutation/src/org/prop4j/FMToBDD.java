package org.prop4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

// convert a fm to a BDD
public class FMToBDD {

	// using JFactory
	public static final String BDD_LIBRARY = "j";

	private static final Logger LOGGER = Logger.getLogger(FMToBDD.class);

	public BDDFactory init;

	List<String> variables;

	public FMToBDD(List<String> variables) {
		init = BDDFactory.init(BDD_LIBRARY, 300000, 30000);
		init.setVarNum(variables.size());
		this.variables = variables;
	}

	enum BDDOp {
		AND, OR, IMPLIES, EQ
	}

	public BDD nodeToBDD(Node n) {
		//return nodeToBDDNormalized(n.eliminate(AtMost.class));
		//return nodeToBDDNormalized(n);
		//TODO MR removed eliminate
		//Node newNode = n.eliminate(Choose.class, AtMost.class,AtLeast.class);
		return nodeToBDDNormalized(n);
	}

	private BDD nodeToBDDNormalized(Node n) {
		LOGGER.debug("converting node " + n.toString() + " of class "	+ n.getClass());
		// as usual
		Node[] children = n.getChildren();
		if (n instanceof And) {
			if (children.length < 2) {
				LOGGER.debug(children[0].toString());
				return nodeToBDD(children[0]);
				// throw new NotTranslableException(n);
			} else
				return distribute(BDDOp.AND, nodesToBDDs(((And) n).getChildren()));
		} else if (n instanceof Or) {
			if (children.length ==1)
				return nodeToBDD(children[0]);
			assert children != null && children.length >= 2 : children.length;
			return distribute(BDDOp.OR, nodesToBDDs(((Or) n).getChildren()));
		} else if (n instanceof Implies) {
			assert children != null && children.length == 2;
			return distribute(BDDOp.IMPLIES,
					nodesToBDDs(((Implies) n).getChildren()));
		} else if (n instanceof Equals) {
			assert children != null && children.length == 2;
			return distribute(BDDOp.EQ, nodesToBDDs(((Equals) n).getChildren()));
		} else if (n instanceof Not) {
			if (children.length != 1)
				throw new NotTranslableException(n);
			return nodeToBDD(((Not) n).getChildren()[0]).not();
		} else if (n instanceof AtMost){
			// at most one is true (it is allowed all false)
			AtMost most = (AtMost) n;
			assert most.max == 1 : most.max;
			//System.out.println("AtMost case: "+Arrays.toString(most.children)+" "+most.max);
			BDD[] ors = new BDD[most.children.length+1];
			Node[] children2 = most.children;
			for (int i = 0; i < children2.length; i++) {
				Node c = children2[i];
				List<Node> otherChildren = new ArrayList<>(Arrays.asList(most.children));
				otherChildren.remove(c);
				// c AND not OR children
				BDD notChildren;
				if (otherChildren.size() == 1)
					notChildren = nodeToBDD(otherChildren.get(0)).not();
				else
					notChildren = distribute(BDDOp.OR,nodesToBDDs(otherChildren.toArray(new Node[otherChildren.size()]))).not();
				ors[i] = distribute(BDDOp.AND, new BDD[] {nodeToBDD(c),notChildren});
			}
			// oppure tutti falsi
			BDD allFalse = distribute(BDDOp.OR,nodesToBDDs(most.children)).not();
			ors[most.children.length] = allFalse;
			return distribute(BDDOp.OR,ors);
		} else {
			assert n instanceof Literal : n.getClass().getCanonicalName();
			return literalToExpression(n);
		}
	}

	private BDD literalToExpression(Node n) {
		Literal l = ((Literal) n);
		// Build not
		if (!l.positive) {
			// get the name without the negation (to be added later)
			String name = l.var.toString();
			assert !name.startsWith("-");
			if (name.equalsIgnoreCase("True")) return init.zero(); 
			if (name.equalsIgnoreCase("False")) return init.one(); 
			int indexOf = variables.indexOf(name);
			assert indexOf >= 0;
			return init.nithVar(indexOf);
		}
		assert l.positive;
		String name = l.toString();
		assert !name.startsWith("-");
		if (name.equalsIgnoreCase("True")) return init.one(); 
		if (name.equalsIgnoreCase("False")) return init.zero(); 
		int indexOf = variables.indexOf(name);
		assert indexOf >= 0: "variable " + name + " not found";
		return init.ithVar(indexOf);
	}

	// distribute operator op among the nodes
	private static BDD distribute(BDDOp op, BDD[] nodes) {
		assert nodes.length >= 2;
		BDD result = nodes[0];
		for (int i = 1; i < nodes.length; i++) {
			switch (op) {
			case AND:
				result.andWith(nodes[i]);
				break;
			case OR:
				result.orWith(nodes[i]);
				break;
			case EQ:
				result.biimpWith(nodes[i]);
				break;
			case IMPLIES:
				result.impWith(nodes[i]);
				break;
			default:
				throw new RuntimeException("operator " + op);
			}
		}
		return result;
	}

	private BDD[] nodesToBDDs(Node[] n) {
		BDD[] strings = new BDD[n.length];
		for (int i = 0; i < n.length; i++) {
			strings[i] = nodeToBDD(n[i]);
		}
		return strings;
	}

}
