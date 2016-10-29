/**
 * 
 */
package fr.amille.amiout.states;

import java.awt.image.BufferedImage;

import fr.amille.amiout.AMIout;
import fr.amille.amiout.entity.Pixel;
import fr.amille.amiout.service.screen.ScreenService;
import fr.amille.amiout.view.MainPanel;

/**
 * @author AMILLE
 * 
 */
public abstract class SearchingPixel implements State {

	protected static final int ONE_SECONDE = 1000;

	@Override
	public void goNext(final Context context) {

		AMIout.mainFrame.renewMainPanel();

		final MainPanel mainPanel = AMIout.mainFrame.getMainPanel();
		mainPanel.createMainLabel(getLabelMessage(context));

		final BufferedImage currentImage = getScreenShot(context);
		final Pixel pixel = findPixel(context, currentImage);

		if (pixel == null) {
			doWhenPixelNotFound(context);
		} else {
			context.setFirstPixel(pixel);
			doWhenPixelFound(context);
		}
	}

	protected abstract String getLabelMessage(final Context context);

	protected abstract Pixel findPixel(final Context context, final BufferedImage currentImage);

	protected abstract void doWhenPixelNotFound(final Context context);

	protected abstract void doWhenPixelFound(final Context context);

	protected BufferedImage getScreenShot(final Context context) {
		final ScreenService screenService = context.getScreenService();

		final BufferedImage currentImage = screenService.getScreen(context);
		context.setCurrentImage(currentImage);

		if (currentImage == null) {
			throw new IllegalArgumentException("Fail to take a screenshot");
		}

		return currentImage;
	}

}
