package unifiedResolution;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 * I have kept //comments(that were used to test/debug the program) in all files as it's just for reference
 * 
 * External GraphStream libraries - 
 * 		gs-core-2.0.jar and gs-ui-swing-2.0.jar 
 * are used for displaying graph when resolution is successful within specified number of steps
 * Both these libraries are included in executable jar 
 */

public class Start {
	/**
	 * The default access modifier is also called package-private, 
	 * which means that all members are visible within the same package 
	 * but aren't accessible from other packages
	 */
	static XMLHandler xmlReader;
	
	/**
	 * Method to convert Simple XML node (that has only 1 or 2 leaf nodes) into predicate node
	 * @param node - XML node
	 * @return PredicateNode - corresponding predicate node (Unary/Binary)
	 */
	public static PredicateNode getPredicateNode(Node node) {
		int childrenCount = node.getChildNodes().getLength();
		PredicateNode predicateNode = null;
		
		String predicateName = node.getAttributes().getNamedItem("text").getNodeValue();
		String parameter1Text = node.getChildNodes().item(0).getAttributes().getNamedItem("text").getNodeValue();
		String parameter1Type = node.getChildNodes().item(0).getNodeName();
		if(parameter1Type.equals("INTEGER")) parameter1Type = "CONSTANT";
		if(predicateName.equals("=")) predicateName = "EQ";
		
		if(childrenCount==1) {
			predicateNode = new UnaryPredicateNode(predicateName,parameter1Text,parameter1Type);
		}
		else if(childrenCount==2) {
			String parameter2Text = node.getChildNodes().item(1).getAttributes().getNamedItem("text").getNodeValue();
			String parameter2Type = node.getChildNodes().item(1).getNodeName();
			if(parameter2Type.equals("INTEGER")) parameter2Type = "CONSTANT";
			
			predicateNode = new BinaryPredicateNode(predicateName,parameter1Text,parameter1Type,parameter2Text,parameter2Type);
		}
		return predicateNode;
	}
	
	/**
	 * Recursive method that builds Expression from XML nodes
	 * @param node - Current node
	 * @param expressionPredicates - collection of PredicateNodes that have been added so far
	 * @return collection of BasicNodes
	 */
	public static ArrayList<BasicNode> getComplexExpression(Node node,ArrayList<BasicNode> expressionPredicates){
		
		NodeList childNodes = node.getChildNodes();
		
		for(int i=0; i<childNodes.getLength();i++) {
			Node currentNode = childNodes.item(i);
			String currentNodeName = currentNode.getNodeName();
			boolean isSimpleNode = xmlReader.isSimple(currentNode);
			
			//DFS style recursion, recurse until we find Simple XML node (that has only 1 or 2 leaf nodes)
			if(!isSimpleNode) {
				getComplexExpression(currentNode,expressionPredicates);
			}
			
			//After reaching Simple XML node, back recursion is followed
			//Form NegatedPredicateNode if currentNameNode is NOT
			if(currentNodeName == "NOT") {
				PredicateNode n = getPredicateNode(currentNode.getChildNodes().item(0));
				NegatedPredicateNode nn = new NegatedPredicateNode(n);
				expressionPredicates.add(nn);
			}
			else if(currentNodeName == "OR") {
				
			}
			else if(currentNode.getParentNode().getNodeName() != "NOT") {
				expressionPredicates.add(getPredicateNode(currentNode));
			}
		}
		return expressionPredicates;
	}
	
	/**
	 * Method to read given XML file and get all Expressions in it
	 * @param filePath - Location path of XML file
	 * @param title - Title for process, which kind of file we are reading
	 * @return collection of Expressions
	 */
	public static ArrayList<Expression> readFile(String filePath, String title){
		ArrayList<Expression> FOLExpressions = new ArrayList<Expression>();
		
		xmlReader = new XMLHandler(filePath);
		/*Display.printLine(Display.LineType.SOLID);
		System.out.println(title);
		System.out.println("Root:" + xmlReader.getRootNodeName());
		System.out.println("#Expressions:" + xmlReader.getNumberOfExpressions());*/
		
		NodeList XMLExpressions = xmlReader.getExpressions();
		for(int i = 0; i < XMLExpressions.getLength(); i++) {
			FOLExpressions.add(new Expression());
			
			Node node = XMLExpressions.item(i);
			boolean isSimpleNode = xmlReader.isSimple(node);
			
			if(isSimpleNode) {
				Expression expressionPredicates = FOLExpressions.get(i);
				expressionPredicates.add(getPredicateNode(node));
			}
			else {
				ArrayList<BasicNode> expressionPredicates = FOLExpressions.get(i).predicates;
				getComplexExpression(node,expressionPredicates);
			}
		}
		
		return FOLExpressions;
	}
	
	/**
	 * Main method - read KB, read Query, call resolve and print output
	 * @param args - requires 4 arguments
	 * args[0]: KB File Path
	 * args[1]: Query File Path
	 * args[2]: max steps for resolution
	 * args[3]: true/false, true for using UnitPreference, false otherwise
	 * @throws NumberFormatException - for args[2]: max steps for resolution 
	 * @throws IOException - for args[0] and args[1]
	 */
	public static void main(String[] args) throws NumberFormatException, IOException{
		if(args.length<4) {
			System.out.printf("Insufficient (%d out of 4) arguments provided, you need to specify KBFilePath, QueryFilePath, MaxStepsForResolution (natural number), {true/false} (true for UNIT_PREFERENCE, false for SOS) in that order",args.length);
			return;
		}
		String KBFilePath = args[0];
		String QueryFilePath = args[1];
		
		KB kb = new KB(readFile(KBFilePath,"Reading KB................................."));
		//kb.print();
		
		KB QueryExpressions = new KB(readFile(QueryFilePath,"Reading Negated Query................................."));
		//QueryExpressions.print();
		
		Expression query = QueryExpressions.KBExpressions.get(0);
		//query.print();
		Expression negatedQuery = new Expression();
		for(int i=0; i<query.predicates.size(); i++) {
			BasicNode n = query.predicates.get(i);
			if(n instanceof PredicateNode) negatedQuery.add(new NegatedPredicateNode((PredicateNode) n));
			else if(n instanceof NegatedPredicateNode) negatedQuery.add(((NegatedPredicateNode) n).node);
		}
		//negatedQuery.print();
		
		UnitPreference up = new UnitPreference(kb,negatedQuery,KBFilePath+"-output");
		Expression ex = up.resolve(Integer.parseInt(args[2]),Boolean.parseBoolean(args[3]));
		
		if(ex == null) {
			System.out.println("True");
		}
		else {
			System.out.println("False "+ex);
		}
	}
}
