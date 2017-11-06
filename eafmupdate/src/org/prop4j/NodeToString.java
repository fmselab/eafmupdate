package org.prop4j;

import java.util.ArrayList;
import java.util.List;

// convert a node to a string
public class NodeToString {

	// dato un node possibilemente and restituisce la lista delle traduzione dei conjoint
	public static List<String> nodeToStrings(Node n){
		List<String> result = new ArrayList<>();
		// eliminate not supported operators
		n.eliminate(Choose.class,AtMost.class, AtLeast.class);
		if (n instanceof And){
			Node[] conjoints = ((And)n).getChildren();
			for(Node l: conjoints){
				result.addAll(nodeToStrings(l));
			}			
		} else {
			result.add(nodeToString(n));
		}
		return result;
	}
	
	// convert a single node to a string
	private static String nodeToString(Node n){
		if (n instanceof And){			
			return distribute(" and ",nodesToStrings(((And)n).getChildren()));
		} else if (n instanceof Or){
			return distribute(" or ",nodesToStrings(((Or)n).getChildren()));
		} else if (n instanceof Implies){
			return distribute(" => ",nodesToStrings(((Implies)n).getChildren()));
		} else if (n instanceof Equals){
			return distribute(" <=> ",nodesToStrings(((Equals)n).getChildren()));
		} else if (n instanceof Not){
			Node[] children = ((Not)n).getChildren();
			assert children.length == 1;
			return " !(" + nodeToString(children[0])+")";
		} else {
			assert n instanceof Literal : n.getClass().getCanonicalName();
			return n.toString();
		}
		
	}
	// distribute operator op among the nodes
	private static String distribute(String op, String[] nodes) {
		String result = "( " + nodes[0];
		for(int i = 1;  i  < nodes.length ; i ++){ 
			result+= op + " " + nodes[i];
		}
		result +=")";
		return result;
	}
	
	private static String[] nodesToStrings(Node[] n){
		String[] strings = new String[n.length];
		for (int i = 0 ; i  < n.length ; i ++){
			strings[i] = nodeToString(n[i]);
		}
		return strings;
	}
	
}
