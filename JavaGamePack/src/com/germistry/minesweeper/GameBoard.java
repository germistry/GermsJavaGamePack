package com.germistry.minesweeper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.germistry.main.Game;
import com.germistry.main.Keyboard;

//minesweeper logic
public class GameBoard extends MinesListener {

	//size of board no of cells etc
	public static final int ROWS = 20;
	public static final int COLS = 45;
	public static final int UNIT_SIZE = 16;
	public static final int BOARD_EDGE = 20;
	public static final int BOARD_WIDTH = COLS * UNIT_SIZE + BOARD_EDGE * 2;
	public static final int BOARD_HEIGHT = ROWS * UNIT_SIZE + BOARD_EDGE * 2;
	private static final int NUM_IMAGES = 15;
	//for the drawing clickbox accuracy considering the board is rendered using a calc on the panel
	private int horizGap = Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2 + 19; //offset by 87 on x-axis
	private int vertGap = 40;
		
    private int x, y;
    private int mouseX, mouseY;
	private int mineCount;
	private int flagCount;
    
    private Mine[][] mines;
    private Flag[][] flags;
    private Cell[][] cells;
    private boolean[][] revealed;
    private boolean[][] highlight = new boolean [ROWS][COLS];
        
    private MinesListener minesListener;
  	private BufferedImage gameBoard;
  	private BufferedImage finalBoard;
   	
  	//for timer
  	private long elapsedMS;
  	private long startTime;
  	private boolean hasStarted = false;
  	//simple saving, TODO 2 look into encrypting
  	private int saveCount;
  	private boolean lost;
	private boolean won;
  	
  	private ScoreManager scores;
	private Leaderboard leaderboard;
  	//resources 	
  	//index 0 new cell, index 1 - 8 numbers, index 9 bomb, index 10 user clicked bomb,
  	//index 11 user flagged wrong bomb, index 12 flag, index 13 highlight cell, 14 is zero
  	public static BufferedImage gameAssets[] = new BufferedImage[NUM_IMAGES];
  	private String path = "/minesweeper/";
  	
