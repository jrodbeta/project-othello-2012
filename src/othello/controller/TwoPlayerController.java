package othello.controller;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// controller for a two player reversi game

public class TwoPlayerController implements Controller
{
  private Board b;
  private Listener l;
  boolean active;
  
  public static void main(String args[])
  {
    ReversiGUI gui = new ReversiGUI();
    TwoPlayerController c = new TwoPlayerController(gui);
    gui.setController(c);
    c.update();
  }
  
  public TwoPlayerController(Listener l)
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
    if(!active) return; /* game is no longer active - no need to check moves */
    
    if(!b.move(x, y)) /* invalid move */
    {
      l.setMessage("Not a legal move");
      return;
    }
  
    b.turn(); /* switch to next player's turn */
    
    if(b.canMove()) /* if  player can move, let them */
    {
      update();
      return;
    }
    
    /* next player can't move */
    l.setMessage("No possible moves for " + sideToString(b));
    b.turn();
    
    if(b.canMove()) /* but original player has a possible move */
    {
      update();
      return;
    }
    
    /* no more moves possible, game over */
    gameOver();
    return;
  }
  
  public void newGame()
  {
    active = true;
    b = new Board(BoardGUI.ROWS);
    update();
    l.setMessage("New game");
  }

	@Override
	public Board getBoard() {
		return b;
	}
}