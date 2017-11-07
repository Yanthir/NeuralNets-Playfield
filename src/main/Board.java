package main;

import java.util.ArrayList;

import neuralnetwork.*;

class Tile implements InputNode {

	public double value = 0.5;
	
	@Override
	public double getValue() {
		return value;
	}
	
}

public class Board {
	public ArrayList<Tile> tiles  = new ArrayList<>();
	int currentPlayer = 0;
	int startingPlayer = 0;
	
	Board() {
		for(int i = 0; i < 54; i++) {
			tiles.add(new Tile());
		}
	}
	
	int makeMove(int column) {
		if(tiles.get(column).value != 0.5) {
			return -1;
		}
		for(int i = 0; i < 5; i++) {
			if(tiles.get((i + 1) * 9 + column).value != 0.5) {
				tiles.get(i * 9 + column).value = currentPlayer;
				if(checkWin(column, i)) return -2;
				currentPlayer = currentPlayer == 0 ? 1 : 0;
				return i;
			}
		}
		tiles.get(45 + column).value = currentPlayer;
		if(checkWin(column, 5)) return -2;
		currentPlayer = currentPlayer == 0 ? 1 : 0;
		return 5;
	}
	
	void undoMove(int column) {
		for(int i = 0; i < 6; i++) {
			if(tiles.get((i) * 9 + column).value != 0.5) {
				tiles.get(i * 9 + column).value = 0.5;
				break;
			}
		}
		currentPlayer = currentPlayer == 0 ? 1 : 0;
	}
	
	void printBoard() {
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 9; j++) {
				if(tiles.get(i * 9 + j).value == 1) System.out.print("X ");
				else if(tiles.get(i * 9 + j).value == 0) System.out.print("O ");
				else System.out.print("- ");
			}
			System.out.println();
		}
	}
	
	boolean checkWin(int x, int y) {
		int streak = 0;
		// |
		for(int i = -3; i <= 3; i++) {
			if(y + i < 0 || y + i > 5) continue;
			if(tiles.get((y + i) * 9 + x).value == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		// /
		streak = 0;
		for(int i = -3; i <= 3; i++) {
			if(y + i < 0 || y + i > 5) continue;
			if(x - i < 0 || x - i > 8) continue;
			if(tiles.get((y + i) * 9 + x - i).value == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		// -
		streak = 0;
		for(int i = -3; i <= 3; i++) {
			if(x + i < 0 || x + i > 8) continue;
			if(tiles.get(y * 9 + x + i).value == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		// \
		streak = 0;
		for(int i = -3; i <= 3; i++) {
			if(y + i < 0 || y + i > 5) continue;
			if(x + i < 0 || x + i > 8) continue;
			if(tiles.get((y + i) * 9 + x + i).value == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		return false;
	}
	
	public void cleanBoard() {
		for(Tile t : tiles) {
			t.value = 0.5;
		}
		currentPlayer = startingPlayer;
	}
	
	public void invert() {
		for(Tile t : tiles) {
			if(t.value == 0.0) t.value = 1.0;
			else if(t.value == 1.0) t.value = 0.0;
		}
	}
}
