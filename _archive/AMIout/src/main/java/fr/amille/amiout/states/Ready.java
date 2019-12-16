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
public class Ready implements State {

	public final static Ready INSTANCE = new Ready();

	private Ready() {
	}

	@Override
	public void goNext(final Context context) {
		AMIout.mainFrame.mainPanel.changeState(MainPanel.States.START, context);
	}

}
