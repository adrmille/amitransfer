package fr.amille.amiout.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import fr.amille.amiout.constant.AMIConstants;
import fr.amille.amiout.states.Context;
import fr.amille.amiout.states.Resume;
import fr.amille.amiout.states.SearchingFirstPiexel;

@SuppressWarnings("serial")
public class MainPanel extends Panel {

	public static enum States {
		EMPTY, START, LOOKING_DAT_PIXEL, READ_CONTROL, READ_BLOCK, PAUSE, END
	}

	private States state;

	private JLabel mainLabel = new JLabel();

	private JButton startButton;

	private JButton resumeButton;

	private JButton restartButton;

	private void hideAll() {
		// THINK ABOUT ME
		mainLabel.setVisible(false);
		if (startButton != null)
			startButton.setVisible(false);
		if (resumeButton != null)
			resumeButton.setVisible(false);
		if (restartButton != null)
			restartButton.setVisible(false);
	}

	public void changeState(States newState, final Context context) {
		hideAll();
		state = newState;
		if (States.START == state) {
			if (startButton == null) {
				startButton = new JButton("START");
				this.add(startButton);
				startButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						context.setState(SearchingFirstPiexel.INSTANCE);
					}
				});
			}
			startButton.setVisible(true);
		} else if (States.LOOKING_DAT_PIXEL == state) {
			final int percent = (context.totalWroteBytes * 100) / context.fileSize;
			mainLabel.setText("Looking for dat pixel (" + percent + "% already done)");
			mainLabel.setVisible(true);
		} else if (States.READ_CONTROL == state) {
			mainLabel.setText("Reading control");
			mainLabel.setVisible(true);
		} else if (States.READ_BLOCK == state) {
			final int percent = (context.totalWroteBytes * 100) / context.fileSize;
			mainLabel.setText("Reading blocks (" + percent + "% already done)");
			mainLabel.setVisible(true);
		} else if (States.PAUSE == state) {
			if (resumeButton == null) {
				resumeButton = new JButton("Mouse moved, RESUME !");
				this.add(resumeButton);
				resumeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						context.setState(Resume.INSTANCE);
					}
				});
			}
			resumeButton.setVisible(true);
		} else if (States.END == state) {
			mainLabel.setText("Finished");
			mainLabel.setVisible(true);
			if (restartButton == null) {
				restartButton = new JButton("Restart");
				this.add(restartButton);
				restartButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						context.setState(null);
					}
				});
			}
			restartButton.setVisible(true);
		}
		validate();
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

	}

}
