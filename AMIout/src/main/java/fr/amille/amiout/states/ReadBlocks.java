/**
 * 
 */
package fr.amille.amiout.states;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fr.amille.amiout.AMIout;
import fr.amille.amiout.states.Context.FirstPixel;
import fr.amille.amiout.view.MainPanel;
import fr.amille.amiout.view.MyAbstractSwingWorker;

/**
 * @author AMILLE
 * 
 */
public class ReadBlocks implements State {

	public final static ReadBlocks INSTANCE = new ReadBlocks();

	private ReadBlocks() {
	}

	@Override
	public void goNext(final Context context) {

		AMIout.mainFrame.mainPanel.changeState(MainPanel.States.READ_BLOCK, context);

		// read pixels
		final int bytesBufferSize = (context.areaW * context.areaH * 3) - 6;
		final int[] bytes = readPixelArea(context.firstPixel, context.currentImage, bytesBufferSize, context.areaW);

		if (!context.isMouseMoved()) {

			// write bytes into the file
			writeBytes(context.currentFile, bytes, context);

			// test if we have write the maximum of byte
			int maxNumberOfByteWeCanWrite = context.fileSize - context.totalWroteBytes;
			if (maxNumberOfByteWeCanWrite <= 0) {
				callForNextScreen(context);
				context.setState(End.INSTANCE);
				return;
			}

			context.switchFirstPixelColor();

			callForNextScreen(context);

			new MyAbstractSwingWorker() {
				@Override
				public void whatToExecute() {
					context.setState(SearchingFirstPiexel.INSTANCE);
				}
			}.execute();

		} else {

			context.setState(Pause.INSTANCE);

		}
	}

	private void callForNextScreen(final Context context) {

		try {

			final Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_N);
			Thread.sleep(100);

		} catch (AWTException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		}

	}

	private int[] readPixelArea(FirstPixel firstPixel, BufferedImage bufferedImage, int buffSize, int areaW) {

		int[] pixels = new int[buffSize];

		int x = firstPixel.getX() + 1;
		int y = firstPixel.getY();
		for (int i = 1; i < buffSize; i = i + 3) {

			final Color color = Context.calculateColor(bufferedImage, x, y);
			pixels[i - 1] = color.getRed();
			pixels[i] = color.getGreen();
			pixels[i + 1] = color.getBlue();

			x++;
			if (x >= firstPixel.getX() + areaW) {
				x = firstPixel.getX();
				y++;
			}

		}

		return pixels;
	}

	private void writeBytes(File file, int[] bytes, Context context) {

		FileOutputStream out = null;
		try {

			out = new FileOutputStream(file, true);

			int maxNumberOfByteWeCanWrite = context.fileSize - context.totalWroteBytes;

			for (int i = 0; i < bytes.length && maxNumberOfByteWeCanWrite > 0; i++) {
				out.write(bytes[i]);
				context.incrTotalWroteBytes();
				maxNumberOfByteWeCanWrite = context.fileSize - context.totalWroteBytes;
			}

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				context.backToStartState();
			}
		}

	}

}
