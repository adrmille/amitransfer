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
public class WaitFile implements State {

	public final static WaitFile INSTANCE = new WaitFile();
	
	private WaitFile() {
	}
	@Override
	public void goNext(final Context context) {
		AMIin.mainFrame.mainPanel.changeState(MainPanel.States.WAITING_FILE, context);
	}

}
