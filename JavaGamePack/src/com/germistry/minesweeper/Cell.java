package com.germistry.minesweeper;

public class Cell {

	private int x, y;
	private int value = 0;
	private boolean revealed = false;
	
	//0 for no mines, 1 - 8 for value otherwise 9 for a mine in the position, 10 is a null read from file

	public Cell(int value, int x, int y, boolean revealed) {
		this.x = x;
		this.y = y;
		this.value = value;
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}	
}
