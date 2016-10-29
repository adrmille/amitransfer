/**
 *
 */
package fr.amille.amiout.states;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import fr.amille.amiout.constant.AMIConstants;
import fr.amille.amiout.entity.Pixel;

/**
 * @author AMILLE
 *
 */
public class SearchingDataPixel extends SearchingPixel {

	public final static SearchingDataPixel INSTANCE = new SearchingDataPixel();

	private SearchingDataPixel() {
	}

	@Override
	protected String getLabelMessage(Context context) {
		final int percent = (context.getTotalWroteBytes() * 100) / context.getFileSize();
		final String message = "Looking for first data pixel (" + percent + "% already done)";
		return message;
	}

	@Override
	protected Pixel findPixel(final Context context, final BufferedImage currentImage) {

		final Pixel controlPixel = context.getScreenService().getPixelInBufferedImage(currentImage,
				context.getFirstPixel().getPoint(),
				new Dimension(context.getFileInformation().getAreaW(), context.getFileInformation().getAreaH()),
				AMIConstants.FIRST_PRINT_PIXEL_COLOR);

		return controlPixel;
	}

	@Override
	protected void doWhenPixelNotFound(Context context) {
		// infinite loop
		System.out.println("First data pixel not found at: " + context.getFirstPixel().getPoint() + " and "
				+ new Dimension(context.getFileInformation().getAreaW(), context.getFileInformation().getAreaH()));
		context.getThreadingService().sleep(ONE_SECONDE);
		context.setState(SearchingDataPixel.INSTANCE);
	}

	@Override
	protected void doWhenPixelFound(Context context) {
		context.setState(ReadData.INSTANCE);
	}

}
