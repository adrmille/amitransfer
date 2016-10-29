package fr.amille.amiout.service.screen;

public class ThreadingService {

	public void sleep(int milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
