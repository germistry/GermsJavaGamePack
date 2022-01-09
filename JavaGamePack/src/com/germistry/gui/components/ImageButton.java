package com.germistry.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.germistry.gui.GuiComponent;
import com.germistry.main.Game;
import com.germistry.utils.DrawUtils;
import com.germistry.utils.Sound;

public class ImageButton extends GuiComponent {

	private enum State {
		RELEASED,
		HOVER,
		PRESSED 
	}
	private State currentState = State.RELEASED;
	private Rectangle clickBox;
	private ArrayList<ActionListener> actionListeners;
	private String labelText = "";
	 
	private Font font = Game.mainBold.deriveFont(20f); 
	
	private Image releasedImage;
	private Image hoverImage;
	private Image pressedImage;
	
	public ImageButton(BufferedImage releasedImage, BufferedImage hoverImage, BufferedImage pressedImage, int x, int y) {
		clickBox = new Rectangle(x, y, releasedImage.getWidth(), releasedImage.getHeight());
		actionListeners = new ArrayList<ActionListener>();
		setReleasedImage(releasedImage);
		setPressedImage(pressedImage);
		setHoverImage(hoverImage);
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void render(Graphics2D g) {
		if(currentState == State.RELEASED) {
			g.drawImage(releasedImage, getX(), getY(), null);
		}
		else if(currentState == State.HOVER) {
			g.drawImage(hoverImage, getX(), getY(), null);
		}
		else {
			g.drawImage(pressedImage, getX(), getY(), null);
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
	
	public Image getReleasedImage() {
		return releasedImage;
	}

	public void setReleasedImage(Image releasedImage) {
		this.releasedImage = releasedImage;
	}
	public void setPressedImage(Image pressedImage) {
		this.pressedImage = pressedImage;
	}
	public void setHoverImage(Image hoverImage) {
		this.hoverImage = hoverImage;
	}
	public void setLabelText(String text) {
		this.labelText = text;
	}
}
