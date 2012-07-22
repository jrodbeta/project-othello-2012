package othello.ai.heuristic;

import java.util.List;
import java.util.Map;

import othello.model.Board;

public interface Heuristic {
	
	/**
	 * Returns a k/v map of utility costs.
	 * @param boardStates
	 * @return
	 */
	Map<Board, Double> getUtility(List<Board> boardStates);
	
	double getMax();
	double getMin();

}
