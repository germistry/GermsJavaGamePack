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
import com.germistry.gui.components.ImageButton;
import com.germistry.main.Game;
import com.germistry.tetris.GameBoard;
import com.germistry.tetris.ScoreManager;
import com.germistry.utils.DrawUtils;

public class TetrisPlayPanel extends GuiPanel {

	private GameBoard board;
	private BufferedImage infoLeft;
	private BufferedImage infoRight;
	private ScoreManager scores;
	private Font scoreFont;
	private Font dummyBtnFont;
	private Color bestScoreColour = new Color(0x895589);
	
	private ImageButton returnToMain; // this is the in-game button to return to the menu
	private ImageButton restartGame; // this is the in-game button to restart
	private ImageButton screenShotBtn;
	
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
	
	public TetrisPlayPanel() {
		dummyBtnFont = Game.mainBold.deriveFont(20f);
		scoreFont = Game.mainBold.deriveFont(24f);
		gameOverFont = Game.mainBold.deriveFont(70f);
		board = new GameBoard(Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2 + 50, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2);
		scores = board.getScores();
		infoLeft = new BufferedImage(290, Game.HEIGHT - 60, BufferedImage.TYPE_INT_RGB);
		infoRight = new BufferedImage(190, Game.HEIGHT - 60, BufferedImage.TYPE_INT_RGB);
		returnToMain = new ImageButton(Game.uiAssets[0], Game.uiAssets[1], Game.uiAssets[2], 50, 30);
		restartGame = new ImageButton(Game.uiAssets[6], Game.uiAssets[7], Game.uiAssets[8], returnToMain.getX() + returnToMain.getWidth() + spacing, 30);
		screenShotBtn = new ImageButton(Game.uiAssets[3], Game.uiAssets[4], Game.uiAssets[5], Game.WIDTH - GameBoard.BOARD_WIDTH, Game.HEIGHT - Game.uiAssets[3].getHeight() - 30);
		
		mainMenu = new GuiButton(Game.WIDTH / 2 - largeButtonWidth / 2, 350, largeButtonWidth, buttonHeight);
		restart = new GuiButton(mainMenu.getX(), mainMenu.getY() - spacing - buttonHeight, smallButtonWidth, buttonHeight);
		screenShot = new GuiButton(restart.getX() + restart.getWidth() + spacing, restart.getY(), smallButtonWidth, buttonHeight);
	
		returnToMain.setLabelText("Menu");
		screenShotBtn.setLabelText("Screen Shot");
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
				add(screenShotBtn);
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
		screenShotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenshot = true;
			}
		}); 
		
		mainMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TETRIS_MENU);
			}
		});
		returnToMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TETRIS_MENU);
				board.setHasStarted(false); 
				board.setShapesCanMove(false);
				
			}
		});
		add(returnToMain);
		add(restartGame);
		add(screenShotBtn);
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
		drawGuiLeft(g);
		drawGuiRight(g);
		board.render(g);
		//take the screenshot before rendering game over
		if(screenshot) {
			BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawGuiLeft(g2d);
			drawGuiRight(g2d);
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
				remove(screenShotBtn);
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
	private void drawGuiLeft(Graphics2D g) {
		//drawing
		Graphics2D g2d = (Graphics2D)infoLeft.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, infoLeft.getWidth(), infoLeft.getHeight());
		g2d.drawImage(Game.uiAssets[0], 0, 0, null);
		g2d.drawImage(Game.uiAssets[6], 140, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(dummyBtnFont);
		g2d.drawString("Menu", Game.uiAssets[0].getWidth() / 2 - DrawUtils.getMessageWidth("Menu", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[0].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		g2d.drawString("Restart", 140 + Game.uiAssets[6].getWidth() / 2 - DrawUtils.getMessageWidth("Restart", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[6].getHeight() / 2 + DrawUtils.getMessageHeight("Restart", dummyBtnFont, g2d) / 2);
		g2d.drawImage(Game.tetrisAssets[0], 0, 70, null);
		g2d.drawImage(Game.tetrisAssets[1], infoLeft.getWidth() / 2 - Game.tetrisAssets[1].getWidth() / 2, 220, null);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.darkGray);
		g2d.setFont(dummyBtnFont);
		g2d.drawString("Rotate", infoLeft.getWidth() / 2 - DrawUtils.getMessageWidth("Rotate", dummyBtnFont, g2d) / 2, 222);
		g2d.drawString("Move Left", 8, 284);
		g2d.drawString("Move Right", infoLeft.getWidth() - DrawUtils.getMessageWidth("Move Right", dummyBtnFont, g2d), 284);
		g2d.drawString("Drop", infoLeft.getWidth() / 2 - DrawUtils.getMessageWidth("Drop", dummyBtnFont, g2d) / 2, 382);
		g2d.dispose();
		g.drawImage(infoLeft, 50, 30, null);
	}
	
	private void drawGuiRight(Graphics2D g) {
		Graphics2D g2d = (Graphics2D)infoRight.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, infoRight.getWidth(), infoRight.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.darkGray);
		g2d.setFont(scoreFont);
		g2d.drawString("Next Shape: ", 0, 30);
        for (int row = 0; row < GameBoard.nextShape.getCoords().length; row++) {
            for (int col = 0; col < GameBoard.nextShape.getCoords()[0].length; col++) {
                if (GameBoard.nextShape.getCoords()[row][col] != 0) {
                	g2d.setColor(new Color(GameBoard.nextShape.getColour()));
                	g2d.fillRect(col * GameBoard.UNIT_SIZE + 10, row * GameBoard.UNIT_SIZE + 50, GameBoard.UNIT_SIZE, GameBoard.UNIT_SIZE);
                	g2d.setColor(Color.lightGray);
                	g2d.drawRect(col * GameBoard.UNIT_SIZE + 9, row * GameBoard.UNIT_SIZE + 49, GameBoard.UNIT_SIZE, GameBoard.UNIT_SIZE);
                }
            }
        }
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.darkGray);
		g2d.setFont(scoreFont);
		g2d.drawString("Score: "+ scores.getCurrentScore(), 0, 170);
		g2d.setColor(bestScoreColour);
		g2d.drawString("Best: " + scores.getCurrentTopScore(), 0, 220);
		g2d.drawImage(Game.uiAssets[3], 0, 404, null);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.white);
		g2d.setFont(dummyBtnFont);
		g2d.drawString("Screen Shot", Game.uiAssets[3].getWidth() / 2 - DrawUtils.getMessageWidth("Screen Shot", dummyBtnFont, g2d) / 2, 
				404 + Game.uiAssets[3].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		g2d.dispose();
		g.drawImage(infoRight, Game.WIDTH - GameBoard.BOARD_WIDTH, 30, null);
	}
	
}
