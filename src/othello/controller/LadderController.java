package othello.controller;

import java.util.ArrayList;
import java.util.List;

import othello.ai.ReversiAI;
import othello.controller.TestController.TestObserver;
import othello.model.Board;
import othello.view.TestFrame;

public class LadderController {
	
	private static class Score {
		private String aiName;
		private int score;
		
		public Score(String aiName) {
			this.aiName = aiName;
		}
	}
	
	public static void main(String[] args) {
		LadderController ladder = new LadderController();
		ladder.runLadder();
	}
	
	
	List<Score> scores;
	private int percentComplete;
	
	public LadderController() {
		scores = new ArrayList<Score>();
		
		for(int i = 0; i < TestFrame.AI.length; i++) {
			scores.add(new Score(TestFrame.AI[i]));
		}
	}
	
	private TestObserver observer = new TestObserver() {

		@Override
		public void notifyStatus(int percentComplete) {
		}

		@Override
		public void notifyBoardChange(Board board) {
		}
		
	};
	
	public void runLadder() {
		
		Score left;
		Score right;
		
		int n = scores.size();
		int current;
		int total = (n * n) - n;
		for(int i = 0; i < n; i++) {
			for(int j = i + 1; j < n; j++) {
				if(i == j) {
					continue;
				}
				left = scores.get(i);
				right = scores.get(j);
				
				ReversiAI agent1 = ReversiAI.getAIByName(left.aiName);
				ReversiAI agent2 = ReversiAI.getAIByName(right.aiName);
				
				TestController tester = new TestController(8, agent1, agent2);
				tester.run(20, observer);
				
				if(tester.getWinner() == agent1) {
					left.score += 10;
				} else {
					right.score += 10;
				}
				
				current = (i * n) + j;
				
				percentComplete = current * 100 / (total);
				System.out.println("Complete: " + percentComplete + "% - " + left.aiName + " vs. " + right.aiName);
			}
		}
		
		for(Score score : scores) {
			System.out.println(score.aiName + " : " + score.score);
		}
	}

}
