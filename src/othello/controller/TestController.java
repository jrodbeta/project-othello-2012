package othello.controller;

import java.awt.Point;
import othello.ai.*;
import othello.model.Board;
import othello.view.*;

// controller for a two AI test harness
public class TestController
{
	private static final boolean VERBOSE = false; // enable information messages - not good for batch runs

	private ReversiAI blackAI;	// black AI
	private ReversiAI whiteAI;	// white AI
	private int boardSize;			// size of game board
	private int runs = 0;				// number of times game was played
	private int bwins = 0;			// number of wins by black
	private int wwins = 0;			// number of wins by white
	
	public static void main(String args[])
	{
		TestController game = new TestController(BoardGUI.ROWS, new MinimaxABAI(4, false), new MinimaxABHeuristicAI(2, false));
		game.run(100);
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
	}
	
	// run AIs against each other n times
	public void run(int n)
	{
		printHeader();
		
		System.out.print("=");
		for(int i = 0; i < n; i++)
		{
			if((100*(i-1))/n < (100*i)/n) System.out.print("=");
			play();
		}
		System.out.println("=\n");
	}
	
	private static void printHeader()
	{
		System.out.print("0%");
		for(int i = 1; i < 10; i++) System.out.print("       " + 10*i + "%");
		System.out.println("       100%");
		for(int i = 0; i < 10; i++) System.out.print("|         ");
		System.out.println("|");
	}
	
	// display win/loss statistics for runs completed by the tester
	public void report()
	{
		System.out.format("Black: " + blackAI.getClass().getName() + " (%.2fs)\n", blackAI.getElapsedTime());
		System.out.format("White: " + whiteAI.getClass().getName() + " (%.2fs)\n", whiteAI.getElapsedTime());
		System.out.println("");
		System.out.println("Winner Statistics");
		System.out.println("Player  Wins");
		System.out.println("Black   " + bwins);
		System.out.println("White   " + wwins);
		System.out.println("Tie     " + (runs - bwins - wwins));
		System.out.println("Total   " + runs);
		System.out.println("");
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
			log(b.getActiveName() + " to move.");
			Board tmp = activeAI.nextMove(b, p.x, p.y); // get the next move

			if(tmp == null) // player couldn't move
			{
				log(b.getActiveName() + " can't move.");
				b.turn(); // next player's turn
				log(b.getActiveName() + " to move.");
				{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; } // swap players
				tmp = activeAI.nextMove(b, -1, -1);
				
				if(tmp == null) break; // neither player could move
			}
			
			b = tmp; // save new board
			
			p = activeAI.getMove();
			log(b.getActiveName() + " move to (" + p.x + "," + p.y + ").");
			if(VERBOSE) b.print();
			
			b.turn(); // next player's turn
			{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; } // switch AIs
		}
		
		log(winnerString(b));
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
	
	public void log(String msg)
	{
		if(VERBOSE) System.out.println(msg);
	}
}