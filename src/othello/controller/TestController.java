package othello.controller;

import java.awt.Point;
import othello.ai.*;
import othello.model.Board;
import othello.view.*;

// controller for a two AI test harness
public class TestController implements Logger
{
	private static final boolean VERBOSE = false; // enable information messages - not good for batch runs

	private ReversiAI blackAI;	// black AI
	private ReversiAI whiteAI;	// white AI
	private int boardSize;			// size of game board
	private int runs = 0;				// number of times game was played
	private int bwins = 0;			// number of wins by black
	private int wwins = 0;			// number of wins by white

	private Logger logger;
	
	public static void main(String args[])
	{
		//TestController game = new TestController(BoardGUI.ROWS, new RandomAI(), new GreedyHeuristicAI());
		TestController game = new TestController(BoardGUI.ROWS, new PluggableHeuristicAI(), new GreedyHeuristicAI());
//		TestController game = new TestController(BoardGUI.ROWS, new GreedyHeuristicAI(), new PluggableHeuristicAI());
//		TestController game = new TestController(BoardGUI.ROWS, new GreedyHeuristicAI(), new GreedyAI());
//		TestController game = new TestController(BoardGUI.ROWS, new GreedyAI(), new GreedyHeuristicAI());
//		TestController game = new TestController(BoardGUI.ROWS, 
//				new PluggableHeuristicAI(), 
//				new PluggableHeuristicAI(PluggableHeuristicAI.EVAL_POSITION));
		
		
		game.run(1000);
		game.report();
	}
	
	// create a new tester, with a given size and specified AIs for black and white
	public TestController(int boardSize, ReversiAI black, ReversiAI white)
	{
		this.boardSize = boardSize;
		
		blackAI = black;
		blackAI.setSize(boardSize);
		whiteAI = white;
		whiteAI.setSize(boardSize);
		
		this.logger = this;
	}
	
	// run AIs against each other n times
	public void run(int n)
	{
		printHeader();
		
		logger.log("=");
		for(int i = 0; i < n; i++)
		{
			if((100*(i-1))/n < (100*i)/n) logger.log("=");
			play();
		}
		logger.logln("=\n");
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
	
	// display win/loss statistics for runs completed by the tester
	public void report()
	{
		
		logger.logln("Black: " + blackAI.getClass().getSimpleName() + String.format(" (%.2fs)", blackAI.getElapsedTime()));
		logger.logln("White: " + whiteAI.getClass().getSimpleName() + String.format(" (%.2fs)", whiteAI.getElapsedTime()));
		logger.logln("");
		logger.logln("Winner Statistics");
		logger.logln("Player  Wins");
		logger.logln("Black   " + bwins);
		logger.logln("White   " + wwins);
		logger.logln("Tie     " + (runs - bwins - wwins));
		logger.logln("Total   " + runs);
		logger.logln("");
	}
	
	public void play()
	
	{
		runs++;
		ReversiAI activeAI = blackAI, inactiveAI = whiteAI; // black is first to move
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
			
			p = activeAI.getMove();
			logDebug(b.getActiveName() + " move to (" + p.x + "," + p.y + ").");
			if(VERBOSE) b.print();
			
			b.turn(); // next player's turn
			{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; } // switch AIs
		}
		
		logDebug(winnerString(b));
	}
	
	private String winnerString(Board b)
	{
		String msg;
		int winner = b.getWinning();
		if(winner != b.getActive()) b.turn();
		
		if(winner == Board.EMPTY) msg = "Tie: ";
		else if(winner == Board.WHITE) { wwins++; msg = "White wins: "; }
		else { bwins++; msg = "Black wins: "; }
		
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
}