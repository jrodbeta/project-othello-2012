package othello.ai;
import java.awt.Point;

import othello.model.Board;

// A depth-limited minimax search, with alpha-beta pruning
public class SimpleMinimaxPrunedAI extends ReversiAI
{
  private int maxDepth;
  private int moves;
  
  public SimpleMinimaxPrunedAI(int depth) { maxDepth = depth; }
  
  private int minMove(Board prev, int depth, int alpha, int beta)
  {
  	moves++; // fixme
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
  	if(depth > maxDepth) return prev.getScore(); // exceeded maximum depth
  	
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
  	bestMove = null;
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				int score = minMove(b, 1, alpha, beta);
  				//printMsg(true, 0, score, i, j); // fixme
  				
  				if(score > maxScore)
  				{
  					bestMove = new Point(i,j);
  					maxScore = score;
  					best = b;
  				}
  				b = new Board(prev);
  			}
  		}
  	}
  	//System.out.println("elapsed: " + ((float)(System.currentTimeMillis()-start)/1000));
  	//System.out.println("moves: " + moves);
  	stopTimer();
  	return best;
  }
}
