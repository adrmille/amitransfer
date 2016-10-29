/**
 *
 */
package fr.amille.amiout.states;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

import fr.amille.amiout.constant.AMIConstants;
import fr.amille.amiout.entity.Pixel;

/**
 * @author AMILLE
 *
 */
public class SearchingControlPixel extends SearchingPixel {

	public final static SearchingControlPixel INSTANCE = new SearchingControlPixel();

	private SearchingControlPixel() {
	}

	@Override
	protected String getLabelMessage(Context context) {
		final int percent = (context.getTotalWroteBytes() * 100) / context.getFileSize();
		final String message = "Looking for control pixel (" + percent + "% already done)";
		return message;
	}

	@Override
	protected Pixel findPixel(Context context, final BufferedImage currentImage) {

		final Pixel controlPixel = context.getScreenService().getPixelInBufferedImage(currentImage, new Point(0, 0),
				new Dimension(currentImage.getWidth(), currentImage.getHeight()), AMIConstants.CONTROL_PIXEL_COLOR);

		return controlPixel;
	}

	@Override
	protected void doWhenPixelNotFound(Context context) {
		// infinite loop
		System.out.println("Control pixel not found");
		context.getThreadingService().sleep(ONE_SECONDE);
		context.setState(SearchingControlPixel.INSTANCE);
	}

	@Override
	protected void doWhenPixelFound(Context context) {
		context.setState(ReadControl.INSTANCE);
	}

}
