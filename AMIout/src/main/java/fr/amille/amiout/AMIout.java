/**
 * 
 */
package fr.amille.amiout;

import javax.swing.SwingUtilities;

import fr.amille.amiout.states.Context;
import fr.amille.amiout.view.MainFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author amille
 * 
 */
public class AMIout {

	public static MainFrame mainFrame;

	public AMIout() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainFrame = new MainFrame();
				AMIout.mainFrame.setVisible(true);
				AMIout.mainFrame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
				final Context context = new Context();
				try {
					context.setState(null);
				} catch (Exception e) {
					context.backToStartState();
				}
			}
		});

	}

}
