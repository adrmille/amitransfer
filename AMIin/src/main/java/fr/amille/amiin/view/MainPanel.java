package fr.amille.amiin.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import fr.amille.amiin.AMIin;
import fr.amille.amiin.constant.AMIConstants;
import fr.amille.amiin.states.Context;

@SuppressWarnings("serial")
public class MainPanel extends Panel {

	public static enum States {
		PRINT, EMPTY, DRAW, WAITING_FILE, DRAW_CONTROL, END
	}

	private States state;

	private int[] bytesToPrint;

	private JLabel mainLabel = new JLabel();

	private JButton fileChooserButton;
	
	private JButton restartButton;

	private void hideAll() {
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
		if (state == States.EMPTY) {
		} else if (state == States.PRINT) {
			mainLabel.setText("Ready !");
			mainLabel.setVisible(true);
		} else if (state == States.WAITING_FILE) {
			if (fileChooserButton != null) {
				this.remove(fileChooserButton);
			}
			fileChooserButton = new JButton("File chooser");
			this.add(fileChooserButton);
			fileChooserButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final JFileChooser fileChooser = new JFileChooser();
					MainPanel.this.add(fileChooser);
					final int returnVal = fileChooser
							.showOpenDialog(MainPanel.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						context.setCurrentFile(fileChooser.getSelectedFile());
					}
				}
			});
		} else if (state == States.DRAW_CONTROL) {
			bytesToPrint = context.getBytesToPrint();
		} else if (state == States.DRAW) {
			bytesToPrint = context.getBytesToPrint();
		} else if (state == States.END) {
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
		}
		repaint();
	}

	public MainPanel() {
		state = States.EMPTY;
		setPreferredSize(new Dimension(AMIConstants.WIN_W, AMIConstants.WIN_H));
		this.add(mainLabel);
		hideAll();
	}

	@Override
	public void paint(Graphics graph) {
		super.paint(graph);

		if ((state == States.DRAW || state == States.DRAW_CONTROL)
				&& bytesToPrint != null) {

			if (state == States.DRAW) {
				graph.setColor(Context.currentFirstPixelColor);
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
				int g = (cnt < bytesToPrint.length && bytesToPrint[cnt] >= 0) ? bytesToPrint[cnt]
						: 0;
				cnt++;
				int b = (cnt < bytesToPrint.length && bytesToPrint[cnt] >= 0) ? bytesToPrint[cnt]
						: 0;
				cnt++;

				graph.setColor(new Color(r, g, b));
				graph.fillRect(x, y, 1, 1);

				x++;
				if (x >= AMIin.mainFrame.mainPanel.getWidth()) {
					x = 0;
					y++;
				}

			}

			graph.setColor(AMIConstants.LAST_PIXEL);
			graph.fillRect(AMIin.mainFrame.mainPanel.getWidth() - 1,
					AMIin.mainFrame.mainPanel.getHeight() - 1, 1, 1);

		}

	}

}
