/**
 * 
 */
package fr.amille.amiin.states;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.amille.amiin.AMIin;
import fr.amille.amiin.view.MainPanel;
import fr.amille.amiin.view.MainPanel.States;
import fr.amille.amiin.view.MyAbstractSwingWorker;

/**
 * @author AMILLE
 * 
 */
public class ShowControlInfo implements State {

	public final static ShowControlInfo INSTANCE = new ShowControlInfo();

	private static KeyAdapter keyAdapter;

	private ShowControlInfo() {
	}

	@Override
	public void goNext(final Context context) {
		if (keyAdapter == null) {
			keyAdapter = new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == 'n') {
						AMIin.mainFrame.mainPanel.removeKeyListener(this);
						AMIin.mainFrame.mainPanel.changeState(States.EMPTY, context);
						new MyAbstractSwingWorker() {
							@Override
							public void whatToExecute() {
								context.setState(PrintBlocks.INSTANCE);
							}
						}.execute();
					}
				}
			};
		}
		AMIin.mainFrame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (!context.isEnded()) {
					AMIin.mainFrame.mainPanel.removeComponentListener(this);
					AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter);
					context.setState(ShowControlInfo.INSTANCE);
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				if (!context.isEnded()) {
					AMIin.mainFrame.mainPanel.removeComponentListener(this);
					AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter);
					context.setState(ShowControlInfo.INSTANCE);
				}
			}
		});
		if (context.getCurrentFile() == null) {
			System.err.println("Missing file in ShowControlInfo state");
			context.setState(null);
		}
		final StringBuilder fileInformations = new StringBuilder();
		fileInformations.append(context.getCurrentFile().getName());
		fileInformations.append("\n");
		fileInformations.append(context.getCurrentFile().length());
		fileInformations.append("\n");
		fileInformations.append(AMIin.mainFrame.mainPanel.getWidth());
		fileInformations.append("\n");
		fileInformations.append(AMIin.mainFrame.mainPanel.getHeight());
		fileInformations.append("\n");
		fileInformations.append(context.getPositionInFile());
		fileInformations.append("EOF_EOF");
		context.setBytesToPrint(fileInformations.toString());
		AMIin.mainFrame.mainPanel.changeState(MainPanel.States.DRAW_CONTROL,
				context);
		AMIin.mainFrame.mainPanel.removeKeyListener(keyAdapter);
		AMIin.mainFrame.mainPanel.addKeyListener(keyAdapter);
	}

}
