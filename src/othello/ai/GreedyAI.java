package othello.ai;

import java.util.Random;

import othello.model.Board;

// very simple AI - return the board reflecting the move that captures the most pieces

public class GreedyAI extends ReversiAI
{
  private Random r = new Random();
  
  public GreedyAI() { this(false); }
  
  public GreedyAI(boolean deterministic)
  {
  	if(deterministic) r = new Random(SEED);
  }
	
  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	startTimer();
	  setMove(-1,-1);
	  
    Board b = new Board(prev), best = null;

    int maxScore = MIN_SCORE;

    for(int j = 0; j < size; j++)
    {
      for(int i = 0; i < size; i++)
      {
        if(b.move(i, j)) // valid move
        {
        	movecount++;
        	int score = b.getScore();
          if(score > maxScore || (score == maxScore && r.nextDouble() < OVERRIDE))
          {
            maxScore = score;
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
