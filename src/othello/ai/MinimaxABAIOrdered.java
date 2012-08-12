package othello.ai;
import java.awt.Point;
import java.util.*;

import othello.model.Board;

// Minimax search, with alpha-beta pruning
// reorder branches for pruning
public class MinimaxABAIOrdered extends ReversiAI
{
  private int maxDepth;
  private int moves;
  
  private Random r = new Random();
  
  public MinimaxABAIOrdered() { this(DEPTH, false); }
  
  public MinimaxABAIOrdered(int depth, boolean deterministic)
  {
  	maxDepth = depth;
  	if(deterministic) r = new Random(SEED);
  }
  
  private int minMove(Board prev, int depth, int alpha, int beta)
  {
  	moves++;
  	if(depth >= maxDepth) return prev.getScore(); // exceeded maximum depth
  	
  	int minScore = MAX_SCORE;
  	Board b = new Board(prev);
  	Point[] moves = moveList(prev.getSize());
  	
  	b.turn(); // min player's turn
  	
  	for(int i = 0; i < moves.length; i++)
  	{
  		Point m = moves[i];
  		if(b.move(m.x, m.y))
  		{
  			b.turn(); // max player's turn
  			int score = maxMove(b, depth + 1, alpha, beta);
  				
  			if(score < minScore) minScore = score;
  			if(minScore <= alpha) return minScore;
  			if(minScore < beta) beta = minScore;
  				
  			b = new Board(prev);
  			b.turn();
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
  	moves++;
  	if(depth >= maxDepth) return prev.getScore(); // exceeded maximum depth
  	
  	int maxScore = MIN_SCORE;
  	Board b = new Board(prev);
  	Point[] moves = moveList(prev.getSize());
  	
  	for(int i = 0; i < moves.length; i++)
  	{
  		Point m = moves[i];
  		if(b.move(m.x, m.y)) // try move
  		{
  			int score = minMove(b, depth + 1, alpha, beta);
  				
  			if(score > maxScore)	maxScore = score;
  			if(maxScore >= beta) return maxScore;
  			if(maxScore > alpha) alpha = maxScore;
  				
  			b = new Board(prev);
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
  
  public static final Point[] moveList(int size)
  {
  	final Point moves[] = new Point[size * size];
  	int k;
  	
  	moves[0] = new Point(0,0);
  	moves[1] = new Point(0, size - 1);
  	moves[2] = new Point(size - 1, 0);
  	moves[3] = new Point(size - 1, size - 1);
  	
  	k = 4;
  	for(int i = 1; i < size - 1; i++, k+=4) 
  	{
  		moves[k] = new Point(0, i);
  		moves[k+1] = new Point(i, 0);
  		moves[k+2] = new Point(size - 1, i);
  		moves[k+3] = new Point(i, size - 1);
  	}
  	
  	for(int j = 1; j < size - 1; j++)
  	{
  		for(int i = 1; i < size - 1; i++, k++)
  		{
  			moves[k] = new Point(i, j);
  		}
  	}
  	
  	return moves;
  }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	startTimer();
  	moves = 0;
  	
  	//long start = System.currentTimeMillis();
  	int maxScore = MIN_SCORE;
  	int alpha = MIN_SCORE, beta = MAX_SCORE;
  	Board best = null, b = new Board(prev);
  	setMove(-1,-1);
  	
  	Point[] moveorder = moveList(prev.getSize());
  	
  	for(int i = 0; i < moveorder.length; i++)
  	{
  		Point m = moveorder[i];
  		
  		if(b.move(m.x, m.y))
  		{
  			int score = minMove(b, 1, alpha, beta);
  		
  			if(score > maxScore || (score == maxScore && r.nextDouble() < OVERRIDE))
  			{
  				setMove(m.x, m.y);
  				maxScore = score;
  				best = b;
  			}
  		
  			b = new Board(prev);
  		}
  	}
  	movecount = moves;
  	
  	//System.out.println("moves: " + moves);
  	stopTimer();
  	return best;
  }
}
