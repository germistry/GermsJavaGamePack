package com.germistry.tetris;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shape {

	private int shapeX = 4, shapeY = 0;
	private int colour;
	private GameBoard board;
	private boolean collision = false;
	private int normalSpeed = 60;
	private int dropSpeed = 2;
	private int delay = 60;
	private int counter;
	
	private int deltaX = 0;
	private int[][] coords;
	private int id;
	
	public Shape(int[][] coords, GameBoard board, int colour, int id) {
		this.id = id;
		this.coords = coords;
		this.board = board;
        this.colour = colour;
	}
	
	public void update() {
		counter++;
		if(collision) {
			//set colour for board
			for(int row = 0; row < coords.length; row++) {
				for(int col = 0; col < coords[0].length; col++) {
					if(coords[row][col] != 0) { 						
						board.getBoard()[shapeY + row][shapeX + col] = colour;
					}
				}
			}
			//next shape
			board.setCurrentShape();
		}
		else {
			moveShape();
			dropShape();
		}
	}
	
	public void render(Graphics2D g) {
		for(int row = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length; col++) {
				if(coords[row][col] != 0) {
					g.setColor(new Color(colour));
					g.fillRect(col * GameBoard.UNIT_SIZE + shapeX * GameBoard.UNIT_SIZE, row * GameBoard.UNIT_SIZE + shapeY * GameBoard.UNIT_SIZE, GameBoard.UNIT_SIZE, GameBoard.UNIT_SIZE);
				}
			}
		}
	}
	
	public void resetPosition() {
		this.shapeX = GameBoard.COLS / 2 - coords[0].length / 2;
		this.shapeY = 0;
		collision = false;
	}
		
	public void rotateShape() {
		int[][] rotatedShape = transposeMatrix(coords);
		reverseRows(rotatedShape);
		if(shapeX + rotatedShape[0].length > GameBoard.COLS - 1
				|| shapeY + rotatedShape.length > GameBoard.ROWS - 1) {
			return;
		}
		for(int row = 0; row < rotatedShape.length; row++) {
			for(int col = 0; col < rotatedShape[row].length; col++) {
				if(rotatedShape[row][col] != 0) {
					if(board.getBoard()[shapeY + row][shapeX + col] != 0) {
						return;
					}
				}
			}
		}
		coords = rotatedShape;
	}
	
	private int[][] transposeMatrix(int[][] matrix) {
		int[][] m = new int[matrix[0].length][matrix.length];
		for(int row = 0; row < matrix.length; row++) {
			for(int col = 0; col < matrix[0].length; col++) {
				m[col][row] = matrix[row][col];
			}
		}
		return m;
	}
	
	private void reverseRows(int[][] matrix) {
		int centre = matrix.length / 2;
		for(int i = 0; i < centre; i++) {
			int[] m = matrix[i];
			matrix[i] = matrix[matrix.length - i - 1];
			matrix[matrix.length - i - 1] = m;
		}
	}
	
	private void moveShape() {
		boolean moveX = true;
		if(!(shapeX + deltaX + coords[0].length > GameBoard.COLS) && !(shapeX + deltaX < 0)) {
			for(int row = 0; row < coords.length; row++) {
				for(int col = 0; col < coords[row].length; col++) {
					if(coords[row][col] != 0) {
						if(board.getBoard()[shapeY + row][shapeX + deltaX + col] != 0) {
							moveX = false;
						}
					}
				}
			}
			if(moveX) {
				shapeX += deltaX;
			}
		}
		deltaX = 0;
	}
	
	private void dropShape() {
		if(board.shapesCanMove()) {
			if(!(shapeY + 1 + coords.length > GameBoard.ROWS)) {
				for(int row = 0; row < coords.length; row++) {
					for(int col = 0; col < coords[row].length; col++) {
						if(coords[row][col] != 0) {
							if(board.getBoard()[shapeY + 1 + row][shapeX + deltaX + col] != 0) {
								collision = true;
							}
						}
					}
				}
				if(!collision) {
					if(counter % delay == 0) {
						shapeY++;
					}
				} 
			}	
			else {
				collision = true;
			}
		}
	}
	
	public int getShapeX() {
		return shapeX;
	}

	public int getShapeY() {
		return shapeY;
	}

	public void setShapeX(int shapeX) {
		this.shapeX = shapeX;
	}

	public void setShapeY(int shapeY) {
		this.shapeY = shapeY;
	}

	public int getColour() {
		return colour;
	}

	public int[][] getCoords() {
		return coords;
	}

	public void setCoords(int[][] coords) {
		this.coords = coords;
	}

	public int getNormalSpeed() {
		return normalSpeed;
	}

	public void setNormalSpeed(int normalSpeed) {
		this.normalSpeed = normalSpeed;
	}

	public int getDropSpeed() {
		return dropSpeed;
	}

	public void setDropSpeed(int dropSpeed) {
		this.dropSpeed = dropSpeed;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public int getId() {
		return id;
	}
	
	
}
