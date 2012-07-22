package othello.ai;
import othello.model.Board;

// simple interface for an AI - given the board, and the last
// move of the human player, return a board with the next move of the AI
public interface ReversiAI
{
  public void setSize(int size); // set the size of the board (necessary before using the AI)
  public Board nextMove(Board prev, int lastx, int lasty); // return board with the next move (passing in coordinates of the previous move
}