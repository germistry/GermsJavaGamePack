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
import com.germistry.gui.components.ImageButton;
import com.germistry.main.Game;
import com.germistry.utils.DrawUtils;

public class MainMenuPanel extends GuiPanel {

	private Font titleFont = Game.mainBold.deriveFont(72f);
	private Font authorFont = Game.mainBold.deriveFont(28f);
	private Font bugLineFont = Game.mainBold.deriveFont(14f);
	private Font bugLineFont2 = Game.mainBold.deriveFont(14f);
	private String title = "Java Game Pack";
	private String author = "germistry's";
	private String bugLine = "If I look 'fuzzy' on windows 10 don't forget to change your java.exe and javaw.exe high DPI scaling property to use System!";
	private String bugLine2 = "I am an old java app afterall!";
	private int horizSpacing = 20; 
	 
	public MainMenuPanel() {
		super();
		ImageButton krackoutButton = new ImageButton(Game.uiAssets[18], Game.uiAssets[19], Game.uiAssets[20], Game.WIDTH / 2 - Game.uiAssets[18].getWidth() / 2, 160);
		ImageButton twenty48Button = new ImageButton(Game.uiAssets[12], Game.uiAssets[13], Game.uiAssets[14], krackoutButton.getX() - horizSpacing - Game.uiAssets[12].getWidth(), 160);
		ImageButton minesweeperButton = new ImageButton(Game.uiAssets[15], Game.uiAssets[16], Game.uiAssets[17], krackoutButton.getX() + horizSpacing + Game.uiAssets[15].getWidth(), 160);
		ImageButton riverRaidButton = new ImageButton(Game.uiAssets[12], Game.uiAssets[13], Game.uiAssets[14], Game.WIDTH / 2 - Game.uiAssets[12].getWidth() / 2, 220);
		ImageButton pipesButton = new ImageButton(Game.uiAssets[15], Game.uiAssets[16], Game.uiAssets[17], riverRaidButton.getX() - horizSpacing - Game.uiAssets[15].getWidth(), 220);
		ImageButton invadersButton = new ImageButton(Game.uiAssets[18], Game.uiAssets[19], Game.uiAssets[20], riverRaidButton.getX() + horizSpacing + Game.uiAssets[18].getWidth(), 220);
		ImageButton sudokuButton = new ImageButton(Game.uiAssets[15], Game.uiAssets[16], Game.uiAssets[17], Game.WIDTH / 2 - Game.uiAssets[15].getWidth() / 2, 280);
		ImageButton snakeButton = new ImageButton(Game.uiAssets[18], Game.uiAssets[19], Game.uiAssets[20], sudokuButton.getX() - horizSpacing - Game.uiAssets[18].getWidth(), 280);
		ImageButton tetrisButton = new ImageButton(Game.uiAssets[12], Game.uiAssets[13], Game.uiAssets[14], sudokuButton.getX() + horizSpacing + Game.uiAssets[12].getWidth(), 280);
		
		ImageButton quitButton = new ImageButton(Game.uiAssets[0], Game.uiAssets[1], Game.uiAssets[2],Game.WIDTH / 2 - Game.uiAssets[0].getWidth() / 2, 340);
		
		krackoutButton.setLabelText("Krackout");
		twenty48Button.setLabelText("2048");
		minesweeperButton.setLabelText("Minesweeper");
		riverRaidButton.setLabelText("River Raid");
		pipesButton.setLabelText("Pipes");
		invadersButton.setLabelText("Space Invaders");
		sudokuButton.setLabelText("Sudoku");
		snakeButton.setLabelText("Snake");
		tetrisButton.setLabelText("Tetris");
		quitButton.setLabelText("QUIT");
		
		krackoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.KRACKOUT_MENU);
			}
		});		
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
		riverRaidButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.RIVERRAID_MENU);
			}
		});
		pipesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.PIPES_MENU);
			}
		});
		invadersButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.INVADERS_MENU);
			}
		});
		sudokuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.SUDOKU_MENU);
			}
		});
		snakeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.SNAKE_MENU);
			}
		});
		tetrisButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TETRIS_MENU);
			}
		});
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});	
		add(krackoutButton);
		add(twenty48Button);
		add(minesweeperButton);
		add(riverRaidButton);
		add(pipesButton);
		add(invadersButton);
		add(sudokuButton);
		add(snakeButton);
		add(tetrisButton);
		add(quitButton);
	}

	@Override
	public void render(Graphics2D g) {
		super.render(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 140);
		g.setFont(authorFont);
		g.setColor(Color.black);
		g.drawString(author, Game.WIDTH / 2 - DrawUtils.getMessageWidth(author, authorFont, g) / 2, 60);
		g.setFont(bugLineFont);
		g.drawString(bugLine, Game.WIDTH / 2 - DrawUtils.getMessageWidth(bugLine, bugLineFont, g) / 2, Game.HEIGHT - 50);
		g.setFont(bugLineFont2);
		g.drawString(bugLine2, Game.WIDTH / 2 - DrawUtils.getMessageWidth(bugLine2, bugLineFont2, g) / 2, Game.HEIGHT - 20);
		
	}
	
	
}
