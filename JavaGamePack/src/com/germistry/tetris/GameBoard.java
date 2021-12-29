package com.germistry.tetris;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.germistry.main.Keyboard;
import com.germistry.main.LeaderBoard;


public class GameBoard {

	public final static int UNIT_SIZE = 24;
	public final static int ROWS = 20;
	public final static int COLS = 10;
	public final static int BOARD_WIDTH = UNIT_SIZE * COLS + 1;
	public final static int BOARD_HEIGHT = UNIT_SIZE * ROWS + 1;
	
	private int x, y;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	
	//for timer
	private long elapsedMS; 
	private long startTime;
	private boolean hasStarted = false;
		
	private ScoreManager scores;
	private LeaderBoard leaderboard;
	private boolean lost;
	private boolean shapesCanMove = false;

	private int[][] board = new int[ROWS][COLS];
	
	private int[] colours = { 
		0xED1C24, 0xFF7F27, 0xFFF200, 0x22B14C, 
		0x00A2E8, 0xA349A4, 0x3f48CC
	};
	private int numberShapes = 7;
	private Shape[] shapes = new Shape[numberShapes];
	public static Shape currentShape, nextShape;
	
	public GameBoard(int x, int y) {
		this.x = x;
		this.y = y;
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		createBoard();
		createShapes();
		leaderboard = LeaderBoard.getInstance();
		leaderboard.loadTopScores();
		scores = new ScoreManager(this);
		scores.loadGame();
		scores.setCurrentTopScore(leaderboard.getTetrisHighScore());
		if(scores.newGame()) {
			start();
			scores.saveGame();
		} 
		else {
			
			nextShape = shapes[scores.getNextShapeType()];
			currentShape = shapes[scores.getCurrentShapeType()];
			currentShape.setShapeX(scores.getShapeX());
			currentShape.setShapeY(scores.getShapeY());
			int shapeRows = scores.getShapeRows();
			int shapeCols = scores.getShapeCols();
			int[][] sMatrix = new int[shapeRows][shapeCols];
			for(int i = 0; i < shapeRows; i++) {
				for(int j = 0; j < shapeCols; j++) {
					sMatrix[i][j] = scores.getShapeCoords()[(i*shapeCols)+j];
				}
			}
			if(currentShape.getId() == 0) {
				if(currentShape.getCoords().length != shapeRows && currentShape.getCoords()[0].length != shapeCols) {
					currentShape.rotateShape();
				}
			}
			else if(currentShape.getId() >= 1 && currentShape.getId() < 6) {
				if(currentShape.getCoords().length != shapeRows && currentShape.getCoords()[0].length != shapeCols) {
					currentShape.rotateShape();
					if(currentShape.getCoords() != sMatrix) {
						currentShape.setCoords(sMatrix);
					}
				}
				else {
					if(currentShape.getCoords() != sMatrix) {
						currentShape.setCoords(sMatrix);
					}
				}
				
			}			
			int[][] boardInt = new int[ROWS][COLS];
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					boardInt[i][j] = scores.getBoard()[(i*COLS)+j];
					if(boardInt[i][j] == 0) continue;
					spawnCell(i, j, boardInt[i][j]);
				}
			}
			lost = checkLost();
		}
		
	}
	
	public void reset() {
		board = new int[ROWS][COLS];
		start();
		scores.saveGame();
		lost = false;
		hasStarted = false;
		shapesCanMove = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
	}
	
	private void spawnCell(int row, int col, int colourValue) {
		board[row][col] = colourValue;
	}
	
	
	private void start() {
		Random random = new Random();
		nextShape = shapes[random.nextInt(shapes.length)];
		currentShape = shapes[random.nextInt(shapes.length)];
		shapesCanMove = false;
	}
	
	public void update() {
		checkInputs();
		currentShape.update();
		checkFullRows();		
		
		if(!lost) {
			if(hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
				scores.setTime(elapsedMS);
			}
			else {
				startTime = System.nanoTime();
				scores.setTime(elapsedMS);
			}
		}
		if(scores.getCurrentScore() > scores.getCurrentTopScore()) {
			scores.setCurrentTopScore(scores.getCurrentScore());
		}
		scores.saveGame();
	}
	
	
	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null); 
		
		currentShape.render(g2d);
		
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				if(board[row][col] != 0) {
					g2d.setColor(new Color(board[row][col])); 
					g2d.fillRect(col * UNIT_SIZE, row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				}
			}
		}
		drawGrid(g2d);
				
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}
	
	
	 public void setNextShape() {
		 Random random = new Random();   
		 int index = random.nextInt(shapes.length);
	     nextShape = new Shape(shapes[index].getCoords(), this, colours[index], index);
	    }
	 
	public void setCurrentShape() {
		currentShape = nextShape;
		setNextShape();
		currentShape.resetPosition();
		int[][] shape = currentShape.getCoords();
		for(int row = 0; row < shape.length; row++) {
			for(int col = 0; col < shape[0].length; col++) {
				if(shape[row][col] != 0) {
					if(board[currentShape.getShapeY() + row][currentShape.getShapeX() + col] != 0) {
						setLost(true);
						scores.saveGame();
						shapesCanMove = false;
					}
				}
			}
		}				
	}
	
	private void createBoard() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(new Color(0x404040));
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
	}
	
	private void createShapes() {
		// create shapes
        shapes[0] = new Shape(new int[][]{
            {1, 1, 1, 1} // long shape;
        }, this, colours[0], 0);

        shapes[1] = new Shape(new int[][]{
            {1, 1, 1},
            {0, 1, 0}, // T shape;
        }, this, colours[1], 1);

        shapes[2] = new Shape(new int[][]{
            {1, 1, 1},
            {1, 0, 0}, // L shape;
        }, this, colours[2], 2);

        shapes[3] = new Shape(new int[][]{
            {1, 1, 1},
            {0, 0, 1}, // opp L shape;
        }, this, colours[3], 3);

        shapes[4] = new Shape(new int[][]{
            {0, 1, 1},
            {1, 1, 0}, // lightning shape;
        }, this, colours[4], 4);

        shapes[5] = new Shape(new int[][]{
            {1, 1, 0},
            {0, 1, 1}, // opp lightning shape;
        }, this, colours[5], 5);

        shapes[6] = new Shape(new int[][]{
            {1, 1},
            {1, 1}, // box shape;
        }, this, colours[6], 6);

	}
	
	private void drawGrid(Graphics2D g) {
		g.setColor(Color.lightGray);
		for(int i = 0; i < ROWS + 1; i++) {
			g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, ROWS * UNIT_SIZE);
			g.drawLine(0, i * UNIT_SIZE, COLS * UNIT_SIZE, i * UNIT_SIZE);
		}
	}
	
	private void checkFullRows() {
		int bottomRow = ROWS - 1;
		int rowsToRemove = 0;
		for(int topRow = ROWS - 1; topRow > 0; topRow--) { 
			int count = 0;
			for(int col = 0; col < COLS; col++) {
				if(board[topRow][col] != 0) {
					count++;
				} 
				board[bottomRow][col] = board[topRow][col];
				
			}
			if(count < COLS) {
				bottomRow--;	
				
			} else {
				rowsToRemove++;
			}
		}
		scores.setCurrentScore(scores.getCurrentScore() + rowsToRemove * rowsToRemove);
		rowsToRemove = 0;
	}
	
	public void checkInputs() {
		if(Keyboard.typed(KeyEvent.VK_LEFT)) {
			currentShape.setDeltaX(-1);
			if(!hasStarted)  {
				hasStarted = !lost;
				shapesCanMove = hasStarted;
			}
		}
		if(Keyboard.typed(KeyEvent.VK_RIGHT)) {
			currentShape.setDeltaX(1);
			if(!hasStarted)  {
				hasStarted = !lost;
				shapesCanMove = hasStarted;
			}
		}
		if(Keyboard.typed(KeyEvent.VK_UP)) {
			currentShape.rotateShape();
			if(!hasStarted)  {
				hasStarted = !lost;
				shapesCanMove = hasStarted;
			}
		}
		if(Keyboard.pressed[KeyEvent.VK_DOWN]) {
			currentShape.setDelay(currentShape.getDropSpeed());
			if(!hasStarted)  {
				hasStarted = !lost;
				shapesCanMove = hasStarted;
			}
		}
		else currentShape.setDelay(currentShape.getNormalSpeed());
	}
	
	private boolean checkLost() {
		int[][] shape = currentShape.getCoords();
		for(int row = 0; row < shape.length; row++) {
			for(int col = 0; col < shape[0].length; col++) {
				if(shape[row][col] != 0) {
					if(board[currentShape.getShapeY() + row][currentShape.getShapeX() + col] != 0) { 
						return true;
					}
				}
			} 
		}
		return false;
	}
		
	//Getters
	public ScoreManager getScores() {
		return scores;
	}
	public boolean hasLost() {
		return lost;
	}
	public void setLost(boolean lost) {
		//ie if not set to lost but you have lost ...
		if(!this.lost && lost) {
			leaderboard.addTopTetrisScore(scores.getCurrentScore());
			leaderboard.saveTopScores();
		}
		this.lost = lost;
	}
	public boolean hasStarted() {
		return hasStarted;
	}
	public void setHasStarted(boolean hasStarted) {
		//snakeCanMove = true;
		this.hasStarted = hasStarted;
	}
	public boolean shapesCanMove() {
		return shapesCanMove;
	}
	public void setShapesCanMove(boolean shapesCanMove) {
		this.shapesCanMove = shapesCanMove;
	}
	public int[][] getBoard() {
		return board;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
