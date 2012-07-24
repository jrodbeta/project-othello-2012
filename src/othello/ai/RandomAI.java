package othello.ai;

import java.awt.Point;
import java.util.Random;

import othello.model.Board;

// very simple AI - return the board reflecting the move that captures the most pieces

public class RandomAI implements ReversiAI
{
  private int size;
  private Point bestMove;
  private Random rand = new Random();
  
  private static final int MAX_TRIES = 256;

  public void setSize(int size) { this.size = size; }

  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	Board b = new Board(prev);
  	bestMove = null;
  	int i, j, c = 0;
  	
  	do // try random moves, until one works, or we exhaust MAX_TRIES attempts
  	{
  		i = rand.nextInt(8);
  		j = rand.nextInt(8);
  		c++;
  	} while(!b.move(i, j) && c < MAX_TRIES);
  	
  	System.out.println(c + " tries");
  	if(!b.equals(prev)) bestMove = new Point(i, j);
  	
  	else // if no moves succeeded, attempt iteratively to find a possible move
  	{
  		System.out.println("random moves failed");
  		for(j = 0; j < size; j++)
  		{
  			for(i = 0; i < size; i++)
  			{
  				if(b.move(i, j))
  				{
  					bestMove = new Point(i, j);
  					break;
  				}
  			}
  		}
  		if(!b.equals(prev)) b = null; // no move possible
  	}
  	
  	return b;
  }
  
  @Override
	public Point getMove() {
		return bestMove;
	}
}
