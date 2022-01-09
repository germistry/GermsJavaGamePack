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

public class Twenty48MenuPanel extends GuiPanel {

	private Font packTitleFont = Game.mainBold.deriveFont(24f);
	private Font gameTitleFont = Game.mainBold.deriveFont(72f);
	private Font authorFont = Game.mainBold;
	private String packTitle = "Java Game Pack";
	private String gameTitle = "2048";
	private String author = "germistry's";
	
	private int buttonWidth = 220;
	private int buttonHeight = 60;
	private int spacing = 90;
	
	public Twenty48MenuPanel() {
		super();
		GuiButton playButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 220, buttonWidth, buttonHeight);
		GuiButton scoresButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, playButton.getY() + spacing, buttonWidth, buttonHeight);
		GuiButton mainMenuButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, scoresButton.getY() + spacing, buttonWidth, buttonHeight);
		
		playButton.setLabelText("Play");
		scoresButton.setLabelText("Scores");
		mainMenuButton.setLabelText("Main Menu");
		
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TWENTY28_PLAY);
			}
		});
		scoresButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TWENTY28_LEADERBOARD);
			}
		});
		mainMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.MAIN_MENU);
			}
		});
		
		add(playButton);
		add(scoresButton);
		add(mainMenuButton);
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(gameTitleFont);
		g.setColor(Color.black);
		g.drawString(gameTitle, Game.WIDTH / 2 - DrawUtils.getMessageWidth(gameTitle, gameTitleFont, g) / 2, 150);
		g.setFont(authorFont);
		g.setColor(Color.black);
		g.drawString(author, 20, Game.HEIGHT - 12);
		g.setFont(packTitleFont);
		g.setColor(Color.black);
		g.drawString(packTitle, DrawUtils.getMessageWidth(author, authorFont, packTitleFont, g) + 25, Game.HEIGHT - 10);
		
	}
}
