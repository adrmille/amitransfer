/**
 * 
 */
package fr.amille.amiout.states;

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

		AMIout.mainFrame.mainPanel.changeState(MainPanel.States.PAUSE, context);

	}

}
