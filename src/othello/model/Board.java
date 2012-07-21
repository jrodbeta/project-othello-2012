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
  public static final int EMPTY = 0;  // unoccupied square
  public static final int WHITE = 1;  // square with a white piece
  public static final int BLACK = 2;  // square with a black piece

  private int size;                   // size of the board
  private int active = BLACK;         // ID of the player currently moving
                                      // in reversi, BLACK moves first
  private int inactive = WHITE;       // ID of the player not currently moving
  private int count[] = new int[3];   // number of squares that are empty,
                                      // occupied by WHITE and occupied by BLACK
                                      
  private int board[][];              // 2D array representing the board
  
  // build a new board, with dimensions size x size
  public Board(int size)
  {
    this.size = size;
    int mid = size / 2 - 1;
    board = new int[size][size];
    board[mid][mid] = board[mid + 1][mid + 1] = WHITE;  // start off with diagonal pieces
    board[mid][mid + 1] = board[mid + 1][mid] = BLACK;  // at the center of the board
    count[BLACK] = count[WHITE] = 2;
    count[EMPTY] = size * size - (count[BLACK] + count[WHITE]);
  }
  
  // copy constructor - duplicate existing board
  public Board(Board b)
  {
    size = b.size;
    active = b.active;
    inactive = b.inactive;
    
    board = (int[][]) b.board.clone();
    for(int i = 0; i < size; i++) board[i] = (int[]) b.board[i].clone();
    count = (int[]) b.count.clone();
  }
  
  // getter methods
  public int getSize() { return size; }
  public int getState(int row, int col) { return board[row][col]; }
  public int getActive() { return active; }
  public int getScore() { return count[active]; }
  public int getOpponentScore() { return count[inactive]; }
  
  // get ID of winning player
  public int getWinning()
  {
    if(count[BLACK] > count[WHITE]) return BLACK;
    else if(count[BLACK] < count[WHITE]) return WHITE;
    else return EMPTY;
  }
  
  // attempt to place a piece at specified coordinates, and update
  // the board appropriately, or return false if not possible
  public boolean move(int row, int col)
  {
    if(board[row][col] != EMPTY) return false; // current square must be unoccupied
  
    int before = count[active];
    System.out.println("trying move " + row + " " + col);
    
    west(row, col);
    east(row, col);
    north(row, col);
    south(row, col);
    northwest(row, col);
    southeast(row, col);
    northeast(row, col);
    southwest(row, col);
    
    if(before == count[active]) return false;
    else
    {
      board[row][col] = active;
      count[active]++;
      count[EMPTY]--;
      return true;
    }
  }

  // end current player's turn
  public void turn()
  {
    int tmp = active;
    active = inactive;
    inactive = tmp;
  }
  
  // can the current player make a move?
  public boolean canMove()
  {
    Board tmp = new Board(this);
    for(int i = 0; i < size; i++)
    {
      for(int j = 0; j < size; j++)
        if(tmp.move(i, j)) return true;
    }
    return false;
  }
  
  // check if game is over - can either player make a move?
  public boolean gameOver()
  {
    if(count[EMPTY] == 0) return true;
    else if(canMove()) return false;
    else
    {
      turn();
      return !canMove();
    }
  }
  
  // methods to check whether pieces can be capture in any of the 8 directions
  // from the current coordinates, and to capture them if they can

  private void west(int x, int y)
  {
    if(x != 0)
    {
      if(board[x - 1][y] == inactive)
      {
        for(int i = x - 2; i >= 0; i--)
        {
          if(board[i][y] == EMPTY) return;
          if(board[i][y] == active)
          {
            int flipped = (x - (i + 1));
            count[active] += flipped;
            count[inactive] -= flipped;
            for(int j = i + 1; j < x; j++)
              board[j][y] = active;
            return;
          }
        }
      }
    }
    return;  
  }
  
  private void east(int x, int y)
  {
    if(x != size - 1)
    {
      if(board[x + 1][y] == inactive)
      {
        for(int i = x + 2; i <= size - 1; i++)
        {
          if(board[i][y] == EMPTY) return;
          if(board[i][y] == active)
          {
            int flipped = ((i - 1) - x);
            count[active] += flipped;
            count[inactive] -= flipped;            
            for(int j = i - 1; j > x; j--)
              board[j][y] = active;
            return;
          }
        }
      }
    }
    return;  
  }
  
  private void north(int x, int y)
  {
    if(y != 0)
    {
      if(board[x][y - 1] == inactive)
      {
        for(int i = y - 2; i >= 0; i--)
        {
          if(board[x][i] == EMPTY) return;
          if(board[x][i] == active)
          {
            int flipped = (y - (i + 1));
            count[active] += flipped;
            count[inactive] -= flipped;            
            for(int j = i + 1; j < y; j++)
              board[x][j] = active;
            return;
          }
        }
      }
    }
    return;    
  }
  
  private void south(int x, int y)
  {
    if(y != size - 1)
    {
      if(board[x][y + 1] == inactive)
      {
        for(int i = y + 2; i <= size-1; i++)
        {
          if(board[x][i] == EMPTY) return;
          if(board[x][i] == active)
          {
            int flipped = ((i - 1) - y);
            count[active] += flipped;
            count[inactive] -= flipped;  
            for(int j = i - 1; j > y; j--)
              board[x][j] = active;
            return;
          }
        }
      }
    }
    return;    
  }
  
  private void northwest(int x, int y)
  {
    if(x != 0 && y != 0)
    {
      if(board[x-1][y-1] == inactive)
      {
        for(int ix = x - 2, iy = y - 2; ix >= 0 && iy >= 0; ix--, iy--)
        {
          if(board[ix][iy] == EMPTY) return;
          if(board[ix][iy] == active)
          {
            int flipped = (x - (ix + 1));
            count[active] += flipped;
            count[inactive] -= flipped; 
            for(int jx = ix + 1, jy = iy + 1; jx < x; jx++, jy++)
                board[jx][jy] = active;
            return;
          }
        }
      }
    }
    return;
  }
  
  private void southeast(int x, int y)
  {
    if(x != size - 1 && y != size - 1)
    {
      if(board[x+1][y+1] == inactive)
      {
        for(int ix = x + 2, iy = y + 2; ix <= size -1 && iy <= size - 1; ix++, iy++)
        {
          if(board[ix][iy] == EMPTY) return;
          if(board[ix][iy] == active)
          {
            int flipped = ((ix - 1) - x);
            count[active] += flipped;
            count[inactive] -= flipped; 
            for(int jx = ix - 1, jy = iy - 1; jx > x; jx--, jy--)
                board[jx][jy] = active;
            return;
          }
        }
      }
    }
    return;
  }

  private void northeast(int x, int y)
  {
    if(x != size - 1 && y != 0)
    {
      if(board[x+1][y-1] == inactive)
      {
        for(int ix = x + 2, iy = y - 2; ix <= size - 1 && iy >= 0; ix++, iy--)
        {
          if(board[ix][iy] == EMPTY) return;
          if(board[ix][iy] == active)
          {
            int flipped = ((ix - 1) - x);
            count[active] += flipped;
            count[inactive] -= flipped; 
            for(int jx = ix - 1, jy = iy + 1; jx > x; jx--, jy++)
                board[jx][jy] = active;
            return;
          }
        }
      }
    }
    return;
  }
  
  private void southwest(int x, int y)
  {
    if(x != 0 && y != size - 1)
    {
      if(board[x-1][y+1] == inactive)
      {
        for(int ix = x - 2, iy = y + 2; ix >= 0 && iy <= size - 1; ix--, iy++)
        {
          if(board[ix][iy] == EMPTY) return;
          if(board[ix][iy] == active)
          {
            int flipped = (x - (ix + 1));
            count[active] += flipped;
            count[inactive] -= flipped; 
            for(int jx = ix + 1, jy = iy - 1; jx < x; jx++, jy--)
                board[jx][jy] = active;
            return;
          }
        }
      }
    }
    return;
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
    for(int i = 0; i < size; i++)
    {
      System.out.print(i + "|");
      for(int j = 0; j < size; j++)
      {
        int t = board[i][j];
        if(t == EMPTY) System.out.print(" ");
        else if(t == WHITE) System.out.print("o");
        else if(t == BLACK) System.out.print("x");
      }
      System.out.print("|\n");
    }
    System.out.print(" --");
    for(int i = 0; i < size; i++) System.out.print("-");
    System.out.print("\n");
    System.out.println("w: " + count[WHITE] + " b: " + count[BLACK] + " e: " + count[EMPTY]);
  }
  
  private static String sideToString(int side)
  {
    if(side == BLACK) return "B";
    else return "W";
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
    
    if(x < 0 || x >= max || y < 0 || y >= max)
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
    if(active == BLACK) System.out.print("x > ");
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
      } while(!getCoords(in, coords, size));
      while(!move(coords[0], coords[1]))
      {
        System.out.println("Can't make move to " + coords[0] + "," + coords[1]);
        do {
          prompt();
        } while(!getCoords(in, coords, size));          
      }
      print();
      turn();
    }
    
    System.out.println("GAME OVER");
    if(count[BLACK] > count[WHITE]) System.out.println("Black wins " + count[BLACK] + " - " + count[WHITE] + ".");
    else if(count[BLACK] < count[WHITE]) System.out.println("White wins " + count[WHITE] + " - " + count[BLACK] + ".");
    else System.out.println("Tie.");
    } catch (IOException ioe) { }
  }
  
  public static void main(String[] args)
  {
    Board b = new Board(4);
    b.game();
    
  }
}