package com.germistry.gui.playPanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import com.germistry.gui.GuiPanel;
import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.components.GuiButton;
import com.germistry.main.Game;

import com.germistry.snake.GameBoard;
import com.germistry.snake.ScoreManager;

import com.germistry.utils.DrawUtils;

public class SnakePlayPanel extends GuiPanel {

	private GameBoard board;
	private BufferedImage info;
	private ScoreManager scores;
	private Font scoreFont;
	private String timeFormatted;
	private GuiButton returnToMain; // this is the in-game button to return to the menu
	private GuiButton restartGame; // this is the in-game button to restart
	
	//Game Over Variables
	private GuiButton restart;
	private GuiButton mainMenu; //this is the twenty48 menu
	private GuiButton screenShot;
	
	private int smallButtonWidth = 160;
	private int spacing = 20;
	private int largeButtonWidth = smallButtonWidth * 2 + spacing;
	private int buttonHeight = 50;
	
	private boolean added; //tracks if buttons have already been added to panel
	private int alpha;
	private Font gameOverFont;
	private boolean screenshot;
	
	public SnakePlayPanel() {
		scoreFont = Game.main.deriveFont(24f);
		gameOverFont = Game.main.deriveFont(70f);
		board = new GameBoard(Game.WIDTH - GameBoard.BOARD_WIDTH - 20, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2);
		scores = board.getScores();
		info = new BufferedImage(Game.WIDTH - GameBoard.BOARD_WIDTH - 20, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
		returnToMain = new GuiButton(30, 220, 250, buttonHeight);
		restartGame = new GuiButton(30, 280, 250, buttonHeight);
		
		mainMenu = new GuiButton(Game.WIDTH / 2 - largeButtonWidth / 2, 350, largeButtonWidth, buttonHeight);
		restart = new GuiButton(mainMenu.getX(), mainMenu.getY() - spacing - buttonHeight, smallButtonWidth, buttonHeight);
		screenShot = new GuiButton(restart.getX() + restart.getWidth() + spacing, restart.getY(), smallButtonWidth, buttonHeight);
	
		returnToMain.setLabelText("Return to Menu");
		restartGame.setLabelText("Restart");
		mainMenu.setLabelText("Return to Menu");
		restart.setLabelText("Restart");
		screenShot.setLabelText("Screen Shot");
		
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
				alpha = 0;
				
				remove(restart);
				remove(screenShot);
				remove(mainMenu);
				added = false;
				add(returnToMain);
				add(restartGame);
			}
		});
		restartGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
			}
		});
		
		screenShot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenshot = true;
			}
		});
		
		mainMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.SNAKE_MENU);
			}
		});
		returnToMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.SNAKE_MENU);
				board.setHasStarted(false); 
				board.setSnakeCanMove(false);
			}
		});
		add(returnToMain);
		add(restartGame);
	}
	@Override
	public void update() {
		board.update();
		if(board.hasLost()) {
			alpha++;
			if(alpha > 170) alpha = 170;
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		drawGui(g);
		board.render(g);
		//take the screenshot before rendering game over
		if(screenshot) {
			BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawGui(g2d);
			board.render(g2d);
			//bizarre way of finding desktop on any system/drive in java!
			try {
				String path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath().toString();
				ImageIO.write(image, "gif", new File(path, "screenshot" + System.nanoTime() + ".gif"));
			}
			catch (Exception e){
				e.printStackTrace();
			}
			screenshot = false;
		}
		if(board.hasLost()) {
			if(!added) {
				added = true;
				add(mainMenu);
				add(restart);
				add(screenShot);
				remove(returnToMain);
				remove(restartGame);
			}
			drawGameOver(g);
		}
		super.render(g);
	}
	
	//TODO 8.2 make this fade in effect a bit nicer
	public void drawGameOver(Graphics2D g) {
		g.setColor(new Color(222, 222, 222, alpha));
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.red);
		g.setFont(gameOverFont);
		g.drawString("Game Over!", Game.WIDTH / 2 - DrawUtils.getMessageWidth("Game Over!", gameOverFont, g) / 2, 250);
	}
	private void drawGui(Graphics2D g) {
		//format time variables
		timeFormatted = DrawUtils.formatTime(scores.getTime());
		//drawing
		Graphics2D g2d = (Graphics2D)info.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.lightGray);
		g2d.setFont(scoreFont);
		g2d.drawString("Score: " + scores.getCurrentScore(), 30, 40);
		g2d.setColor(Color.red);
		g2d.drawString("Best: " + scores.getCurrentTopScore(), 30, 90);
		g2d.setColor(Color.darkGray);
		g2d.drawString("Time: " + timeFormatted, 30, 140);
		g2d.dispose();
		g.drawImage(info, 0, 0, null);
	}
	
}
