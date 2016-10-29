/**
 * 
 */
package fr.amille.amiout.states;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fr.amille.amiout.AMIout;
import fr.amille.amiout.view.MainPanel;

/**
 * @author AMILLE
 * 
 */
public class Pause implements State {

	public final static Pause INSTANCE = new Pause();

	private Pause() {
	}

	@Override
	public void goNext(final Context context) {

		final MainPanel mainPanel = AMIout.mainFrame.getMainPanel();

		final JButton resumeButton = mainPanel.createButton("Mouse moved, RESUME !");
		resumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				context.setState(SearchingControlPixel.INSTANCE);
			}
		});
	}

}
