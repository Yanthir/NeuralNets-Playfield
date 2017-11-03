package main;

import java.util.ArrayList;

public class Perceptron {
	ArrayList<Perceptron> inputs = null;
	ArrayList<Double> weights = null;
	
	boolean calculated = false;
	double outputValue = 0;
	double bias = 0;
	
	Perceptron() {
	}
	
	Perceptron(ArrayList<Perceptron> inputLayer) {
		inputs = inputLayer;
		weights = new ArrayList<>();
		for(int i = 0; i < inputLayer.size(); i++) weights.add(Math.random() * 2 - 1);
		bias = Math.random() * 2 - 1;
	}
	
	double getValue() {
		if(inputs == null || calculated == true) {
			return outputValue;
		} else {
			double ret = 0;
			for(int i = 0; i < inputs.size(); i++) {
				ret += inputs.get(i).getValue() * weights.get(i);
			}
			ret += bias;
			ret = calculateValue(ret);
			outputValue = ret;
			calculated = true;
			return ret;
		}
	}
	
	private static double calculateValue(double x) {
		return Math.min(1.0, Math.max(-1.0, 2.0 / (1.0 + Math.pow(Math.E, -x)) - 1));
	}
	
	public String toString() {
		if(inputs == null) {
			return Double.toString(outputValue);
		} else {
			StringBuffer sb = new StringBuffer();
			for(Perceptron p : inputs) {
				sb.append(Double.toString(p.outputValue));
				sb.append(' ');
			}
			sb.append('\n');
			if(inputs.get(0).inputs == null) {
				return sb.toString();
			} else {
				return inputs.get(0) + sb.toString();
			}
		}
	}
	
	public void markForRecalculation() {
		calculated = false;
		for(Perceptron p : inputs) {
			if(p.calculated) p.markForRecalculation();
		}
	}
	
	public void mutate() {
		if(calculated) return;
		for(int i = 0; i < weights.size(); i++) {
			if(Math.random() > 0.95) {
				weights.set(i, weights.get(i) * (Math.random() * 1.01 - 0.02));
			}
		}
		if(Math.random() > 0.95) {
			bias  *= (Math.random() * 1.01 - 0.02);
		}
		for(Perceptron p : inputs) {
			if(p.inputs != null) p.mutate();
		}
		calculated = true;
	}
}
