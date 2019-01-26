package fr.amille.amiout;

import java.awt.Color;

public class Pixel {

	private int x;
	private int y;
	private Color color;

	public Pixel(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.setColor(color);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
