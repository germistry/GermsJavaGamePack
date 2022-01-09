package com.germistry.pipes;

public class Tap {

	private int row, col;
	// 0 down, 1 up, 2 right, 3 left
	private int direction;
	private boolean flowing;
	
	public Tap(boolean flowing, int row, int col, int direction) {
		this.flowing = flowing;
		this.row = row;
		this.col = col;
		this.direction = direction;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public boolean isFlowing() {
		return flowing;
	}

	public void setFlowing(boolean flowing) {
		this.flowing = flowing;
	}
}
