package com.germistry.minesweeper;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MinesListener {

	private ArrayList<GameBoard> gameBoards;
	
	private static MinesListener minesListener;
	
	protected MinesListener() {
		gameBoards = new ArrayList<GameBoard>();
	}
	public static MinesListener getInstance() {
		if(minesListener == null) {
			minesListener = new MinesListener();
		}
		return minesListener;
	}
	
	public void add(GameBoard gameBoard) {
		gameBoards.add(gameBoard);
	}
	
	public void remove(GameBoard gameBoard) {
		gameBoards.remove(gameBoard);
	}
	
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < gameBoards.size(); i++) {
			gameBoards.get(i).mousePressed(e);
		}
	}
	public void mouseReleased(MouseEvent e) {
		for(int i = 0; i < gameBoards.size(); i++) {
			gameBoards.get(i).mouseReleased(e);
		}
	}
	public void mouseDragged(MouseEvent e) {
		for(int i = 0; i < gameBoards.size(); i++) {
			gameBoards.get(i).mouseDragged(e);
		}
	}
	public void mouseMoved(MouseEvent e) {
		for(int i = 0; i < gameBoards.size(); i++) {
			gameBoards.get(i).mouseMoved(e);
		}
	}
}
