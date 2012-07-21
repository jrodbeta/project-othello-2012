package othello.model;
/* Basic Reversi Board abstraction

important methods are:
  - Board(int) - create a new n x n board
  - Board(Board) - duplicate an existing n x n board
  - move(int, int) - attempt to put a piece on coordinate x, y for the active player
    -if successful, the board is updated with to include newly captured pieces
    -if unsuccessful, the board is not modified and false is returned
  -turn() - end the turn of the current player
  -canMove() - return true if the active (current) player can make a move
  -gameOver() - return true if neither player can move

*/

import java.io.*;

public class Board
{
  public static final int BLACK = 0;
  public static final int WHITE = 1;
  public static final int EMPTY = 2;

  private long black_board = 0; // bitboard - 1 means a black piece occupies the position
  private long white_board = 0; // bitboard - 1 means a white piece occupies the position

  private short bcount = 0; // number of black pieces on the board
  private short wcount = 0; // number of white pieces on the board
  
  private short size;       // dimensions of the board
  private short moves = 0;  // number of moves made so far (not used yet)
  
  private boolean active = true; // true if it's black's turn, false if it's white's turn
  
  // build a new board, with dimensions size x size
  public Board(int boardsize)
  {
    size = (short)boardsize;
    int mid = boardsize / 2 - 1;
    setSquare(mid, mid, false); // set the middle 4 positions
    setSquare(mid + 1, mid + 1, false);
    setSquare(mid + 1, mid, true);
    setSquare(mid, mid + 1, true);
  }
  
  // copy constructor
  public Board(Board b)
  {
    black_board = b.black_board;
    white_board = b.white_board;
    
    bcount = b.bcount;
    wcount = b.wcount;

    moves = b.moves;
    size = b.size;
    
    active  = b.active;
  }
  
  // place a piece at position x, y for the given player
  private void setSquare(int x, int y, boolean black)
  {
    if(black)
    {
      black_board |= (1L << (y * size + x));
      bcount++;
    }
    else
    {
      white_board |= (1L << (y * size + x));
      wcount++;
    }
  }
  
  // flip the piece at position x, y from the inactive player to the active one
  private void flipSquare(int x, int y)
  {
    long val = (1L << (y * size + x));
    if(active) // black's turn
    {
      black_board |= val;
      white_board &= ~val;
    }
    else
    {
      white_board |= val;
      black_board &= ~val;    
    }
  }
  
  // returns true if the given player occupies the given square
  private boolean getSquare(int x, int y, boolean black)
  {
    if(x < 0 || y < 0 || x >= size || y >= size) return false;
  
    if(black) return (black_board & (1L << (y * size + x))) != 0;
    else return (white_board & (1L << (y * size + x))) != 0;
  }
  
  // getter methods
  public int getSize() { return size; }
  
  public int getActive()
  {
    if(active) return BLACK;
    else return WHITE;
  }
  
  public int getScore() { if(active) return bcount; else return wcount; }
  public int getOpponentScore() { if(active) return wcount; else return bcount; }
  
  // get ID of winning player
  public int getWinning()
  {
    if(bcount > wcount) return BLACK;
    else if(wcount > bcount) return WHITE;
    else return EMPTY;
  }
  
  public int getState(int x, int y)
  {
    if(getSquare(x, y, true)) return BLACK;
    else if(getSquare(x, y, false)) return WHITE;
    else return EMPTY;
  }
  
  // attempt to place a piece at specified coordinates, and update
  // the board appropriately, or return false if not possible
  public boolean move(int x, int y)
  {
    if(getState(x, y) != EMPTY) return false; // current square must be unoccupied
  
    int before = bcount;
    
    west(x - 1, y); // checks if can capture in this direction, flipping pieces where necessary
    east(x + 1, y);
    north(x, y - 1);
    south(x, y + 1);
    northwest(x - 1, y - 1);
    southeast(x + 1, y + 1);
    northeast(x + 1, y - 1);
    southwest(x - 1, y + 1);
    
    if(before == bcount) return false; // if no changes, move was unsuccessful
    
    // place piece at current position
    setSquare(x, y, active);
    return true;
  }

  // end current player's turn
  public void turn() { active = !active; }
  
  // can the current player make a move?
  public boolean canMove()
  {
    Board tmp = new Board(this); // duplicate board
    for(int j = 0; j < size; j++)
    {
      for(int i = 0; i < size; i++)
        if(tmp.move(i, j)) { tmp = null; return true; } // try moves
    }
    return false;
  }
  
  // check if game is over - can either player make a move?
  public boolean gameOver()
  {
    if(bcount + wcount == size * size) return true;
    else if(canMove()) return false;
    else
    {
      turn();
      return !canMove();
    }
  }
  
  // update piece counts for current player capturing 'flipped' pieces
  private void updateCount(int flipped)
  {
    if(active)
    {
      bcount += flipped;
      wcount -= flipped;
    }
    else
    {
      wcount += flipped;
      bcount -= flipped;
    }
  }
  
  // methods to check whether pieces can be capture in any of the 8 directions
  // from the current coordinates, and to capture them if they can
  private void west(int x, int y)
  {
    if(x <= 0) return; // can't capture - no room
    
    int i;
    for(i = x; getSquare(i, y, !active); i--) // traverse squares that could be captured
      if(i == 0) return; // can't capture if reached the edge of the board
      
    if(i != x && getSquare(i, y, active)) // if capturable square is followed by captured square
    {
      updateCount(x - i); // number of squares captured
      
      for(i++; i <= x; i++) flipSquare(i, y); // flip them
    }
  }

