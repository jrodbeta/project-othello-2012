package othello.ai;

import othello.model.Board;

public class MobilityHeuristicAI extends ReversiAI {


	public Board nextMove(Board prev, int lastx, int lasty)
	{
		startTimer();
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
		stopTimer();
	  return best;
	}
}
