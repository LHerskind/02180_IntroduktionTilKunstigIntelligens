package heurika_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Proof {

	// TODO: Mangler stadig at den kigger tilbage i sine parents og ser om den
	// har modsætningen til hvad der har været tidligere, hvis den har det kan
	// den også godt stoppe det.

	static String andOperator = "&";
	static String orOperator = "|";
	static String implicationOperator = "->";
	static String negOperator = "neg-";
	static String splitter = ",";

	public static void main(String[] args) {
		onlyOneLiteral();
		transitivity();
		contrapositioning();
		monotonicity();
		breakfast();
		example();
		ancestor();
	}
	
	public static void onlyOneLiteral(){
		System.out.println("---------------------------------");
		System.out.println("Test for only one literal example:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();
		ArrayList<String> b = new ArrayList<>();
		b.add("p");
		b.add("q");
		alpha.add(b);

		String[][] KB = new String[3][2];
		KB[0] = new String[] { "neg-p", "neg-q" };
		KB[1] = new String[] { "neg-p", "q" };
		KB[2] = new String[] { "p", "neg-q" };

		String[] facts = new String[0];

		new Proof().solve(alpha, KB, facts);
		
	}

	public static void ancestor() {
		System.out.println("---------------------------------");
		System.out.println("Ancestor resolution example:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();
		ArrayList<String> b = new ArrayList<>();
		b.add("neg-q");
		alpha.add(b);

		String[][] KB = new String[2][2];
		KB[0] = new String[] { "p", "q" };
		KB[1] = new String[] { "q", "neg-p" };

		String[] facts = new String[0];

		new Proof().solve(alpha, KB, facts);
	}

	public static void breakfast() {
		System.out.println("---------------------------------");
		System.out.println("Breakfast example:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();
		ArrayList<String> b = new ArrayList<>();
		b.add("neg-Breakfast");
		alpha.add(b);

		String[][] KB = new String[3][3];
		KB[0] = new String[] { "neg-Hotdrink", "neg-Food", "Breakfast" };
		KB[1] = new String[] { "Food", "neg-Butter", "neg-Toast" };
		KB[2] = new String[] { "neg-Tea", "Hotdrink" };

		String[] facts = new String[] { "Coffee", "Tea", "Toast", "Butter" };
		new Proof().solve(alpha, KB, facts);
	}

	public static void example() {
		// Bottom at http://logic.stanford.edu/intrologic/notes/chapter_05.html
		System.out.println("---------------------------------");
		System.out.println("Example from stanford:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();

		ArrayList<String> p = new ArrayList<>();
		p.add("p");
		p.add("r");
		alpha.add(p);

		ArrayList<String> q = new ArrayList<>();
		q.add("neg-q");
		alpha.add(q);

		ArrayList<String> s = new ArrayList<>();
		s.add("neg-s");
		alpha.add(s);

		String[][] KB = new String[5][2];
		KB[0] = new String[] { "neg-p", "q" };
		KB[1] = new String[] { "neg-r", "s" };
		KB[2] = new String[] { "p", "r" };
		KB[3] = new String[] { "neg-q" };
		KB[4] = new String[] { "neg-s" };

		String[] facts = new String[0];

		new Proof().solve(alpha, KB, facts);

	}

	public static void transitivity() {
		System.out.println("---------------------------------");
		System.out.println("Resolution prove on transitivity:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();

		ArrayList<String> p = new ArrayList<>();
		p.add("p");
		alpha.add(p);

		ArrayList<String> q = new ArrayList<>();
		q.add("neg-r");
		alpha.add(q);

		String[][] KB = new String[4][2];
		KB[0] = new String[] { "neg-p", "q" };
		KB[1] = new String[] { "neg-q", "r" };
		KB[2] = new String[] { "p" };
		KB[3] = new String[] { "neg-r" };

		String[] facts = new String[0];

		new Proof().solve(alpha, KB, facts);
	}

	public static void contrapositioning() {
		System.out.println("---------------------------------");
		System.out.println("Resolution prove on contrapositioning:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();

		ArrayList<String> p = new ArrayList<>();
		p.add("p");
		alpha.add(p);

		ArrayList<String> q = new ArrayList<>();
		q.add("neg-q");
		alpha.add(q);

		ArrayList<String> r = new ArrayList<>();
		r.add("neg-q");
		r.add("p");
		alpha.add(r);

		String[][] KB = new String[3][2];
		KB[0] = new String[] { "neg-p", "q" };
		KB[1] = new String[] { "p" };
		KB[2] = new String[] { "neg-r" };

		String[] facts = new String[0];

		new Proof().solve(alpha, KB, facts);
	}

	public static void monotonicity() {
		System.out.println("---------------------------------");
		System.out.println("Resolution prove on monotonicity:");
		ArrayList<ArrayList<String>> alpha = new ArrayList<>();

		ArrayList<String> p = new ArrayList<>();
		p.add("p");
		alpha.add(p);

		ArrayList<String> q = new ArrayList<>();
		q.add("neg-q");
		alpha.add(q);

		ArrayList<String> r = new ArrayList<>();
		r.add("r");
		alpha.add(r);

		String[][] KB = new String[4][2];
		KB[0] = new String[] { "neg-p", "q" };
		KB[1] = new String[] { "p" };
		KB[2] = new String[] { "neg-q" };
		KB[3] = new String[] { "r" };

		String[] facts = new String[0];

		new Proof().solve(alpha, KB, facts);
	}

	public static String implication(String input) {
		String[] littleList = input.split(implicationOperator);
		return negOperator + "(" + littleList[0] + ")" + orOperator + littleList[1];
	}

	static String[][] KB;
	static String[] facts;

	private HashMap<Long, State> closedSet = new HashMap<>();
	private PriorityQueue<State> frontier = new PriorityQueue<State>(100, new Comparator<State>() {
		@Override
		public int compare(State a, State b) {
			return a.getPriority() - b.getPriority();
		}
	});

	public void solve(ArrayList<ArrayList<String>> alpha, String[][] KB, String[] facts) {
		this.KB = KB;
		this.facts = facts;

		for (ArrayList<String> initter : alpha) {
			addToFrontier(new State(initter));
		}

		while (!frontier.isEmpty()) {
			State currentState = frontier.poll();

			if (currentState.alpha.size() == 0) {
				goalFound(currentState);
				return;
			}

			for (State newState : newStates(currentState)) {
				addToFrontier(newState);
			}
		}
		System.out.println("Not possible");
	}

	/**
	 * Printing the "route" to the goal
	 * 
	 * @param goal
	 *            / the state reaching the goal
	 */
	public void goalFound(State goal) {
		if (goal.parent != null) {
			goalFound(goal.parent);
		}
		String result = "";
		for (String a : goal.alpha) {
			result += a + ", ";
		}
		System.out.println(result + "\t : \t" + goal.action);
	}

	/**
	 * Adding a state to the frontier and the closed set, only if it has not
	 * already been visited
	 * 
	 * @param currentState
	 */
	public void addToFrontier(State currentState) {
		if (!closedSet.containsKey(currentState.getKey())) {
			closedSet.put(currentState.getKey(), currentState);
			frontier.add(currentState);
		}
	}

	/**
	 * Returning the a list of the possible next states from the current state.
	 * 
	 * @param currentState
	 * @return ArrayList<State>
	 */
	public ArrayList<State> newStates(State currentState) {
		ArrayList<State> newStates = new ArrayList<>();

		for (String alpha_element : currentState.alpha) {

			for (String fact : facts) {
				if (alpha_element.contains("neg-")) {
					String tempSearch = alpha_element.replace("neg-", "");
					if (fact.contains(tempSearch)) {
						State s = new State(currentState, removeOneElement(alpha_element, currentState.alpha));
						s.setAction(fact);
						newStates.add(s);
					}
				} else if (fact.contains("neg-" + alpha_element)) {
					State s = new State(currentState, removeOneElement(alpha_element, currentState.alpha));
					s.setAction(fact);
					newStates.add(s);
				}
			}

			for (String[] kb : KB) {
				for (String kbb : kb) {
					if (alpha_element.contains("neg-")) {
						String tempSearch = alpha_element.replace("neg-", "");
						if (kbb.contains(tempSearch)) {
							State s = new State(currentState, addElement(kb, currentState.alpha));
							s.setAction(getAction(kb));
							newStates.add(s);
						}
					} else if (kbb.contains("neg-" + alpha_element)) {
						State s = new State(currentState, addElement(kb, currentState.alpha));
						s.setAction(getAction(kb));
						newStates.add(s);
					}
				}
			}

			// Ancestor Resolution
			for (ArrayList<String> AR : getAncestorResolution(currentState)) {
				for (String ar : AR) {
					if (alpha_element.contains("neg-")) {
						String tempSearch = alpha_element.replace("neg-", "");
						if (ar.contains(tempSearch)) {
							State s = new State(currentState,
									addElement(AR.toArray(new String[AR.size()]), currentState.alpha));
							s.setAction(getAction(AR.toArray(new String[AR.size()])));
							newStates.add(s);
						}
					} else if (ar.contains("neg-" + alpha_element)) {
						State s = new State(currentState,
								addElement(AR.toArray(new String[AR.size()]), currentState.alpha));
						s.setAction(getAction(AR.toArray(new String[AR.size()])));
						newStates.add(s);
					}
				}
			}

		}
		return newStates;
	}

	public ArrayList<ArrayList<String>> getAncestorResolution(State current) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();

		while (current.parent != null) {
			current = current.parent;
			result.add(current.alpha);
		}

		return result;
	}

	public String getAction(String[] array) {
		String result = "";
		for (String s : array) {
			result += s + orOperator;
		}
		return result;
	}

	/**
	 * Doing the join of two "rules" here removing if there is both a neg and a
	 * normal.
	 * 
	 * @param newElements
	 *            (String[]) an array of "new" elements
	 * @param ori
	 *            (ArrayList<String>) of "alpha" before the move
	 * @return ArrayList<String> of the new "alpha"
	 */
	public ArrayList<String> addElement(String[] newElements, ArrayList<String> ori) {
		ArrayList<String> result = new ArrayList<>();
		result.addAll(ori);

		for (String element : newElements) {
			if (!result.contains(element)) {
				if (!element.contains("neg-")) {
					if (result.contains("neg-" + element)) {
						result.remove("neg-" + element);
					} else {
						result.add(element);
					}
				} else {
					String tempSearch = element.replace("neg-", "");
					if (result.contains(tempSearch)) {
						result.remove(tempSearch);
					} else {
						result.add(element);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returning a new list where a single element has been removed.
	 * 
	 * @param element
	 *            (String)
	 * @param ori
	 *            (ArrayList<String) alpha from the state before a move
	 * @return ArrayList<String> the new "alpha" where an element has been
	 *         removed
	 */
	public ArrayList<String> removeOneElement(String element, ArrayList<String> ori) {
		ArrayList<String> newList = new ArrayList<>();
		newList.addAll(ori);
		newList.remove(element);
		return newList;
	}

	class State {

		State parent;
		int heuristic;
		int g = 0;
		ArrayList<String> alpha = new ArrayList<>();
		String action;

		public State(ArrayList<String> alpha) {
			this.alpha.addAll(alpha);
			this.heuristic = alpha.size();
		}

		public State(State parent, ArrayList<String> alpha) {
			this.parent = parent;
			this.alpha.addAll(alpha);
			this.g = parent.g + 1;
			this.heuristic = alpha.size();
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getAction() {
			return action;
		}

		public int getPriority() {
			return this.g + this.heuristic;
		}

		public long getKey() {
			return alpha.hashCode();
		}

	}

}
