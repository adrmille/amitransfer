package fr.amille.amiout.service.screen;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import fr.amille.amiout.entity.Pixel;
import fr.amille.amiout.states.Context;

/**
 * Service for all screen's accesses and calculations.
 *
 * @author amille
 *
 */
public class ScreenService {

	public Color calculateColor(BufferedImage bufferedImage, int x, int y) {
		final int rgb = bufferedImage.getRGB(x, y);
		final int r = (rgb >> 16) & 0xFF;
		final int g = (rgb >> 8) & 0xFF;
		final int b = (rgb & 0xFF);
		return new Color(r, g, b);
	}

	public BufferedImage getScreen(final Context context) {
		try {

			final Robot robot = new Robot();

			// Capture the whole screen
			final Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			return robot.createScreenCapture(area);

		} catch (final AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public Pixel getPixelInBufferedImage(final BufferedImage bufferedImage, Point startingPoint, Dimension dimension,
			Color pixelColorSearched) {

		Pixel pixel = null;

		for (int y = startingPoint.y; (y < dimension.height) && (pixel == null); y++) {

			for (int x = startingPoint.x; (x < dimension.width) && (pixel == null); x++) {

				final Color color = this.calculateColor(bufferedImage, x, y);

				if (color.equals(pixelColorSearched)) {
					pixel = new Pixel(new Point(x, y), color);
				}
			}
		}

		return pixel;
	}

	public int[] readPixelToBytes(Pixel firstPixel, BufferedImage bufferedImage, int buffSize) {

		final int[] bytes = new int[buffSize];

		int x = firstPixel.getPoint().x + 1;
		int y = firstPixel.getPoint().y;
		for (int i = 0; i < buffSize; i = i + 3) {

			final Color color = this.calculateColor(bufferedImage, x, y);
			bytes[i] = color.getRed();
			bytes[i + 1] = color.getGreen();
			bytes[i + 2] = color.getBlue();

			x++;
			if (x >= (firstPixel.getPoint().x + bufferedImage.getWidth())) {
				x = firstPixel.getPoint().y;
				y++;
			}

		}

		return bytes;
	}

	public int[] readPixelArea(Pixel firstPixel, BufferedImage bufferedImage, int buffSize, int areaW) {

		final int[] pixels = new int[buffSize];

		int x = firstPixel.getPoint().x + 1;
		int y = firstPixel.getPoint().y;
		for (int i = 1; i < buffSize; i = i + 3) {

			final Color color = this.calculateColor(bufferedImage, x, y);
			pixels[i - 1] = color.getRed();
			pixels[i] = color.getGreen();
			pixels[i + 1] = color.getBlue();

			x++;
			if (x >= (firstPixel.getPoint().x + areaW)) {
				x = firstPixel.getPoint().y;
				y++;
			}

		}

		return pixels;
	}

	public String intToString(int[] bytes) {
		final char[] chars = new char[bytes.length];
		String result;
		for (int i = 0; i < bytes.length; i++) {
			chars[i] = (char) bytes[i];
		}
		result = new String(chars);
		return result;
	}

}
