package com.germistry.snake;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

//this does the temp saving during the game, the leaderboard class does the final saving 
public class ScoreManager {
	
	//current scores
	private int currentScore;
	private int currentTopScore;
	private long time;
	private long startingTime;
	
	private int[] board = new int[GameBoard.ROWS * GameBoard.COLS];
	private int fruitX, fruitY, fruitColValue;
	
	private ArrayList<Integer> snakeX = new ArrayList<Integer>(); 
	private ArrayList<Integer> snakeY = new ArrayList<Integer>();
	private int direction;
	//file
	private String filePath;
	private String temp = "SNAKETEMP.tmp";
	private GameBoard gameBoard;
	
	private boolean newGame;
	
	public ScoreManager(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		//get to file location
		filePath = new File("").getAbsolutePath();
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
			//fruit location/colour as 0,0,0
			writer.write("0,0,0");
			writer.newLine();
			//snake direction
			writer.write("" + 0);
			//snake arraylist  values 
			writer.write("" + 0);
			writer.newLine();
			writer.write("" + 0);
			writer.newLine();
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
			writer.write("" + gameBoard.getFruitX() + "," + gameBoard.getFruitY() + "," + gameBoard.getFruitColour());
			writer.newLine();
			writer.write("" + gameBoard.getDirection());
			writer.newLine();
			for (int i = 0; i < gameBoard.getSnakeX().size(); i++) {
				int x = gameBoard.getSnakeX().get(i);
				snakeX.add(x);
				if(i == gameBoard.getSnakeX().size() - 1) {
					writer.write("" + x);
				}
				else {
					writer.write(x + "-");
				}
			}
			writer.newLine();
			for (int i = 0; i < gameBoard.getSnakeY().size(); i++) {
				int y = gameBoard.getSnakeY().get(i);
				snakeY.add(y);
				if(i == gameBoard.getSnakeY().size() - 1) {
					writer.write("" + y);
				}
				else {
					writer.write(y + "-");
				}
			}
			writer.newLine();
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
			
			String[] fruitLine = reader.readLine().split(",");
			fruitX = Integer.parseInt(fruitLine[0]);
			fruitY = Integer.parseInt(fruitLine[1]);
			fruitColValue = Integer.parseInt(fruitLine[2]);
			
			direction = Integer.parseInt(reader.readLine());
			
			String s = reader.readLine();
			if(s.length() > 1) { 
				String[] snakeXString = s.split("-");
				for(int i = 0; i < snakeXString.length; i++) {
					int x = Integer.parseInt(snakeXString[i]);
					snakeX.add(x);
				}
			}
			else {
				snakeX.add(Integer.parseInt(s));
			} 
			String s2 = reader.readLine();
			if(s2.length() > 1) {
				String[] snakeYString = s2.split("-");
				for(int i = 0; i < snakeYString.length; i++) {
					int y = Integer.parseInt(snakeYString[i]);
					snakeY.add(y);
				}
			}
			else {
				snakeY.add(Integer.parseInt(s2));
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int getFruitX() {
		return fruitX;
	}
	public int getFruitY() {
		return fruitY;
	}
	public int getFruitColValue() {
		return fruitColValue;
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
	public boolean newGame() {
		return newGame;
	}
	public ArrayList<Integer> getSnakeX() {
		return snakeX;
	}
	public ArrayList<Integer> getSnakeY() {
		return snakeY;
	}
	public int getDirection() {
		return direction;
	}
}
