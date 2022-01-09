package com.germistry.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.germistry.gui.GuiComponent;
import com.germistry.main.Game;
import com.germistry.utils.DrawUtils;
import com.germistry.utils.Sound;

public class GuiButton extends GuiComponent {
	
	private enum State {
		RELEASED,
		HOVER,
		PRESSED
	}
	private State currentState = State.RELEASED;
	private Rectangle clickBox;
	private ArrayList<ActionListener> actionListeners;
	private String labelText = "";
	
	//TODO 8.2 Add in bufferedImages of the 3 states of the button for fancy buttons? Otherwise just boring rectangles.
	private Color released;
	private Color hover;
	private Color pressed;
	
	private Font font = Game.mainBold.deriveFont(20f); 
	//private AudioHandler audio;
	
	public GuiButton(int x, int y, int width, int height) {
		clickBox = new Rectangle(x, y, width, height);
		actionListeners = new ArrayList<ActionListener>();
		released = new Color(173, 177, 179);
		hover = new Color(150, 156, 158);
		pressed = new Color(111, 116, 117);
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void render(Graphics2D g) {
		if(currentState == State.RELEASED) {
			g.setColor(released);
			g.fill(clickBox);
		}
		else if(currentState == State.HOVER) {
			g.setColor(hover);
			g.fill(clickBox);
		}
		else {
			g.setColor(pressed);
			g.fill(clickBox);
		}
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(labelText, clickBox.x + clickBox.width / 2 - DrawUtils.getMessageWidth(labelText, font, g) / 2, 
				clickBox.y + clickBox.height / 2 + DrawUtils.getMessageHeight(labelText, font, g) / 2);
	}
	
	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}
	
	public void mousePressed(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			currentState = State.PRESSED;
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			for(ActionListener al : actionListeners) {
				al.actionPerformed(null);
			}
			Sound.CLICK.play();
		}
		currentState = State.RELEASED;
	}
	
	public void mouseDragged(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			currentState = State.PRESSED;
		}
		else {
			currentState = State.RELEASED;
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		if(clickBox.contains(e.getPoint())){
			currentState = State.HOVER;
		}
		else {
			currentState = State.RELEASED;
		}
	}
	
	//Getters & setters
	public int getX() {
		return clickBox.x;
	}
	public int getY() {
		return clickBox.y;
	}
	public int getWidth() {
		return clickBox.width;
	}
	public int getHeight() {
		return clickBox.height;
	}
	public void setLabelText(String text) {
		this.labelText = text;
	}
}
