package othello.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import othello.ai.heuristic.BestScore;
import othello.ai.heuristic.EvalPosition;
import othello.ai.heuristic.Heuristic;
import othello.ai.heuristic.Mobility;
import othello.model.Board;
import othello.view.BoardGUI;

public class PluggableHeuristicAI extends ReversiAI {
	
	private Map<Heuristic, Double> heuristics;

	{
		heuristics = new HashMap<Heuristic, Double>();
		heuristics.put( new BestScore(), 0.5 );
		heuristics.put( new EvalPosition(BoardGUI.ROWS), 1.0 );
		heuristics.put( new Mobility(BoardGUI.ROWS), 0.25);
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
		
		for(Heuristic h : heuristics.keySet()) {
			scores = h.getUtility(boards);
			
			// 1 - (-1) = 2
			// 1 / 2 = 0.5
			// 0.5 + 0.5
			
			// -1 - 1 = -2
			// 1 / -2 = -0.5
			// -0.5 + -0.5 = -1
			// offset = -1 - (-0.5) = -0.5
			
			// Based off rules of heuristic change evaluation
			// to range of 0 to 1, 1 being highest.
			
			double divisor = h.getMax() - h.getMin();
			double offset = h.getMax() - divisor;
			double weight = heuristics.get(h);

//			System.out.println(h.getClass().getSimpleName());
			
			for(Board key : boards) {
				double value = (divisor != 0) ? scores.get(key) / divisor:
								scores.get(key);
				value += offset;
				value = Math.abs(value) * weight;
				
//				System.out.println(value);
				
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
