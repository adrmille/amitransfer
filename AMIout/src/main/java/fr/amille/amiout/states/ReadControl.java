/**
 *
 */
package fr.amille.amiout.states;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;

import fr.amille.amiout.AMIout;
import fr.amille.amiout.entity.FileInformation;
import fr.amille.amiout.entity.Pixel;
import fr.amille.amiout.service.screen.ScreenService;
import fr.amille.amiout.view.MainPanel;

/**
 * @author AMILLE
 *
 */
public class ReadControl implements State {

	public final static ReadControl INSTANCE = new ReadControl();

	private ReadControl() {
	}

	@Override
	public void goNext(final Context context) {

		final MainPanel mainPanel = AMIout.mainFrame.getMainPanel();
		mainPanel.createMainLabel("Reading control");

		this.updateFileInformations(context);

		this.focusAndGoToNext(context);

		// Continue the flow
		context.setState(SearchingDataPixel.INSTANCE);
	}

	private void updateFileInformations(final Context context) {

		final String fileInformationString = this.getFileInformationsFromScreen(context);

		if (!this.validateFileInformtaions(fileInformationString)) {
			throw new IllegalArgumentException("Missing file informations");
		}

		final FileInformation fileInformation = new FileInformation(fileInformationString);
		System.out.println(fileInformation.toString());

		context.setFileInformation(fileInformation);
	}

	private void focusAndGoToNext(final Context context) {
		// Stock the current/initial position for later
		final Point initialCursorPosition = MouseInfo.getPointerInfo().getLocation();

		// Move to the controlPixel
		context.getMouseService().moveMouseTo(context.getFirstPixel().getPoint());

		// Click on the point to get focus on the AMIin window
		context.getThreadingService().sleep(100);
		context.getMouseService().leftClick();
		context.getThreadingService().sleep(1000);
		context.getMouseService().pressKeyN();
		context.getThreadingService().sleep(100);

		// Move back to the initial position
		context.getMouseService().moveMouseTo(initialCursorPosition);
		context.setWhereTheMouseShouldBe(initialCursorPosition);
	}

	private String getFileInformationsFromScreen(final Context context) {

		String fileInformations = null;

		final ScreenService screenService = context.getScreenService();
		final BufferedImage currentImage = context.getCurrentImage();
		final Pixel firstPixel = context.getFirstPixel();

		final int[] bytes = screenService.readPixelToBytes(firstPixel, currentImage, 450);
		final String fileInformationsRaw = screenService.intToString(bytes);
		if (!fileInformationsRaw.isEmpty()) {

			final int eofIndex = fileInformationsRaw.indexOf("EOF_EOF");
			if (eofIndex != -1) {

				fileInformations = fileInformationsRaw.substring(0, eofIndex);

			} else {
				System.err.println("File informations malformed");
			}
		} else {
			System.err.println("File informations is empty");
		}

		return fileInformations;
	}

	private boolean validateFileInformtaions(final String fileInformations) {
		if ((fileInformations == null) || fileInformations.isEmpty()) {

			throw new IllegalArgumentException("Error when recovering the file informations");
		}
		return true;
	}
}
