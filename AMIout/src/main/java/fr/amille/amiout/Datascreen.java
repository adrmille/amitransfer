package fr.amille.amiout;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import fr.amille.amiout.constant.AMIConstants;
import fr.amille.amiout.states.ReadBlocks;
import fr.amille.amiout.states.ReadControl;
import fr.amille.amiout.states.SearchingFirstPiexel;
import fr.amille.amiout.view.MyAbstractSwingWorker;

/**
 * A screen of data-pixels
 */
public class Datascreen {
	
	public void createDatascreen(Color color) throws InterruptedException {

		firstPixel = null;

		final BufferedImage currentImage = getScreen();

		final int w = currentImage.getWidth();
		final int h = currentImage.getHeight();

		for (int y = 0; y < h; y++) {

			for (int x = 0; x < w; x++) {

				final Color color = calculateColor(currentImage, x, y);

				if (color.equals(expectedFirstPixelColor)) {

					// get last pixel color
					final int lastPixelx = x + areaW - 1;
					final int lastPixely = y + areaH - 1;
					final Color lastPixelColor = calculateColor(currentImage, lastPixelx, lastPixely);
					if (!AMIConstants.LAST_PIXEL.equals(lastPixelColor)) {
						System.out.println("Last pixel not found");
						new MyAbstractSwingWorker() {
							@Override
							public void whatToExecute() {
								setState(SearchingFirstPiexel.INSTANCE);
							}
						}.execute();
						return;
					}

					firstPixel = new FirstPixel(x, y, color);
					setState(ReadBlocks.INSTANCE);
					return;
				} else if (color.equals(AMIConstants.CONTROL_PIXEL)) {
					firstPixel = new FirstPixel(x, y, color);
					setState(ReadControl.INSTANCE);
					return;
				}

			}

		}

		if (firstPixel == null) {
			System.out.println("First pixel not found");
			new MyAbstractSwingWorker() {
				@Override
				public void whatToExecute() {
					setState(SearchingFirstPiexel.INSTANCE);
				}
			}.execute();
		}

	}
	
	private BufferedImage getScreen() {
		BufferedImage screen = null;
		try {

			final Robot robot = new Robot();

			// Capture the whole screen
			final Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			return robot.createScreenCapture(area);

		} catch (AWTException e) {
			System.err.println(e.getMessage());
			setState(null);
		}
		return screen;
	}
	
}
