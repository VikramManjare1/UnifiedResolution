package unifiedResolution;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 */

/**
 * Class that implements resolution
 * resolve method works in loop as follows:
 * It uses PSWStack which stores all information of steps in the loop so far
 * DFS style loop,
 * loop until we are getting matchingExpression in currentPSW.kb
 * 		or max steps not reached or PSWStack is not empty
 * 
 * In each loop step, call Unifier.unify 
 * with first expression as current query/goal
 * and second expression as first expression that is not visited
 * 
 * Resolution is successful only if we get [] expression from unification
 * 
 * Exit status:
 * Exit status:1 Resolution loop steps limit (failed)
 * Exit status:2 Resolution complete (success)
 * Exit status:3,4 Resolution complete (failed, as no matching expression found)
 * Exit status:5 Resolution completed (assumed to be success (OWA), not reachable probably, occurs only when query and KB are totally not related)
 */
public class UnitPreference {
	private KB kb;
	private Expression goal;
	String outputFileName;
	
	public UnitPreference(KB kbSample, Expression neagatedGoal, String outputFileName){
		kb = kbSample;
		goal = neagatedGoal;
		this.outputFileName = outputFileName;
	}
	
	public Expression resolve(int steps, boolean isUnitPreference) throws IOException {
		int maxLimit = steps;
		Display.printLine(Display.LineType.SOLID);
		
		printKB();
		ArrayList<String> result = kb.printKB();
		PrintWriter writer = new PrintWriter(outputFileName+"-"+(isUnitPreference?"UnitPreference":"SOS")+".txt", "UTF-8");
		writer.println("Knowledge Base = { ");
		for(int i=0; i<result.size();i++) {
			writer.println("\t"+result.get(i));
		}
		writer.println("}");
		writer.println("");
		System.out.printf("~Goal ----> %s\n", goal);
		writer.println("~Goal ----> "+goal);
		writer.println("");
		Display.printLine(Display.LineType.DASHED);
		
		System.out.println("Resolution started................................");
		
		Stack<PSW> PSWStack = new Stack<>();
		Set<Integer> matchingExpressionIndices = getMatchingExpressionIndices(goal, kb); //,new Stack<Integer>()
		
		if(matchingExpressionIndices.size()>0) {
			//KB tempkb = kb;
			PSWStack.push(new PSW(kb,goal));
			PSW currentPSW = PSWStack.peek();
			currentPSW.TotalSequence.addAll(matchingExpressionIndices);
			currentPSW.querySequence.add(goal);
			KB currentKB, tempKB;
			
			while(!PSWStack.empty()) {
				maxLimit--;
				if(maxLimit == 0) {
					Display.printLine(Display.LineType.DASHED);
					currentPSW.writeToFile(writer, false);
					System.out.printf("Exit status:1 Resolution loop steps limit (%d steps performed) reached, exiting the resolve method\n",steps);
					writer.close();
					return currentPSW.kb.getLastExpression();
				}
				
				currentPSW = PSWStack.peek();
				currentKB = currentPSW.kb;
				
				Display.printLine(Display.LineType.DASHED);
				System.out.printf("STEP: %d\n",steps-maxLimit);
				System.out.printf("query ----> %s\n", currentPSW.tempGoal);
				currentPSW.print();
				
				if(matchingExpressionIndices.size()>0) {
					
					int currentExIndex;
					
					if(isUnitPreference)currentExIndex = currentPSW.getShortestExpressionIndex();
					else currentExIndex = currentPSW.TotalSequence.peek();
					
					System.out.printf("SelectedIndex: %d\n", currentExIndex);
					Expression ex1 = currentPSW.tempGoal;
					Expression ex2 = currentKB.KBExpressions.get(currentExIndex);
					
					Expression expressions[] = {ex1, ex2};
					
					//currentKB.print();
					//Display.printLine(Display.LineType.STAR);
					
					Expression ex = Unifier.unify(expressions);
					
					if(ex != null) {
						System.out.println("After resolve ---> " + ex);
						if(currentPSW.kb.contains(ex)) {
							Stack<Integer> tempSequence = currentPSW.VisitedSequence;
							Integer currentNodeIndex;
							if(isUnitPreference)currentNodeIndex = currentPSW.getShortestExpressionIndex();
							else currentNodeIndex = currentPSW.TotalSequence.peek();
							
							System.out.println("Branch redundant" + tempSequence+currentNodeIndex);
							currentPSW.TotalSequence.remove();
							while(currentPSW.TotalSequence.isEmpty()) {
								currentPSW.kb.removeLastExpression();
								System.out.println("Branch failed"+tempSequence+currentNodeIndex);
								if(!PSWStack.isEmpty()) {
									PSWStack.pop();
									currentPSW = PSWStack.peek();
									System.out.println();
									tempSequence = currentPSW.VisitedSequence;
									if(currentPSW.TotalSequence.isEmpty()) {
										if(isUnitPreference)currentNodeIndex = currentPSW.getShortestExpressionIndex();
										else currentNodeIndex = currentPSW.TotalSequence.peek();
									}
									else {
										currentNodeIndex = null;
									}
									currentPSW.TotalSequence.remove();
									currentPSW.VisitedSequence.pop();
									currentPSW.print();
								}
							}
						}
						else {
							currentPSW.VisitedSequence.add(currentExIndex);
							//currentPSW.printSequences();
							
							if(ex.predicates.size()==0) {
								currentPSW.displayGraph();
								currentPSW.writeToFile(writer, true);
								System.out.printf("Exit status:2 Resolution complete (%d steps performed)\n", steps-maxLimit);
								writer.close();
								return null;
							}
							
							//currentKB.print();
							//Display.printLine(Display.LineType.STAR);
							tempKB = currentKB;
							tempKB.KBExpressions.add(ex);
							PSW tempPSW = new PSW(tempKB, ex);
							tempPSW.VisitedSequence.addAll(currentPSW.VisitedSequence);
							tempPSW.querySequence.addAll(currentPSW.querySequence);
							tempPSW.querySequence.add(ex);
							//tempPSW.print();
							
							matchingExpressionIndices = getMatchingExpressionIndices(tempPSW.tempGoal, tempKB); //,currentPSW.VisitedSequence
							if(matchingExpressionIndices.size()>0) {
								tempPSW.TotalSequence.addAll(matchingExpressionIndices);
								PSWStack.push(tempPSW);
							}
							else {
								Display.printLine(Display.LineType.DASHED);
								currentPSW.writeToFile(writer, false);
								System.out.printf("query ----> %s\n", ex);
								System.out.println("No matching expression found, exiting the resolve method");
								System.out.printf("Exit status:3 Resolution complete (%d steps performed)\n", steps-maxLimit);
								writer.close();
								return currentPSW.kb.getLastExpression();
							}
						}
					}
					else {
						Stack<Integer> tempSequence = currentPSW.VisitedSequence;
						Integer currentNodeIndex;
						if(isUnitPreference)currentNodeIndex = currentPSW.getShortestExpressionIndex();
						else currentNodeIndex = currentPSW.TotalSequence.peek();
						
						currentPSW.TotalSequence.remove();
						System.out.println("Branch failed"+tempSequence+currentNodeIndex);
						
						while(currentPSW.TotalSequence.isEmpty()) {
							currentPSW.kb.removeLastExpression();
							System.out.println("Branch failed"+tempSequence+currentNodeIndex);
							if(!PSWStack.isEmpty()) {
								PSWStack.pop();
								currentPSW = PSWStack.peek();
								System.out.println();
								tempSequence = currentPSW.VisitedSequence;
								if(currentPSW.TotalSequence.isEmpty()) {
									if(isUnitPreference)currentNodeIndex = currentPSW.getShortestExpressionIndex();
									else currentNodeIndex = currentPSW.TotalSequence.peek();
								}
								else {
									currentNodeIndex = null;
								}
								currentPSW.TotalSequence.remove();
								currentPSW.VisitedSequence.pop();
								currentPSW.print();
							}
						}
					}
				}
				else {
					currentPSW.writeToFile(writer, false);
					System.out.println("No matching expression found, exiting the resolve method");
					System.out.printf("Exit status:4 Resolution complete (%d steps performed)\n", steps-maxLimit);
					writer.close();
					return currentPSW.kb.getLastExpression();
				}
			}
		}
		
		Display.printLine(Display.LineType.SOLID);
		//currentPSW.writeToFile(writer, true);
		System.out.printf("Exit status:5 Resolution complete (%d steps performed)\n", steps-maxLimit);
		writer.close();
		return null;
	}
	
