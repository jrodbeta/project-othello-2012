// simple interface for an AI - given the board, and the last
// move of the human player, return a board with the next move of the AI
public interface ReversiAI
{
  public Board nextMove(Board prev, int lastx, int lasty);
}