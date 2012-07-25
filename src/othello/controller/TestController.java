package othello.controller;

import java.awt.Point;
import othello.ai.*;
import othello.model.Board;
import othello.view.*;

// controller for a two AI test harness
public class TestController
{
	private static final boolean VERBOSE = false;

	private ReversiAI blackAI, whiteAI;
	private int boardSize;
	private int runs = 0;
	private int bwins = 0;
	private int wwins = 0;
	
	public static void main(String args[])
	{
		TestController game = new TestController(BoardGUI.ROWS, new MinimaxABAI(4, false), new MinimaxABHeuristicAI(2, false));
		game.run(100);
		game.report();
	}
	
	public TestController(int boardSize, ReversiAI black, ReversiAI white)
	{
		this.boardSize = boardSize;
		
		blackAI = black;
		blackAI.setSize(boardSize);
		whiteAI = white;
		whiteAI.setSize(boardSize);
	}
	
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
		
		ReversiAI activeAI = blackAI, inactiveAI = whiteAI;
		Board b = new Board(boardSize);
		Point p = new Point(-1, -1);
		if(VERBOSE) b.print();
		
		while(true)
		{
			log(b.getActiveName() + " to move.");
			Board tmp = activeAI.nextMove(b, p.x, p.y);
			ReversiAI aiTemp;
			if(tmp == null) 
			{
				log(b.getActiveName() + " can't move.");
				b.turn();
				log(b.getActiveName() + " to move.");
				{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; }
				tmp = activeAI.nextMove(b, -1, -1);
			}
			if(tmp == null) break;
			
			b = tmp;
			p = activeAI.getMove();
			
			log(b.getActiveName() + " move to (" + p.x + "," + p.y + ").");
			if(VERBOSE) b.print();
			
			b.turn();
			{ aiTemp = activeAI; activeAI = inactiveAI; inactiveAI = aiTemp; } // switch AIs
		}
		
		String msg;
		int winner = b.getWinning();
		if(winner != b.getActive()) b.turn();
		
		if(winner == Board.EMPTY) msg = "Tie: ";
		else if(winner == Board.WHITE) { wwins++; msg = "White wins: "; }
		else { bwins++; msg = "Black wins: "; }
		msg += b.getTotal(true) + " - " + b.getTotal(false) + " (" + ((boardSize * boardSize) - b.getMoves()) + " moves)";
		log(msg);
	}
	
	public void log(String msg)
	{
		if(VERBOSE) System.out.println(msg);
	}
}