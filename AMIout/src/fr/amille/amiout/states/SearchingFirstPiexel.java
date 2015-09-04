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
public class SearchingFirstPiexel implements State {

	public final static SearchingFirstPiexel INSTANCE = new SearchingFirstPiexel();

	private SearchingFirstPiexel() {
	}

	@Override
	public void goNext(final Context context) {
		AMIout.mainFrame.mainPanel.changeState(
				MainPanel.States.LOOKING_DAT_PIXEL, context);
		try {
			context.findFirstPixel();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			context.backToStartState();
		}
	}

}
