package com.germistry.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class GuiScreen {

	private static GuiScreen screen;
	private HashMap<PanelName, GuiPanel> panels;
	private PanelName currentPanel;
	
	private GuiScreen() {
		panels = new HashMap<PanelName, GuiPanel>();
	}
	
	public static GuiScreen getInstance() {
		if(screen == null) {
			screen = new GuiScreen();
		}
		return screen;
	}
	
	public void update() {
		if(panels.get(currentPanel)!= null) {
			panels.get(currentPanel).update();
		}
	}
	
	public void render(Graphics2D g) {
		if(panels.get(currentPanel) != null) {
			panels.get(currentPanel).render(g);
		}
	}
	
	public void add(PanelName panelName, GuiPanel panel) {
		panels.put(panelName, panel);
	}
	
	
	public PanelName getCurrentPanel() {
		return currentPanel;
	}

	public void setCurrentPanel(PanelName panelName) {
		currentPanel = panelName;
	}
	
	public void mousePressed(MouseEvent e) {
		if(panels.get(currentPanel) != null) {
			panels.get(currentPanel).mousePressed(e);
		}
	}
	public void mouseReleased(MouseEvent e) {
		if(panels.get(currentPanel) != null) {
			panels.get(currentPanel).mouseReleased(e);
		}	
	}
	public void mouseDragged(MouseEvent e) {
		if(panels.get(currentPanel) != null) {
			panels.get(currentPanel).mouseDragged(e);
		}
	}
	public void mouseMoved(MouseEvent e) {
		if(panels.get(currentPanel) != null) {
			panels.get(currentPanel).mouseMoved(e);
		}
	}
}
