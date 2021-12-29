package com.germistry.tetris;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.germistry.utils.FileUtils;

public class ScoreManager {

	//current scores
	private int currentScore;
	private int currentTopScore;
	private long time;
	private long startingTime;
	//file
	private String filePath;
	private String temp = "TetrisTEMP.tmp"; 
	private GameBoard gameBoard;
	
	private boolean newGame;
	
	//next shape
	private int nextShapeType;
	//current shape & location
	private int currentShapeType;
	private int shapeX;
	private int shapeY;
	private int shapeRows, shapeCols;
	private int[] shapeCoords;
	//board image
	private int[] board = new int[GameBoard.ROWS * GameBoard.COLS];
	//current shape coords
	
	
	public ScoreManager(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		//get to file location
		filePath = FileUtils.filePath();
	}
	//reset when game is lost
	public void reset() {
		File f = new File(filePath, temp);
		if (f.isFile()) {
			f.delete();
		}
		newGame = true;
		setStartingTime(0);
		currentScore = 0;
		time = 0;
	}
	
	private void createFile() {
		FileWriter output = null;
		newGame = true;
		try {
			File f = new File(filePath, temp);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			//current score
			writer.write("" + 0);
			writer.newLine();
			//current top score
			writer.write("" + 0);
			writer.newLine();
			//time 
			writer.write("" + 0);
			writer.newLine();
			//next shape id
			writer.write("" + 7); // 7 is a null
			writer.newLine();
			//current shape id
			writer.write("" + 7); // 7 is a null
			writer.newLine();
			//current shape x & y
			writer.write("4,0"); // the initial value of a new spawn
			writer.newLine();
			//shape matrix dimensions
			writer.write("0,0");
			writer.newLine();
			//shape matrix data
			writer.write("" + 0);
			writer.newLine();
			//board
			for(int row = 0; row < GameBoard.ROWS; row++) {
				for(int col = 0; col < GameBoard.COLS; col++) {
					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
						writer.write("" + 0); 
					}
					else {
						writer.write(0 + "-");
					}
				}
			}
			writer.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void saveGame() {
		FileWriter output = null;
		if(newGame) newGame = false;
		try {
			File f = new File(filePath, temp);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("" + currentScore);
			writer.newLine();
			writer.write("" + currentTopScore);
			writer.newLine();
			writer.write("" + time);
			writer.newLine();
			writer.write("" + GameBoard.nextShape.getId());
			writer.newLine();
			writer.write("" + GameBoard.currentShape.getId());
			writer.newLine();
			writer.write("" + GameBoard.currentShape.getShapeX() + "," + GameBoard.currentShape.getShapeY());
			writer.newLine();
			writer.write("" + GameBoard.currentShape.getCoords().length + "," + GameBoard.currentShape.getCoords()[0].length);
			writer.newLine();
			this.shapeCoords = new int[GameBoard.currentShape.getCoords().length * GameBoard.currentShape.getCoords()[0].length];
			for(int row = 0; row < GameBoard.currentShape.getCoords().length; row++) {
				for(int col = 0; col < GameBoard.currentShape.getCoords()[0].length; col++) {
					int matrixPos = row * GameBoard.currentShape.getCoords()[0].length + col;
					int coord = GameBoard.currentShape.getCoords()[row][col];
					this.shapeCoords[matrixPos] = coord != 0 ? 1 : 0;
					if(row == GameBoard.currentShape.getCoords().length - 1 && col == GameBoard.currentShape.getCoords()[0].length - 1) {
						writer.write("" + shapeCoords[matrixPos]);
					}
					else {
						writer.write(shapeCoords[matrixPos] + "-");
					}
				}
			}
			writer.newLine();
			for(int row = 0; row < GameBoard.ROWS; row++) {
				for(int col = 0; col < GameBoard.COLS; col++) { 
					int location = row * GameBoard.COLS + col;
					int cell = gameBoard.getBoard()[row][col];
					this.board[location] = cell != 0 ? cell : 0;
					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
						writer.write("" + board[location]);
					}
					else {
						writer.write(board[location] + "-");
					}
				}
			}
			writer.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadGame() {
		try {
			File f = new File(filePath, temp);
			if(!f.isFile()) {
				createFile();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			currentScore = Integer.parseInt(reader.readLine());
			currentTopScore = Integer.parseInt(reader.readLine());
			time = Long.parseLong(reader.readLine());
			setStartingTime(time);	
			nextShapeType = Integer.parseInt(reader.readLine());
			currentShapeType = Integer.parseInt(reader.readLine());
			String[] shapeXYLine = reader.readLine().split(",");
			shapeX = Integer.parseInt(shapeXYLine[0]);
			shapeY = Integer.parseInt(shapeXYLine[1]);
			String[] matrixSizeString = reader.readLine().split(",");
			shapeRows = Integer.parseInt(matrixSizeString[0]);
			shapeCols = Integer.parseInt(matrixSizeString[1]);
			String s = reader.readLine();
			if(s.length() > 1) { 
				String[] shapeCoordsString = s.split("-");
				this.shapeCoords = new int[shapeCoordsString.length];
				for(int i = 0; i < shapeCoordsString.length; i++) {
					this.shapeCoords[i] = Integer.parseInt(shapeCoordsString[i]);
				}
			}
			else {
				this.shapeCoords = new int[1];
				this.shapeCoords[0] = 0;
			} 
			String[] cellString = reader.readLine().split("-");
			for(int i = 0; i < cellString.length; i++) {
				this.board[i] = Integer.parseInt(cellString[i]);
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}	
			
	public int getCurrentScore() {
		return currentScore;
	}
	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}
	public int getCurrentTopScore() {
		return currentTopScore;
	}
	public void setCurrentTopScore(int currentTopScore) {
		this.currentTopScore = currentTopScore;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time + startingTime;
	}
	public long getStartingTime() {
		return startingTime;
	}
	public void setStartingTime(long startingTime) {
		this.startingTime = startingTime;
	}
	public int[] getBoard() {
		return board;
	}
	public int getNextShapeType() {
		return nextShapeType;
	}
	public int getCurrentShapeType() {
		return currentShapeType;
	}
	public int getShapeX() {
		return shapeX;
	}
	public int getShapeY() {
		return shapeY;
	}
	public int getShapeRows() {
		return shapeRows;
	}
	public int getShapeCols() {
		return shapeCols;
	}
	public int[] getShapeCoords() {
		return shapeCoords;
	}
	public boolean newGame() {
		return newGame;
	}
}
