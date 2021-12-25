package com.germistry.minesweeper;

public class Flag {
	
	private int x, y;
	private boolean correct;
	private boolean revealed;
	
	public Flag(boolean correct, int x, int y, boolean revealed) {
		this.x = x;
		this.y = y;
		this.correct = correct;
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

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}
	
}