  private void east(int x, int y)
  {
    if(x >= size - 1) return;
  
    int i;
    for(i = x; getSquare(i, y, !active); i++)
      if(i == size - 1) return;
      
    if(i != x && getSquare(i, y, active))
    {
      updateCount(i - x);
        
      for(i--; i >= x; i--) flipSquare(i, y);
    }
  }

  private void north(int x, int y)
  {
    if(y <= 0) return;
  
    int i;
    for(i = y; getSquare(x, i, !active); i--)
      if(i == 0) return;
      
    if(i != y && getSquare(x, i, active))
    {
      updateCount(y - i);
      
      for(i++; i <= y; i++) flipSquare(x, i);
    }
  }

  private void south(int x, int y)
  {
    if(y >= size - 1) return;
  
    int i;
    for(i = y; getSquare(x, i, !active); i++)
      if(i == size - 1) return;
      
    if(i != y && getSquare(x, i, active))
    {
      updateCount(i - y);
      
      for(i--; i >= y; i--) flipSquare(x, i);
    }
  }
  
  private void northwest(int x, int y)
  {
    if(x <= 0 || y <= 0) return;
  
    int i, j;
    for(i = x, j = y; getSquare(i, j, !active); i--, j--)
      if(i == 0 || j == 0) return;
      
    if(i != x && getSquare(i, j, active))
    {
      updateCount(x - i);
        
      for(i++, j++; i <= x; i++, j++) flipSquare(i, j);
    }
  }
  
  private void southeast(int x, int y)
  {
    if(x >= size - 1 || y >= size - 1) return;
  
    int i, j;
    for(i = x, j = y; getSquare(i, j, !active); i++, j++)
        if(i == size - 1 || j == size - 1) return;
      
    if(i != x && getSquare(i, j, active))
    {
      updateCount(i - x);
        
      for(i--, j--; i >= x; i--, j--) flipSquare(i, j);
    }
  }

  private void northeast(int x, int y)
  {
    if(x >= size - 1 || y <= 0) return;
    
    int i, j;
    for(i = x, j = y; getSquare(i, j, !active); i++, j--)
        if(i == size - 1 || j == 0) return;
      
    if(i != x && getSquare(i, j, active))
    {
      updateCount(i - x);
      
      for(i--, j++; i >= x; i--, j++) flipSquare(i, j);
    }
  }
  
  private void southwest(int x, int y)
  {
    if(x <= 0 || y >= size - 1) return;
    
    int i, j;
    for(i = x, j = y; getSquare(i, j, !active); i--, j++)
      if(i == 0 || j == size - 1) return;
      
    if(i != x && getSquare(i, j, active))
    {
      updateCount(x - i);
        
      for(i++, j--; i <= x; i++, j--) flipSquare(i, j);
    }
  }
  
  // ASCII printout of the current board
  public void print()
  {
    System.out.print("  ");
    for(int i = 0; i < size; i++) System.out.print(i);
    System.out.print("\n");
  
    System.out.print(" --");
    for(int i = 0; i < size; i++) System.out.print("-");
    System.out.print("\n");
    for(int j = 0; j < size; j++)
    {
      System.out.print(j + "|");
      for(int i = 0; i < size; i++)
      {
        int t = getState(i, j);
        if(t == EMPTY) System.out.print(" ");
        else if(t == WHITE) System.out.print("o");
        else if(t == BLACK) System.out.print("x");
      }
      System.out.print("|\n");
    }
    System.out.print(" --");
    for(int i = 0; i < size; i++) System.out.print("-");
    System.out.print("\n");
    System.out.println("b: " + bcount + " w: " + wcount + " e: " + (size * size - (bcount + wcount)));
  }
  
  // get coordinates from the user
  private static boolean getCoords(BufferedReader in, int[] coords, int max) throws IOException
  {
    String line = in.readLine();
    if(line.equals("quit")) System.exit(0);
    
    String input[] = line.split(" ");
    if(input.length != 2)
    {
      System.out.println("Invalid input format");
      return false;
    }
    int x = Integer.parseInt(input[0]);
    int y = Integer.parseInt(input[1]);
    
    if(x < 0 || x > max || y < 0 || y > max)
    {
      System.out.println("Coordinates must be between 0 and " + max);
      return false;
    }
    coords[0] = x;
    coords[1] = y;
    return true;
  }
  
  private void prompt()
  {
    if(active) System.out.print("x > ");
    else System.out.print("o > ");  
  }
  
  // run a text-based Reversi game
  public void game()
  {
    try {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    int coords[] = new int[2];
    
    System.out.println("Reversi!");
    print();
    while(!gameOver())
    {
      do {
        prompt();
      } while(!getCoords(in, coords, size - 1));
      while(!move(coords[0], coords[1]))
      {
        System.out.println("Can't make move to " + coords[0] + "," + coords[1]);
        do {
          prompt();
        } while(!getCoords(in, coords, size - 1));          
      }
      print();
      turn();
    }
    
    System.out.println("GAME OVER");
    int winner = getWinning();
    
    if(winner == BLACK) System.out.println("Black wins " + bcount + " - " + wcount + ".");
    else if(winner == WHITE) System.out.println("White wins " + wcount + " - " + bcount + ".");
    else System.out.println("Tie " + bcount + " - " + wcount + ".");
    } catch (IOException ioe) { }
  }
  
  public static void main(String[] args)
  {
    Board b = new Board(4);
    b.game();
    
  }
}