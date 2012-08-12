package othello.ai;
import java.util.*;

import othello.model.Board;

// Simple minimax AI search where utility function is just the value of the board
public class MinimaxAI extends ReversiAI
{
  private int maxDepth;
  private int moves;
  
  private Random r = new Random();
  
  public MinimaxAI() { this(DEPTH, false); }
  
  public MinimaxAI(int depth, boolean deterministic)
  {
  	maxDepth = depth;
  	if(deterministic) r = new Random(SEED);
  }
  
  private int minMove(Board prev, int depth)
  {
  	moves++;
  	if(depth > maxDepth) return prev.getScore(); // exceeded maximum depth
  	
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
  				int score = maxMove(b, depth + 1);
  				//printMsg(false, depth, score, i, j); // fixme
  				
  				if(score < minScore) minScore = score;
  				
  				b = new Board(prev);
  				b.turn();
  			}
  		}
  	}
  	
  	if(minScore == MAX_SCORE) // min player can't make a move
  	{
  		b.turn();
  		if(b.canMove()) return maxMove(b, depth + 1); // max player can make a move
  		else return prev.getScore(); // max player can't make a move either - game over
  	}
  	
		return minScore;
  }
  
  private int maxMove(Board prev, int depth)
  {
  	moves++;
  	if(depth > maxDepth) return prev.getScore(); // exceeded maximum depth
  	
  	int maxScore = MIN_SCORE;
  	Board b = new Board(prev);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j)) // try move
  			{
  				int score = minMove(b, depth + 1);
  				//printMsg(true, depth, score, i, j); // fixme
  				
  				if(score > maxScore)	maxScore = score;
  				
  				b = new Board(prev);
  			}
  		}
  	}
  	
  	if(maxScore == MIN_SCORE) // no moves found
  	{
  		b.turn();
  		if(b.canMove()) { b.turn(); return minMove(b, depth + 1); }
  		else return prev.getScore();
  	}
  	
		return maxScore;
  }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	startTimer();
  	moves = 0;
  	//long start = System.currentTimeMillis();
  	int maxScore = MIN_SCORE;
  	Board best = null, b = new Board(prev);
  	setMove(-1, -1);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				int score = minMove(b, 1);
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
  	//System.out.println("moves: " + moves);
  	movecount += moves;
  	stopTimer();
  	return best;
  }
  
  /*private int printMsg(boolean max, int depth, int score, int x, int y)
  {
  	for(int i = 0; i < depth; i++) System.out.print("  ");
  	if(max) System.out.print("max ");
  	else System.out.print("min ");
  	System.out.println("(" + x + "," + y + "): " + score);
  	return score;
  }*/
}
