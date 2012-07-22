package othello.ai;
import othello.model.Board;

// very simple AI - return the board reflecting the move that captures the most pieces

public class SimpleMinimaxAI implements ReversiAI
{
	public static final int MAX_DEPTH = 5;
	public static final int SCORE_MAX = 10000;
	
  private int size;
  
  public void setSize(int size) { this.size = size; }
  
  private int minMove(Board prev, int depth)
  {
  	// assumption:  it is min player's turn
  	if(!prev.canMove() || depth > MAX_DEPTH) return prev.getOpponentScore();
  	
  	int minScore = SCORE_MAX;
  	Board b = new Board(prev);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				b.turn(); // max player's turn
  				int score = maxMove(b, depth + 1);
  				
  				if(score < minScore) minScore = score;
  				
  				b = new Board(prev);
  			}
  		}
  	}
		return minScore;
  }
  
  private int maxMove(Board prev, int depth)
  {
  	// assumption:  it is max player's turn
  	if(!prev.canMove() || depth > MAX_DEPTH) return prev.getScore();
  	
  	int maxScore = -SCORE_MAX;
  	Board b = new Board(prev);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j)) // try move
  			{
  				b.turn(); // min player's turn
  				int score = minMove(b, depth + 1);
  				
  				if(score > maxScore)	maxScore = score;
  				
  				b = new Board(prev);
  			}
  		}
  	}
		return maxScore;
  }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	if(!prev.canMove()) return null;
  	
  	int maxScore = -SCORE_MAX;
  	Board best = null, b = new Board(prev);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				b.turn(); // now it's min player's turn
  				int score = minMove(b, 1);
  				if(score > maxScore)
  				{
  					maxScore = score;
  					best = b;
  				}
  				b = new Board(prev);
  			}
  		}
  	}
  	best.turn();
		return best;
  }
}
