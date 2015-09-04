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
public class End implements State {

	public final static End INSTANCE = new End();

	private End() {
	}

	@Override
	public void goNext(final Context context) {
		AMIout.mainFrame.mainPanel.changeState(MainPanel.States.END, context);
	}

}
