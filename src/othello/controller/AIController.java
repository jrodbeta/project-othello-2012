package othello.controller;

import othello.ai.*;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// a controller, where both players are AI threads

public class AIController extends Controller {

	private AIThread aiThreads[] = new AIThread[2]; // each thread runs one AI

	public static void main(String args[]) {
		ReversiGUI gui = new ReversiGUI(true);
		AIController c = new AIController(gui);
		gui.setController(c);

		c.update();
	}

	public AIController(Listener l) {
		this.l = l;
		newGame();

		ReversiAI aiWhite = new PluggableHeuristicAI();
		aiWhite.setSize(b.getSize());
		
		ReversiAI aiBlack = new PluggableHeuristicAI();
		aiBlack.setSize(b.getSize());

		aiThreads[0] = new AIThread(aiWhite, this, Board.WHITE);
		aiThreads[0].start();

		aiThreads[1] = new AIThread(aiBlack, this, Board.BLACK);
		synchronized(aiThreads[1]) {
			// Black goes first so wait for thread to initialize.
			aiThreads[1].start();			
			try {
				aiThreads[1].wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void update() {
		super.update();

		if (aiThreads[0] != null) {
			synchronized (AIThread.syncObject) {
				AIThread.syncObject.notifyAll();
			}
		}
	}

	public void move(int x, int y) {
		if (!active)
			return;

		if (!b.move(x, y)) /* invalid move */
		{
			l.setMessage("Not a legal move");
			return;
		}
		b.turn(); // Next players turn

		// if we get here, AI can't move
		if (b.canMove()) {
			update();
			playerLog("Next player's turn.");
			return;
		} else {
			playerLog("Can't move so yielding.");
			b.turn();
			if (b.canMove()) {
				update();
				return;
			}
		}

		System.out.println("No more possible moves so ending.");
		/* no more moves possible, game over */
		update();
		gameOver();
		return;
	}

	public Board getBoard() {
		return b;
	}

	public void newGame() {
		active = true;
		b = new Board(BoardGUI.ROWS);

		update();
		l.setMessage("New game");
	}

	public void playerLog(String msg) {
		System.out.println(b.getActiveName() + "-" + msg);
	}
}