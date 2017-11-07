package main;

import java.util.ArrayList;
import java.util.Scanner;

import neuralnetwork.*;

public class Main {
	static NeuralNet player1 = null;
	static NeuralNet player2 = null;
	static Board board = new Board();
	
	static void playTrain(NeuralNet p1, NeuralNet p2) {
		NeuralNet currentPlayer = p1;
		for(int i = 0; i < 1000; i++) {
			System.out.println("Game " + i);
			board.cleanBoard();
			currentPlayer = p1;
			boolean gameInProgress = true;
			int lastMove = 0;
			while(gameInProgress) {
				currentPlayer.calculate();
				int nextMove = 0;
				double[] networkOutput = currentPlayer.getOutputs();
				while(true) {
					for(int j = 0; j < 9; j++) {
						if(networkOutput[j] > networkOutput[nextMove]) {
							nextMove = j;
						}
					}
					int result = board.makeMove(nextMove);
					if(result == -2) {
						//current win
						gameInProgress = false;
						board.undoMove(nextMove);
						board.undoMove(lastMove);
						double[] blockingMove = {0, 0, 0, 0, 0, 0, 0, 0, 0};
						blockingMove[nextMove] = 1;
						if(currentPlayer == p1) {
							p2.trainToOutput(blockingMove, 0.01);
						} else {
							p1.trainToOutput(blockingMove, 0.01);
						}
						break;
					}
					if(result == -1) {
						if(networkOutput[nextMove] == 0) {
							try {
								player2 = new NeuralNet(new ArrayList<InputNode>(board.tiles), 3, 100, 100, 9);
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
							gameInProgress = false;
							break;
						}
						networkOutput[nextMove] = 0;
						continue;
					}
					break;
				}
				lastMove = nextMove;
				currentPlayer = (currentPlayer == p1 ? p2 : p1);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			player1 = new NeuralNet(new ArrayList<InputNode>(board.tiles), 3, 100, 100, 9);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			player2 = new NeuralNet(new ArrayList<InputNode>(board.tiles), 3, 100, 100, 9);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Scanner sc = new Scanner(System.in);
		
		boolean running = true;
		while(running) {
			playTrain(player1, player2);
			
			board.cleanBoard();
			boolean gameInProgress = true;
			while(gameInProgress) {
				player1.calculate();
				int nextMove = 0;
				double[] networkOutput = player1.getOutputs();
				while(true) {
					for(int j = 0; j < 9; j++) {
						if(networkOutput[j] > networkOutput[nextMove]) {
							nextMove = j;
						}
					}
					int result = board.makeMove(nextMove);
					if(result == -2) {
						gameInProgress = false;
						break;
					}
					if(result == -1) {
						if(networkOutput[nextMove] == 0) {
							gameInProgress = false;
							break;
						}
						networkOutput[nextMove] = 0;
						continue;
					}
					break;
				}
				if(!gameInProgress) break;
				
				board.printBoard();
				
				while(true) {
					int playerMove = sc.nextInt();
					if(playerMove < 1 || playerMove > 9) {
						running = false;
						gameInProgress = false;
						break;
					}
					playerMove--;
					int result = board.makeMove(playerMove);
					if(result == -2) {
						double[] blockingMove = player1.getOutputs();
						board.undoMove(nextMove);
						board.undoMove(playerMove);
						blockingMove[playerMove] = 1;
						player1.trainToOutput(blockingMove, 0.1);
						gameInProgress = false;
						break;
					}
					if(result == -1) {
						continue;
					}
					break;
				}
			}
			board.printBoard();
		}
		
		sc.close();
	}
}
