package com.ryanb3.JavaCAS.Test.FunctionGenerator;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.ryanb3.JavaCAS.Library.Functionv2;
import com.ryanb3.JavaCAS.Test.MathOlympicsOptimize;

public class Worker extends Thread {

	ArrayList<Functionv2> storage;
	String[] functions = { "1", "x", "abs(x)", "(x)^2", "1/(x)", "cos(x)", "sin(x)", "arctan(x)", "e^(x)", "ln(x)" };
	int[] costs = { 1, 7, 7, 12, 4, 14, 14, 3, 42, 4 };
	int cost = 0;
	int goal;
	double start;
	double end;
	double interval;
	int id;
	ArrayList<String> answers;

	public Worker(int cost, int goal, int id, double start, double end, double interval,
			ArrayList<String> answers) {
		this.storage = new ArrayList<Functionv2>();
		this.cost = cost;
		this.goal = goal;
		this.start = start;
		this.end = end;
		this.id = id;
		this.interval = interval;
		this.answers = answers;
	}

	public void run() {
		while (storage.size() < goal) {
			ArrayList<Integer> next = new ArrayList<Integer>();
			for (int x = 0; x < 10 * Math.random(); x++) {
				next.add((int) ((Math.random() * functions.length)));
			}
			if (this.checkPrice(cost, next)) {
				ArrayList<String> toProcess = new ArrayList<String>();
				for (int x : next) {
					toProcess.add(functions[x]);
				}
				for (int i = 0; i < 5; i++) {
					Functionv2 toAdd = randomFunction(toProcess);
					if (!storage.contains(toAdd)) {
						storage.add(toAdd);
					}
				}
			}
		}
		answers.add(getBiggestInt(storage, start, end, interval).baseFunction);
	}

	public Functionv2 getMostExtremas(ArrayList<Functionv2> toUse, double start, double end, double interval) {
		double biggestVal = 0;
		int count = 0;
		Functionv2 biggestFunc = null;
		for (Functionv2 x : toUse) {
			count++;
			double percent = (int)(10000 * 1 / (count / toUse.size())) / 10000;
			if(percent % 20 == 0) {
				JOptionPane.showMessageDialog(null, "%" + percent + " Done");
			}
			double current = x.getNumberOfExtremas(start, end, interval);
			if (current >= biggestVal && !undef(x)) {
				biggestVal = current;
				biggestFunc = x;
			}
		}
		return biggestFunc;
	}
	
	public Functionv2 getBiggestInt(ArrayList<Functionv2> toUse, double start, double end, double interval) {
		double biggestVal = 0;
		Functionv2 biggestFunc = null;
		for (Functionv2 x : toUse) {
			double current = x.integralOfFunc(start, end, interval);
			if (current >= biggestVal && !undef(x)) {
				biggestVal = current;
				biggestFunc = x;
			}
		}
		return biggestFunc;
	}
	
	public boolean undef(Functionv2 toCheck) {
		if(toCheck.getValueAt(Math.PI) == Double.POSITIVE_INFINITY || toCheck.getValueAt(1) == Double.POSITIVE_INFINITY || 
		toCheck.getValueAt(Math.PI / 2) == Double.POSITIVE_INFINITY) {
			return true;
		}
		return false;
	}

	public Functionv2 randomFunction(ArrayList<String> toUse) {
		ArrayList<String> functionVar = (ArrayList<String>) toUse.clone();
		int start = (int) (Math.random() * functionVar.size());
		double random = Math.random();
		Functionv2 toReturn = new Functionv2(functionVar.get(start));
		functionVar.remove(start);
		for (String x : functionVar) {
			if (random < .25) {
				toReturn.insertFunctionAtRandomPoint(x);
			} else {
				int levelToInsertAt = (int)(toReturn.getLevels() * Math.random());
				if (random < .5) {
					toReturn.addFuncAtLevel(x, levelToInsertAt);
				} else if (random < .75) {
					toReturn.multiplyFuncAtLevel(x, levelToInsertAt);
				} else {
					toReturn.subtractFuncAtLevel(x, levelToInsertAt);
				}
			}
		}
		return toReturn;
	}

	public boolean checkPrice(int toCheckTo, ArrayList<Integer> toCheck) {
		int totalCost = 0;
		for (Integer x : toCheck) {
			totalCost += costs[x];
		}
		if (totalCost > toCheckTo) {
			return false;
		}
		return true;
	}

}