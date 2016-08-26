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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
	public static Color CURRENT_FIRST_PIXEL_COLOR = AMIConstants.STARTER_PIXEL1;
	public static KeyAdapter KEY_ADAPTER_1;
	public static KeyAdapter KEY_ADAPTER_2;

	public static enum States {
		PRINT, EMPTY, DRAW, WAITING_FILE, DRAW_CONTROL, END
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {

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
		public static final Color STARTER_PIXEL1 = new Color(255, 73, 250);
		public static final Color STARTER_PIXEL2 = new Color(255, 73, 251);
		public static final Color CONTROL_PIXEL = new Color(255, 73, 252);
		public static final Color LAST_PIXEL = new Color(255, 73, 253);
	}

	public static class Context {

		private State currentState;
		private File currentFile;
		private int positionInFile;
		private int[] bytesToPrint;

		public void goNext() {
			if (currentState == null) {
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
			if (CURRENT_FIRST_PIXEL_COLOR.getBlue() == AMIConstants.STARTER_PIXEL1.getBlue()) {
				CURRENT_FIRST_PIXEL_COLOR = AMIConstants.STARTER_PIXEL2;
			} else {
				CURRENT_FIRST_PIXEL_COLOR = AMIConstants.STARTER_PIXEL1;
			}
		}
	}

	public static class End implements State {

		public final static End INSTANCE = new End();

		private End() {
		}

		@Override
		public void goNext(final Context context) {
			MAIN_FRAME.mainPanel.changeState(States.END, context);
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
			if (KEY_ADAPTER_1 == null) {
				KEY_ADAPTER_1 = new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (e.getKeyChar() == 'n') {
							final int blockSize = (MAIN_FRAME.mainPanel.getWidth()
									* MAIN_FRAME.mainPanel.getHeight() * 3) - 6;
							MAIN_FRAME.mainPanel.removeKeyListener(this);
							context.setPositionInFile(context.getPositionInFile() + blockSize);
							context.switchFirstPixelColor();
							MAIN_FRAME.mainPanel.changeState(States.EMPTY, context);
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
			if (KEY_ADAPTER_2 == null) {
				KEY_ADAPTER_2 = new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (e.getKeyChar() == 'n') {
							MAIN_FRAME.mainPanel.removeKeyListener(this);
							context.setState(End.INSTANCE);
						}
					}
				};
			}
			MAIN_FRAME.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					if (!context.isEnded()) {
						MAIN_FRAME.mainPanel.removeComponentListener(this);
						MAIN_FRAME.mainPanel.removeKeyListener(KEY_ADAPTER_1);
						MAIN_FRAME.mainPanel.removeKeyListener(KEY_ADAPTER_2);
						context.setState(ShowControlInfo.INSTANCE);
					}
				}

				public void componentMoved(ComponentEvent e) {
					if (!context.isEnded()) {
						MAIN_FRAME.mainPanel.removeComponentListener(this);
						MAIN_FRAME.mainPanel.removeKeyListener(KEY_ADAPTER_1);
						MAIN_FRAME.mainPanel.removeKeyListener(KEY_ADAPTER_2);
						context.setState(ShowControlInfo.INSTANCE);
					}
				}
			});
			FileInputStream in = null;
			try {

				if (!context.getCurrentFile().exists()) {
					System.err.println("Missing file in PrintBlock state");
					context.setState(null);
				}

				in = new FileInputStream(context.getCurrentFile());
				final int blockSize = (MAIN_FRAME.mainPanel.getWidth()
						* MAIN_FRAME.mainPanel.getHeight() * 3) - 6;
				final int[] bytesToPrint = extractBytesFromFile(in, context.getPositionInFile(), blockSize);
				context.setBytesToPrint(bytesToPrint);
				MAIN_FRAME.mainPanel.changeState(States.DRAW, context);

				if (bytesToPrint[bytesToPrint.length - 2] != -1) {
					MAIN_FRAME.mainPanel.removeKeyListener(KEY_ADAPTER_1);
					MAIN_FRAME.mainPanel.addKeyListener(KEY_ADAPTER_1);
				} else {
					MAIN_FRAME.mainPanel.removeKeyListener(KEY_ADAPTER_2);
					MAIN_FRAME.mainPanel.addKeyListener(KEY_ADAPTER_2);
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

		private int[] extractBytesFromFile(FileInputStream in, int start, int blockSize) throws IOException {

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

	public static class ShowControlInfo implements State {

		public final static ShowControlInfo INSTANCE = new ShowControlInfo();
		private static KeyAdapter keyAdapter;

		private ShowControlInfo() {
		}

		@Override
		public void goNext(final Context context) {
			if (keyAdapter == null) {
				keyAdapter = new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (e.getKeyChar() == 'n') {
							MAIN_FRAME.mainPanel.removeKeyListener(this);
							MAIN_FRAME.mainPanel.changeState(States.EMPTY, context);
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
			MAIN_FRAME.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					if (!context.isEnded()) {
						MAIN_FRAME.mainPanel.removeComponentListener(this);
						MAIN_FRAME.mainPanel.removeKeyListener(keyAdapter);
						context.setState(ShowControlInfo.INSTANCE);
					}
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					if (!context.isEnded()) {
						MAIN_FRAME.mainPanel.removeComponentListener(this);
						MAIN_FRAME.mainPanel.removeKeyListener(keyAdapter);
						context.setState(ShowControlInfo.INSTANCE);
					}
				}
			});
			if (context.getCurrentFile() == null) {
				System.err.println("Missing file in ShowControlInfo state");
				context.setState(null);
			}
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
			context.setBytesToPrint(fileInformations.toString());
			MAIN_FRAME.mainPanel.changeState(States.DRAW_CONTROL, context);
			MAIN_FRAME.mainPanel.removeKeyListener(keyAdapter);
			MAIN_FRAME.mainPanel.addKeyListener(keyAdapter);
			// AMIinOneFile.mainFrame.removeKeyListener(keyAdapter);
			// AMIinOneFile.mainFrame.addKeyListener(keyAdapter);
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
			MAIN_FRAME.mainPanel.changeState(States.WAITING_FILE, context);
			MAIN_FRAME.mainPanel.hideAll();
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
	public static class MainFrame extends JFrame {

		public MainPanel mainPanel = new MainPanel();

		public MainFrame() {
			this.getContentPane().add(mainPanel);
			this.pack();
			this.setVisible(true);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setTitle("AMIin");
		}
	}

	@SuppressWarnings("serial")
	public static class MainPanel extends Panel {

		private States state;
		private int[] bytesToPrint;
		private JLabel mainLabel = new JLabel();
		private JButton fileChooserButton;
		private JButton restartButton;

		public MainPanel() {
			state = States.EMPTY;
			setPreferredSize(new Dimension(AMIConstants.WIN_W, AMIConstants.WIN_H));
			this.add(mainLabel);
			hideAll();
		}

		public void showFileChooser(Context context) {
			final JFileChooser fileChooser = new JFileChooser();
			MainPanel.this.add(fileChooser);
			final int returnVal = fileChooser.showOpenDialog(MainPanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				context.setCurrentFile(fileChooser.getSelectedFile());
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

		public void changeState(States newState, final Context context) {
			hideAll();
			state = newState;

			switch (state) {
			case DRAW:
				bytesToPrint = context.getBytesToPrint();
				break;
			case DRAW_CONTROL:
				bytesToPrint = context.getBytesToPrint();
				break;
			case EMPTY:
				break;
			case END:
				mainLabel.setText("Finish !");
				mainLabel.setVisible(true);
				if (restartButton != null) {
					this.remove(restartButton);
				}
				restartButton = new JButton("Restart");
				this.add(restartButton);
				restartButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						context.setState(null);
					}
				});
				break;
			case PRINT:
				mainLabel.setText("Ready !");
				mainLabel.setVisible(true);
				break;
			case WAITING_FILE:
				break;
			default:
				break;
			}
			repaint();
		}

		@Override
		public void paint(Graphics graph) {
			super.paint(graph);

			if ((state == States.DRAW || state == States.DRAW_CONTROL) && bytesToPrint != null) {

				if (state == States.DRAW) {
					graph.setColor(AMIinOneFile.CURRENT_FIRST_PIXEL_COLOR);
				} else if (state == States.DRAW_CONTROL) {
					graph.setColor(AMIConstants.CONTROL_PIXEL);
				}

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
				graph.fillRect(MAIN_FRAME.mainPanel.getWidth() - 1,
						MAIN_FRAME.mainPanel.getHeight() - 1, 1, 1);
			}
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
	}

	public static abstract class MyAbstractSwingWorker {

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