	/*private boolean checkUniversalTruth(Expression ex) {
		for(int i=0;i<ex.predicates.size();i++) {
			if(ex.predicates.get(i).text.equals("EQ") && ex.predicates.get(i) instanceof NegatedPredicateNode && ((BinaryPredicateNode)((NegatedPredicateNode)ex.predicates.get(i)).node).node1.type.equals(((BinaryPredicateNode)((NegatedPredicateNode)ex.predicates.get(i)).node).node2.type) && !((BinaryPredicateNode)((NegatedPredicateNode)ex.predicates.get(i)).node).node1.text.equals(((BinaryPredicateNode)((NegatedPredicateNode)ex.predicates.get(i)).node).node2.text)){
				System.out.printf("Truth found: %s\n",ex.predicates.get(i).toString());
				return true;
			}
		}
		return false;
	}*/

	//Returns expressions in the kb that have predicates matching to tempGoal
	private Set<Integer> getMatchingExpressionIndices(Expression tempGoal,KB kb){ //,Stack<Integer> visitedSequence
		Set<Integer> matchingExpressionIndices = new HashSet<Integer>();
		
		for(int i=0; i<tempGoal.predicates.size(); i++) {
			matchingExpressionIndices.addAll(kb.getIndicesOfExpressionsHavingPredicate(tempGoal.getNode(i).text,tempGoal.getNode(i)instanceof NegatedPredicateNode));
		}
		
		return matchingExpressionIndices;
	}
	
