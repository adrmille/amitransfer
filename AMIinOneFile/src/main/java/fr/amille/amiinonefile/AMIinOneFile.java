package fr.amille.amiinonefile;
/**
 * 
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Don't mind the code not following standards, this have to fit into one class
 * so it can be easily copied into the target environment.
 * 
 * @author amille
 */
public class AMIinOneFile {

	public static MainFrame MAIN_FRAME;
	public static AMIinOneFile MAIN_INSTANCE;
	public static char NEXT_KEY = 'n';

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {
		MAIN_INSTANCE = new AMIinOneFile();
	}

	public AMIinOneFile() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MAIN_FRAME = new MainFrame();
				MAIN_FRAME.setVisible(true);
				final Context context = new Context();
				context.goNext();
			}
		});
	}

	public interface AMIConstants {
		public static final int WIN_W = 300;
		public static final int WIN_H = 300;
		public static Color FIRST_PRINT_PIXEL_COLOR = new Color(255, 73, 250);
		public static final Color CONTROL_PIXEL_COLOR = new Color(255, 73, 252);
		public static final Color LAST_PIXEL = new Color(255, 73, 253);
	}

	public class Context {

		private State currentState;
		private File currentFile;
		private int positionInFile;

		public void goNext() {
			if (currentState == null) {
				positionInFile = 0;
				currentFile = null;
				currentState = WaitFile.INSTANCE;
			}
			currentState.goNext(this);
		}

		public void setState(State state) {
			currentState = state;
			goNext();
		}

		public File getCurrentFile() {
			return currentFile;
		}

		public void setCurrentFile(File currentFile) {
			this.currentFile = currentFile;
		}

		public int getPositionInFile() {
			return positionInFile;
		}

		public void setPositionInFile(int positionInFile) {
			this.positionInFile = positionInFile;
		}

		public boolean isEnded() {
			return currentState == End.INSTANCE;
		}
	}

	public static class End implements State {

		public final static End INSTANCE = new End();

		private End() {
		}

		@Override
		public void goNext(final Context context) {
			MAIN_FRAME.renewMainPanel();
			MAIN_FRAME.mainPanel.showText("Finish !");
			MAIN_FRAME.mainPanel.showRestartButton(context);
		}
	}

	public static class PrintBlocks implements State {

		public final static PrintBlocks INSTANCE = new PrintBlocks();

		private PrintBlocks() {
		}

		@Override
		public void goNext(Context context) {
			printBlocks(context);
		}

		private void printBlocks(final Context context) {
			MAIN_FRAME.renewMainPanel();
			final KeyAdapter printNext = new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == NEXT_KEY) {
						final int blockSize = (MAIN_FRAME.mainPanel.getWidth() * MAIN_FRAME.mainPanel.getHeight() * 3)
								- 6;
						context.setPositionInFile(context.getPositionInFile() + blockSize);
						MAIN_INSTANCE.new MyAbstractSwingWorker() {
							@Override
							public void whatToExecute() {
								context.setState(PrintBlocks.INSTANCE);
							}
						}.execute();
					}
				}
			};
			final KeyAdapter printEnd = new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == NEXT_KEY) {
						MAIN_FRAME.mainPanel.removeKeyListener(this);
						context.setState(End.INSTANCE);
					}
				}
			};

			FileInputStream in = null;
			try {

				in = new FileInputStream(context.getCurrentFile());
				final int blockSize = (MAIN_FRAME.mainPanel.getWidth() * MAIN_FRAME.mainPanel.getHeight() * 3) - 6;
				final byte[] bytesToPrint = extractBytesFromFile(in, context.getPositionInFile(), blockSize);
				MAIN_FRAME.mainPanel.setBytesToPrint(AMIConstants.FIRST_PRINT_PIXEL_COLOR, bytesToPrint);

				if (bytesToPrint[bytesToPrint.length - 2] != -1) {
					MAIN_FRAME.addKeyListener(printNext);
				} else {
					MAIN_FRAME.addKeyListener(printEnd);
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

		private byte[] extractBytesFromFile(FileInputStream in, int start, int blockSize) throws IOException {

			final byte[] values = new byte[blockSize];
			int index = 0;
			int c;
			in.skip(start);
			while (index < blockSize) {
				c = in.read();
				values[index] = (byte) c;
				index++;
			}
			return values;
		}
	}

	public static class ShowControlInfo implements State {

		public final static ShowControlInfo INSTANCE = new ShowControlInfo();

		private ShowControlInfo() {
		}

		@Override
		public void goNext(final Context context) {
			MAIN_FRAME.renewMainPanel();
			final KeyAdapter keyAdapter = new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == NEXT_KEY) {
						MAIN_INSTANCE.new MyAbstractSwingWorker() {
							@Override
							public void whatToExecute() {
								context.setState(PrintBlocks.INSTANCE);
							}
						}.execute();
					}
				}
			};
			final StringBuilder fileInformations = new StringBuilder();
			fileInformations.append(context.getCurrentFile().getName());
			fileInformations.append("\n");
			fileInformations.append(context.getCurrentFile().length());
			fileInformations.append("\n");
			fileInformations.append(MAIN_FRAME.mainPanel.getWidth());
			fileInformations.append("\n");
			fileInformations.append(MAIN_FRAME.mainPanel.getHeight());
			fileInformations.append("\n");
			fileInformations.append(context.getPositionInFile());
			fileInformations.append("EOF_EOF");
			MAIN_FRAME.mainPanel.setBytesToPrint(AMIConstants.CONTROL_PIXEL_COLOR, fileInformations.toString());
			MAIN_FRAME.addKeyListener(keyAdapter);
		}
	}

	public interface State {

		/**
		 * Execute and go next state.
		 * 
		 * @param context
		 */
		public void goNext(Context context);

	}

	public static class WaitFile implements State {

		public final static WaitFile INSTANCE = new WaitFile();

		private WaitFile() {
		}

		@Override
		public void goNext(final Context context) {
			MAIN_FRAME.renewMainPanel();
			final JButton fileChooserButton = new JButton("File chooser");
			MAIN_FRAME.mainPanel.setFileChooserButton(fileChooserButton);
			fileChooserButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MAIN_FRAME.mainPanel.showFileChooser(context);
					context.setState(ShowControlInfo.INSTANCE);
				}
			});
		}
	}

	@SuppressWarnings("serial")
	public class MainFrame extends JFrame {

		public MainPanel mainPanel;

		public MainFrame() {
			renewMainPanel();
			this.setVisible(true);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setTitle("AMIin");
		}

		public void renewMainPanel() {
			if (mainPanel != null) {
				this.getContentPane().remove(mainPanel);
			}
			final int winW = (this.getContentPane().getWidth() != 0) ? this.getContentPane().getWidth()
					: AMIConstants.WIN_W;
			final int winH = (this.getContentPane().getHeight() != 0) ? this.getContentPane().getHeight()
					: AMIConstants.WIN_H;
			mainPanel = MAIN_INSTANCE.new MainPanel(winW, winH);
			this.getContentPane().add(mainPanel);
			this.pack();
			mainPanel.setVisible(true);
		}

		@Override
		public synchronized void addKeyListener(KeyListener l) {
			final KeyListener[] keyListeners = this.getKeyListeners();
			for (int i = 0; i < keyListeners.length; i++) {
				removeKeyListener(keyListeners[i]);
			}
			super.addKeyListener(l);
		}
	}

	@SuppressWarnings("serial")
	public class MainPanel extends Panel {

		private int[] bytesToPrint;
		private JLabel mainLabel = new JLabel();
		private JButton fileChooserButton;
		private JButton restartButton;

		public MainPanel(int w, int h) {
			setPreferredSize(new Dimension(w, h));
			this.add(mainLabel);
			hideAll();
		}

		public void showFileChooser(Context context) {
			final JFileChooser fileChooser = new JFileChooser();
			MainPanel.this.add(fileChooser);
			final int returnVal = fileChooser.showOpenDialog(MainPanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				final File file = fileChooser.getSelectedFile();
				if (file == null || !file.exists()) {
					context.setState(WaitFile.INSTANCE);
				} else {
					context.setCurrentFile(fileChooser.getSelectedFile());
				}
			}
		}

		public void hideAll() {
			bytesToPrint = null;
			mainLabel.setVisible(false);
			if (fileChooserButton != null)
				fileChooserButton.setVisible(false);
			if (restartButton != null)
				restartButton.setVisible(false);
		}

		@Override
		public void paint(Graphics graph) {
			super.paint(graph);

			if (bytesToPrint != null) {

				graph.fillRect(0, 0, 1, 1);

				int x = 1;
				int y = 0;
				int cnt = 0;
				while (cnt < bytesToPrint.length) {

					int r = (bytesToPrint[cnt] >= 0) ? bytesToPrint[cnt] : 0;
					cnt++;
					int g = (cnt < bytesToPrint.length && bytesToPrint[cnt] >= 0) ? bytesToPrint[cnt] : 0;
					cnt++;
					int b = (cnt < bytesToPrint.length && bytesToPrint[cnt] >= 0) ? bytesToPrint[cnt] : 0;
					cnt++;

					graph.setColor(new Color(r, g, b));
					graph.fillRect(x, y, 1, 1);

					x++;
					if (x >= MAIN_FRAME.mainPanel.getWidth()) {
						x = 0;
						y++;
					}

				}

				graph.setColor(AMIConstants.LAST_PIXEL);
				graph.fillRect(MAIN_FRAME.mainPanel.getWidth() - 1, MAIN_FRAME.mainPanel.getHeight() - 1, 1, 1);
			}
		}

		public void showText(String text) {
			mainLabel.setText(text);
			mainLabel.setVisible(true);
		}

		public void showRestartButton(Context context) {
			restartButton = new JButton("Restart");
			this.add(restartButton);
			restartButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					context.setState(null);
				}
			});
		}

		public JButton getFileChooserButton() {
			return fileChooserButton;
		}

		public void setFileChooserButton(JButton fileChooserButton) {
			if (this.fileChooserButton != null) {
				this.remove(fileChooserButton);
			}
			this.add(fileChooserButton);
			this.fileChooserButton = fileChooserButton;
		}

		public JButton getRestartButton() {
			return restartButton;
		}

		public void setRestartButton(JButton restartButton) {
			this.restartButton = restartButton;
		}

		public int[] getBytesToPrint() {
			return bytesToPrint;
		}

		private byte[] colorToBytes(Color color) {
			final byte[] bytes = new byte[3];
			bytes[0] = (byte) color.getRed();
			bytes[1] = (byte) color.getGreen();
			bytes[2] = (byte) color.getBlue();
			return bytes;
		}

		public void setBytesToPrint(Color starterPixel, byte[] bytesToPrint) {
			this.bytesToPrint = bytesToInt(colorToBytes(starterPixel), bytesToPrint);
			MAIN_FRAME.revalidate();
			MAIN_FRAME.repaint();
			this.revalidate();
			this.repaint();
		}

		public void setBytesToPrint(Color starterPixel, String stringToPrint) {
			this.bytesToPrint = bytesToInt(colorToBytes(starterPixel), stringToPrint.getBytes());
			MAIN_FRAME.revalidate();
			MAIN_FRAME.repaint();
			this.revalidate();
			this.repaint();
		}

		private int[] bytesToInt(byte[] starterBytes, byte[] bytes) {
			int[] result = new int[starterBytes.length + bytes.length];
			for (int i = 0; i < starterBytes.length; i++) {
				result[i] = starterBytes[i];
			}
			for (int i = starterBytes.length; i < bytes.length; i++) {
				result[i] = bytes[i];
			}
			return result;
		}
	}

	public abstract class MyAbstractSwingWorker {

		public static final int SLEEP_TIME_MS = 50;

		public void execute() {
			final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					try {
						Thread.sleep(MyAbstractSwingWorker.SLEEP_TIME_MS);
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
					} finally {
						whatToExecute();
					}
					return null;
				}
			};
			swingWorker.execute();
		}

		public abstract void whatToExecute();
	}

}
