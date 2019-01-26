/**
 * 
 */
package fr.amille.amiout.states;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import fr.amille.amiout.Datascreen;
import fr.amille.amiout.Pixel;
import fr.amille.amiout.constant.AMIConstants;
import fr.amille.amiout.view.MyAbstractSwingWorker;

/**
 * @author AMILLE
 * 
 */
public class Context {

	State currentState;
	File currentFile;
	Pixel firstPixel;
	Datascreen datascreen;
	public static Color expectedFirstPixelColor = AMIConstants.STARTER_PIXEL1;
	Point whereTheMouseShouldBe;
	int totalWroteBytes;
	String fileName;
	int fileSize = -1;
	int areaW = -1;
	int areaH = -1;

	public void goNext() {
		if (currentState == null) {
			// THINK ABOUT ME
			firstPixel = null;
			currentFile = null;
			datascreen = null;
			currentState = Ready.INSTANCE;
			whereTheMouseShouldBe = null;
			fileName = null;
			fileSize = -1;
			areaW = -1;
			areaH = -1;
			totalWroteBytes = 0;
		}
		currentState.goNext(this);
	}

	public void setState(State state) {
		currentState = state;
		goNext();
	}

	public static Color calculateColor(BufferedImage bufferedImage, int x, int y) {
		final int rgb = bufferedImage.getRGB(x, y);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb & 0xFF);
		return new Color(r, g, b);
	}

	public void updateFileInformations() {

		String fileInformations = null;

		if (currentImage == null) {
			System.err.println("No current image");
			backToStartState();
			return;
		}

		final int[] bytes = readPixelToBytes(firstPixel, currentImage, 450);
		final String fileInformationsRaw = intToString(bytes);
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

		if (fileInformations == null || fileInformations.isEmpty()) {

			System.err.println("File informations is empty");
			backToStartState();

		} else {

			final String[] fileInfos = fileInformations.split("\\n");
			if (fileInfos.length != 5) {

				System.err.println("File informations malformed, should have 5 parameters, found: " + fileInfos.length);
				backToStartState();

			} else {

				try {
					setFileName(fileInfos[0]);
					setFileSize(Integer.parseInt(fileInfos[1]));
					areaW = Integer.parseInt(fileInfos[2]);
					areaH = Integer.parseInt(fileInfos[3]);

					validateFileInformations();

					final Robot robot = new Robot();
					whereTheMouseShouldBe = MouseInfo.getPointerInfo().getLocation();
					robot.mouseMove(firstPixel.getX(), firstPixel.getY());

					Point tempWhereTheMouseIsNow;
					do {

						Thread.sleep(100);
						tempWhereTheMouseIsNow = MouseInfo.getPointerInfo().getLocation();

					} while (tempWhereTheMouseIsNow.getX() != firstPixel.getX()
							&& tempWhereTheMouseIsNow.getY() != firstPixel.getY());

					Thread.sleep(100);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					Thread.sleep(1000);
					robot.keyPress(KeyEvent.VK_N);
					Thread.sleep(100);

					robot.mouseMove((int) whereTheMouseShouldBe.getX(), (int) whereTheMouseShouldBe.getY());

					do {

						Thread.sleep(100);
						tempWhereTheMouseIsNow = MouseInfo.getPointerInfo().getLocation();

					} while (tempWhereTheMouseIsNow.getX() != whereTheMouseShouldBe.getX()
							&& tempWhereTheMouseIsNow.getY() != whereTheMouseShouldBe.getY());

					Thread.sleep(100);
					new MyAbstractSwingWorker() {
						@Override
						public void whatToExecute() {
							setState(SearchingFirstPiexel.INSTANCE);
						}
					}.execute();

				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
					backToStartState();
				} catch (AWTException e) {
					System.err.println(e.getMessage());
					backToStartState();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
					backToStartState();
				}

			}
		}

	}

	private void validateFileInformations() {

		final StringBuilder stringBuilder = new StringBuilder();
		if (fileName == null || fileName.isEmpty()) {
			stringBuilder.append("File name is missing");
		}
		if (!stringBuilder.toString().isEmpty()) {
			System.err.println(stringBuilder);
			backToStartState();
		}
	}

	public void backToStartState() {
		new MyAbstractSwingWorker() {
			@Override
			public void whatToExecute() {
				setState(Ready.INSTANCE);
			}
		}.execute();
	}

	private String intToString(int[] bytes) {
		char[] chars = new char[bytes.length];
		String result;
		for (int i = 0; i < bytes.length; i++) {
			chars[i] = (char) bytes[i];
		}
		result = new String(chars);
		return result;
	}

	private int[] readPixelToBytes(FirstPixel firstPixel, BufferedImage bufferedImage, int buffSize) {

		int[] bytes = new int[buffSize];

		int x = firstPixel.getX() + 1;
		int y = firstPixel.getY();
		for (int i = 0; i < buffSize; i = i + 3) {

			final Color color = calculateColor(bufferedImage, x, y);
			bytes[i] = color.getRed();
			bytes[i + 1] = color.getGreen();
			bytes[i + 2] = color.getBlue();

			x++;
			if (x >= firstPixel.getX() + AMIConstants.WIN_W) {
				x = firstPixel.getX();
				y++;
			}

		}

		return bytes;
	}

	public void setFileName(String fileName) {

		if (this.fileName != null && !fileName.equals(this.fileName)) {
			System.out.println("Filename changed, restarting the application");
			backToStartState();
		}

		this.fileName = fileName;
		currentFile = new File(fileName);
	}

	public void setFileSize(int fileSize) {

		if (this.fileSize != -1 && fileSize != this.fileSize) {
			System.out.println("fileSize changed, restarting the application");
			backToStartState();
		}

		this.fileSize = fileSize;

	}

	public void switchFirstPixelColor() {
		if (expectedFirstPixelColor.getBlue() == AMIConstants.STARTER_PIXEL1.getBlue()) {
			expectedFirstPixelColor = AMIConstants.STARTER_PIXEL2;
		} else {
			expectedFirstPixelColor = AMIConstants.STARTER_PIXEL1;
		}
	}

	/**
	 * @param currentFile
	 *            the currentFile to set
	 */
	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	public boolean isMouseMoved() {
		final Point currentMouseLocation = MouseInfo.getPointerInfo().getLocation();
		if (currentMouseLocation.getX() != whereTheMouseShouldBe.getX()
				|| currentMouseLocation.getY() != whereTheMouseShouldBe.getY()) {
			return true;
		}
		return false;
	}

	public void incrTotalWroteBytes() {
		totalWroteBytes++;
	}

}
