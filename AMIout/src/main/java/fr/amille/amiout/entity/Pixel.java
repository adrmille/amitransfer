package fr.amille.amiout.entity;

import java.awt.Color;
import java.awt.Point;

public class Pixel {

	private Color color;
	private Point point;

	public Pixel(Point point, Color color) {
		super();
		this.point = point;
		this.setColor(color);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

}
