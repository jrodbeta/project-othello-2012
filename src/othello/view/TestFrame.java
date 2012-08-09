package othello.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import othello.ai.AIThread;
import othello.ai.EvalLearningAgent;
import othello.ai.GreedyAI;
import othello.ai.GreedyHeuristicAI;
import othello.ai.MinimaxABAI;
import othello.ai.MinimaxABHeuristicAI;
import othello.ai.MinimaxAI;
import othello.ai.PluggableHeuristicAI;
import othello.ai.ReversiAI;
import othello.controller.AIController;
import othello.controller.TestController;
import othello.model.Board;
import othello.model.Listener;

@SuppressWarnings("serial")
public class TestFrame extends JFrame implements ActionListener, Logger, Listener {
	private static Border THIN_BORDER = new EmptyBorder(4, 4, 4, 4);
	private static Border BORDER = new EmptyBorder(8, 8, 8, 8);

	public static final String AI[] = { ReversiAI.Types.GREEDY,
			ReversiAI.Types.HEURISTIC, ReversiAI.Types.PLUGGABLE,
			ReversiAI.Types.MINIMAX, ReversiAI.Types.MINIMAX_AB,
			ReversiAI.Types.MINIMAX_AB_HEU };

	public JComboBox leftAICombo;
	public JComboBox rightAICombo;
	public JTextArea resultsArea;
	private JButton runTests;
	private JButton viewGame;
	private BoardGUI boardGUI;
	private JProgressBar progressBar;
		
	private boolean testFinished;
	
	private EvalLearningAgent learningAgent;

	public TestFrame() {
		super();
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		runTests = new JButton("Run tests");
		runTests.addActionListener(this);
		viewGame = new JButton("View game->");
		viewGame.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(runTests);
		buttonPanel.add(viewGame);

		leftAICombo = new JComboBox(AI);
		leftAICombo.setBorder(BorderFactory.createTitledBorder("Agent 1"));
		
		rightAICombo = new JComboBox(AI);
		rightAICombo.setBorder(BorderFactory.createTitledBorder("Agent 2"));

		JLabel vsLabel = new JLabel("vs");

		JPanel aiPanel = new JPanel();
		aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.LINE_AXIS));
		aiPanel.add(leftAICombo);
		aiPanel.add(vsLabel);
		aiPanel.add(rightAICombo);
		aiPanel.setBorder(THIN_BORDER);

		JPanel controlPanel = new JPanel();
		controlPanel
				.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		controlPanel.add(aiPanel);
		controlPanel.add(buttonPanel);

		resultsArea = new JTextArea(22, 50);
		resultsArea.setEditable(false);
		resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

		JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
		resultsScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		progressBar = new JProgressBar(0);
		progressBar.setStringPainted(true);
		
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BorderLayout());
		progressPanel.add(resultsScrollPane, BorderLayout.CENTER);
		progressPanel.add(progressBar, BorderLayout.PAGE_START);
		

		logln("Ready to rock!");

		boardGUI = new BoardGUI();

		JPanel testingPanel = new JPanel();
		testingPanel.setLayout(new BorderLayout());
		testingPanel.add(controlPanel, BorderLayout.PAGE_START);
		testingPanel.add(progressPanel, BorderLayout.CENTER);
		testingPanel.setBorder(THIN_BORDER);

		contentPane.add(testingPanel, BorderLayout.LINE_START);
		contentPane.add(boardGUI, BorderLayout.CENTER);

		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == runTests) {
			runTests.setEnabled(false);
			runTests();
		}
	}

	private void runTests() {
		resultsArea.setText("");
		
		String s_leftAI = (String) leftAICombo.getSelectedItem();
		String s_rightAI = (String) rightAICombo.getSelectedItem();

		final ReversiAI leftAI = ReversiAI.getAIByName(s_leftAI);
		final ReversiAI rightAI = ReversiAI.getAIByName(s_rightAI);
		
		final ReversiAI blackAI = ReversiAI.getAIByName(s_leftAI);
		final ReversiAI whiteAI = ReversiAI.getAIByName(s_rightAI);
		
		testFinished = false;
		AIThread.goSlow();

		new Thread() {
			@Override
			public void run() {
				AIController aiController = new AIController(TestFrame.this, whiteAI,
						blackAI);
				
				while(!testFinished) {
					aiController.newGame();
					
					while(!aiController.getBoard().gameOver()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					try {
						// Give us a peek at the board.
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				learningAgent = new EvalLearningAgent(BoardGUI.ROWS);
				
				TestController testController = new TestController(8, leftAI,
						rightAI);
				testController.setLogger(TestFrame.this);
				testController.run(500, new TestController.TestObserver() {
					
					@Override
					public void notifyStatus(int percentComplete) {
						progressBar.setValue(percentComplete);
						progressBar.setString(percentComplete + "%");
					}
					
					@Override
					public void notifyBoardChange(Board board) {
						learningAgent.notifyBoardChange(board);
					}
				});
				
				testController.report();
				learningAgent.printResults();
				
				progressBar.setValue(100);
				progressBar.setString("100%");
				
				runTests.setEnabled(true);
				testFinished = true;
				AIThread.hurryUp();
			}
		}.start();
	}

	@Override
	public void log(String msg) {
		resultsArea.setText(resultsArea.getText() + msg);
		resultsArea.setCaretPosition(resultsArea.getText().length());
	}

	@Override
	public void logln(String msg) {
		log(msg + "\n");
	}

	public void setBoard(Board b) {
		boardGUI.setModel(b);
	}

	public void setMessage(String m) {
//		gameMessage.setText(m);
	}

	public void setTurn(String m) {
//		gameTurn.setText(m);
	}

	public void setScore(String m) {
//		gameScore.setText(m);
	}

	public void repaint() {
		boardGUI.validate();
		boardGUI.repaint();
	}

}
