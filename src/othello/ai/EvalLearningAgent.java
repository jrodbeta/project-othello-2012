package othello.ai;

import othello.controller.TestController;
import othello.model.Board;

public class EvalLearningAgent implements TestController.TestObserver {
	
	private int[][] swaps;

	private int boardSize;
	private Board prevBoard;

	public EvalLearningAgent(int boardSize) {
		this.boardSize = boardSize;
		swaps = new int[boardSize / 2][boardSize / 2];
	}
	
	@Override
	public void notifyStatus(int percentComplete) {
	}

	@Override
	public void notifyBoardChange(Board board) {
		if(prevBoard == null) {
			prevBoard = board;
			return;
		}
		
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				
				int prevState = prevBoard.getState(i, j);
				int currState = board.getState(i, j);
				if(prevState != Board.EMPTY && currState != Board.EMPTY && currState != prevState) {
					addSwap(i,j);
				}
			}
		}
		
		prevBoard = board;
	}
	
	private void addSwap(int x, int y) {
		int xPos = x;
		int yPos = y;
		int max = boardSize - 1;
		
		if(x > max / 2) {
			xPos = max - x;
		}
		
		if(y > max / 2) {
			yPos = max - y;
		}
		
		swaps[xPos][yPos]++;
	}
	
	public void printResults() {
		for(int i = 0; i < swaps.length; i++) {
			for(int j = 0; j < swaps.length; j++) {
				System.out.print("-" + Math.round(swaps[i][j] / 1000.0) + "\t");
			}
			System.out.println();
		}
	}
}