package com.germistry.minesweeper;

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
	private ArrayList<Long> topTimes;

	private Leaderboard() {
		filePath = new File("").getAbsolutePath();
		highScores = "MinesweeperScores.tmp";
		
		topTimes = new ArrayList<Long>();
	}
	
	public static Leaderboard getInstance() {
		if(leaderBoard == null) {
			leaderBoard = new Leaderboard();
		}
		return leaderBoard;
	}

	public void addTopTime(long millis) {
		for(int i = 0; i < topTimes.size(); i++) {
			if(millis <= topTimes.get(i)) {
				topTimes.add(i, millis);
				topTimes.remove(topTimes.size() - 1);
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
			topTimes.clear();
			String[] times = reader.readLine().split("-");
			for(int i = 0; i < times.length; i++) {
				topTimes.add(Long.parseLong(times[i]));
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
			writer.write(topTimes.get(0) + "-" + topTimes.get(1) + "-" + topTimes.get(2) + "-" + topTimes.get(3) + "-" + topTimes.get(4));
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
			writer.write(Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE + "-" + Integer.MAX_VALUE);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	//getters
	
	public long getFastestTime() {
		return topTimes.get(0);
	}
	
	public ArrayList<Long> getTopTimes() {
		return topTimes;
	}
}
