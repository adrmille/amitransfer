/**
 * 
 */
package fr.amille.amiout.states;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

import fr.amille.amiout.view.MyAbstractSwingWorker;

/**
 * @author AMILLE
 * 
 */
public class Resume implements State {

	public final static Resume INSTANCE = new Resume();

	private Resume() {
	}

	@Override
	public void goNext(final Context context) {

		try {

			final Robot robot = new Robot();
			context.whereTheMouseShouldBe = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(context.firstPixel.getX(), context.firstPixel.getY());
			Thread.sleep(100);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			Thread.sleep(100);
			robot.mouseMove((int) context.whereTheMouseShouldBe.getX(), (int) context.whereTheMouseShouldBe.getY());
			Thread.sleep(100);
			new MyAbstractSwingWorker() {
				@Override
				public void whatToExecute() {
					context.setState(SearchingFirstPiexel.INSTANCE);
				}
			}.execute();

		} catch (AWTException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		}

	}

}
