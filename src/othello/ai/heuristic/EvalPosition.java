package othello.ai.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import othello.model.Board;

public class EvalPosition implements Heuristic {

	private int size;

	private int[][] weights = { 
			{ 20,  -3,  5,  2 }, 
			{ -3,  -7, -4,  1 },
			{  11, -4,  2,  2 }, 
			{  8,   1,  2, -3 } };


//	private int[][] weights = { 
//			{  0,  -2,  -3,  -4  }, 
//			{ -2,  -11, -14, -16 },
//			{ -3,  -14, -24, -30 }, 
//			{ -4,  -16, -30, -48 } };
	/*
Greedy vs Greedy
0		1856	2909	3720	
1852	10779	14256	15857	
3000	14098	24297	28839	
3939	16229	30011	47612	

-0	-2	-3	-4	
-2	-11	-14	-16	
-3	-14	-24	-29	
-4	-16	-30	-48
	 */


	public int cost(int x, int y) {
		if (x > (size - 1) / 2)
			x = (size - 1) - x; // deal with symmetry
		if (y > (size - 1) / 2)
			y = (size - 1) - y;
		return weights[x][y];
	}
	
	public EvalPosition(int size) {
		this.size = size;
	}

	@Override
	public Map<Board, Double> getUtility(List<Board> boardStates) {

		Map<Board, Double> utilities = new HashMap<Board, Double>();
		List<Board> bestBoards = new ArrayList<Board>();
		
		int maxScore = Integer.MIN_VALUE;
		for (Board b : boardStates) {
			int newscore = 0;

			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					int state = b.getState(x, y);
					if (state == b.getActive()) {
						newscore += cost(x, y);
					} else if (state != Board.EMPTY) {
						newscore -= cost(x, y);						
					}
				}
			}
			
			utilities.put(b, 0.0);
			
			if(newscore > maxScore) {
				maxScore = newscore;
				bestBoards.clear();
				bestBoards.add(b);
			} else if(newscore == maxScore) {
				bestBoards.add(b);
			}
			
		}
		
		for(Board b : bestBoards) {
			utilities.put(b, 1.0);
		}
		
		return utilities;
	}

	@Override
	public double getMax() {
		return 1.0;
	}

	@Override
	public double getMin() {
		return 0;
	}

}
