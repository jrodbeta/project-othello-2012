package othello.controller;

import othello.ai.AIThread;
import othello.ai.GreedyAI;
import othello.ai.GreedyHeuristicAI;
import othello.ai.PluggableHeuristicAI;
import othello.ai.ReversiAI;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// a controller, where one player (BLACK) is a human, and the other (WHITE) is an AI

public class AIController implements Controller {
	private Board b; // state of the game (model for this controller)
	private Listener l; // listener for the game
	boolean active; // is the game still active

	AIThread aiThreads[] = new AIThread[2];

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

	private static String sideToString(Board b) {
		if (b.getActive() == Board.BLACK)
			return "Black";
		else
			return "White";
	}

	public void update() {
		l.setBoard(b);
		l.setMessage(" ");
		l.setTurn(sideToString(b) + " to move");
		l.setScore(b.getScore() + " - " + b.getOpponentScore());
		l.repaint();

		if (aiThreads[0] != null) {
			synchronized (AIThread.syncObject) {
				AIThread.syncObject.notifyAll();
			}
		}
	}

	private static String scoreToString(Board b) {
		int a = b.getScore(), o = b.getOpponentScore();
		return "" + (int) Math.max((double) a, (double) o) + " - "
				+ (int) Math.min((double) a, (double) o);
	}

	private static String winnerToString(Board b) {
		int winner = b.getWinning();
		if (winner == Board.BLACK)
			return "Black wins";
		else if (winner == Board.WHITE)
			return "White wins";
		else
			return "Black and White tie";
	}

	private void gameOver() {
		update();
		l.setMessage("Game over");
		l.setScore(scoreToString(b));
		l.setTurn(winnerToString(b));
		active = false;
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