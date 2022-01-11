package com.germistry.pipes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.germistry.utils.FileUtils;

public class ScoreManager {

	//current scores
	private long time;
	private long startingTime;
	private long bestTime;
	private int pipeCount;
	
	private int[] board = new int[GameBoard.ROWS * GameBoard.COLS];
	
	//file
	private String filePath;
	private String temp = "PipesTEMP.tmp";
	private GameBoard gameBoard;
	
	private boolean newGame;
	
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
		time = 0;
	}
		
	private void createFile() {
		FileWriter output = null;
		newGame = true;
		try {
			File f = new File(filePath, temp);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			//time 
			writer.write("" + 0);
			writer.newLine();
			//best time
			writer.write("" + 0);
			writer.newLine();
			//pipeCount
			writer.write("" + 1);
			writer.newLine();
			//game board int[]
//			for(int row = 0; row < GameBoard.ROWS; row++) {
//				for(int col = 0; col < GameBoard.COLS; col++) {
//					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
//						writer.write("" + 0);
//					}
//					else {
//						writer.write(0 + "-");
//					}
//				}
//			}
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
			writer.write("" + time);
			writer.newLine();
			writer.write("" + bestTime);
			writer.newLine();
			writer.write("" + pipeCount);
			writer.newLine();
//			for(int row = 0; row < GameBoard.ROWS; row++) {
//				for(int col = 0; col < GameBoard.COLS; col++) {
//					//convert 2d array to 1 dimension
//					int location = row * GameBoard.COLS + col;
//					//get tile
//					Tile tile = gameBoard.getBoard()[row][col];
//					//get the value of tile if null make 0
//					this.board[location] = tile != null ? tile.getValue() : 0;
//					
//					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
//						writer.write("" + board[location]);
//					}
//					else {
//						writer.write(board[location] + "-");
//					}
//				}
//			}
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
			time = Long.parseLong(reader.readLine());
			setStartingTime(time);
			bestTime = Long.parseLong(reader.readLine());
			pipeCount = Integer.parseInt(reader.readLine());
//			String[] boardString = reader.readLine().split("-");
//			for(int i = 0; i < boardString.length; i++) {
//				this.board[i] = Integer.parseInt(boardString[i]);
//			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int getPipeCount() {
		return pipeCount;
	}
	public void setPipeCount(int pipeCount) {
		this.pipeCount = pipeCount;
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
	public long getBestTime() {
		return bestTime;
	}
	public void setBestTime(long bestTime) {
		this.bestTime = bestTime;
	}
	public int[] getBoard() {
		return board;
	}
	public boolean newGame() {
		return newGame;
	}
	
}
