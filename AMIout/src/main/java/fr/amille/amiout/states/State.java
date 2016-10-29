/**
 * 
 */
package fr.amille.amiout.states;

/**
 * @author AMILLE
 *
 */
public interface State {

	/**
	 * Execute and go next state.
	 * 
	 * @param context
	 */
	public void goNext(Context context);

}
