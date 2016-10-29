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
public class End implements State {

	public final static End INSTANCE = new End();

	private End() {
	}

	@Override
	public void goNext(final Context context) {

		final MainPanel mainPanel = AMIout.mainFrame.getMainPanel();

		mainPanel.createMainLabel("Finished");
		final JButton restartButton = mainPanel.createButton("Restart");

		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				context.backToStartState();
			}
		});
	}

}
