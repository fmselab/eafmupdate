package org.prop4j;

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
		return nodeToBDDNormalized(n.eliminate(Choose.class, AtMost.class,AtLeast.class));
	}

	private BDD nodeToBDDNormalized(Node n) {
		LOGGER.debug("converting node " + n.toString() + " of class "	+ n.getClass());
		// as usual
		if (n instanceof And) {
			if (n.getChildren().length < 2) {
				LOGGER.debug(n.getChildren()[0].toString());
				return nodeToBDD(n.getChildren()[0]);
				// throw new NotTranslableException(n);
			} else
				return distribute(BDDOp.AND, nodesToBDDs(((And) n).getChildren()));
		} else if (n instanceof Or) {
			assert n.getChildren() != null && n.getChildren().length >= 2;
			return distribute(BDDOp.OR, nodesToBDDs(((Or) n).getChildren()));
		} else if (n instanceof Implies) {
			assert n.getChildren() != null && n.getChildren().length == 2;
			return distribute(BDDOp.IMPLIES,
					nodesToBDDs(((Implies) n).getChildren()));
		} else if (n instanceof Equals) {
			assert n.getChildren() != null && n.getChildren().length == 2;
			return distribute(BDDOp.EQ, nodesToBDDs(((Equals) n).getChildren()));
		} else if (n instanceof Not) {
			if (n.getChildren().length != 1)
				throw new NotTranslableException(n);
			Node[] children = ((Not) n).getChildren();
			return nodeToBDD(children[0]).not();
		} else {
			assert n instanceof Literal : n.getClass().getCanonicalName();
			return literalToExpression(n);
		} /*
		 * (n.toString().equalsIgnoreCase("-True")) return init.zero(); if
		 * (n.toString().equalsIgnoreCase("-False"))
		 * 
		 * return init.one(); }
		 */

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
