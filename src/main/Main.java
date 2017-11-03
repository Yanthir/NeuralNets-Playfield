package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
	static ArrayList<NeuralNet> nets = new ArrayList<>();
	public static void main(String[] args) {
		for(int i = 0; i < 30; i++) {
			ArrayList<Perceptron> inputLayer = new ArrayList<>();
			for(int j = 0; j < 9*6; j++) {
				inputLayer.add(new Perceptron());
			}
			try {
				NeuralNet net = new NeuralNet(inputLayer, 4, 150, 300, 100, 9);
				nets.add(net);
			} catch (InsufficientArgumentsException e) {
				System.out.println(e.getMessage());
				return;
			}
		}
		Scanner s = new Scanner(System.in);
		int generation = 1;
		while(true) {
			int trainingRounds = 20;
			while(trainingRounds > 0) {
				System.out.println("Generation " + generation + " are now playing...");
				ArrayList<Thread> runningGames = new ArrayList<>();
				for(NeuralNet n : nets) {
					Thread game = new Thread(() -> {
					for(NeuralNet m : nets) {
						if(n == m) continue;
							if(n.playAgainst(m)) {
								n.fitness += 1;
								m.fitness -= 1;
							} else {
								m.fitness += 1;
								n.fitness -= 1;
							}
						}
					});
					runningGames.add(game);
					game.start();
				}
				for(Thread t : runningGames) {
					try {
						t.join();
					} catch (InterruptedException e1) {
					}
				}
				
				nets.sort(new Comparator<NeuralNet>() {
					@Override
					public int compare(NeuralNet n, NeuralNet m) {
						return m.fitness - n.fitness == 0 ? 0 : m.fitness > n.fitness ? 1 : -1;
					}
				});
				
				for(NeuralNet n : nets) {
					n.fitness -= nets.get(nets.size() - 1).fitness;
					System.out.println(n.fitness);
				}
				
				ArrayList<NeuralNet> newGeneration = new ArrayList<>();
					
				double sumOfFitness = 0;
				for(NeuralNet n : nets) {
					sumOfFitness += n.fitness;
				}
				for(int i = 0; i < nets.size(); i++) {
					double spin = Math.random() * sumOfFitness;
					for(int j = 0; j < nets.size(); j++) {
						spin -= nets.get(j).fitness;
						if(spin <= 0) {
							NeuralNet newNet = new NeuralNet(nets.get(j));
							newNet.mutate();
							newGeneration.add(newNet);
							break;
						}
					}
				}
				
				for(NeuralNet n : newGeneration) {
					n.fitness = 0;
					n.clearInputs();
					n.markForRecalculation();
				}
				
				nets = newGeneration;
				trainingRounds--;
				generation++;
			}
			
			Board board = new Board(1);
			while(true) {
				nets.get(0).markForRecalculation();
				int moveX = -1;
				int moveY = -1;
				while(moveY == -1) {
					moveX = nets.get(0).getNextMove();
					if(moveX == -1) break;
					moveY = board.makeMove(moveX);
				}
				board.printBoard();
				if(moveY == -2) {
					break;
				}
				if(moveX == -1) {
					break;
				}
				
				nets.get(0).layers.get(0).get(moveX + moveY * 9).outputValue = 1;
				
				moveX = -1;
				moveY = -1;
				while(moveY == -1) {
					moveX = s.nextInt() - 1;
					moveY = board.makeMove(moveX);
				}
				if(moveY == -2) {
					break;
				}

				nets.get(0).layers.get(0).get(moveX + moveY * 9).outputValue = -1;
			}
			board.printBoard();
		}
	}
}