	public GameBoard(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		mines = new Mine[ROWS][COLS];
		flags = new Flag[ROWS][COLS];
		cells = new Cell[ROWS][COLS];
		revealed = new boolean[ROWS][COLS];
		mineCount = 0;
		flagCount = 0;
		minesListener = MinesListener.getInstance();
		minesListener.add(this);
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			for (int i = 0; i < NUM_IMAGES; i++) {
	            var fullpath = path + i + ".png";
	            System.out.print("Trying to load: " + fullpath + " ...");
	            gameAssets[i] = ImageIO.read(getClass().getResource(fullpath));
	            System.out.println("succeeded!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("failed!");
		}
		createBoardImage();
		leaderboard = Leaderboard.getInstance();
		leaderboard.loadTopScores();
		scores = new ScoreManager(this);
		scores.loadGame();
		scores.setBestTime(leaderboard.getFastestTime());
		if(scores.newGame()) {
			start();
			scores.saveGame();
		}
		else {
			mineCount = scores.getMineCount();
			flagCount = scores.getFlagCount();
			
			int[][] revealedInt = new int[ROWS][COLS];
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					revealedInt[i][j] = scores.getRevealed()[(i*COLS)+j];
					if (revealedInt[i][j] == 1) revealed[i][j] = true;
					else revealed[i][j] = false;
				}
			}
			int[][] cellsInt = new int[ROWS][COLS];
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					cellsInt[i][j] = scores.getCells()[(i*COLS)+j];
					if(cellsInt[i][j] == 10) continue;
					spawnCell(i, j, cellsInt[i][j]);
				}
			}
			int[][] flagsInt = new int[ROWS][COLS];
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					flagsInt[i][j] = scores.getFlags()[(i*COLS)+j];
					if(flagsInt[i][j] == 0) continue;
					spawnFlag(i, j);
				}
			}
			lost = checkLost();
			won = checkWon(); 
		}
	}

	public void reset() {
		revealed = new boolean[ROWS][COLS];
		mines = new Mine[ROWS][COLS];
		flags = new Flag[ROWS][COLS];
		cells = new Cell[ROWS][COLS];
		mineCount = 0;
		flagCount = 0;
		start();
		scores.saveGame();
		lost = false;
		won = false;
		hasStarted = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		saveCount = 0;
	}
	
	public void update() {
		saveCount++;
		//could change this to save on the same thread but not causing any issues like this as very small data
		if(saveCount >= 120) {
			saveCount = 0;
			scores.saveGame();
		}
		if(!won && !lost) {
			if(hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
				scores.setTime(elapsedMS);
				
			}
			else {
				startTime = System.nanoTime();
				scores.setTime(elapsedMS);
			}
		}
		checkInputs();
		updateBoard();
		scores.setDisplayMines(mineCount - flagCount);
	}
	
	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null);
		renderBoard(g2d);
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}
	
	
	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
	}
	
	private void spawnCell(int row, int col, int value) {
		cells[row][col] = new Cell(value, col, row, revealed[row][col]);
		if(cells[row][col].getValue() == 9) {
			Mine mine = new Mine(revealed[row][col], col, row);
			mines[row][col] = mine;
		}
	}
	
	private void spawnFlag(int row, int col) {
		if(mines[row][col] != null) {
			flags[row][col] = new Flag(true, col, row, revealed[row][col]);
		}
		else {
			flags[row][col] = new Flag(false, col, row, revealed[row][col]);
		}
	}
	
	private void start() {
		setNewMines();
		calculateNear();
	}
	
	private void updateBoard() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				Cell cell = cells[row][col];		
				if(flags[row][col] != null) {
					cells[row][col].setRevealed(false);
				}
				else {
					if(revealed[row][col] == true && mines[row][col] != null) {
						mines[row][col].setRevealed(true);
						setLost(true);
						blowUpMines();
						checkFlags();
					}
					if(revealed[row][col] == true) {
						if(cell.getValue() == 0) {
							locateEmptyCells(row, col);
						}
						cells[row][col].setRevealed(true);
					}	
				}
			}
		}
		checkWon();
	}
	
	private void renderBoard(Graphics2D g) {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				//the button click animation
				if(highlight[row][col]) {
					g.drawImage(gameAssets[13], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
				}
				else {
					Cell currentCell = cells[row][col];
					if(flags[row][col] != null) { 
						if(flags[row][col].isCorrect() == false && flags[row][col].isRevealed() == true) {
							g.drawImage(gameAssets[11], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
						}
						else {
							g.drawImage(gameAssets[12], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
						}	
					}
					else {
						if(revealed[row][col] == true) {
							//not flag, is revealed, is mine
							if(mines[row][col] != null && row == mouseCellY() && col == mouseCellX()) {
								g.drawImage(gameAssets[10], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
							}
							else if (mines[row][col] != null) {
								g.drawImage(gameAssets[9], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
							}
							//not flag & is revealed
							else {
								switch(currentCell.getValue()) {
								case 0:
									g.drawImage(gameAssets[14], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 1:
									g.drawImage(gameAssets[1], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 2:
									g.drawImage(gameAssets[2], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 3:
									g.drawImage(gameAssets[3], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 4:
									g.drawImage(gameAssets[4], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 5:
									g.drawImage(gameAssets[5], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 6:
									g.drawImage(gameAssets[6], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 7:
									g.drawImage(gameAssets[7], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								case 8:
									g.drawImage(gameAssets[8], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);
									break;
								}
							}
						}
						else {
							//not flag, not revealed
							g.drawImage(gameAssets[0], null, col * UNIT_SIZE + BOARD_EDGE, row * UNIT_SIZE + BOARD_EDGE);	
						}
					}
				}
			}
		}
	}
	
	private void setNewMines() {
		Random random = new Random();	
		for(int row = 0; row < ROWS; row++) { 
			for(int col = 0; col < COLS; col++) { 
				if(random.nextInt(100) < 20) {
					Mine mine = new Mine(false, col, row);
					mines[row][col] = mine;
					mineCount++;
					Cell minecell = new Cell(9, col, row, false);
					cells[row][col] = minecell;
					revealed[row][col] = false;
					highlight[row][col] = false;
					
				}
				else {
					Cell cell = new Cell(0, col, row, false);
					cells[row][col] = cell;
					revealed[row][col] = false;
					highlight[row][col] = false;
					} 
				}
			}
		}
	
	private boolean isMine(int row, int col, int cRow, int cCol) {
		return (row - cRow < 2 && row - cRow > -2 
				&& col - cCol < 2 && col - cCol > -2 && mines[cRow][cCol] != null);
	}
	
	private void calculateNear() {
		for(int row = 0; row < ROWS; row++) { 
			for(int col = 0; col < COLS; col++) {
				int i = 0;
				for(int nRow = 0; nRow < ROWS; nRow++) { 
					for(int nCol = 0; nCol < COLS; nCol++) { 
						if(!(nRow == row && nCol == col)) {
							if(!(cells[row][col].getValue() == 9)) {
								if(isMine(row, col, nRow, nCol) == true) 
									i++;
							}
						}
					}
				}
				if(!(cells[row][col].getValue() == 9))
					cells[row][col].setValue(i);
			} 
		}
	}
	
	private void locateEmptyCells(int row, int col) 	{
	     int rMin = Math.max(row - 1, 0);
	     int cMin = Math.max(col - 1, 0);
	     int rMax = Math.min(row + 1, ROWS - 1);
	     int cMax = Math.min(col + 1, COLS - 1);
	     for (int row2 = rMin; row2 <= rMax; row2++) {
	        for (int col2 = cMin; col2 <= cMax; col2++) {
	        	if(mines[row2][col2] == null && flags[row2][col2] == null) {
	        		revealed[row2][col2] = true;
	        		cells[row2][col2].setRevealed(true);
	        	}
	        }
	    }
	}
	
	private void blowUpMines() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(cells[row][col].getValue() == 9 && revealed[row][col] == false && flags[row][col] == null) {
					revealed[row][col] = true;
					cells[row][col].setRevealed(true);
					mines[row][col].setRevealed(true);
				}	
			}
		}
	}
	private void checkFlags() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(flags[row][col] != null) {
					if(flags[row][col].isCorrect() == false) {
						revealed[row][col] = true;
						cells[row][col].setRevealed(true);
						flags[row][col].setRevealed(true);
					}
				}	
			}
		}
	}
	
	private boolean checkLost() {
		if(lost) {
			return true;
		}
		return false;
	}
	
	private boolean checkWon() {		
		boolean[][] output = new boolean[ROWS][COLS];
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(cells[row][col].getValue() == 9) {
					if(flags[row][col] != null) {
						output[row][col] = true;
					}
					else {
						output[row][col] = false;
						return false;
					}
				}
				if(cells[row][col].getValue() >= 0 && cells[row][col].getValue() < 9) {
					if(cells[row][col].isRevealed()) {
						output[row][col] = true;
					}
						
					else {
						output[row][col] = false;
						return false; 
					}
				}
			}
		}
		setWon(true);
		return true;
	}
	
	private void toggleFlag(int row, int col) {
		if(cells[row][col].isRevealed() == false || revealed[row][col] == false) {
			//add a flag
			if(flags[row][col] == null) {
				if(mines[row][col] != null) {
					Flag flag = new Flag(true, col, row, false);
					flags[row][col] = flag;
					flagCount++;
				}
				else {
					Flag flag = new Flag(false, col, row, false);
					flags[row][col] = flag;
					flagCount++;
				}
			}
			//remove flag
			else if(flags[row][col] != null){
				flags[row][col] = null;
				flagCount--;
			}
		}
	}
	private void checkInputs() {
		if(Keyboard.typed(KeyEvent.VK_ENTER)) {
			if(!hasStarted) hasStarted = !lost;
		}
	}
	
	private int mouseCellY() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(mouseX >= col * UNIT_SIZE + horizGap && mouseX < col * UNIT_SIZE + UNIT_SIZE + horizGap 
						&& mouseY >= row * UNIT_SIZE + vertGap && mouseY < row * UNIT_SIZE + UNIT_SIZE + vertGap)
					return row;
			}
		}			
		return -1;
	}
	
	private int mouseCellX() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(mouseX >= col * UNIT_SIZE + horizGap && mouseX < col * UNIT_SIZE + UNIT_SIZE + horizGap 
						&& mouseY >= row * UNIT_SIZE + vertGap && mouseY < row * UNIT_SIZE + UNIT_SIZE + vertGap)
					return col;
			}
		}		
		return -1;
	}
	
	public void mousePressed(MouseEvent e) {
		if(hasStarted) {
			int mY = mouseCellY();
			int mX = mouseCellX();
			if(mY != -1 && mX != -1) {
				if(e.getButton() == 1) {
					if(flags[mY][mX] == null && revealed[mY][mX] == false) {
						highlight[mY][mX] = true; 
					}
				}
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		if(hasStarted) {
			int mY = mouseCellY();
			int mX = mouseCellX();
			if(mY != -1 && mX != -1) {
				//left reveal cell
				if(e.getButton() == 1) {
					if(flags[mY][mX] == null && revealed[mY][mX] == false) {
						revealed[mY][mX] = true;
						highlight[mY][mX] = false;
					}
				}
				//right set flag
				else if(e.getButton() == 3) {
					toggleFlag(mY, mX);
				}
			}
		}		
	}
	public void mouseDragged(MouseEvent e) {
		
	}
	public void mouseMoved(MouseEvent e) {
		if(hasStarted) {
			mouseX = e.getX();
			mouseY = e.getY();
		}	
	}	

	public BufferedImage[] getGameAssets() {
		return gameAssets;
	}

	public static BufferedImage getGameAsset(int index) {
		return gameAssets[index];
	}
	
	public boolean hasLost() {
		return lost;
	}
	public void setLost(boolean lost) {
		//ie if not set to lost but you have lost ...
		if(!this.lost && lost) {
			leaderboard.saveTopScores();
		}
		this.lost = lost;
	}
	
	public boolean hasWon() {
		return won;
	}
	public void setWon(boolean won) {
		//ie if not set to won but you have won ...
		if(!this.won && won) {
			leaderboard.addTopTime(scores.getTime());
			leaderboard.saveTopScores();
		}
		this.won = won;
	}
	
	public ScoreManager getScores() {
		return scores;
	}

	public int getFlagCount() {
		return flagCount;
	}

	public int getMineCount() {
		return mineCount;
	}

	public Flag[][] getFlags() {
		return flags;
	}

	public Cell[][] getCells() {
		return cells;
	}

	public boolean[][] getRevealed() {
		return revealed;
	}
	public int getCellX(int col) {
		return col * UNIT_SIZE + BOARD_EDGE;
	}
	public int getCellY(int row) {
		return row * UNIT_SIZE + BOARD_EDGE;
	}
	public boolean hasStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}
}
