/**
 * 
 */
package fr.amille.amiout.view;

import javax.swing.JFrame;

/**
 * @author amille
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	public MainPanel mainPanel = new MainPanel();

	public MainFrame() {
		this.getContentPane().add(mainPanel);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("AMIout");
	}

}
