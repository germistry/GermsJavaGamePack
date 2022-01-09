package com.germistry.pipes;

import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class PipesListener {

private ArrayList<GameBoard> gameBoards;
	
	private static PipesListener pipesListener;
	
	protected PipesListener() {
		gameBoards = new ArrayList<GameBoard>();
	}
	public static PipesListener getInstance() {
		if(pipesListener == null) {
			pipesListener = new PipesListener();
		}
		return pipesListener;
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
