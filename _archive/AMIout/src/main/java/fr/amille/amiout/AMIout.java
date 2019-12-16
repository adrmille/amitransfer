/**
 * 
 */
package fr.amille.amiout;

import javax.swing.SwingUtilities;

import fr.amille.amiout.states.Context;
import fr.amille.amiout.view.MainFrame;

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
				final Context context = new Context();
				context.goNext();
			}
		});

	}

}
