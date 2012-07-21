package othello.ai;
import othello.model.Board;

public class GreedyHeuristicAI implements ReversiAI
	{
	  private int size;
	  private int[][] weights = {{50, -1, 5, 2}, {-1, -10, 1, 1},
	                            {5, 1, 1, 1}, {2, 1, 1, 0}};

	  public void setSize(int size) { this.size = size; }

	  public int cost(int x, int y)
	  {
	    if(x > (size - 1) / 2) x = (size - 1) - x; // deal with symmetry
	    if(y > (size - 1) / 2) y = (size - 1) - y;
	    return weights[x][y];
	  }

	  public Board nextMove(Board prev, int lastx, int lasty)
	  {
	    Board b = new Board(prev), best = null;

	    int score = -1000;
	    int color = b.getActive();

	    for(int j = 0; j < size; j++)
	    {
	      for(int i = 0; i < size; i++)
	      {
	        if(b.move(i, j)) // valid move
	        {
	          int newscore = 0;
	          
	          for(int y = 0; y < 8; y++)
	          {
	            for(int x = 0; x < 8; x++)
	            {
	              int state = b.getState(x, y);
	              if(state == color) newscore += cost(x, y);
	              else if(state != Board.EMPTY) newscore -= cost(x,y);
	            }
	          }
	          
	          if(newscore > score)
	          {
	            score = newscore;
	            best = b;
	          }
	          b = new Board(prev);
	        }
	      }
	    }
	    return best;
	  }
	}

