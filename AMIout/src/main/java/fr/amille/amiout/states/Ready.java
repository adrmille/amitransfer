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
public class Ready implements State {

	public final static Ready INSTANCE = new Ready();

	private Ready() {
	}

	@Override
	public void goNext(final Context context) {

		final MainPanel mainPanel = AMIout.mainFrame.getMainPanel();
		final JButton startButton = mainPanel.createButton("START");

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				context.setState(SearchingControlPixel.INSTANCE);
			}
		});
		startButton.setVisible(true);
	}

}
