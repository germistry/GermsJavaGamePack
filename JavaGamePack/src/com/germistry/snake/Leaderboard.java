package com.germistry.snake;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Leaderboard {

	private static Leaderboard leaderBoard;
	private String filePath;
	private String highScores;
	
	//All time leaderboards
	private ArrayList<Integer> topScores;

	private Leaderboard() {
		filePath = new File("").getAbsolutePath();
		highScores = "SnakeScores.tmp";
		topScores = new ArrayList<Integer>();
	}
	
	public static Leaderboard getInstance() {
		if(leaderBoard == null) {
			leaderBoard = new Leaderboard();
		}
		return leaderBoard;
	}

	public void addTopScore(int score) {
		for(int i = 0; i < topScores.size(); i++) {
			if (score >= topScores.get(i)) {
				topScores.add(i, score);
				topScores.remove(topScores.size() - 1);
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
			topScores.clear();
			String[] scores = reader.readLine().split("-");
			for(int i = 0; i < scores.length; i++) {
				topScores.add(Integer.parseInt(scores[i]));
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
			writer.write(topScores.get(0) + "-" + topScores.get(1) + "-" + topScores.get(2) + "-" + topScores.get(3) + "-" + topScores.get(4));
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
			writer.write("0-0-0-0-0");
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	//getters
	public int getHighScore() {
		return topScores.get(0);
	}
	
	public int getScoreAtIndex(int index) {
		return topScores.get(index);
	}

	public ArrayList<Integer> getTopScores() {
		return topScores;
	}
}
