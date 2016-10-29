package fr.amille.amiout.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;

import javax.swing.JButton;
import javax.swing.JLabel;

import fr.amille.amiout.AMIout;

@SuppressWarnings("serial")
public class MainPanel extends Panel {

	private JLabel mainLabel;

	public MainPanel(int w, int h) {
		setPreferredSize(new Dimension(w, h));
	}

	public JLabel createMainLabel(String text) {
		if (mainLabel != null) {
			mainLabel.setText(text);
		} else {
			mainLabel = new JLabel(text);
		}
		this.add(mainLabel);
		mainLabel.setVisible(true);
		refreshPanel();
		return mainLabel;
	}

	public JButton createButton(final String label) {
		final JButton jButton = new JButton(label);
		this.add(jButton);
		jButton.setVisible(true);
		refreshPanel();
		return jButton;
	}

	public void refreshPanel() {
		AMIout.mainFrame.revalidate();
		AMIout.mainFrame.repaint();
		this.revalidate();
		this.repaint();
	}

	@Override
	public void paint(Graphics graph) {
		super.paint(graph);
	}

}
