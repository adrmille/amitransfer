/**
 * 
 */
package fr.amille.amiin.states;

import java.awt.Color;
import java.io.File;

import fr.amille.amiin.constant.AMIConstants;

/**
 * @author AMILLE
 * 
 */
public class Context {

	private State currentState;

	private File currentFile;

	private int positionInFile;

	private int[] bytesToPrint;

	public static Color currentFirstPixelColor = AMIConstants.STARTER_PIXEL1;

	public void goNext() {
		if (currentState == null) {
			// THINK ABOUT ME
			positionInFile = 0;
			bytesToPrint = null;
			currentFile = null;
			currentState = WaitFile.INSTANCE;
		}
		currentState.goNext(this);
	}

	public void setState(State state) {
		currentState = state;
		goNext();
	}

	private int[] bytesToInt(byte[] bytes) {
		int[] result = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
		currentState = ShowControlInfo.INSTANCE;
		currentState.goNext(this);
	}

	public int getPositionInFile() {
		return positionInFile;
	}

	public void setPositionInFile(int positionInFile) {
		this.positionInFile = positionInFile;
	}

	public int[] getBytesToPrint() {
		return bytesToPrint;
	}

	public void setBytesToPrint(int[] bytesToPrint) {
		this.bytesToPrint = bytesToPrint;
	}

	public void setBytesToPrint(String stringToPrint) {
		this.bytesToPrint = bytesToInt(stringToPrint.getBytes());
	}

	public boolean isEnded() {
		return currentState == End.INSTANCE;
	}

	public void switchFirstPixelColor() {
		if (currentFirstPixelColor.getBlue() == AMIConstants.STARTER_PIXEL1
				.getBlue()) {
			currentFirstPixelColor = AMIConstants.STARTER_PIXEL2;
		} else {
			currentFirstPixelColor = AMIConstants.STARTER_PIXEL1;
		}
	}

}
