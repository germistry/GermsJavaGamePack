package com.germistry.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.germistry.utils.FileUtils;

public class LeaderBoard {

	private static LeaderBoard leaderBoard;
	private String filePath;
	private String highScores;
	
	//All time leaderboards
	private ArrayList<Integer> top2048Scores;
	private ArrayList<Integer> top2048Tiles;
	private ArrayList<Long> top2048Times;
	private ArrayList<Integer> topSnakeScores;
	private ArrayList<Integer> topMinesweeperMineCount;
	private ArrayList<Long> topMinesweeperTimes;

	private LeaderBoard() { 
		filePath = FileUtils.filePath();
		highScores = "Scores.txt";
		top2048Scores = new ArrayList<Integer>();
		top2048Tiles = new ArrayList<Integer>();
		top2048Times = new ArrayList<Long>();
		topSnakeScores = new ArrayList<Integer>();
		topMinesweeperMineCount = new ArrayList<Integer>();
		topMinesweeperTimes = new ArrayList<Long>();
	}
	
	public static LeaderBoard getInstance() {
		if(leaderBoard == null) {
			leaderBoard = new LeaderBoard();
		}
		return leaderBoard;
	}
	
	public void addTop2048Score(int score) {
		for(int i = 0; i < top2048Scores.size(); i++) {
			if (score >= top2048Scores.get(i)) {
				top2048Scores.add(i, score);
				top2048Scores.remove(top2048Scores.size() - 1);
				return;
			}
		}
	}
	public void addTop2048Tile(int tileValue) {
		for(int i = 0; i < top2048Tiles.size(); i++) {
			if (tileValue >= top2048Tiles.get(i)) {
				top2048Tiles.add(i, tileValue);
				top2048Tiles.remove(top2048Tiles.size() - 1);
				return;
			}
		}
	}
	public void addTop2048Time(long millis) {
		for(int i = 0; i < top2048Times.size(); i++) {
			if(millis <= top2048Times.get(i)) {
				top2048Times.add(i, millis);
				top2048Times.remove(top2048Times.size() - 1);
				return;
			}
		}
	}
	
