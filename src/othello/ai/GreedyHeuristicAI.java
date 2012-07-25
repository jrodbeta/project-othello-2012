package othello.ai;
import java.awt.Point;
import java.util.*;

import othello.model.Board;

public class GreedyHeuristicAI extends ReversiAI
{
	
	private Random r = new Random();
	
	private int[][] weights =
		{{50,  -1, 5, 2}, 
		 {-1, -10, 1, 1},
	   {5,    1, 1, 1}, 
	   {2,    1, 1, 0}};

	public int cost(int x, int y)
  {
		if(x > (size - 1) / 2) x = (size - 1) - x; // deal with symmetry
		if(y > (size - 1) / 2) y = (size - 1) - y;
		return weights[x][y];
	}
	
	public GreedyHeuristicAI() { this(false); }
	
	public GreedyHeuristicAI(boolean deterministic)
	{
		if(deterministic) r = new Random(SEED);
	}

	public Board nextMove(Board prev, int lastx, int lasty)
	{
		startTimer();
	  Board b = new Board(prev), best = null;
		int color = b.getActive();
		int score = -1000;
		  
		for(int j = 0; j < size; j++)
		{
		  for(int i = 0; i < size; i++)
		  {
		    if(b.move(i, j)) // valid move
		    {
		      int newscore = 0;
		      
		      for(int y = 0; y < size; y++)
		      {
		        for(int x = 0; x < size; x++)
		        {
		          int state = b.getState(x, y);
		          if(state == color) newscore += cost(x, y);
		          else if(state != Board.EMPTY) newscore -= cost(x,y);
		        }
		      }
		          
		      if(newscore > score || (newscore == score && r.nextDouble() < OVERRIDE))
		      {
		        score = newscore;
		        best = b;
		        bestMove = new Point(i,j);
		      }
		      b = new Board(prev);
		    }
		  }
		}
		stopTimer();
		return best;
	}
}


