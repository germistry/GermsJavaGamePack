package com.germistry.minesweeper;

public class Mine {
	
	private int x, y;
	private boolean revealed = false;
	
	public Mine(boolean revealed, int x, int y) {
		this.x = x;
		this.y = y;
		this.revealed = revealed;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}
	
	
}
