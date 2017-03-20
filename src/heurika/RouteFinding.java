package heurika;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class RouteFinding {

	private HashMap<String, Road> roads = new HashMap<>();
	private HashMap<Point, Intersection> intersections = new HashMap<>();
	private Point goal;

	private HashMap<Integer, State> closed = new HashMap<>();

	private PriorityQueue<State> frontier = new PriorityQueue<State>(100, new Comparator<State>() {
		@Override
		public int compare(State a, State b) {
			return a.priority() - b.priority();
		}
	});

	public static void main(String[] args) {
		new RouteFinding();
	}

	public RouteFinding() {
		initializeMap();

		Point start = new Point(35, 80); //the corner of SktPedersStraede & Larsbjoernsstraede
		Point end = new Point(45, 70); //the corner of Studiestraede & Larsbjoernsstraede.
		
		// Husk at google får en anden rute da teglgårdsstrædet ikke længere er ensrettet 

		printRoute(start, end);
	}

	public void printRoute(Point start, Point goal) {
		State finalState = findRoute(start, goal);
		
		if(finalState == null){
			System.out.println("No possible route");
			return;
		}
		
		ArrayList<Point> route = new ArrayList<>();

		while (finalState.parent != null) {
			route.add(finalState.intersection.getPosition());
			finalState = finalState.parent;
		}
		route.add(finalState.intersection.getPosition());

		for (int i = route.size() - 2; i >= 0; i--) {
			Point end1 = route.get(i);
			Point start1 = route.get(i + 1);
			String roadKey = "" + start1.x + "," + start1.y + ":" + end1.x + "," + end1.y;
			Road road = roads.get(roadKey);
			System.out.println(road.getName() + " " +road.getEnd().getX() +","+road.getEnd().getY());
		}

	}

	public void initializeMap() {
		addRoad("Vestervoldgade", new Point(10, 70), new Point(20, 50));
		addRoad("Vestervoldgade", new Point(20, 50), new Point(10, 70));
		addRoad("Vestervoldgade", new Point(20, 50), new Point(35, 35));
		addRoad("Vestervoldgade", new Point(35, 35), new Point(20, 50));
					
		addRoad("SktPedersStraede", new Point(10, 70), new Point(35, 80));
		addRoad("SktPedersStraede", new Point(35, 80), new Point(50, 90));
		addRoad("SktPedersStraede", new Point(65, 100), new Point(50, 90));
		
		addRoad("Studiestraede", new Point(20, 50), new Point(45, 70));
		addRoad("Studiestraede", new Point(45, 70), new Point(70, 85));
		
		addRoad("Noerregade", new Point(60, 150), new Point(65, 110));
		addRoad("Noerregade", new Point(65, 110), new Point(65, 100));
		addRoad("Noerregade", new Point(65, 100), new Point(70, 85));
		addRoad("Noerregade", new Point(70, 85), new Point(80, 70));

		addRoad("Larsbjoernsstraede", new Point(45, 70), new Point(55, 55));
		addRoad("Larsbjoernsstraede", new Point(45, 70), new Point(35, 80));
		
		addRoad("TeglgaardsStraede", new Point(25, 100), new Point(35, 80));

		addRoad("LarslejStraede", new Point(50, 90), new Point(35, 120));
		
		addRoad("Noerrevoldgade", new Point(10, 70), new Point(25, 100));
		addRoad("Noerrevoldgade", new Point(25, 100), new Point(10, 70));
		addRoad("Noerrevoldgade", new Point(25, 100), new Point(35, 120));
		addRoad("Noerrevoldgade", new Point(35, 120), new Point(25, 100));
		addRoad("Noerrevoldgade", new Point(35, 120), new Point(60, 150));
		addRoad("Noerrevoldgade", new Point(60, 150), new Point(35, 120));
	}

	public void printInterSections() {
		for (Intersection intersection : intersections.values()) {
			System.out.print(intersection.getPosition().getX() + "," + intersection.getPosition().getY() + ": ");
			for (Intersection neighbour : intersection.getNeighbours()) {
				System.out.print(neighbour.getPosition().getX() + "," + neighbour.getPosition().getY() + " ; ");
			}
			System.out.println();
		}
	}

	public State findRoute(Point from, Point goal) {
		this.goal = goal;

		Intersection initial = intersections.get(from);
		State initialState = new State(null, initial);

		frontier.add(initialState);

		while (!frontier.isEmpty()) {

			State currentState = frontier.poll();

			if (currentState.isGoal()) {
				return currentState;
			}

			for (Intersection intersection : currentState.intersection.getNeighbours()) {
				State nextState = new State(currentState, intersection);
				if (!frontier.contains(nextState)) {
					frontier.add(nextState);
				}
			}

		}

		return null;
	}

	private void addRoad(String name, Point start, Point end) {
		Road road = new Road(name, start, end);
		if (!roads.containsValue(road)) {
			roads.put(road.getKey(), road);
		}
		addIntersection(start, end);
	}

	private void addIntersection(Point start, Point end) {
		if (!intersections.containsKey(start)) {
			intersections.put(start, new Intersection(start));
		}
		if (!intersections.containsKey(end)) {
			intersections.put(end, new Intersection(end));
		}

		Intersection intersectionStart = intersections.get(start);
		Intersection intersectionEnd = intersections.get(end);

		if (!intersectionStart.getNeighbours().contains(intersectionEnd)) {
			intersectionStart.addNeighbour(intersectionEnd);
		}

	}

	public class State {

		private Intersection intersection;
		private State parent;

		private int heuristic;
		private int g;
		private int gFactor = 0;

		public State(State parent, Intersection intersection) {
			this.parent = parent;
			this.intersection = intersection;

			heuristic = (int) intersection.getPosition().distance(goal);
			if (parent != null) {
				g = parent.g + gFactor;
			} else {
				g = 0;
			}
		}

		public int priority() {
			return heuristic + g;
		}

		public boolean isGoal() {
			return intersection.getPosition().distance(goal) == 0;
		}

	}

}
