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
public class ReadControl implements State {

	public final static ReadControl INSTANCE = new ReadControl();

	private ReadControl() {
	}

	@Override
	public void goNext(final Context context) {
		AMIout.mainFrame.mainPanel.changeState(MainPanel.States.READ_CONTROL, context);
		context.updateFileInformations();
	}

}
