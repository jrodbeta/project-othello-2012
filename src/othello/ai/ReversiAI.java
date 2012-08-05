package othello.ai;

import java.awt.Point;

import othello.model.Board;

// simple interface for an AI - given the board, and the last
// move of the human player, return a board with the next move of the AI

public abstract class ReversiAI
{

	public interface Types {
		String GREEDY = "Greedy";
		String HEURISTIC = "Heuristic";
		String PLUGGABLE = "Pluggable";
		String MINIMAX = "Minimax";
		String MINIMAX_AB = "Minimax AB";
		String MINIMAX_AB_HEU = "Minimax AB w/ Heuristic";
		
	}
	
	public static final int MAX_SCORE = 1000000;
	public static final int MIN_SCORE = -1000000;

	private static final int BAD_TIME = -1000;
	
	protected static final double OVERRIDE = 0.3;
	protected static final int DEPTH = 3;
	protected static final int SEED = 1000;
	
	protected int size;
	private Point bestMove;
	protected double elapsed = 0.0;
	protected long start = BAD_TIME;
	
  public void setSize(int size) { this.size = size; }
  public Point getMove() { return bestMove; }
  protected void setMove(int x, int y) { if(x == -1) bestMove = null; else bestMove = new Point(x, y); }
  protected void setMove(Point p) { bestMove = p; }
  
  protected void startTimer() { start = System.currentTimeMillis(); }
  protected void stopTimer()
  {
  	if(start != BAD_TIME)
  	{
  		elapsed += ((double)(System.currentTimeMillis() - start) / 1000);
  		start = BAD_TIME;
  	}
  }
  public double getElapsedTime() { return elapsed; }
  
  public abstract Board nextMove(Board prev, int lastx, int lasty); // return board with the next move (passing in coordinates of the previous move
}