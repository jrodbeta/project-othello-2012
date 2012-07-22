package othello.controller;

import othello.model.Board;

// interface for the controller in the Model-View-Controller for Reversi

public interface Controller {
	public void update();
	public void move(int x, int y);
	public void newGame();
	public Board getBoard();
}