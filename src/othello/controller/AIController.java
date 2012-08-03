package othello.controller;

import othello.ai.*;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// a controller, where both players are AI threads

public class AIController extends Controller {

	private AIThread aiThreads[] = new AIThread[2]; // each thread runs one AI

	private static int boardSize = BoardGUI.ROWS;
	
	public static void main(String args[]) {
		ReversiGUI gui = new ReversiGUI(false);
		
		ReversiAI aiWhite = new GreedyHeuristicAI();
		
		ReversiAI aiBlack = new PluggableHeuristicAI();
		
		AIController c = new AIController(gui, aiWhite, aiBlack);
		gui.setController(c);
	}

	public AIController(Listener l, ReversiAI aiWhite, ReversiAI aiBlack) {
		this.l = l;
		aiWhite.setSize(boardSize);
		aiBlack.setSize(boardSize);

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

		if (!b.move(x, y)) // invalid move
		{
			l.setMessage("Not a legal move");
			return;
		}
		b.turn(); // Next player's turn

		if (b.canMove()) // player can move
		{
			update();
			playerLog("Next player's turn.");
			return;
		}

		// player can't move, so switch to other player
		playerLog("Can't move so yielding.");
		b.turn();
		
		if (b.canMove()) // other player can move
		{
			update();
			return;
		}

		// no more moves possible, game over
		playerLog("No more possible moves so ending.");
		update();
		gameOver();
		return;
	}

	public Board getBoard() { return b;	}

	public void newGame() {
		active = true;
		b = new Board(BoardGUI.ROWS);

		update();
		l.setMessage("New game");
	}

	public void playerLog(String msg) {
		if(LOG_ENABLED) System.out.println(b.getActiveName() + "-" + msg);
	}
}