	public void printKB() {
		System.out.println("Current KB:");
		kb.printKB();
		Display.printLine(Display.LineType.DASHED);
	}
}

/**
 * PSW means Program Status Word
 * Used in the context that it stores all information about specific step in resolution loop
 * Also used to write output in output file
 * Used to display the graph of resolution steps as well
 */
class PSW {
	public KB kb;
	public Queue<Integer> TotalSequence;
	public Stack<Integer> VisitedSequence;
	public Stack<Expression> querySequence;
	Expression tempGoal;
	
	public PSW(KB kb, Expression goal) {
		this.kb = kb;
		tempGoal = goal;
		TotalSequence = new LinkedList<Integer>();
		VisitedSequence = new Stack<Integer>();
		querySequence = new Stack<Expression>();
	}

	public void print() {
		kb.printKB();
		System.out.println("Goal:" + tempGoal);
		printSequences();
	}
	
	public void printSequences() {
		System.out.println("Total Sequence:" + TotalSequence);
		System.out.println("Visited Sequence:" + VisitedSequence);
		System.out.println("Query Sequence:" + querySequence);
	}
	
	//For UnitPreference, expression with minimum number of predicates is calculated
	public int getShortestExpressionIndex() {
		ArrayList<Integer> tList = new ArrayList<Integer>(TotalSequence);
		int shortestIndex = tList.get(0);
		int shortestLength = kb.KBExpressions.get(tList.get(0)).predicates.size();
		for(int i=1; i<TotalSequence.size();i++) {
			if(kb.KBExpressions.get(tList.get(i)).predicates.size()<shortestLength) {
				shortestIndex=tList.get(i);
				shortestLength = kb.KBExpressions.get(tList.get(i)).predicates.size();
			}
		}
		return shortestIndex;
	}
	
	public void writeToFile(PrintWriter writer, boolean success) {
		for(int i=0; i<VisitedSequence.size();i++){
			String node1, node2,nodeResult;
			node1 = kb.KBExpressions.get(VisitedSequence.get(i)).toString();
			node2 = querySequence.get(i).toString();
			if (i==VisitedSequence.size()-1) {
				if(success) nodeResult = "[]";
				else nodeResult = kb.getLastExpression().toString();
			}
			else
				nodeResult = querySequence.get(i+1).toString();
			writer.println("query ----> "+node2);
			writer.println("unification with ----> "+node1);
			writer.println("after resolve ----> "+nodeResult);
			writer.println("");
		}
		if(success) writer.println("True");
		else {
			writer.println("False "+kb.getLastExpression());
		}
	}
	
	public void displayGraph() throws IOException {
		Graph graph = new SingleGraph("DAG");

		String style = "node { size-mode: fit; shape: freeplane; fill-color: white; stroke-mode: plain; padding: 3px, 2px; }";
		graph.setAttribute("ui.stylesheet", style);
		
		//System.out.println(querySequence);
		for(int i=0; i<VisitedSequence.size();i++){
			String node1, node2,nodeResult;
			node1 = i+" "+kb.KBExpressions.get(VisitedSequence.get(i)).toString();
			node2 = querySequence.get(i).toString();
			if (i==VisitedSequence.size()-1)
				nodeResult = "[]";
			else {
				//System.out.println("\n"+node1);
				//System.out.println(node2);
				//System.out.println("Result: "+querySequence.get(i+1).toString());
				nodeResult = querySequence.get(i+1).toString();
			}
			if(graph.getNode(node1)==null) graph.addNode(node1);
			if(graph.getNode(node2)==null) graph.addNode(node2);
			if(graph.getNode(nodeResult)==null) graph.addNode(nodeResult);
			graph.addEdge("n1"+i, node1, nodeResult, true);
			graph.addEdge("n2"+i, node2, nodeResult, true);
		}
		
		System.setProperty("org.graphstream.ui", "swing");
		for (org.graphstream.graph.Node node : graph) {
            node.setAttribute("ui.label", node.getId());
		}
		
		//graph.display();
		Viewer viewer = graph.display();
		//viewer.enableAutoLayout(null);;
		viewer.enableAutoLayout();
	}
}