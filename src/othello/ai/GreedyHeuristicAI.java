package othello.ai;
import othello.model.Board;

public class GreedyHeuristicAI implements ReversiAI
{
	  private int size;
	  private int[][] weights = {{50, -1, 5, 2}, {-1, -10, 1, 1},
	                            {5, 1, 1, 1}, {2, 1, 1, 0}};

	  public void setSize(int size) { this.size = size; }

	  public int cost(int x, int y)
	  {
		  if(x > (size - 1) / 2) x = (size - 1) - x; // deal with symmetry
		  if(y > (size - 1) / 2) y = (size - 1) - y;
		  return weights[x][y];
	  }

	  public Board nextMove(Board prev, int lastx, int lasty)
	  {
		  Board b = new Board(prev), best = null;
		  Board temp;
		  
		  int mobility = 0;
		  int lowestMobility = 100;

		  for(int j = 0; j < size; j++)
		  {
			  for(int i = 0; i < size; i++)
			  {
				  if(b.move(i, j)) // valid move
				  {
					  temp = new Board(b);
					  
					  for(int c = 0; c < size; c++)
					  {
						  for(int d = 0; d < size; d++)
						  {
							  if(temp.move(d, c)) // valid move
							  {
								  mobility++;
							  }
							  
							  temp = new Board(b);
						  }
					  }
					  
					  if(mobility < lowestMobility) {
						  lowestMobility = mobility;
						  mobility = 0;
						  best = b;
					  }
					  
					  b = new Board(prev);
				  }
			  }
		  }

		  return best;
	  }
}

