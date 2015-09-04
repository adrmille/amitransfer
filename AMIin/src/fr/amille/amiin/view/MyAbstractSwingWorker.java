package fr.amille.amiin.view;

import javax.swing.SwingWorker;

public abstract class MyAbstractSwingWorker {

	public static final int SLEEP_TIME_MS = 50;

	public void execute() {
		final SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					Thread.sleep(MyAbstractSwingWorker.SLEEP_TIME_MS);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				} finally {
					whatToExecute();
				}
				return null;
			}
		};
		swingWorker.execute();
	}

	public abstract void whatToExecute();

}
