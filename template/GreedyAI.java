// very simple AI - return the board reflecting the move that captures the most pieces

public class GreedyAI implements ReversiAI
{
  public Board nextMove(Board prev, int lastx, int lasty)
  {
    Board b = new Board(prev), best = null;

    int score = -1;
    int size = b.getSize();

    for(int i = 0; i < size; i++)
    {
      for(int j = 0; j < size; j++)
      {
        if(b.move(i,j)) // valid move
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