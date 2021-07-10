package unifiedResolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 */

/**
 * BasicNode is the parent of all nodes, text indicates name of node, type means type of node
 * PredicateNode and NegatedPredicateNode directly inherited from BasicNode
 * UnaryPredicateNode and BinaryPredicateNode directly inherited from PredicateNode
 * Expression is a collection of BasicNodes
 * KB is a collection of Expressions
 */
class BasicNode {
	protected String text;			//PREDICATE_NAME, NEGATED_PREDICATE_NAME
	protected String type;			//PREDICATE, NEGATED_PREDICATE
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			//System.out.printf("Checked1 : %s and %s = True\n", this, obj);
			return true;
		}
		if (obj == null) {
			//System.out.printf("Checked2 : %s and %s = False\n", this, obj);
			return false;
		}
		if (getClass() != obj.getClass()) {
			//System.out.printf("Checked3 : %s and %s = False\n", this, obj);
			return false;
		}
	
		BasicNode other = (BasicNode) obj;
		if (text == null) {
			if (other.text != null) {
				//System.out.printf("Checked4 : %s and %s = False\n", this, obj);
				return false;
			}
		} else if (!text.equals(other.text)) {
			//System.out.printf("Checked5 : %s and %s = False\n", this, obj);
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				//System.out.printf("Checked6 : %s and %s = False\n", this, obj);
				return false;
			}
		} else if (!type.equals(other.type)) {
			//System.out.printf("Checked7 : %s and %s = False\n", this, obj);
			return false;
		}
		//System.out.printf("Checked8 : %s and %s = True\n", this, obj);
		if(this instanceof NegatedPredicateNode && other instanceof NegatedPredicateNode) {
			PredicateNode node1 = ((NegatedPredicateNode)this).node;
			PredicateNode node2 = ((NegatedPredicateNode)other).node;
			
			if(node1 instanceof BinaryPredicateNode && node2 instanceof BinaryPredicateNode) {
				//System.out.printf("Checked9 : %s and %s = %s\n", node1, node2,checkBinaryMatches((BinaryPredicateNode)node1, (BinaryPredicateNode)node2));
				return checkBinaryMatches((BinaryPredicateNode)node1, (BinaryPredicateNode)node2);
			}
			if(node1 instanceof UnaryPredicateNode && node2 instanceof UnaryPredicateNode) {
				return checkUnaryMatches((UnaryPredicateNode)node1, (UnaryPredicateNode)node2);
			}
		}
		if(this instanceof BinaryPredicateNode && other instanceof BinaryPredicateNode) {
			return checkBinaryMatches((BinaryPredicateNode)this,(BinaryPredicateNode)other);
		}
		if(this instanceof UnaryPredicateNode && other instanceof UnaryPredicateNode) {
			return checkUnaryMatches((UnaryPredicateNode)this,(UnaryPredicateNode)other);
		}
		return true;
	}
	
	private boolean checkUnaryMatches(UnaryPredicateNode n1, UnaryPredicateNode n2) {
		String text1 = n1.node1.text;
		String text2 = n2.node1.text;
		return text1.equals(text2);
	}
	private boolean checkBinaryMatches(BinaryPredicateNode n1, BinaryPredicateNode n2) {
		String text11 = n1.node1.text;
		String text12 = n1.node2.text;
		String text21 = n2.node1.text;
		String text22 = n2.node2.text;
		return text11.equals(text21) && text12.equals(text22);
	}
		
	public BasicNode(String text, String type) {
		this.text = text;
		this.type = type;
	}
	
	public String getText() {
		return text;
	}
	
	public String getType() {
		return type;
	}
	
	public String describeNode() {
		return "";
	}
}

class PredicateNode extends BasicNode {
	
	public PredicateNode(String text) {
		super(text, "PREDICATE");
	}
	
	public String toString() {
		return "";
	}
	
	public String describeNode() {
		return "";
	}
}

class UnaryPredicateNode extends PredicateNode {
	public BasicNode node1; 

	public UnaryPredicateNode(String text, String parameter1Text, String parameter1Type) {
		super(text);
		node1 = new BasicNode(parameter1Text,parameter1Type);
	}
	
