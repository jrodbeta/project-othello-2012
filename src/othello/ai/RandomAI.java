package othello.ai;

import java.util.Random;

import othello.model.Board;

// very simple AI - make a random move
public class RandomAI extends ReversiAI
{
  private Random r = new Random();
  
  private static final int MAX_TRIES = 128;

  public RandomAI() { this(false); }
  
  public RandomAI(boolean deterministic)
  {
  	if(deterministic) r = new Random(SEED);
  }
  
  public Board nextMove(Board prev, int lastx, int lasty)
  {
  	startTimer();
  	Board b = new Board(prev);
  	setMove(-1, -1);
  	int i, j, c = 0;
  	
  	do // try random moves, until one works, or we exhaust MAX_TRIES attempts
  	{
  		i = r.nextInt(8);
  		j = r.nextInt(8);
  		c++;
  	} while(!b.move(i, j) && c < MAX_TRIES);
  	
  	if(!b.equals(prev))
  	{
  		setMove(i, j);
  		stopTimer();
  		return b;
  	}
  	
  	else // if no moves succeeded, attempt iteratively to find a possible move
  	{
  		for(j = 0; j < size; j++)
  		{
  			for(i = 0; i < size; i++)
  			{
  				if(b.move(i, j))
  				{
  					setMove(i, j);
  					stopTimer();
  					return b;
  				}
  			}
  		}
  	}
  	
  	stopTimer();
  	return null;
  }
  
}
