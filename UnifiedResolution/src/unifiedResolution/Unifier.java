package unifiedResolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 */

public class Unifier {
	/**
	 * Method that tries to UNIFY 2 given expressions
	 * Calls isUnifiable method for each pair of predicates from 2 expressions
	 * Substitution is stored in map called hm (hash map)
	 * If hm is not null, resolved expression is obtained using replaceTextBy1, replaceTextBy2
	 * @param Expressions - Array of 2 expressions that are to be unified
	 * @return Expression - resolved expression after unification, it's null if unification fails
	 */
	public static Expression unify(Expression Expressions[]) {
		//Display.printLine(Display.LineType.SOLID);
		//System.out.println("Checking unification..................................");
		//System.out.printf("Expression1 %s has %d clauses\n", Expressions[0].clauses, Expressions[0].clauses.size());
		//System.out.printf("Expression2 %s has %d clauses\n", Expressions[1].clauses, Expressions[1].clauses.size());

		for (int i = 0; i < Expressions[0].predicates.size(); i++) {
			//Display.printLine(Display.LineType.DASHED);

			for (int j = 0; j < Expressions[1].predicates.size(); j++) {
				BasicNode[] N = { Expressions[0].predicates.get(i), Expressions[1].predicates.get(j) };
				//System.out.printf("Checking %s and %s\n", N[0], N[1]);

				ArrayList<Map<String, String>> hm = isUnifiable(N[0], N[1]);

				if (hm != null) {
					// Display.printLine(Display.LineType.STAR);
					System.out.printf("%s <--- Unification with ---> %s, Substitution = %s\n", Expressions[0],
							Expressions[1], hm);
					Expression ex = new Expression();

					for (int l = 0; l < 2; l++) {
						for (int k = 0; k < Expressions[l].predicates.size(); k++) {
							BasicNode n = Expressions[l].predicates.get(k);
							// System.out.printf("\nNot Matching %s and %s: %s\n", n, N[l],
							// !n.equals(N[l]));

							if (!n.equals(N[l])) {
								if (n instanceof BinaryPredicateNode) {
									n = replaceTextBy2((BinaryPredicateNode) n, hm);

									// System.out.printf("Node %s added\n", n);
									ex.add(n);
								} else if (n instanceof UnaryPredicateNode) {
									n = replaceTextBy1((UnaryPredicateNode) n, hm);

									// System.out.printf("Node %s added\n", n);
									ex.add(n);
								} else if (n instanceof NegatedPredicateNode) {
									if (((NegatedPredicateNode) n).node instanceof BinaryPredicateNode) {
										n = replaceTextBy2((BinaryPredicateNode) ((NegatedPredicateNode) n).node, hm);
									} else if (((NegatedPredicateNode) n).node instanceof UnaryPredicateNode) {
										n = replaceTextBy1((UnaryPredicateNode) ((NegatedPredicateNode) n).node, hm);
									}

									n = new NegatedPredicateNode((PredicateNode) n);

									ex.add(n);
									// System.out.printf("Node %s added\n", n.describeNode());
								}
							} else {
								// System.out.printf("Node %s removed\n", N[l]);
							}
							// System.out.println("Expresssion: "+ex);
						}
					}
					// System.out.printf("Ex %s\n", ex);
					Set<BasicNode> ex1 = new HashSet<BasicNode>(ex.predicates);
					ex = new Expression(new ArrayList<BasicNode>(ex1));
					// System.out.printf("Ex %s\n", ex);
					return ex;
				}
			}
		}
		return null;
	}

	/**
	 * Method to get modified UnaryPredicateNode based on given substitution map hm
	 * @param node - UnaryPredicateNode on which substitution is to be applied
	 * @param hm - Hash Map of substitution
	 * @return new modified UnaryPredicateNode
	 */
	private static UnaryPredicateNode replaceTextBy1(UnaryPredicateNode node, ArrayList<Map<String, String>> hm) {
		String text1 = node.node1.text;
		String type1 = node.node1.type;

		if (hm.get(0).get(node.node1.text) != null) {
			text1 = hm.get(0).get(node.node1.text);
			type1 = hm.get(1).get(text1);
		}
		return new UnaryPredicateNode(node.text, text1, type1);
	}

