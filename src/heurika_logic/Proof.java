package heurika_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Proof {

	static String andOperator = "&";
	static String orOperator = "|";
	static String implicationOperator = "->";
	static String negOperator = "neg-";
	static String splitter = ",";

	public static void main(String[] args) {

		String alpha = "Breakfast";

		String KB = "Breakfast -> Food & Drink & Smell, Breakfast -> Food & Drink, Food -> Egg & Bacon, Food -> Egg & Bread, Drink -> Coffee & Juice, Drink -> The";

		String facts = "Coffee, Juice, Egg, Bread, Bacon";

		alpha = alpha.replaceAll("\\s", "");
		KB = KB.replaceAll("\\s", "");
		facts = facts.replaceAll("\\s", "");

		Proof proof = new Proof();

		proof.solve(alpha, KB, facts);
	}

	static String[] alpha_Array;
	static String[] KB_Array;
	static String[] facts_Array;

	private HashMap<Long, State> closedSet = new HashMap<>();
	private PriorityQueue<State> frontier = new PriorityQueue<State>(100, new Comparator<State>() {
		@Override
		public int compare(State a, State b) {
			return a.getPriority() - b.getPriority();
		}
	});

	public void solve(String alpha, String KB, String facts) {
		alpha_Array = alpha.split(splitter);
		KB_Array = KB.split(splitter);
		facts_Array = facts.split(splitter);

		addToFrontier(new State(alpha_Array));

		while (!frontier.isEmpty()) {
			State currentState = frontier.poll();

			if (currentState.alpha.length == 0) {
				goalFound(currentState);
				return;
			}

			for (State newState : newStates(currentState)) {
				addToFrontier(newState);
			}
		}
		System.out.println("Ikke muligt");
	}

	public void goalFound(State goal) {
		if (goal.parent != null) {
			goalFound(goal.parent);
		}
		String result = "";
		for (String a : goal.alpha) {
			result += a + ", ";
		}
		System.out.println(goal.alpha.length + ": " + result);
	}

	public void addToFrontier(State currentState) {
		if (!closedSet.containsKey(currentState.getKey())) {
			closedSet.put(currentState.getKey(), currentState);
			frontier.add(currentState);
		}
	}

	public ArrayList<State> newStates(State currentState) {
		ArrayList<State> newStates = new ArrayList<>();

		for (String alpha_element : currentState.alpha) {

			for (String fact : facts_Array) {
				if (fact.contains(alpha_element)) {
					newStates.add(new State(currentState, removeOneElement(alpha_element, currentState.alpha)));
				}
			}

			for (String kb : KB_Array) {
				if (kb.contains(alpha_element + implicationOperator)) {
					newStates.add(new State(currentState, replace(kb, alpha_element, currentState.alpha)));
				}
			}

		}

		return newStates;
	}

	public String[] replace(String newElement, String oldElement, String[] ori) {
		String[] newComers = newElement.split(implicationOperator)[1].split(andOperator);

		String[] cleaned = removeOneElement(oldElement, ori);

		String[] newArray = new String[ori.length - 1 + newComers.length];
		int i = 0;
		for (String oriElement : cleaned) {
			newArray[i++] = oriElement;
			if (oriElement == null) {
				System.out.println("HMMMM");
			}
		}
		for (String newComer : newComers) {
			newArray[i++] = newComer;
		}
		return newArray;
	}

	public String[] removeOneElement(String element, String[] ori) {
		String[] newArray = new String[ori.length - 1];
		int i = 0;
		for (String oriElement : ori) {
			System.out.println(oriElement + " " + ori.length);
			if (!oriElement.equals(element)) {
				newArray[i] = oriElement;
				i++;
			}
		}
		return newArray;
	}

	class State {

		State parent;
		int heuristic;
		int g = 0;
		String[] alpha;

		public State(String[] alpha) {
			this.alpha = alpha.clone();
			this.heuristic = alpha.length;
		}

		public State(State parent, String[] alpha) {
			this.parent = parent;
			this.alpha = alpha.clone();
			this.g = parent.g + 1;
			this.heuristic = alpha.length;
		}

		public int getPriority() {
			return this.g + this.heuristic;
		}

		public long getKey() {
			return Arrays.deepHashCode(alpha);
		}

	}

}
