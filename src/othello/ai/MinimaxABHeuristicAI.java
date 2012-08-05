package othello.ai;
import java.awt.Point;
import java.util.*;

import othello.model.Board;

// minimax based AI - uses AB pruning and a board-weight utility function
public class MinimaxABHeuristicAI extends ReversiAI
{
	private static final double OVERRIDE = 0.3;
	
  private int maxDepth;
  private int moves;
  private Random r = new Random();
  
  public MinimaxABHeuristicAI() { this(DEPTH, false); }
  
  public MinimaxABHeuristicAI(int depth, boolean deterministic)
  {
  	if(deterministic) r = new Random(SEED);
  	maxDepth = depth;
  }
  
  public int cost(int x, int y)
  {
	  if(x > (size - 1) / 2) x = (size - 1) - x; // deal with symmetry
	  if(y > (size - 1) / 2) y = (size - 1) - y;
	  return weights[x][y];
  }
  
  public int getCost(Board b)
  {
	  int color = b.getActive(), score = 0;
	  
  	for(int y = 0; y < size; y++)
    {
      for(int x = 0; x < size; x++)
      {
        int state = b.getState(x, y);
        if(state == color) score += cost(x, y);
        else if(state != Board.EMPTY) score -= cost(x,y);
      }
    }
  	return score;
  }
  
  private int[][] weights = {
			{50,  -1, 5, 2}, 
			{-1, -10, 1, 1},
                  {5,    1, 1, 1}, 
                  {2,    1, 1, 0}};
  
  private int minMove(Board prev, int depth, int alpha, int beta)
  {
  	moves++; // fixme
  	if(depth > maxDepth) return getCost(prev); // exceeded maximum depth
  	
  	int minScore = MAX_SCORE;
  	Board b = new Board(prev);
  	b.turn(); // min player's turn
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				b.turn(); // max player's turn
  				int score = maxMove(b, depth + 1, alpha, beta);
  				//printMsg(false, depth, score, i, j); // fixme
  				
  				if(score < minScore) minScore = score;
  				if(minScore <= alpha) return minScore;
  				if(minScore < beta) beta = minScore;
  				
  				b = new Board(prev);
  				b.turn();
  			}
  		}
  	}
  	
  	if(minScore == MAX_SCORE) // min player can't make a move
  	{
  		b.turn();
  		if(b.canMove()) return maxMove(b, depth + 1, alpha, beta); // max player can make a move
  		else return prev.getScore(); // max player can't make a move either - game over
  	}
  	
		return minScore;
  }
  
  private int maxMove(Board prev, int depth, int alpha, int beta)
  {
  	moves++; // fixme
  	if(depth > maxDepth) return getCost(prev); // exceeded maximum depth
  	
  	int maxScore = MIN_SCORE;
  	Board b = new Board(prev);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j)) // try move
  			{
  				int score = minMove(b, depth + 1, alpha, beta);
  				//printMsg(true, depth, score, i, j); // fixme
  				
  				if(score > maxScore)	maxScore = score;
  				if(maxScore >= beta) return maxScore;
  				if(maxScore > alpha) alpha = maxScore;
  				
  				b = new Board(prev);
  			}
  		}
  	}
  	
  	if(maxScore == MIN_SCORE) // no moves found
  	{
  		b.turn();
  		if(b.canMove()) { b.turn(); return minMove(b, depth + 1, alpha, beta); }
  		else return prev.getScore();
  	}
  	
		return maxScore;
  }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	startTimer();
  	moves = 0; // fixme
  	long start = System.currentTimeMillis();
  	int maxScore = MIN_SCORE;
  	int alpha = MIN_SCORE, beta = MAX_SCORE;
  	Board best = null, b = new Board(prev);
  	setMove(-1, -1);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				int score = minMove(b, 1, alpha, beta);
  				//printMsg(true, 0, score, i, j); // fixme
  				
  				if(score > maxScore || (score == maxScore && r.nextDouble() < OVERRIDE))
  				{
  					setMove(i,j);
  					maxScore = score;
  					best = b;
  				}
  				b = new Board(prev);
  			}
  		}
  	}
  	//System.out.println("elapsed: " + ((float)(System.currentTimeMillis()-start)/1000));
  	//System.out.println("ab " + b.getMoves() + " moves: " + moves);
  	stopTimer();
  	return best;
  }
}
