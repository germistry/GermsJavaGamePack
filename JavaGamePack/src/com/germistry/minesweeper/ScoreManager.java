package com.germistry.minesweeper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.germistry.utils.FileUtils;


public class ScoreManager {
	//current times
	private long time;
	private long startingTime;
	private long bestTime;
	//minecount & flagCount
	private int mineCount; //actual minecount for game
	private int flagCount;
	private int blownMineRow, blownMineCol; //if lost records the mine at coords which blew 
	private int displayMines; //mines as displayed on screen, minecount - flags, 
    
	private int[] cells = new int[GameBoard.ROWS * GameBoard.COLS];
    private int[] revealed = new int[GameBoard.ROWS * GameBoard.COLS];
    private int[] flags = new int[GameBoard.ROWS * GameBoard.COLS];//if null at position treat as a zero
    		
	//file 
    private String filePath;
	private String temp = "MinesweeperTEMP.tmp";
	
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
			//actual mines
			writer.write("" + 0);
			writer.newLine();
			//flags
			writer.write("" + 0);
			writer.newLine();
			//displayMines 
			writer.write("" + 0);
			writer.newLine();
			//blown mine X & Y 
			writer.write("0,0");
			writer.newLine();
			//cells
			for(int row = 0; row < GameBoard.ROWS; row++) {
				for(int col = 0; col < GameBoard.COLS; col++) {
					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
						writer.write("" + 10); //10 used as a null as 0 is an empty value
					}
					else {
						writer.write(10 + "-");
					}
				}
			}
			writer.newLine();
			//revealed boolean[]
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
			//flags
			writer.newLine();
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
			//time
			writer.write("" + time);
			writer.newLine();
			//best time
			writer.write("" + bestTime);
			writer.newLine();
			//actual mines
			writer.write("" + gameBoard.getMineCount());
			writer.newLine();
			//flag count
			writer.write("" + gameBoard.getFlagCount());
			writer.newLine();
			//display mines
			writer.write("" + displayMines);
			writer.newLine();
			//blown mine X & Y
			writer.write("" + gameBoard.getBlownMineRow() + "," + gameBoard.getBlownMineCol());
			writer.newLine();
			//cells
			for(int row = 0; row < GameBoard.ROWS; row++) {
				for(int col = 0; col < GameBoard.COLS; col++) {
					int location = row * GameBoard.COLS + col;
					Cell cell = gameBoard.getCells()[row][col];
					this.cells[location] = cell != null ? cell.getValue() : 10;
					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
						writer.write("" + cells[location]);
					}
					else {
						writer.write(cells[location] + "-");
					}
				}
			}
			//revealed boolean
			writer.newLine();
			for(int row = 0; row < GameBoard.ROWS; row++) {
				for(int col = 0; col < GameBoard.COLS; col++) {
					int location = row * GameBoard.COLS + col;
					boolean cell = gameBoard.getRevealed()[row][col];
					this.revealed[location] = cell ? 1 : 0;
					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
						writer.write("" + revealed[location]);
					}
					else {
						writer.write(revealed[location] + "-");
					}
				}
			}
			//flags
			writer.newLine();
			for(int row = 0; row < GameBoard.ROWS; row++) {
				for(int col = 0; col < GameBoard.COLS; col++) {
					int location = row * GameBoard.COLS + col;
					Flag cell = gameBoard.getFlags()[row][col];
					this.flags[location] = cell != null ? 1 : 0;
					if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1) {
						writer.write("" + flags[location]);
					}
					else {
						writer.write(flags[location] + "-");
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
			time = Long.parseLong(reader.readLine());
			setStartingTime(time);
			bestTime = Long.parseLong(reader.readLine());
			mineCount = Integer.parseInt(reader.readLine());
			flagCount = Integer.parseInt(reader.readLine());
			displayMines = Integer.parseInt(reader.readLine());
			String[] blownMineLine = reader.readLine().split(",");
			blownMineRow = Integer.parseInt(blownMineLine[0]);
			blownMineCol = Integer.parseInt(blownMineLine[1]);
			
			String[] cellString = reader.readLine().split("-");
			for(int i = 0; i < cellString.length; i++) {
				this.cells[i] = Integer.parseInt(cellString[i]);
			}
			String[] revealedString = reader.readLine().split("-");
			for(int i = 0; i < revealedString.length; i++) {
				this.revealed[i] = Integer.parseInt(revealedString[i]);
			}
			String[] flagString = reader.readLine().split("-");
			for(int i = 0; i < flagString.length; i++) {
				this.flags[i] = Integer.parseInt(flagString[i]);
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
	public int getDisplayMines() {
		return displayMines;
	}
	public void setDisplayMines(int displayMines) {
		this.displayMines = displayMines;
	}
	public int getMineCount() {
		return mineCount;
	}
	public int getFlagCount() {
		return flagCount;
	}
	public int getBlownMineRow() {
		return blownMineRow;
	}
	public int getBlownMineCol() {
		return blownMineCol;
	}
	public int[] getCells() {
		return cells;
	}
	public int[] getRevealed() {
		return revealed;
	}
	public int[] getFlags() {
		return flags;
	}
	public boolean newGame() {
		return newGame;
	}
}
