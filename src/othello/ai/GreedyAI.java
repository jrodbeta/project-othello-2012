package othello.ai;
import java.awt.Point;

import othello.model.Board;

// very simple AI - return the board reflecting the move that captures the most pieces

public class GreedyAI extends ReversiAI
{

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	startTimer();
	  bestMove = null;
	  
    Board b = new Board(prev), best = null;

    int maxScore = MIN_SCORE;

    for(int j = 0; j < size; j++)
    {
      for(int i = 0; i < size; i++)
      {
        if(b.move(i, j)) // valid move
        {
        	int score = b.getScore();
          if(score > maxScore)
          {
            maxScore = score;
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
