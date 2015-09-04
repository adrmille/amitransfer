/**
 * 
 */
package fr.amille.amiin.states;

import fr.amille.amiin.AMIin;
import fr.amille.amiin.view.MainPanel;

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
		AMIin.mainFrame.mainPanel.changeState(MainPanel.States.END, context);
	}

}
