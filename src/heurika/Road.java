package heurika;

import java.awt.Point;

public class Road {

	private String name;
	private Point start;
	private Point end;

	public Road() {

	}

	public Road(String name, Point start, Point end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public String getKey() {
		return "" + start.x + "," + start.y + ":" + end.x + "," + end.y;
	}

}
