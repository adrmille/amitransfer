/**
 *
 */
package fr.amille.amiout.states;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import fr.amille.amiout.entity.FileInformation;
import fr.amille.amiout.entity.Pixel;
import fr.amille.amiout.service.screen.MouseAndKeyboardService;
import fr.amille.amiout.service.screen.ScreenService;
import fr.amille.amiout.service.screen.ThreadingService;
import fr.amille.amiout.view.MyAbstractSwingWorker;

/**
 * @author AMILLE
 *
 */
public class Context {

	private State currentState;

	private File currentFile;

	private Pixel firstPixel;

	private BufferedImage currentImage;

	private Point whereTheMouseShouldBe;

	private int totalWroteBytes;

	private final ScreenService screenService;

	private final MouseAndKeyboardService mouseService;

	private final ThreadingService threadingService;

	private FileInformation fileInformation;

	private String fileName;
	private int fileSize = -1;

	public Context() {
		this.screenService = new ScreenService();
		this.mouseService = new MouseAndKeyboardService();
		this.threadingService = new ThreadingService();
	}

	private void goNext() {
		if (this.currentState == null) {
			this.firstPixel = null;
			this.currentFile = null;
			this.currentImage = null;
			this.currentState = Ready.INSTANCE;
			this.whereTheMouseShouldBe = null;
			this.fileName = null;
			this.fileSize = -1;
			this.totalWroteBytes = 0;
		}
		System.out.println("New state: " + this.currentState.getClass().getSimpleName());
		this.currentState.goNext(this);
	}

	public void setState(State state) {
		new MyAbstractSwingWorker() {
			@Override
			public void whatToExecute() {
				Context.this.currentState = state;
				Context.this.goNext();
			}
		}.execute();
	}

	public File getCurrentFile() {
		return this.currentFile;
	}

	public Pixel getFirstPixel() {
		return this.firstPixel;
	}

	public void setFirstPixel(Pixel firstPixel) {
		this.firstPixel = firstPixel;
	}

	public void backToStartState() {
		new MyAbstractSwingWorker() {
			@Override
			public void whatToExecute() {
				Context.this.setState(Ready.INSTANCE);
			}
		}.execute();
	}

	public BufferedImage getCurrentImage() {
		return this.currentImage;
	}

	public void setCurrentImage(BufferedImage currentImage) {
		this.currentImage = currentImage;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {

		if ((this.fileName != null) && !fileName.equals(this.fileName)) {
			throw new IllegalArgumentException("Filename changed, restarting the application");
		}

		this.fileName = fileName;
		this.currentFile = new File(this.getFileName());
	}

	public int getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(int fileSize) {

		if ((this.fileSize != -1) && (fileSize != this.fileSize)) {
			throw new IllegalArgumentException("fileSize changed, restarting the application");
		}

		this.fileSize = fileSize;

	}

	public Point getWhereTheMouseShouldBe() {
		return this.whereTheMouseShouldBe;
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
		if ((currentMouseLocation.getX() != this.getWhereTheMouseShouldBe().getX())
				|| (currentMouseLocation.getY() != this.getWhereTheMouseShouldBe().getY())) {
			return true;
		}
		return false;
	}

	/**
	 * @param whereTheMouseShouldBe
	 *            the whereTheMouseShouldBe to set
	 */
	public void setWhereTheMouseShouldBe(Point whereTheMouseShouldBe) {
		this.whereTheMouseShouldBe = whereTheMouseShouldBe;
	}

	/**
	 * @return the totalWroteBytes
	 */
	public int getTotalWroteBytes() {
		return this.totalWroteBytes;
	}

	public void incrTotalWroteBytes() {
		this.totalWroteBytes++;
	}

	public State getCurrentState() {
		return this.currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public void setTotalWroteBytes(int totalWroteBytes) {
		this.totalWroteBytes = totalWroteBytes;
	}

	public ScreenService getScreenService() {
		return this.screenService;
	}

	public MouseAndKeyboardService getMouseService() {
		return this.mouseService;
	}

	public ThreadingService getThreadingService() {
		return this.threadingService;
	}

	public FileInformation getFileInformation() {
		return this.fileInformation;
	}

	public void setFileInformation(FileInformation fileInformation) {
		this.fileInformation = fileInformation;
	}

}
