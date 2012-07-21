README
======

Board.java - the Othello board, and logic for checking moves
Controller.java - interface for running the game/GUI
GreedyAI.java - simple greedy AI
Listener.java - interface for listening to Board changes
OnePlayerController.java - implement 1-player game - AI is player 2
ReversiAI.java - interface for an AI
ReversiGUI.java - SWING-based Othello GUI
TwoPlayerController.java - implement 2-player game

You can build using make or using script:
./build.sh

To run (2 player): 
java -classpath bin othello.controller.TwoPlayerController
       
       (1 player): 
java -classpath bin othello.controller.OnePlayerController

