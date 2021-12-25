package com.germistry.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.minesweeper.MinesListener;

public class Mouse implements MouseListener, MouseMotionListener{

	private MinesListener minesListener;
	private GuiScreen screen;
	private int x, y;
	
	
	private static Mouse mouse;
	
	private Mouse( ) {
		screen = GuiScreen.getInstance(); 
		minesListener = MinesListener.getInstance();
	}

	public static Mouse getInstance() {
		if(mouse == null) {
			mouse = new Mouse();
		}
		return mouse;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		screen.mouseDragged(e);
		if(screen.getCurrentPanel() == PanelName.MINESWEEPER_PLAY)
			minesListener.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		screen.mouseMoved(e);
		if(screen.getCurrentPanel() == PanelName.MINESWEEPER_PLAY)
			minesListener.mouseMoved(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		screen.mousePressed(e);
		if(screen.getCurrentPanel() == PanelName.MINESWEEPER_PLAY)
			minesListener.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		screen.mouseReleased(e);
		if(screen.getCurrentPanel() == PanelName.MINESWEEPER_PLAY)
			minesListener.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}


	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
		
}
