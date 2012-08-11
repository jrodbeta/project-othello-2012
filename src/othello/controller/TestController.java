package othello.controller;

import java.awt.Point;
import othello.ai.*;
import othello.model.Board;
import othello.view.*;

// controller for a two AI test harness
public class TestController implements Logger
{
	private static final boolean VERBOSE = false; // enable information messages - not good for batch runs

	private ReversiAI agent1;	// black AI
	private ReversiAI agent2;	// white AI
	
	private ReversiAI winner;
	private ReversiAI loser;
	
	private ResultSet resultsDirection1 = new ResultSet();
	private ResultSet resultsDirection2 = new ResultSet();
	private int boardSize;			// size of game board
	
	public class ResultSet {
		private int runs = 0;				// number of times game was played
		private int bwins = 0;			// number of wins by black
		private int wwins = 0;			// number of wins by white
	}

	private Logger logger;
	
	public static void main(String args[])
	{
		ReversiAI black = new MinimaxABAI();
		ReversiAI white = new GreedyHeuristicAI();
		
		TestController game = new TestController(BoardGUI.ROWS, black, white);
		game.run(20);
		game.report();
	}
	
	// create a new tester, with a given size and specified AIs for black and white
	public TestController(int boardSize, ReversiAI black, ReversiAI white)
	{
		this.boardSize = boardSize;
		
		agent1 = black;
		agent1.setSize(boardSize);
		agent2 = white;
		agent2.setSize(boardSize);
		
		this.logger = this;
	}
	
	public void run(int n) {
		run(n, null);
	}
	
	// run AIs against each other n times
	public void run(int n, TestObserver observer)
	{
		runSingle(n, resultsDirection1, observer);
		swapAgents();
		
		runSingle(n, resultsDirection2, observer);
		swapAgents();
		
		if(resultsDirection1.bwins + resultsDirection2.wwins > resultsDirection1.wwins + resultsDirection2.bwins) {
			winner = agent1;
			loser = agent2;
		} else {
			winner = agent2;
			loser = agent1;
		}
	}
	
	private void swapAgents() {
		ReversiAI tempAgent = agent1;
		agent1 = agent2;
		agent2 = tempAgent;
	}
	
	private void runSingle(int n, ResultSet results, TestObserver observer) {
		if(observer == null) {
			printHeader();
			logger.log("=");
		}
		
		int offset = 0;
		if(results == resultsDirection2) {
			offset = n;
		}
		
		for(int i = 0; i < n; i++)
		{
			if(observer == null) {
				if((100*(i-1))/n < (100*i)/n) logger.log("=");
			} else {
				observer.notifyStatus(( i + offset) * 100 / n / 2);
			}
			
			play(results, observer);
		}
		
		if(observer == null) {
			logger.logln("=\n");
		}
	}
	
	private void printHeader()
	{
		logger.log("0%");
		for(int i = 1; i < 10; i++) {
			logger.log("       " + 10*i + "%");
		}
		logger.logln("       100%");
		for(int i = 0; i < 10; i++) {
			logger.log("|         ");
		}
		logger.logln("|");
	}
	
	public void report() {
		report(resultsDirection1);
		report(resultsDirection2);
	}
	
	// display win/loss statistics for runs completed by the tester
	public void report(ResultSet results)
	{
		if(results == resultsDirection1) {
			logger.logln("Black: " + agent1.getClass().getSimpleName() + String.format(" (%.2fs)", agent1.getElapsedTime()));
			logger.logln("White: " + agent2.getClass().getSimpleName() + String.format(" (%.2fs)", agent2.getElapsedTime()));			
		} else {
			logger.logln("Black: " + agent2.getClass().getSimpleName() + String.format(" (%.2fs)", agent2.getElapsedTime()));
			logger.logln("White: " + agent1.getClass().getSimpleName() + String.format(" (%.2fs)", agent1.getElapsedTime()));
		}
		
		logger.logln("");
		logger.logln("Winner Statistics");
		logger.logln("Player  Wins");
		logger.logln("Black   " + results.bwins);
		logger.logln("White   " + results.wwins);
		logger.logln("Tie     " + (results.runs - results.bwins - results.wwins));
		logger.logln("Total   " + results.runs);
		logger.logln("");
	}
	
	public void play(ResultSet results, TestObserver observer)
	
	{
		results.runs++;
		ReversiAI activeAI = agent1, inactiveAI = agent2; // black is first to move
		ReversiAI aiTemp;
		Board b = new Board(boardSize); // create game board
		Point p = new Point(-1, -1);
		if(VERBOSE) b.print();
		
		while(true)
		{
			logDebug(b.getActiveName() + " to move.");
			Board tmp = activeAI.nextMove(b, p.x, p.y); // get the next move

			if(tmp == null) // player couldn't move
			{
				logDebug(b.getActiveName() + " can't move.");
				b.turn(); // next player's turn
				logDebug(b.getActiveName() + " to move.");
				{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; } // swap players
				tmp = activeAI.nextMove(b, -1, -1);
				
				if(tmp == null) break; // neither player could move
			}
			
			b = tmp; // save new board
			

			if(observer != null) observer.notifyBoardChange(b);
			
			p = activeAI.getMove();
			logDebug(b.getActiveName() + " move to (" + p.x + "," + p.y + ").");
			if(VERBOSE) b.print();
			
			b.turn(); // next player's turn
			{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; } // switch AIs
		}

		logDebug(winnerString(b, results));

	}
	
	private String winnerString(Board b, ResultSet results)
	{
		String msg;
		int winner = b.getWinning();
		if(winner != b.getActive()) b.turn();
		
		if(winner == Board.EMPTY) msg = "Tie: ";
		else if(winner == Board.WHITE) { results.wwins++; msg = "White wins: "; }
		else { results.bwins++; msg = "Black wins: "; }
		
		msg += b.getTotal(true) + " - " + b.getTotal(false) + " (" +
				((boardSize * boardSize) - b.getMoves()) + " moves)";
		return msg;
	}
	
	
	public void logDebug(String msg)
	{
		if(VERBOSE) System.out.println(msg);
	}

	@Override
	public void log(String msg) {
		System.out.print(msg);
	}
	
	@Override
	public void logln(String msg) {
		System.out.println(msg);
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public interface TestObserver {
		void notifyStatus(int percentComplete);
		void notifyBoardChange(Board board);
	}
	
	public ReversiAI getLoser() {
		return loser;
	}
	
	public ReversiAI getWinner() {
		return winner;
	}
	
}