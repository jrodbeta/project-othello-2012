package othello.ai;

import java.awt.Point;

import othello.controller.Controller;
import othello.model.Board;

public class AIThread extends Thread {
	private static final int MIN_WAIT = 1000;
	
	public static String syncObject = "Doh!";

	private int color;
	private Controller controller;
	private ReversiAI ai;

	public AIThread(ReversiAI ai, Controller controller, int color) {
		if (color == Board.WHITE) {
			setName("AIThread-WHITE");
		} else {
			setName("AIThread-BLACK");
		}

		this.controller = controller;
		this.color = color;
		this.ai = ai;

	}

	@Override
	public void run() {
		while (true) {

			// Wait on it's self
			// until notified by an outside source.
			synchronized (syncObject) {
				try {
					log("Waiting...");
					syncObject.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Check to make sure it's our turn.
			log("Woke up checking turn...");
			if (controller.getBoard().getActive() == color) {
				long start = System.currentTimeMillis();
				
				log("My turn making a move yo.");
				Point lastMove = controller.getBoard().getLastPlayerMove();
				if (lastMove == null) {
					lastMove = new Point(0, 0);
				}

				ai.nextMove(controller.getBoard(), lastMove.x, lastMove.y);
				// Successful move.

				long diff = System.currentTimeMillis() - start;
				long sleepTime = MIN_WAIT - diff;
				
				// Think about our move.
				if(sleepTime > 10) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}

				Point move = ai.getMove();
				log("Place piece at : " + move.toString());
				controller.move(move.x, move.y);

			} else {
				log("Not my turn.");
				// It's not this AI's turn so continue on.
				continue;
			}

		}
	}

	private void log(String msg) {
		System.out.println(getName() + "-" + msg);
	}

}
