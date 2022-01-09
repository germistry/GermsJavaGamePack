package com.germistry.gui.leaderboardPanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
//import java.util.ArrayList;
import java.util.ArrayList;

import com.germistry.gui.GuiPanel;
import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.components.GuiButton;
import com.germistry.main.Game;
import com.germistry.main.LeaderBoard;
import com.germistry.utils.DrawUtils;

public class TetrisLeaderboardPanel extends GuiPanel {

	private Font packTitleFont = Game.mainBold.deriveFont(24f);
	private Font gameTitleFont = Game.mainBold.deriveFont(64f);
	private Font authorFont = Game.mainBold;
	private Font scoresFont = Game.mainBold.deriveFont(28f);
	private String packTitle = "Java Game Pack";
	private String gameTitle = "Tetris Leaderboard";
	private String author = "germistry's";
	
	private int buttonWidth = 200;
	private int buttonHeight = 60; 
	private int spacing = 40;
	
	private LeaderBoard leaderboard;
	private ArrayList<Integer> topScores;	
	private BufferedImage info;
	private int infoWidth = 300;
	private int infoHeight = 200;
	
	public TetrisLeaderboardPanel() {
		super();
		leaderboard = LeaderBoard.getInstance();
		leaderboard.loadTopScores();
		topScores = new ArrayList<Integer>();
		topScores = leaderboard.getTopTetrisScores();
		info = new BufferedImage(infoWidth, infoHeight, BufferedImage.TYPE_INT_RGB);
		GuiButton mainMenuButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, Game.HEIGHT - buttonHeight - spacing, buttonWidth, buttonHeight);
		
		mainMenuButton.setLabelText("Main Menu");
				
		mainMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TETRIS_MENU);
			}
		});
		
		add(mainMenuButton);
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
		for (int i = 0; i < topScores.size(); i++) {
			if (topScores.get(i) != 0) {
				String s = (i + 1) + ". " + topScores.get(i);
				g2d.drawString(s, 0, DrawUtils.getMessageHeight(s, scoresFont, g2d) + i * spacing);
			}
			else {
				String s = (i + 1) + ". ";
				g2d.drawString(s, 0, DrawUtils.getMessageHeight(s, scoresFont, g2d) + i * spacing);
			}
		}	
		g2d.dispose();
		g.drawImage(info, Game.WIDTH / 2 - infoWidth / 2, 170, null);
	}
	
}
