package heurika;

import java.awt.Point;
import java.util.ArrayList;

public class Intersection {

	private Point position;
	private ArrayList<Intersection> neighbours = new ArrayList<>();

	public Intersection() {

	}

	public Intersection(Point position) {
		this.position = position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void addNeighbour(Intersection neighbour) {
		if (!neighbours.contains(neighbour)) {
			neighbours.add(neighbour);
		}
	}

	public Point getPosition() {
		return position;
	}

	public ArrayList<Intersection> getNeighbours() {
		return neighbours;
	}

}
