package othello.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import othello.ai.GreedyAI;
import othello.ai.GreedyHeuristicAI;
import othello.ai.PluggableHeuristicAI;
import othello.ai.ReversiAI;
import othello.controller.TestController;

@SuppressWarnings("serial")
public class TestFrame extends JFrame implements ActionListener, Logger {
	private static Border THIN_BORDER = new EmptyBorder(4,4,4,4);
	private static Border BORDER = new EmptyBorder(8,8,8,8);
	
	public static final String AI[] = {
		ReversiAI.Types.GREEDY, 
		ReversiAI.Types.HEURISTIC,
		ReversiAI.Types.PLUGGABLE
		};
	
	public JComboBox leftAICombo;
	public JComboBox rightAICombo;
	public JTextArea resultsArea;
	private JButton runTests;
	private JButton viewGame;
	private JScrollBar resultScrollBar;
	
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
		leftAICombo.setBorder(THIN_BORDER);
		rightAICombo = new JComboBox(AI);
		rightAICombo.setBorder(THIN_BORDER);
		
		JLabel vsLabel = new JLabel("vs");

		JPanel aiPanel = new JPanel();
		aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.LINE_AXIS));		
		aiPanel.add(leftAICombo);
		aiPanel.add(vsLabel);
		aiPanel.add(rightAICombo);
		aiPanel.setBorder(THIN_BORDER);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		controlPanel.add(aiPanel);
		controlPanel.add(buttonPanel);
		
		
		resultsArea = new JTextArea(20,105);
		resultsArea.setEditable(false);
		resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		
		JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
		resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		resultScrollBar = resultsScrollPane.getVerticalScrollBar();
		
		logln("Ready to rock!");
		
		
		
		JPanel testingPanel = new JPanel();
		testingPanel.setLayout(new BorderLayout());
		testingPanel.add(controlPanel, BorderLayout.PAGE_START);
		testingPanel.add(resultsScrollPane, BorderLayout.CENTER);
		testingPanel.setBorder(THIN_BORDER);
		
		contentPane.add(testingPanel, BorderLayout.CENTER);
		
		
		pack();
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if(event.getSource() == runTests) {
			runTests();
		}
	}

	private void runTests() {
		String s_leftAI = (String) leftAICombo.getSelectedItem();
		String s_rightAI = (String) rightAICombo.getSelectedItem();
		
		final ReversiAI leftAI = getAI(s_leftAI);
		final ReversiAI rightAI = getAI(s_rightAI);
		
		new Thread() {
			@Override
			public void run() {
				TestController testController = new TestController(8, leftAI, rightAI);
				testController.setLogger(TestFrame.this);
				testController.run(1000);
				testController.report();
				
			}
		}.start();
	}

	private ReversiAI getAI(String aiName) {
		if(ReversiAI.Types.GREEDY.equals(aiName)) {
			return new GreedyAI();
		} else if(ReversiAI.Types.HEURISTIC.equals(aiName)) {
			return new GreedyHeuristicAI();
		} else if(ReversiAI.Types.PLUGGABLE.equals(aiName)) {
			return new PluggableHeuristicAI();
		} else {
			throw new IllegalArgumentException("Unknown AI");
		}			
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

}
