package neuralnetwork;

import java.util.*;

class Perceptron implements InputNode {
	LinkedHashMap<InputNode, Double> inputs = null;
	
	double outputValue = 0;
	double bias = 0;
	
	public Perceptron() {
	}
	
	public Perceptron(ArrayList<InputNode> inputLayer) {
		this.inputs = new LinkedHashMap<>();
		for(InputNode i : inputLayer) {
			inputs.put(i, Math.random() * 2 - 1);
		}
		bias = Math.random() * 2 - 1;
	}
	
	public Perceptron(LinkedHashMap<InputNode, Double> inputs, double bias) {
		this.inputs = new LinkedHashMap<>(inputs);
		this.bias = bias;
	}
	
	public double getValue() {
		return outputValue;
	}
	
	public void calculateValue() {
		double val = 0.0;
		for(Map.Entry<InputNode, Double> node : inputs.entrySet()) {
			val += node.getKey().getValue() * node.getValue();
		}
		val += bias;
		outputValue = calculateValue(val);
	}
	
	public double getActivationValue() {
		double val = 0.0;
		for(Map.Entry<InputNode, Double> node : inputs.entrySet()) {
			val += node.getKey().getValue() * node.getValue();
		}
		return val;
	}
	
	private static double calculateValue(double x) {
		return (1.0 / (1.0 + Math.pow(Math.E, -x)));
	}
}

public class NeuralNet {
	ArrayList<ArrayList<Perceptron>> layers = new ArrayList<>();
	double fitness = 0;
	
	public NeuralNet(ArrayList<InputNode> inputLayer, int layerCount, int ... layerSize) throws Exception {
		if(layerCount > layerSize.length) throw new Exception("Error: Insufficient layer size data");
		ArrayList<Perceptron> newLayer = new ArrayList<>();;
		for(int i = 0; i < layerSize[0]; i++) {
			newLayer.add(new Perceptron(inputLayer));
		}
		layers.add(newLayer);
		for(int i = 1; i < layerCount; i++) {
			newLayer = new ArrayList<>();
			for(int j = 0; j < layerSize[i]; j++) {
				newLayer.add(new Perceptron(new ArrayList<InputNode>(layers.get(i - 1))));
			}
			layers.add(newLayer);
		}
	}
	
	public String toString() {
		StringBuffer ret = new StringBuffer();
		for(ArrayList<Perceptron> layer : layers) {
			for(Perceptron p : layer) {
				ret.append(p.getValue());
				ret.append(" ");
			}
			ret.append("\n");
		}
		return ret.toString();
	}
	
	public NeuralNet(NeuralNet toCopy) {
		for(ArrayList<Perceptron> layer : toCopy.layers) {
			ArrayList<Perceptron> copiedLayer = new ArrayList<>();
			for(Perceptron p : layer) {
				Perceptron copiedPerceptron = new Perceptron(p.inputs, p.bias);
				copiedLayer.add(copiedPerceptron);
			}
			layers.add(copiedLayer);
		}
	}
	
	public void calculate() {
		for(ArrayList<Perceptron> layer : layers) {
			for(Perceptron p : layer) {
				p.calculateValue();
			}
		}
	}
	
	public double[] getOutputs() {
		double[] ret = new double[layers.get(layers.size() - 1).size()];
		for(int i = 0; i < layers.get(layers.size() - 1).size(); i++) {
			ret[i] = layers.get(layers.size() - 1).get(i).getValue();
		}
		return ret;
	}
	
	public void trainToOutput(double[] outputs, double trainingCoefficient) {
		ArrayList<ArrayList<Double>> errorCoefficients = new ArrayList<>();
		for(int i = 0; i < layers.size(); i++) {
			errorCoefficients.add(new ArrayList<>());
			for(int j = 0; j < layers.get(i).size(); j++) {
				errorCoefficients.get(i).add(0.0);
			}
		}
		double[] errors = getOutputs();
		for(int i = 0; i < errors.length; i++) {
			errors[i] = outputs[i] - errors[i];
		}
		int loopSize = errorCoefficients.get(errorCoefficients.size() - 1).size();
		for(int i = 0; i < loopSize; i++) {
			errorCoefficients.get(errorCoefficients.size() - 1).set(i, calculateDerivative(layers.get(layers.size() - 1).get(i).getActivationValue()) * errors[i]);
		}
		for(int i = errorCoefficients.size() - 1; i > 0; i--) {
			for(int j = 0; j < layers.get(i - 1).size(); j++) {
				for(int h = 0; h < layers.get(i).size(); h++) {
					errorCoefficients.get(i - 1).set(j, errorCoefficients.get(i - 1).get(j) + errorCoefficients.get(i).get(h));
				}
				errorCoefficients.get(i - 1).set(j, errorCoefficients.get(i - 1).get(j) * calculateDerivative(layers.get(i - 1).get(j).getActivationValue()));
			}
		}
		for(int i = 0; i < layers.size(); i++) {
			for(int j = 0; j < layers.get(i).size(); j++) {
				for(Map.Entry<InputNode, Double> node : layers.get(i).get(j).inputs.entrySet()) {
					node.setValue(node.getValue() + (errorCoefficients.get(i).get(j) * node.getKey().getValue() * trainingCoefficient));
				}
			}
		}
	}
	
	private static double calculateDerivative(double x) {
		return (Math.pow(Math.E, -x) / Math.pow((1 + Math.pow(Math.E, -x)), 2));
	}
}
