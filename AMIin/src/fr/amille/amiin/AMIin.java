/**
 * 
 */
package fr.amille.amiin;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.FileNotFoundException;

import javax.swing.SwingUtilities;

import fr.amille.amiin.states.Context;
import fr.amille.amiin.view.MainFrame;

/**
 * @author amille
 * 
 */
public class AMIin {

	public static MainFrame mainFrame;

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainFrame = new MainFrame();
				AMIin.mainFrame.setVisible(true);
				final Context context = new Context();
				context.goNext();
			}
		});

	}

}
