package othello.ai;
import java.awt.Point;
import java.util.*;

import othello.model.Board;

// Minimax search, with alpha-beta pruning
// uses the multi-cut prob method

// first explore the tree to a fixed depth
// then prune unpromising candidates early before doing a full exploration
public class MinimaxABAIMulticut extends ReversiAI
{
  private int maxDepth;
  private int moves;
  
  private final int INIT_DEPTH = 4; // initial depth
  
  private Random r = new Random();
  
  public MinimaxABAIMulticut() { this(DEPTH, false); }
  
  public MinimaxABAIMulticut(int depth, boolean deterministic)
  {
  	maxDepth = depth;
  	if(deterministic) r = new Random(SEED);
  }
  
  
  private int minMove(Board prev, int depth, int alpha, int beta)
  {
  	moves++; // fixme
  	if(depth >= maxDepth) return prev.getScore(); // exceeded maximum depth
  	
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
  	moves++;
  	if(depth >= maxDepth) return prev.getScore(); // exceeded maximum depth
  	
  	int maxScore = MIN_SCORE;
  	Board b = new Board(prev);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j)) // try move
  			{
  				int score = minMove(b, depth + 1, alpha, beta);
  				
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
  
  public Board tryToDepth(Board prev, int depth)
  {
  	maxDepth = depth;
  	int maxScore = MIN_SCORE;
  	int alpha = MIN_SCORE, beta = MAX_SCORE;
  	Board best = null, b = new Board(prev);
  	setMove(-1,-1);
  	
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				int score = minMove(b, 1, alpha, beta);
  				
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
  	
  	return best;  	
  }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	
    startTimer();
    
    moves = 0;
  	
  	PriorityQueue<ScoredMove> pq = new PriorityQueue<ScoredMove>(10,
  			new Comparator<ScoredMove>() {
  				public int compare(ScoredMove l, ScoredMove r) { return (int)(r.score - l.score); }
  		}
  	);
  	
  	int maxScore = MIN_SCORE, depth = maxDepth;
  	int alpha = MIN_SCORE, beta = MAX_SCORE;
  	Board best = null, b = new Board(prev);
  	setMove(-1,-1);
  	
  	// initial exploration
  	maxDepth = INIT_DEPTH;
  	for(int j = 0; j < size; j++)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			if(b.move(i, j))
  			{
  				int score = minMove(b, 1, alpha, beta);
  				pq.add(new ScoredMove(i, j, score));
  				
  				b = new Board(prev);
  			}
  		}
  	}
  	
  	maxDepth = depth;
  	maxScore = MIN_SCORE;
  	b = new Board(prev);
  	
  	ScoredMove m;
  	while((m = pq.poll()) != null && m.score > -100)
  	{
  		b.move(m.move.x, m.move.y);
  		int score = minMove(b, 1, alpha, beta);
  		
  		if(score > maxScore || (score == maxScore && r.nextDouble() < OVERRIDE))
  		{
  			setMove(m.move.x, m.move.y);
  			maxScore = score;
  			best = b;
  		}
  		b = new Board(prev);
  	}
  	
  	
  	// accidentally pruned too much - oh well
  	if(best == null)
  	{
  		for(int i = 0; i < size; i++)
  		{
  			for(int j = 0; j < size; j++)
  			{
  				if(b.move(i,j))
  				{
  					setMove(i, j);
  					stopTimer();
  					return b;
  				}
  			}
  		}
  	}
  	//System.out.println("multicut " + b.getMoves() + " moves: " + moves);
    movecount += moves;
    stopTimer();
    	
    return best;
  }
  
  protected class ScoredMove
  {
  	public Point move;
  	public Double score;
  	
  	public ScoredMove(int x, int y, double d) { move = new Point(x, y); score = d; }
  }
}