	public String toString() {
		return String.format("%s(%s)",text,node1.text);
	}
	
	public String describeNode() {
		return String.format("%s(%s)",type,node1.type);
	}
}

class BinaryPredicateNode extends UnaryPredicateNode {
	public BasicNode node2;

	public BinaryPredicateNode(String text, String parameter1Text, String parameter1Type, String parameter2Text, String parameter2Type) {
		super(text,parameter1Text,parameter1Type);
		node2 = new BasicNode(parameter2Text,parameter2Type);
	}
	
	public String toString() {
		return String.format("%s(%s,%s)",text,node1.text,node2.text);
	}
	
	public String describeNode() {
		return String.format("%s(%s,%s)",type,node1.type,node2.type);
	}
}

class NegatedPredicateNode extends BasicNode {
	public PredicateNode node;

	public NegatedPredicateNode(PredicateNode node) {
		super(node.text, "NEGATED_PREDICATE");
		this.node = node;
	}
	
	public String toString() {
		return String.format("~%s",node.toString());
	}
	
	public String describeNode() {
		return String.format("~%s",node.describeNode());
	}
}

class Expression {
	public ArrayList<BasicNode> predicates;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicates == null) ? 0 : predicates.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expression other = (Expression) obj;
		if (predicates == null) {
			if (other.predicates != null)
				return false;
		} else if (!predicates.equals(other.predicates))
			return false;
		return true;
	}
	
	public Expression() {
		predicates = new ArrayList<BasicNode>();
	}
	
	public Expression(ArrayList<BasicNode> clauses) {
		this.predicates = clauses;
	}
	
	public void add(BasicNode node) {
		predicates.add(node);
	}
	
	public BasicNode getNode(int index) {
		return predicates.get(index);
	}
	
	public String toString() {
		return predicates.toString();
	}
	
	public void print() {
		Display.printLine(Display.LineType.DASHED);
		for(int j=0; j<predicates.size();j++) {
			System.out.printf("%-30s%s\n",predicates.get(j),predicates.get(j).describeNode());
		}
	}
}

class KB {
	ArrayList<Expression> KBExpressions;

	public KB(ArrayList<Expression> KBExpressions) {
		this.KBExpressions = KBExpressions;
	}
	
	public boolean contains(Expression ex) {
		for(int i=0; i<KBExpressions.size();i++) {
			if(ex.equals(KBExpressions.get(i))) return true;
		}
		return false;
	}
	
	public Expression getLastExpression() {
		return KBExpressions.get(KBExpressions.size()-1);
	}
	
	public void removeLastExpression() {
		KBExpressions.remove(KBExpressions.size()-1);
	}
	
	public ArrayList<String> printKB() {
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<KBExpressions.size();i++) {
			result.add(KBExpressions.get(i).toString());
			System.out.printf("%-10d%s\n",i,KBExpressions.get(i));
		}
		return result;
	}
	
	public Set<Integer> getIndicesOfExpressionsHavingPredicate(String predicateName, boolean isOriginalNegated){ //Set<Integer> toBeExcludedIndices
		Set<Integer> tempI = new HashSet<Integer>();
		
		//System.out.printf("Predicate name: %s, isNegated: %s\n",predicateName,isOriginalNegated);
		for(int i=0; i<KBExpressions.size();i++) {
			Expression ex = KBExpressions.get(i);
			for(int j=0; j<ex.predicates.size();j++) {
				boolean satisfiesConditions = ex.getNode(j).text.equals(predicateName) && ((ex.getNode(j) instanceof NegatedPredicateNode && !isOriginalNegated) || (!(ex.getNode(j) instanceof NegatedPredicateNode) && isOriginalNegated));
				//System.out.printf("%d.%d\tmatchPredicate name: %s--%s, %s\n",i,j,ex.getNode(j),ex.getNode(j).text,satisfiesConditions?"Added":"Not added");
				if(satisfiesConditions) { //&& !toBeExcludedIndices.contains(i)
					tempI.add(i);
				}
			}
		}
		
		return tempI;
	}
	
	public void print() {
		for(int i=0; i<KBExpressions.size();i++) {
			KBExpressions.get(i).print();
		}
	}
}
