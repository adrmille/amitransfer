/**
 * 
 */
package fr.amille.amiout.view;

import javax.swing.JFrame;

import fr.amille.amiout.constant.AMIConstants;

/**
 * @author amille
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private MainPanel mainPanel;

	public MainFrame() {
		renewMainPanel();
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("AMIout");
	}

	public void renewMainPanel() {
		if (mainPanel != null) {
			this.getContentPane().remove(mainPanel);
		}
		final int winW = (this.getContentPane().getWidth() != 0) ? this.getContentPane().getWidth()
				: AMIConstants.WIN_W;
		final int winH = (this.getContentPane().getHeight() != 0) ? this.getContentPane().getHeight()
				: AMIConstants.WIN_H;
		mainPanel = new MainPanel(winW, winH);
		this.getContentPane().add(mainPanel);
		mainPanel.setVisible(true);
		this.pack();
	}

	public MainPanel getMainPanel() {
		renewMainPanel();
		return mainPanel;
	}

}
