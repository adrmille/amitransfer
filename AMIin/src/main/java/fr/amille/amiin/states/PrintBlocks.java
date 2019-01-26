/**
 * 
 */
package fr.amille.amiin.states;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.amille.amiin.AMIin;
import fr.amille.amiin.view.MainPanel.States;
import fr.amille.amiin.view.MyAbstractSwingWorker;

/**
 * @author AMILLE
 * 
 */
public class PrintBlocks implements State {

	public final static PrintBlocks INSTANCE = new PrintBlocks();

	private static KeyAdapter keyAdapter1;

	private static KeyAdapter keyAdapter2;

	private PrintBlocks() {
	}

	@Override
	public void goNext(Context context) {
		printBlocks(context);
	}

	private void printBlocks(final Context context) {
		if (keyAdapter1 == null) {
			keyAdapter1 = new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == 'n') {
						final int blockSize = (AMIin.mainFrame.mainPanel
								.getWidth()
								* AMIin.mainFrame.mainPanel.getHeight() * 3) - 6;
						AMIin.mainFrame.removeKeyListener(this);
						context.setPositionInFile(context.getPositionInFile()
								+ blockSize);
						context.switchFirstPixelColor();
						AMIin.mainFrame.mainPanel.changeState(States.EMPTY,
								context);
						new MyAbstractSwingWorker() {
							@Override
							public void whatToExecute() {
								context.setState(PrintBlocks.INSTANCE);
							}
						}.execute();
					}
				}
			};
		}
		if (keyAdapter2 == null) {
			keyAdapter2 = new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == 'n') {
						AMIin.mainFrame.mainPanel.removeKeyListener(this);
						context.setState(End.INSTANCE);
					}
				}
			};
		}
		AMIin.mainFrame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (!context.isEnded()) {
					AMIin.mainFrame.mainPanel.removeComponentListener(this);
					AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter1);
					AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter2);
					context.setState(ShowControlInfo.INSTANCE);
				}
			}

			public void componentMoved(ComponentEvent e) {
				if (!context.isEnded()) {
					AMIin.mainFrame.mainPanel.removeComponentListener(this);
					AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter1);
					AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter2);
					context.setState(ShowControlInfo.INSTANCE);
				}
			}
		});
		FileInputStream in = null;
		try {

			if (!context.currentFile.exists()) {
				System.err.println("Missing file in PrintBlock state");
				context.setState(null);
			}

			in = new FileInputStream(context.currentFile);
			final int blockSize = (AMIin.mainFrame.mainPanel.getWidth()
					* AMIin.mainFrame.mainPanel.getHeight() * 3) - 6;
			final int[] bytesToPrint = extractBytesFromFile(in,
					context.getPositionInFile(), blockSize);
			context.setBytesToPrint(bytesToPrint);
			AMIin.mainFrame.mainPanel.changeState(States.DRAW, context);

			if (bytesToPrint[bytesToPrint.length - 2] != -1) {
				AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter1);
				AMIin.mainFrame.mainPanel.addKeyListener(keyAdapter1);
			} else {
				AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter2);
				AMIin.mainFrame.mainPanel.addKeyListener(keyAdapter2);
			}

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			context.setState(null);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			context.setState(null);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				context.setState(null);
			}
		}
	}

	private int[] extractBytesFromFile(FileInputStream in, int start,
			int blockSize) throws IOException {

		final int[] values = new int[blockSize];
		int index = 0;
		int c;
		in.skip(start);
		while (index < blockSize) {
			c = in.read();
			values[index] = c;
			index++;
		}
		return values;
	}

}
