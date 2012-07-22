package othello.ai;

import java.awt.Point;

import othello.model.Board;

// simple interface for an AI - given the board, and the last
// move of the human player, return a board with the next move of the AI
public interface ReversiAI {
	public void setSize(int size);

	public Board nextMove(Board prev, int lastx, int lasty);

	public Point getMove();
}