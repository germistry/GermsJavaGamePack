package com.germistry.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GuiPanel {

	private ArrayList<GuiComponent> components;
	
	public GuiPanel() {
		components = new ArrayList<GuiComponent>();
	}
	
	public void update() {
		for(int i = 0; i < components.size(); i++) {
			components.get(i).update();
		}
	}
	
	public void render(Graphics2D g) {
		for(int i = 0; i < components.size(); i++) {
			components.get(i).render(g);
		}
	}
	
	public void add(GuiComponent component) {
		components.add(component);
	}
	
	public void remove(GuiComponent component) {
		components.remove(component);
	}
	
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < components.size(); i++) {
			components.get(i).mousePressed(e);
		}
	}
	public void mouseReleased(MouseEvent e) {
		for(int i = 0; i < components.size(); i++) {
			components.get(i).mouseReleased(e);
		}
	}
	public void mouseDragged(MouseEvent e) {
		for(int i = 0; i < components.size(); i++) {
			components.get(i).mouseDragged(e);
		}
	}
	public void mouseMoved(MouseEvent e) {
		for(int i = 0; i < components.size(); i++) {
			components.get(i).mouseMoved(e);
		}
	}
	
}
