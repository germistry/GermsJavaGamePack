package com.germistry.gui.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.germistry.gui.GuiPanel;
import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.components.GuiButton;
import com.germistry.main.Game;
import com.germistry.utils.DrawUtils;

public class MainMenuPanel extends GuiPanel {

	private Font titleFont = Game.main.deriveFont(72f);
	private Font authorFont = Game.main.deriveFont(28f);
	private Font bugLineFont = Game.main.deriveFont(12f);
	private String title = "Java Game Pack";
	private String author = "germistry's";
	private String bugLine = "Please run me at 100% resolution on your fancy laptop as I'm an old java app!";
	
	private int buttonWidth = 220;
	private int buttonHeight = 60;
	private int vertSpacing = 90;
	private int horizSpacing = 20;
	
	public MainMenuPanel() {
		super();
		GuiButton minesweeperButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 200, buttonWidth, buttonHeight);
		GuiButton twenty48Button = new GuiButton(minesweeperButton.getX() - horizSpacing - buttonWidth, 200, buttonWidth, buttonHeight);
		GuiButton snakeButton = new GuiButton(minesweeperButton.getX() + horizSpacing + buttonWidth, 200, buttonWidth, buttonHeight);
		GuiButton quitButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, minesweeperButton.getY() + vertSpacing, buttonWidth, buttonHeight);
		
		twenty48Button.setLabelText("2048");
		minesweeperButton.setLabelText("Minesweeper");
		snakeButton.setLabelText("Snake");
		quitButton.setLabelText("QUIT");
		
		twenty48Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TWENTY28_MENU);
			}
		});
		minesweeperButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.MINESWEEPER_MENU);
			}
		});
		snakeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.SNAKE_MENU);
			}
		});
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});	
		
		add(twenty48Button);
		add(minesweeperButton);
		add(snakeButton);
		add(quitButton);
	}

	@Override
	public void render(Graphics2D g) {
		super.render(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 160);
		g.setFont(authorFont);
		g.setColor(Color.black);
		g.drawString(author, Game.WIDTH / 2 - DrawUtils.getMessageWidth(author, authorFont, g) / 2, 80);
		g.setFont(bugLineFont);
		g.drawString(bugLine, Game.WIDTH / 2 - DrawUtils.getMessageWidth(bugLine, bugLineFont, g) / 2, Game.HEIGHT - 20);
		
	}
	
	
}