	public void addTopSnakeScore(int score) {
		for(int i = 0; i < topSnakeScores.size(); i++) {
			if (score >= topSnakeScores.get(i)) {
				topSnakeScores.add(i, score);
				topSnakeScores.remove(topSnakeScores.size() - 1);
				return;
			}
		}
	}
	public void addTopMinesweeperMineCount(int mineCount) {
		for(int i = 0; i < topMinesweeperMineCount.size(); i++) {
			if (mineCount <= topMinesweeperMineCount.get(i)) {
				topMinesweeperMineCount.add(i, mineCount);
				topMinesweeperMineCount.remove(topMinesweeperMineCount.size() - 1);
				return;
			}
		}
	}
	public void addTopMinesweeperTime(long millis) {
		for(int i = 0; i < topMinesweeperTimes.size(); i++) {
			if(millis <= topMinesweeperTimes.get(i)) {
				topMinesweeperTimes.add(i, millis);
				topMinesweeperTimes.remove(topMinesweeperTimes.size() - 1);
				return;
			}
		}
	}
	public void loadTopScores() {
		try {
			File f = new File(filePath, highScores);
			if(!f.isFile()) { 
				createSaveData();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			top2048Scores.clear();
			top2048Tiles.clear();
			top2048Times.clear();
			topSnakeScores.clear();
			topMinesweeperMineCount.clear();
			topMinesweeperTimes.clear();
			String[] twenty48Scores = reader.readLine().split("-");
			String[] twenty48Tiles = reader.readLine().split("-");
			String[] twenty48Times = reader.readLine().split("-");
			String[] snakeScores = reader.readLine().split("-");
			String[] minesweeperMineCount = reader.readLine().split("-");
			String[] minesweeperTimes = reader.readLine().split("-");
			for(int i = 0; i < twenty48Scores.length; i++) {
				top2048Scores.add(Integer.parseInt(twenty48Scores[i]));
			}
			for(int i = 0; i < twenty48Tiles.length; i++) {
				top2048Tiles.add(Integer.parseInt(twenty48Tiles[i]));
			}
			for(int i = 0; i < twenty48Times.length; i++) {
				top2048Times.add(Long.parseLong(twenty48Times[i]));
			}
			for(int i = 0; i < snakeScores.length; i++) {
				topSnakeScores.add(Integer.parseInt(snakeScores[i]));
			}
			for(int i = 0; i < minesweeperMineCount.length; i++) {
				topMinesweeperMineCount.add(Integer.parseInt(minesweeperMineCount[i]));
			}
			for(int i = 0; i < minesweeperTimes.length; i++) {
				topMinesweeperTimes.add(Long.parseLong(minesweeperTimes[i]));
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveTopScores() {
		FileWriter output = null;
		try {
			File f = new File(filePath, highScores);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			//top 2048 scores
			writer.write(top2048Scores.get(0) + "-" + top2048Scores.get(1) + "-" + top2048Scores.get(2) + "-" + top2048Scores.get(3) + "-" + top2048Scores.get(4));
			writer.newLine();
			//top 2048 tiles
			writer.write(top2048Tiles.get(0) + "-" + top2048Tiles.get(1) + "-" + top2048Tiles.get(2) + "-" + top2048Tiles.get(3) + "-" + top2048Tiles.get(4));
			writer.newLine();
			//top 2048 times
			writer.write(top2048Times.get(0) + "-" + top2048Times.get(1) + "-" + top2048Times.get(2) + "-" + top2048Times.get(3) + "-" + top2048Times.get(4));
			writer.newLine();
			//top snake scores
			writer.write(topSnakeScores.get(0) + "-" + topSnakeScores.get(1) + "-" + topSnakeScores.get(2) + "-" + topSnakeScores.get(3) + "-" + topSnakeScores.get(4));
			writer.newLine();
			//top minesweeper minecount
			writer.write(topMinesweeperMineCount.get(0) + "-" + topMinesweeperMineCount.get(1) + "-" + topMinesweeperMineCount.get(2) + "-" + topMinesweeperMineCount.get(3) + "-" + topMinesweeperMineCount.get(4));
			writer.newLine();
			//top minesweeper times
			writer.write(topMinesweeperTimes.get(0) + "-" + topMinesweeperTimes.get(1) + "-" + topMinesweeperTimes.get(2) + "-" + topMinesweeperTimes.get(3) + "-" + topMinesweeperTimes.get(4));
			writer.newLine();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createSaveData() {
		FileWriter output = null;
		try {
			File f = new File(filePath, highScores);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			//top 2048 scores
			writer.write("0-0-0-0-0");
			writer.newLine();
			//top 2048 tiles
			writer.write("0-0-0-0-0");
			writer.newLine();
			//top 2048 times
			writer.write(Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE);
			writer.newLine();
			//top snake scores
			writer.write("0-0-0-0-0");
			writer.newLine();
			//top minesweeper minecount
			writer.write(Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE);
			writer.newLine();
			//top minesweeper times
			writer.write(Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	//getters
	public int get2048HighScore() {
		return top2048Scores.get(0);
	}
	public int get2048HighestTile() {
		return top2048Tiles.get(0);
	}
	public long get2048FastestTime() {
		return top2048Times.get(0);
	}
	public int getSnakeHighScore() {
		return topSnakeScores.get(0);
	}
	public int getMinesweeperLowestMineCount() {
		return topMinesweeperMineCount.get(0);
	}
	public long getMinesweeperFastestTime() {
		return topMinesweeperTimes.get(0);
	}
	
	public int getSnakeScoreAtIndex(int index) {
		return topSnakeScores.get(index);
	}

	public int get2048ScoreAtIndex(int index) {
		return top2048Scores.get(index);
	}

	public ArrayList<Integer> getTop2048Scores() {
		return top2048Scores;
	}

	public ArrayList<Integer> getTop2048Tiles() {
		return top2048Tiles;
	}

	public ArrayList<Long> getTop2048Times() {
		return top2048Times;
	}
	
	public ArrayList<Integer> getTopSnakeScores() {
		return topSnakeScores;
	}
	public ArrayList<Integer> getTopMinesweeperMineCount() {
		return topMinesweeperMineCount;
	}

	public ArrayList<Long> getTopMinesweeperTimes() {
		return topMinesweeperTimes;
	}
}
