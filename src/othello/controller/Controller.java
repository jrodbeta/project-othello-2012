package othello.controller;
// interface for the controller in the Model-View-Controller for Reversi

public interface Controller
{
  public void update();						// update the display
  public void move(int x, int y);	// attempt to place a piece at coordinate x,y
  public void newGame();					// start a new game
}