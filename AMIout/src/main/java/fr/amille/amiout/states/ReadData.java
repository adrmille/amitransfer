/**
 * 
 */
package fr.amille.amiout.states;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fr.amille.amiout.AMIout;
import fr.amille.amiout.view.MainPanel;

/**
 * @author AMILLE
 * 
 */
public class ReadData implements State {

	public final static ReadData INSTANCE = new ReadData();

	private ReadData() {
	}

	@Override
	public void goNext(final Context context) {

		final MainPanel mainPanel = AMIout.mainFrame.getMainPanel();

		final int percent = (context.getTotalWroteBytes() * 100) / context.getFileSize();
		mainPanel.createMainLabel("Reading blocks (" + percent + "% already done)");

		// read pixels
		final int[] bytes = readPixelAreaToBits(context);

		if (!context.isMouseMoved()) {

			// write bytes into the file
			writeBytes(context.getCurrentFile(), bytes, context);

			callForNextScreen(context);

			// test if we have write the maximum of byte
			int maxNumberOfByteWeCanWrite = context.getFileSize() - context.getTotalWroteBytes();

			if (maxNumberOfByteWeCanWrite <= 0) {
				context.setState(End.INSTANCE);
			} else {
				context.setState(SearchingDataPixel.INSTANCE);
			}

		} else {

			context.setState(Pause.INSTANCE);
		}
	}

	private int[] readPixelAreaToBits(final Context context) {
		final int areaW = context.getFileInformation().getAreaW();
		final int areaH = context.getFileInformation().getAreaH();
		final int bytesBufferSize = (areaW * areaH * 3) - 6;
		return context.getScreenService().readPixelArea(context.getFirstPixel(), context.getCurrentImage(),
				bytesBufferSize, areaW);
	}

	private void callForNextScreen(final Context context) {
		context.getMouseService().pressKeyN();
		context.getThreadingService().sleep(100);
	}

	private void writeBytes(File file, int[] bytes, Context context) {

		FileOutputStream out = null;
		try {

			out = new FileOutputStream(file, true);

			int maxNumberOfByteWeCanWrite = context.getFileSize() - context.getTotalWroteBytes();

			for (int i = 0; i < bytes.length && maxNumberOfByteWeCanWrite > 0; i++) {
				out.write(bytes[i]);
				context.incrTotalWroteBytes();
				maxNumberOfByteWeCanWrite = context.getFileSize() - context.getTotalWroteBytes();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
