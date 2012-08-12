package othello.ai;

import java.util.*;

import othello.model.Board;

public class GreedyHeuristicAI extends ReversiAI
{
	
	private Random r = new Random();
	
	private int[][] weights = { 
			{ 20,  -3,  5,  2 }, 
			{ -3,  -7, -4,  1 },
			{  11, -4,  2,  2 }, 
			{  8,   1,  2, -3 } };

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
		setMove(-1, -1);
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
		        setMove(i,j);
		      }
		      b = new Board(prev);
		    }
		  }
		}
		stopTimer();
		return best;
	}
}


