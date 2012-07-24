package othello.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import othello.ai.heuristic.BestScore;
import othello.ai.heuristic.Heuristic;
import othello.model.Board;

public class PluggableHeuristicAI extends ReversiAI {
	
	private Set<Heuristic> heuristics;

	{
		heuristics = new HashSet<Heuristic>();
		heuristics.add( new BestScore() );
	}

	@Override
	public Board nextMove(Board prev, int lastx, int lasty) {
		startTimer();
		bestMove = null;

		Board b = new Board(prev);

		Map<Board, Point> moves = new HashMap<Board, Point>();
		List<Board> boards = new ArrayList<Board>();
		
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				if (b.move(i, j)) {
					moves.put(b, new Point(i,j));
					boards.add(b);
				}
				
				b = new Board(prev);
			}
		}
		
		Map<Board, Double> overall = new HashMap<Board, Double>();
		
		Map<Board, Double> scores;
		Double prevValue = null;
		
		for(Heuristic h : heuristics) {
			scores = h.getUtility(boards);
			
			// 1 - (-1) = 2
			// 1 / 2 = 0.5
			// 0.5 + 0.5
			
			// -1 - 1 = -2
			// 1 / -2 = -0.5
			// -0.5 + -0.5 = -1
			
			// Based off rules of heuristic change evaluation
			// to range of 0 to 1, 1 being highest.
			
			double divisor = h.getMax() - h.getMin();
			double offset = h.getMax() * divisor;
			
			for(Board key : boards) {
				double value = scores.get(key) / divisor;
				value += offset;
				value = Math.abs(value);
				
				if((prevValue = overall.get(key)) == null) {
					overall.put(key, value);
				} else {
					overall.put(key, prevValue + value);
				}
			}
		}
		
		Board bestBoard = null;
		double bestScore = -1;
		for(Board key : boards) {
			if(overall.get(key) > bestScore) {
				bestBoard = key;
				bestScore = overall.get(key);
			}
		}
		
		bestMove = moves.get(bestBoard);
		stopTimer();
		return bestBoard;
	}
}
