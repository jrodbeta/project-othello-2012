package othello.ai.heuristic;

import othello.ai.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import othello.model.Board;

public class BestScore implements Heuristic {

	@Override
	public Map<Board, Double> getUtility(List<Board> boardStates) {
		Map<Board, Double> scores = new HashMap<Board, Double>();
		
		List<Board> bestBoards = new ArrayList<Board>();
		
		int maxScore = ReversiAI.MIN_SCORE;
		for(Board b : boardStates) {
			int score = b.getScore();
			if (score > maxScore) {
				maxScore = score;
				bestBoards.clear();
				bestBoards.add(b);
			} else if (score == maxScore) {
				bestBoards.add(b);
			}
			
			// While we're looping set scores to 0.
			scores.put(b, 0.0);
		}

		for(Board b : bestBoards) {
			scores.put(b, 1.0);
		}
		
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
