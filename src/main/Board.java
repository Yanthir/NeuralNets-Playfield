package main;

public class Board {
	int tiles[][] = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	int currentPlayer = 1;
	
	Board(int player) {
		currentPlayer = player;
	}
	
	int makeMove(int column) {
		if(tiles[0][column] != 0) {
			return -1;
		}
		for(int i = 0; i < 5; i++) {
			if(tiles[i + 1][column] != 0) {
				tiles[i][column] = currentPlayer;
				if(checkWin(column, i)) return -2;
				currentPlayer *= -1;
				return i;
			}
		}
		tiles[5][column] = currentPlayer;
		if(checkWin(column, 5)) return -2;
		currentPlayer *= -1;
		return 5;
	}
	
	void printBoard() {
		for(int[] i : tiles) {
			for(int j : i) {
				if(j == -1) System.out.print("X ");
				else if(j == 1) System.out.print("O ");
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
			if(tiles[y + i][x] == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		// /
		streak = 0;
		for(int i = -3; i <= 3; i++) {
			if(y + i < 0 || y + i > 5) continue;
			if(x - i < 0 || x - i > 8) continue;
			if(tiles[y + i][x - i] == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		// -
		streak = 0;
		for(int i = -3; i <= 3; i++) {
			if(x + i < 0 || x + i > 8) continue;
			if(tiles[y][x + i] == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		// \
		streak = 0;
		for(int i = -3; i <= 3; i++) {
			if(y + i < 0 || y + i > 5) continue;
			if(x + i < 0 || x + i > 8) continue;
			if(tiles[y + i][x + i] == currentPlayer) streak++;
			else streak = 0;
			if(streak == 4) return true;
		}
		return false;
	}
}
