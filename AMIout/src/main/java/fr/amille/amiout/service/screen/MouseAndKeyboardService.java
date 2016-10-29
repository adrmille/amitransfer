package fr.amille.amiout.service.screen;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MouseAndKeyboardService {

	private Robot robot;

	public MouseAndKeyboardService() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public void moveMouseTo(final Point point) {
		robot.mouseMove(point.x, point.y);
	}

	public void leftClick() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public void pressKeyN() {
		robot.keyPress(KeyEvent.VK_N);
	}

}
