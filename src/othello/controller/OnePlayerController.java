package othello.controller;
import othello.ai.GreedyAI;
import othello.ai.GreedyHeuristicAI;
import othello.ai.ReversiAI;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// a controller, where one player (BLACK) is a human, and the other (WHITE) is an AI

public class OnePlayerController implements Controller
{
	private Board b;        // state of the game (model for this controller)
	private Listener l;     // listener for the game
	boolean active;         // is the game still active
	ReversiAI r;            // AI to control the non-human player

	public static void main(String args[])
	{
		ReversiGUI gui = new ReversiGUI();
		OnePlayerController c = new OnePlayerController(gui);
		gui.setController(c);
		c.update();
	}

	public OnePlayerController(Listener l)
	{
		this.l = l;
		newGame();
	}

	private static String sideToString(Board b)
	{
		if(b.getActive() == Board.BLACK) return "Black";
		else return "White";
	}

	public void update()
	{
		l.setBoard(b);
		l.setMessage(" ");
		l.setTurn(sideToString(b) + " to move");
		l.setScore(b.getScore() + " - " + b.getOpponentScore());
		l.repaint();
	}

	private static String scoreToString(Board b)
	{
		int a = b.getScore(), o = b.getOpponentScore();
		return "" + (int)Math.max((double)a, (double)o) + " - " + (int)Math.min((double)a, (double)o);
	}

	private static String winnerToString(Board b)
	{
		int winner = b.getWinning();
		if(winner == Board.BLACK) return "Black wins";
		else if(winner == Board.WHITE) return "White wins";
		else return "Black and White tie";
	}

	private void gameOver()
	{
		update();
		l.setMessage("Game over");
		l.setScore(scoreToString(b));
		l.setTurn(winnerToString(b));
		active = false;
	}

	public void move(int x, int y)
	{
		if(!active) return;

		if(!b.move(x, y)) /* invalid move */
		{
			l.setMessage("Not a legal move");
			return;
		}
		b.turn(); // AI's move

		Board c;
		while((c = r.nextMove(b, x, y)) != null)
		{
			System.out.println("AI");
			//try {
			//  Thread.currentThread().sleep(500);
			//} catch(InterruptedException e) { }
			b = c; // have a valid AI move
			b.turn(); // human's turn
			update();
			if(!b.canMove()) // if human can't move
			{
				l.setMessage("No possible moves for " + sideToString(b));
				// fixme pause
				b.turn();
			}
			else { System.out.println("human"); return; }

		}

		// if we get here,  AI can't move
		b.turn(); // human's turn
		if(b.canMove())
		{
			update();
			System.out.println("human");
			return;
		}

		/* no more moves possible, game over */
		update();
		gameOver();
		return;
	}

	public void newGame()
	{
		active = true;
		b = new Board(BoardGUI.ROWS);
		r = new GreedyHeuristicAI();
		r.setSize(b.getSize());
		update();
		l.setMessage("New game");
	}
}