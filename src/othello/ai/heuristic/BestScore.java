package othello.ai.heuristic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import othello.model.Board;

public class BestScore implements Heuristic {

	@Override
	public Map<Board, Double> getUtility(List<Board> boardStates) {
		Map<Board, Double> scores = new HashMap<Board, Double>();
		
		Board best = null;
		int score = -1;
		for(Board b : boardStates) {
			if (b.getScore() > score) {
				score = b.getScore();
				best = b;
			}
			
			// While we're looping set scores to 0.
			scores.put(b, 0.0);
		}
		
		scores.put(best, 1.0);
		
		return scores;
	}

	@Override
	public double getMax() {
		return 1;
	}

	@Override
	public double getMin() {
		return 0;
	}

}