	/**
	 * Method to get modified BinaryPredicateNode based on given substitution map hm
	 * @param node - BinaryPredicateNode on which substitution is to be applied
	 * @param hm - Hash Map of substitution
	 * @return new modified BinaryPredicateNode
	 */
	private static BinaryPredicateNode replaceTextBy2(BinaryPredicateNode node, ArrayList<Map<String, String>> hm) {
		String text1, text2, type1, type2;
		text1 = node.node1.text;
		type1 = node.node1.type;
		text2 = node.node2.text;
		type2 = node.node2.type;

		if (hm.get(0).get(node.node1.text) != null) {
			text1 = hm.get(0).get(node.node1.text);
			type1 = hm.get(1).get(text1);

		}
		if (hm.get(0).get(node.node2.text) != null) {
			text2 = hm.get(0).get(node.node2.text);
			type2 = hm.get(1).get(text2);
		}
		return new BinaryPredicateNode(node.text, text1, type1, text2, type2);
	}

	/**
	 * Method to check if 2 given BasicNodes are Unifiable
	 * and to get corresponding substitution if they are Unifiable
	 * @param p1 - First BasicNode
	 * @param p2 - Second BasicNode
	 * @return Hash map hm that is nothing but substitution map
	 */
	public static ArrayList<Map<String, String>> isUnifiable(BasicNode p1, BasicNode p2) {
		ArrayList<Map<String, String>> hm = new ArrayList<Map<String, String>>();
		hm.add(new HashMap<String, String>());
		hm.add(new HashMap<String, String>());

		if (p1.getClass().equals(p2.getClass())) {
			// System.out.printf("One of the predicates must be negative\nUNIFICATION
			// FAILED\n\n");
			return null;
		} else {
			// System.out.printf("One of the predicates is negative\n");
		}

		if (p1 instanceof NegatedPredicateNode) {
			p1 = ((NegatedPredicateNode) p1).node;
		}
		if (p2 instanceof NegatedPredicateNode) {
			p2 = ((NegatedPredicateNode) p2).node;
		}

		if (!p1.text.equals(p2.text)) {
			// System.out.printf("Predicate names %s and %s don't match\nUNIFICATION
			// FAILED\n\n",p1.text,p2.text);
			return null;
		} else {
			// System.out.printf("Predicate names %s and %s match\n",p1.text,p2.text);

			if (!p1.getClass().equals(p2.getClass())) {
				// System.out.printf("Predicate types %s and %s don't match\nUNIFICATION
				// FAILED\n\n",p1.getClass(),p2.getClass());
				return null;
			} else {
				// System.out.printf("Predicate types %s and %s
				// match\n",p1.getClass(),p2.getClass());

				if (p1 instanceof BinaryPredicateNode) {
					String arg1Text[] = { ((BinaryPredicateNode) p1).node1.text,
							((BinaryPredicateNode) p1).node2.text };
					String arg2Text[] = { ((BinaryPredicateNode) p2).node1.text,
							((BinaryPredicateNode) p2).node2.text };

					String arg1Type[] = { ((BinaryPredicateNode) p1).node1.type,
							((BinaryPredicateNode) p1).node2.type };
					String arg2Type[] = { ((BinaryPredicateNode) p2).node1.type,
							((BinaryPredicateNode) p2).node2.type };

					// System.out.printf("Binary Types %s and
					// %s\n",p1.describeNode(),p2.describeNode());

					for (int i = 0; i < 2; i++) {
						int i2 = i == 0 ? 1 : 0;
						if (arg1Type[i].equals("CONSTANT") && arg2Type[i].equals("CONSTANT")
								|| arg1Type[i].equals("VARIABLE") && arg2Type[i].equals("VARIABLE")) {
							String tmpStr = arg1Type[i].equals("CONSTANT") ? "CONSTANTS" : "VARIABLES";

							if (!arg1Text[i].equals(arg2Text[i])) {
								if (arg1Type[i].equals("CONSTANT") && arg2Type[i].equals("CONSTANT")) {
									// System.out.printf("%s %s and %s don't match\nUNIFICATION
									// FAILED\n\n",tmpStr,arg1Text[i],arg2Text[i]);
									return null;
								}
								else if (arg1Type[i2].equals("CONSTANT") && arg2Type[i2].equals("CONSTANT")) {
									if (arg1Text[i2].equals(arg2Text[i2])) {
										// System.out.printf("%s %s and %s don't match, but substituted as other
										// constants are matching\n",tmpStr, arg1Text[i],arg2Text[i]);
										hm.get(0).put(arg1Text[i], arg1Text[i]);
										hm.get(1).put(arg1Text[i], arg1Type[i]);
									} else {
										System.out.printf(
												"%s %s and %s don't match and other constants don't match\nUNIFICATION FAILED\n\n",
												tmpStr, arg1Text[i], arg2Text[i]);
										return null;
									}
								}
								else if (arg1Type[i2].equals("CONSTANT") && arg2Type[i2].equals("VARIABLE")) {
									// System.out.printf("%s %s and %s don't match and other constants don't match
									// but other first is constant and second is
									// variable\n",tmpStr,arg1Text[i],arg2Text[i]);
									hm.get(0).put(arg2Text[i2], arg1Text[i2]);
									hm.get(1).put(arg1Text[i2], arg1Type[i2]);
									if (arg2Text[i].equals(arg2Text[i2])) {
										// System.out.printf("Applying transitive substitution\n");
										hm.get(0).put(arg1Text[i], arg1Text[i2]);
										hm.get(1).put(arg1Text[i2], arg1Type[i2]);
										// System.out.println(hm);
									} else {
										// System.out.printf("No transitive substitution\n");
										hm.get(0).put(arg2Text[i], arg1Text[i]);
										hm.get(1).put(arg1Text[i], arg1Type[i]);
									}
								} 
								else if (arg1Type[i2].equals("VARIABLE") && arg2Type[i2].equals("CONSTANT")) {
									// System.out.printf("%s %s and %s don't match and other constants don't match
									// but other first is variable and second is
									// constant\n",tmpStr,arg1Text[i],arg2Text[i]);
									hm.get(0).put(arg1Text[i2], arg2Text[i2]);
									hm.get(1).put(arg2Text[i2], arg2Type[i2]);
									if (arg1Text[i].equals(arg1Text[i2])) {
										// System.out.printf("Applying transitive substitution\n");
										hm.get(0).put(arg2Text[i], arg2Text[i2]);
										hm.get(1).put(arg2Text[i2], arg2Type[i2]);
										// System.out.println(hm);
									} else {
										// System.out.printf("No transitive substitution\n");
										hm.get(0).put(arg1Text[i], arg2Text[i]);
										hm.get(1).put(arg2Text[i], arg2Type[i]);
									}
								} 
								else if (arg1Type[i2].equals("VARIABLE") && arg2Type[i2].equals("VARIABLE")) {
									if (i == 0) {
										System.out.printf(
												"%s %s and %s don't match and constants/variables don't match but other first is variable and second is variable\n",
												tmpStr, arg1Text[i], arg2Text[i]);
										hm.get(0).put(arg2Text[i2], arg1Text[i2]);
										hm.get(1).put(arg1Text[i2], arg1Type[i2]);
										System.out.println(hm);
										if (arg2Text[i].equals(arg2Text[i2])) {
											System.out.printf("Applying transitive substitution\n");
											hm.get(0).put(arg1Text[i], arg1Text[i2]);
											hm.get(1).put(arg1Text[i2], arg1Type[i2]);
											System.out.println(hm);
										} else {
											System.out.printf("No transitive substitution\n");
											hm.get(0).put(arg2Text[i], arg1Text[i]);
											hm.get(1).put(arg1Text[i], arg1Type[i]);
										}
									}
								} 
								else {
									// System.out.println("UNIFICATION FAILED because of unimplemented reasons");
									return null;
								}
							} 
							else {
								//System.out.printf("%s %s and %s match\n",tmpStr, arg1Text[i],arg2Text[i]);
								hm.get(0).put(arg1Text[i], arg1Text[i]);
								hm.get(1).put(arg1Text[i], arg1Type[i]);
							}
						} else if (arg1Type[i].equals("VARIABLE") && arg2Type[i].equals("CONSTANT")) {
							
							if(arg1Type[i].equals("VARIABLE") && arg1Type[i2].equals("VARIABLE") && arg1Text[i].equals(arg1Text[i2]) && !arg2Text[i].equals(arg2Text[i2])) {
								System.out.printf("VARIABLES %s and %s match but constants %s and %s don't match\n",arg1Text[i],arg1Text[i2],arg2Text[i], arg2Text[i2]) ;
								return null;
							}
							hm.get(0).put(arg1Text[i], arg2Text[i]);
							hm.get(1).put(arg2Text[i], arg2Type[i]);
						} else if (arg1Type[i].equals("CONSTANT") && arg2Type[i].equals("VARIABLE")) {
							
							if(arg2Type[i].equals("VARIABLE") && arg2Type[i2].equals("VARIABLE") && arg2Text[i].equals(arg2Text[i2]) && !arg1Text[i].equals(arg1Text[i2])) {
								System.out.printf("VARIABLES %s and %s match but constants %s and %s don't match\n",arg2Text[i], arg2Text[i2], arg1Text[i],arg1Text[i2]);
								return null;
							}
							hm.get(0).put(arg2Text[i], arg1Text[i]);
							hm.get(1).put(arg1Text[i], arg1Type[i]);
						}
					}
				}

				else if (p1 instanceof UnaryPredicateNode) {
					String arg11Text = ((UnaryPredicateNode) p1).node1.text;
					String arg21Text = ((UnaryPredicateNode) p2).node1.text;

					String arg11Type = ((UnaryPredicateNode) p1).node1.type;
					String arg21Type = ((UnaryPredicateNode) p2).node1.type;

					// System.out.printf("Unary Types %s and
					// %s\n",p1.describeNode(),p2.describeNode());
					if (arg11Type.equals("CONSTANT") && arg21Type.equals("CONSTANT")
							|| arg11Type.equals("VARIABLE") && arg21Type.equals("VARIABLE")) {
						//String tmpStr = arg11Type.equals("CONSTANT") ? "CONSTANTS" : "VARIABLES";
						if (!arg11Text.equals(arg21Text)) {
							if (arg11Type.equals("CONSTANT") && arg21Type.equals("CONSTANT")) {
								// System.out.printf("%s %s and %s don't match\nUNIFICATION
								// FAILED\n\n",tmpStr,arg1Text[i],arg2Text[i]);
								return null;
							} else if (arg11Type.equals("VARIABLE") && arg21Type.equals("VARIABLE")) {
								hm.get(0).put(arg21Text, arg11Text);
								hm.get(1).put(arg11Text, arg11Type);
							} else {
								// System.out.println("UNIFICATION FAILED because of unimplemented reasons");
								return null;
							}
						} else {
							// System.out.printf("%s %s and %s match\n",tmpStr, arg11Text,arg21Text);
							hm.get(0).put(arg11Text, arg11Text);
							hm.get(1).put(arg11Text, arg11Type);
						}
					} else if (arg11Type.equals("VARIABLE") && arg21Type.equals("CONSTANT")) {
						hm.get(0).put(arg11Text, arg21Text);
						hm.get(1).put(arg21Text, arg21Type);
					} else if (arg11Type.equals("CONSTANT") && arg21Type.equals("VARIABLE")) {
						hm.get(0).put(arg21Text, arg11Text);
						hm.get(1).put(arg11Text, arg11Type);
					}
				}
			}
		}

		return hm;
	}
}
