package othello.ai;
import othello.model.Board;

// very simple AI - return the board reflecting the move that captures the most pieces

public class GreedyAI implements ReversiAI
{
  private int size;

  public void setSize(int size) { this.size = size; }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
    Board b = new Board(prev), best = null;

    int score = -1;

    for(int j = 0; j < size; j++)
    {
      for(int i = 0; i < size; i++)
      {
        if(b.move(i, j)) // valid move
        {
          if(b.getScore() > score)
          {
            score = b.getScore();
            best = b;
          }
          b = new Board(prev);
        }
      }
    }
    return best;
  }
}
