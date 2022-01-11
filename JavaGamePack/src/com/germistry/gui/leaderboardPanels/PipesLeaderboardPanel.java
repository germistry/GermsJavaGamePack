package com.germistry.gui.leaderboardPanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.germistry.gui.GuiPanel;
import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.components.GuiButton;
import com.germistry.main.Game;
import com.germistry.main.LeaderBoard;
import com.germistry.utils.DrawUtils;

public class PipesLeaderboardPanel extends GuiPanel {

	private Font packTitleFont = Game.mainBold.deriveFont(24f);
	private Font gameTitleFont = Game.mainBold.deriveFont(64f);
	private Font authorFont = Game.mainBold;
	private Font scoresFont = Game.mainBold.deriveFont(28f);
	private String packTitle = "Java Game Pack";
	private String gameTitle = "Pipes Leaderboard";
	private String author = "germistry's";
	
	private int buttonWidth = 200;
	private int buttonHeight = 60;
	private int horizontalSpacing = 20; 
	private int verticalSpacing = 40;
	
	private enum InfoPanel {
		PIPES,
		TIMES
	}
	private InfoPanel currentPanel = InfoPanel.PIPES;
	
	private LeaderBoard leaderboard;	
	private ArrayList<Integer> topPipes;
	private ArrayList<Long> topTimes;
	private BufferedImage info;
	private int infoWidth = 300;
	private int infoHeight = 200;
	
	public PipesLeaderboardPanel() {
		super();
		leaderboard = LeaderBoard.getInstance();
		leaderboard.loadTopScores();
		topPipes = new ArrayList<Integer>();
		topPipes = leaderboard.getTopNumberPipes();
		topTimes = new ArrayList<Long>();
		topTimes = leaderboard.getTopPipesTimes();
		info = new BufferedImage(infoWidth, infoHeight, BufferedImage.TYPE_INT_RGB);
		GuiButton mainMenuButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, Game.HEIGHT - buttonHeight - horizontalSpacing, buttonWidth, buttonHeight);
		GuiButton pipesButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 120, buttonWidth, buttonHeight);
		GuiButton timesButton = new GuiButton(pipesButton.getX() + horizontalSpacing + buttonWidth, 120, buttonWidth, buttonHeight);
		
		mainMenuButton.setLabelText("Main Menu");
		pipesButton.setLabelText("Pipes");
		timesButton.setLabelText("Times");
		
		mainMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.PIPES_MENU);
			}
		});
		
		pipesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPanel = InfoPanel.PIPES;
			}
		});
		timesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPanel = InfoPanel.TIMES;
			}
		});
		
		add(mainMenuButton);
		add(pipesButton);
		add(timesButton);
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
		drawInfo(g);
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(gameTitleFont);
		g.setColor(Color.black);
		g.drawString(gameTitle, Game.WIDTH / 2 - DrawUtils.getMessageWidth(gameTitle, gameTitleFont, g) / 2, 100);
		g.setFont(authorFont);
		g.setColor(Color.black);
		g.drawString(author, 20, Game.HEIGHT - 12);
		g.setFont(packTitleFont);
		g.setColor(Color.black);
		g.drawString(packTitle, DrawUtils.getMessageWidth(author, authorFont, packTitleFont, g) + 25, Game.HEIGHT - 10);
		
	}
	private void drawInfo(Graphics2D g) {
		Graphics2D g2d = (Graphics2D)info.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(scoresFont);
		g2d.setColor(Color.black);
		if(currentPanel == InfoPanel.PIPES) {
			for (int i = 0; i < topPipes.size(); i++) {
				if (topPipes.get(i) != 0) { 
					String s = (i + 1) + ". " + topPipes.get(i);
					g2d.drawString(s, 0, DrawUtils.getMessageHeight(s, scoresFont, g2d) + i * verticalSpacing);
				}
				else {
					String s = (i + 1) + ". ";
					g2d.drawString(s, 0, DrawUtils.getMessageHeight(s, scoresFont, g2d) + i * verticalSpacing);
				}
			}	
		}
		else {
			for (int i = 0; i < topTimes.size(); i++) {
				if (topTimes.get(i) != Integer.MAX_VALUE) {
					String s = (i + 1) + ". " + DrawUtils.formatTime(topTimes.get(i));
					g2d.drawString(s, 0, DrawUtils.getMessageHeight(s, scoresFont, g2d) + i * verticalSpacing);
				}
				else {
					String s = (i + 1) + ". ";
					g2d.drawString(s, 0, DrawUtils.getMessageHeight(s, scoresFont, g2d) + i * verticalSpacing);
				}
			}	
		}
		g2d.dispose();
		g.drawImage(info, Game.WIDTH / 2 - infoWidth / 2, 210, null);
	}
}
