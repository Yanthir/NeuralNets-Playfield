package main;

import java.util.ArrayList;

class InsufficientArgumentsException extends Exception {
	InsufficientArgumentsException(String message) {
		super(message);
	}
	
	InsufficientArgumentsException() {
		super("Error: Insufficient arguments provided");
	}
}

public class NeuralNet {
	ArrayList<ArrayList<Perceptron>> layers = new ArrayList<>();
	double fitness = 0;
	
	NeuralNet(ArrayList<Perceptron> inputLayer, int layerCount, int ... layerSize) throws InsufficientArgumentsException {
		if(layerCount > layerSize.length) throw new InsufficientArgumentsException("Error: Insufficient layer size data");
		layers.add(inputLayer);
		ArrayList<Perceptron> newLayer;
		for(int i = 0; i < layerCount; i++) {
			newLayer = new ArrayList<>();
			for(int j = 0; j < layerSize[i]; j++) {
				newLayer.add(new Perceptron(layers.get(i)));
			}
			layers.add(newLayer);
		}
	}
	
	NeuralNet(NeuralNet toCopy) {
		for(ArrayList<Perceptron> l : toCopy.layers) {
			ArrayList<Perceptron> copiedLayer = new ArrayList<>();
			for(Perceptron p : l) {
				Perceptron copiedPerceptron;
				if(p.inputs == null) {
					copiedPerceptron = new Perceptron();
				} else {
					copiedPerceptron = new Perceptron(p.inputs);
				}
				copiedPerceptron.bias = p.bias;
				if(p.weights != null) {
					copiedPerceptron.weights = new ArrayList<Double>(p.weights);
				}
				copiedLayer.add(copiedPerceptron);
			}
			layers.add(copiedLayer);
		}
	}
	
	public int getNextMove() {
		double maxConfidency = layers.get(layers.size() - 1).get(0).getValue();
		int maxConfidencyOutput = 0;
		int loopMax = layers.get(layers.size() - 1).size();
		for(int i = 1; i < loopMax; i++) {
			double pendingConfidency = layers.get(layers.size() - 1).get(i).getValue();
			if(pendingConfidency > maxConfidency) {
				maxConfidency = pendingConfidency;
				maxConfidencyOutput = i;
			}
		}
		return maxConfidencyOutput;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(layers.get(layers.size() - 1).get(0));
		for(int i = 0; i < layers.get(layers.size() - 1).size(); i++) {
			sb.append(layers.get(layers.size() - 1).get(i).outputValue + " ");			
		}
		return sb.toString();
	}
	
	public void markForRecalculation() {
		for(Perceptron p : layers.get(layers.size() - 1)) {
			p.markForRecalculation();
		}
	}
	
	public void clearInputs() {
		for(Perceptron p : layers.get(0)) {
			p.outputValue = 0;
		}
	}
	
	public void mutate() {
		markForRecalculation();
		for(Perceptron p : layers.get(layers.size() - 1)) {
			p.mutate();
		}
	}
	
	boolean playAgainst(NeuralNet opponent) {
		NeuralNet currentPlayer = this;
		Board board = new Board(1);
		while(true) {
			markForRecalculation();
			int moveX = -1;
			int moveY = -1;
			moveX = currentPlayer.getNextMove();
			try {
				moveY = board.makeMove(moveX);
			} catch (Exception e) {
				System.out.println(currentPlayer);
			}
			if(moveY == -2 || moveY == -1) {
				if(moveY == -1) {
					if(currentPlayer == this) currentPlayer = opponent;
					else currentPlayer = this;
				}
				break;
			}
			
			currentPlayer.layers.get(0).get(moveX + moveY * 9).outputValue = 1;
			
			if(currentPlayer == this) currentPlayer = opponent;
			else currentPlayer = this;

			currentPlayer.layers.get(0).get(moveX + moveY * 9).outputValue = -1;
		}
		clearInputs();
		opponent.clearInputs();
		return currentPlayer == this;
	}
}
