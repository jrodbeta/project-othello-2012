package othello.controller;
import othello.model.Board;
import othello.model.Listener;
import othello.view.BoardGUI;
import othello.view.ReversiGUI;

// controller for a two player reversi game

public class TwoPlayerController extends Controller
{
	
  public static void main(String args[])
  {
    ReversiGUI gui = new ReversiGUI(true);
    TwoPlayerController c = new TwoPlayerController(gui);
    gui.setController(c);
    c.update();
  }
  
  public TwoPlayerController(Listener l)
  {
    this.l = l;
    newGame();
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
    
    // fixme
    //System.out.println("player is " + b.getActiveName());
    //System.out.println("corners: " + b.getCornerCount(true) + " opponent: " + b.getCornerCount(false));
    //System.out.println("frontier discs:" + b.getFrontierCount(true) + " opponent: " + b.getFrontierCount(false));
    //System.out.println("empty corner neighbors:" + b.getEmptyCornerNeighbors(true) + " opponent: " + b.getEmptyCornerNeighbors(false));
    
    
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