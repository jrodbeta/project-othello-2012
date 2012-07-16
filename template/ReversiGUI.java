// GUI for the game - View in the MVC model

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ReversiGUI extends JFrame implements Listener
{
  private JLabel gameMessage = new JLabel(" ");
  private JLabel gameTurn = new JLabel(" ");
  private JLabel gameScore = new JLabel(" ");
  private JButton newGame = new JButton("New Game");
  private JButton quitGame = new JButton("Quit");
  private BoardGUI gameBoard = new BoardGUI();
  
  private Controller c;

  public ReversiGUI() // build the GUI - status info, a board, and two buttons
  {
    super("Reversi");
    JComponent content = (JComponent) getContentPane();
    content.setLayout(new BorderLayout());
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
    infoPanel.add(gameMessage);
    infoPanel.add(gameTurn);
    infoPanel.add(gameScore);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    
    newGame.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          c.newGame();
        }
      }
    );
    
    quitGame.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
      }
    );
    buttonPanel.add(newGame);
    buttonPanel.add(quitGame);
    
    gameBoard.addMouseListener(new BoardActionListener());
    
    content.add(gameBoard, BorderLayout.CENTER);
    content.add(infoPanel, BorderLayout.PAGE_START);
    content.add(buttonPanel, BorderLayout.PAGE_END);
    
    pack();
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
  
  public void setBoard(Board b) { gameBoard.setModel(b); }
  public void setMessage(String m) { gameMessage.setText(m); }
  public void setTurn(String m) { gameTurn.setText(m); }
  public void setScore(String m) { gameScore.setText(m); }
  public void setController(Controller c) { this.c = c; }
  public void repaint() { gameBoard.repaint(); }
  
  public class BoardActionListener implements MouseListener
  {
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    
    public void mouseClicked(MouseEvent e) // translate clicks to squares on the board
    {
      int x = e.getX();
      int y = e.getY();
      if(x <BoardGUI.SIZE && y < BoardGUI.SIZE)
      {
        int row = x / BoardGUI.CELLSIZE;
        int col = y / BoardGUI.CELLSIZE;
        if(c != null) c.move(row, col); // if there is a controller, notify it
      }
    }
  }
}

// the actual board (canvas)
class BoardGUI extends JPanel
{
  public static final int ROWS = 8;
  public static final int SIZE = 400;
  public static final int CELLSIZE = SIZE / ROWS;
  public static final double OFFSET = 0.1;

  // the model that will be drawn next
  private Board model;
  
  public BoardGUI()
  {
    setBackground(Color.GRAY);
    setLayout(null);
  }
  
  // external interface to redraw the board
  public void setModel(Board b) { model = b; repaint(); }
  
  public Dimension getPreferredSize() { return new Dimension(SIZE+1, SIZE+1); }
  
  // redraw the board
  public void paintComponent(Graphics g)
  {
    g.setColor(Color.BLACK);
    super.paintComponent(g);
    
    for(int i = 0; i <= ROWS; i++) // divide the board into squares
    {
      g.drawLine(i * CELLSIZE, 0, i * CELLSIZE, SIZE);
      g.drawLine(0, i * CELLSIZE, SIZE, i * CELLSIZE);
    }
    
    g.fillOval(2*CELLSIZE - 2, 2*CELLSIZE - 2, 5, 5); // mark the inner quadrant
    g.fillOval(SIZE-2*CELLSIZE - 2, 2*CELLSIZE - 2, 5, 5);
    g.fillOval(2*CELLSIZE - 2, SIZE-2*CELLSIZE - 2, 5, 5);
    g.fillOval(SIZE-2*CELLSIZE - 2, SIZE-2*CELLSIZE - 2, 5, 5);
    
    // draw all pieces currently on the board
    for(int i = 0; i < ROWS; i++)
    {
      for(int j = 0; j < ROWS; j++)
      {
        int state = model.getState(i, j);
        if(state != Board.EMPTY)
        {
          if(state == Board.BLACK) g.setColor(Color.BLACK);
          else g.setColor(Color.WHITE);
          g.fillOval((int)(i * CELLSIZE + OFFSET * CELLSIZE), (int)(j * CELLSIZE + OFFSET * CELLSIZE),
                     (int)(CELLSIZE - 2 * OFFSET * CELLSIZE), (int)(CELLSIZE - 2 * OFFSET * CELLSIZE));
        }
      }
    }
  }  
  
}
