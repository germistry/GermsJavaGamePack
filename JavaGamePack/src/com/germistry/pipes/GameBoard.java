package com.germistry.pipes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import com.germistry.main.Game;
import com.germistry.main.LeaderBoard;

public class GameBoard extends PipesListener {

	public final static int UNIT_SIZE = 64;
	public final static int ROWS = 7;
	public final static int COLS = 9;
	public final static int BOARD_WIDTH = UNIT_SIZE * COLS + 1;
	public final static int BOARD_HEIGHT = UNIT_SIZE * ROWS + 1;
	//for the drawing clickbox accuracy considering the board is rendered using a calc on the panel
	private int horizGap = Game.WIDTH - GameBoard.BOARD_WIDTH - 120; //offset by 87 on x-axis
	private int vertGap = Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2; 
	
	private int x, y;
	private int mouseX, mouseY;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	
	//for timer
	private long elapsedMS; 
	private long startTime;
	private boolean hasStarted = false;
	private int saveCount;
	
	private PipesListener pipesListener;
	private ScoreManager scores;
	private LeaderBoard leaderboard;
	private boolean lost, won;
	
	private Random random = new Random();
	private Pipe[][] board = new Pipe[ROWS][COLS];
	private Tap tap;
	private Drain drain;
	private boolean fluidCanFlow = false;
	private int pipeCount;  //number of pipes used
	private ArrayList<Pipe> waterPath = new ArrayList<Pipe>();
	
	private int pipeFilling, pipesFilled;
	private int counter;
	private int cycles;
	private ArrayList<Integer> waterFlowX = new ArrayList<Integer>(); 
	private ArrayList<Integer> waterFlowY = new ArrayList<Integer>();
	//private int direction;  // 0 down, 1 up, 2 right, 3 left
	private int[] xDir = {0, 0, 1, -1};  //right - left
	private int[] yDir = {1, -1, 0, 0};  //down - up
	
	private int numberPipes = 7;
	private Pipe[] pipeOptions = new Pipe[numberPipes];
	public static Pipe currentPipe, option1Pipe, option2Pipe, option3Pipe;
	//pipe resources 	
  	//index 0 - 3 tap empty Nth Est Wst Sth, index 4 - 7 drain empty Nth Est Wst Sth
  	private static final int NUM_IMAGES = 15;
	public static BufferedImage gameAssets[] = new BufferedImage[NUM_IMAGES];
  	private String path = "/pipes/pipes/";
  	//water resources 	
  	private static final int WATER_NUM_IMAGES = 20;
	public static BufferedImage waterAssets[] = new BufferedImage[WATER_NUM_IMAGES];
  	private String wpath = "/pipes/water/";
  	
	public GameBoard(int x, int y) {
		this.x = x;
		this.y = y;
		pipesListener = PipesListener.getInstance();
		pipesListener.add(this);
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
		try {
			for (int i = 0; i < WATER_NUM_IMAGES; i++) {
	            var fullpath = wpath + i + ".png";
	            System.out.print("Trying to load: " + fullpath + " ...");
	            waterAssets[i] = ImageIO.read(getClass().getResource(fullpath));
	            System.out.println("succeeded!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("failed!");
		}
		createBoard();
		createPipes();
		leaderboard = LeaderBoard.getInstance();
		leaderboard.loadTopScores();
		scores = new ScoreManager(this);
		scores.loadGame();
		scores.setBestTime(leaderboard.getPipesFastestTime());
		scores.setCurrentTopScore(leaderboard.getPipesHighScore());
		if(scores.newGame()) {
			start();
			scores.saveGame();
		}
		else {
			
			start();
			//load stuff from file
			
			lost = checkLost();
			won = checkWon();
		}
	}
	
	public void reset() {
		board = new Pipe[ROWS][COLS];
		fluidCanFlow = false;
		if(!waterPath.isEmpty()) 
			waterPath.clear();
		if(!waterFlowX.isEmpty())
			waterFlowX.clear();
		if(!waterFlowY.isEmpty()) 
			waterFlowY.clear();
		counter = 0;
		cycles = 0;
		pipeCount = 0;
		start();
		scores.saveGame();
		lost = false;
		won = false;
		hasStarted = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		saveCount = 0;
	}
	
	private void start() {
		spawnTap();
		spawnDrain();
		option3Pipe = pipeOptions[random.nextInt(pipeOptions.length)];
		option2Pipe = pipeOptions[random.nextInt(pipeOptions.length)];
		option1Pipe = pipeOptions[random.nextInt(pipeOptions.length)];
		currentPipe = pipeOptions[random.nextInt(pipeOptions.length)];
	}
	
	public void update() {
		saveCount++;
		//could change this to save on the same thread but not causing any issues like this as very small data
		if(saveCount >= 60) {
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

		if(scores.getCurrentScore() > scores.getCurrentTopScore()) {
			scores.setCurrentTopScore(scores.getCurrentScore());
		}
		if(scores.getTime() > 15 * 1000 && hasStarted) {
			updateWaterFlow();
		}
	}
	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null);
		if(fluidCanFlow) {
			if(waterFlowX.isEmpty() || waterFlowY.isEmpty()) {
				String excMsg = "x or y coordinates do not exist!";
				throw new IllegalStateException(excMsg);
				
			}
			else {		
				for (int i = 0; i < waterFlowX.size(); i++) {
					g2d.drawImage(waterAssets[0], waterFlowX.get(i), waterFlowY.get(i), null);
				}
			}
		}
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				Pipe current = board[row][col];
				if(current == null) continue;
				current.render(g2d);
			}
		}
		drawGrid(g2d);
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}
	
	private void createBoard() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(new Color(0x404040));
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
	}
	
	private void drawGrid(Graphics2D g) {
		g.setColor(Color.lightGray);
		for(int i = 0; i < ROWS + 1; i++) {
			g.drawLine(0, i * UNIT_SIZE, COLS * UNIT_SIZE, i * UNIT_SIZE);
		}
		for (int i = 0; i < COLS + 1; i++) {
			g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, COLS * UNIT_SIZE);
		}
	}
	
	private void createPipes() {
		pipeOptions[0] = new Pipe(false, 2);
		pipeOptions[0].setPipeType(PipeType.VERTICAL);
		pipeOptions[1] = new Pipe(false, 3);
		pipeOptions[1].setPipeType(PipeType.HORIZONTAL);
		pipeOptions[2] = new Pipe(false, 4);
		pipeOptions[2].setPipeType(PipeType.CROSS);
		pipeOptions[3] = new Pipe(false, 5);
		pipeOptions[3].setPipeType(PipeType.TOP_LEFT_CORNER);
		pipeOptions[4] = new Pipe(false, 6);
		pipeOptions[4].setPipeType(PipeType.TOP_RIGHT_CORNER);
		pipeOptions[5] = new Pipe(false, 7);
		pipeOptions[5].setPipeType(PipeType.BOTTOM_LEFT_CORNER);
		pipeOptions[6] = new Pipe(false, 8);
		pipeOptions[6].setPipeType(PipeType.BOTTOM_RIGHT_CORNER);
	}
	
	private void spawnTap() {
		int row = 1 + random.nextInt(ROWS - 2);
		int col = 1 + random.nextInt(COLS - 2); 
		int dir = random.nextInt(4);
		tap = new Tap(false, row, col, dir);
		// 0 down, 1 up, 2 right, 3 left
		Pipe tap = new Pipe(false, 9);
		tap.setRow(row);
		tap.setCol(col);
		if (dir == 3) {
			tap.setPipeType(PipeType.TAP_WEST);
			tap.setFlowDirection(3);
		}
		else if (dir == 2) {
			tap.setPipeType(PipeType.TAP_EAST);
			tap.setFlowDirection(4);
		}
		else if (dir == 1) {
			tap.setPipeType(PipeType.TAP_NORTH);
			tap.setFlowDirection(1);
		}
		else {
			tap.setPipeType(PipeType.TAP_SOUTH);
			tap.setFlowDirection(2);
		}	
		board[row][col] = tap;
		waterPath.add(board[row][col]);
		waterFlowX.add(col * UNIT_SIZE + 12);
		waterFlowY.add(row * UNIT_SIZE + 12);
	}
	private void spawnDrain() {
		int row = 1 + random.nextInt(ROWS - 2);
		int col = 1 + random.nextInt(COLS - 2); 
		int direction = random.nextInt(4);
		int tapRow = tap.getRow();
		int tapCol = tap.getCol();
		int tapDir = tap.getDirection();
		if(row == tapRow && col == tap.getCol() 
				|| row == tapRow - 1 && col == tapCol
				|| row == tapRow + 1 && col == tapCol 
				|| row == tapRow && col == tapCol - 1
				|| row == tapRow && col == tapCol + 1
				|| row == tapRow - 2 && col == tapCol && direction == 0 && tapDir == 1
				|| row == tapRow + 2 && col == tapCol && direction == 1 && tapDir == 0
				|| row == tapRow && col == tapCol - 2 && direction == 3 && tapDir == 2
				|| row == tapRow && col == tapCol + 2 && direction == 2 && tapDir == 3
				) {
			spawnDrain();
		}
		else {
			drain = new Drain(false, row, col, direction);
			Pipe drain = new Pipe(false, 10);
			drain.setRow(row);
			drain.setCol(col);
			if(direction == 3) {
				drain.setPipeType(PipeType.DRAIN_WEST);
				drain.setFlowDirection(4);
			}
			else if(direction == 2) {
				drain.setPipeType(PipeType.DRAIN_EAST);
				drain.setFlowDirection(3);
			}
			else if(direction == 1) {
				drain.setPipeType(PipeType.DRAIN_NORTH);
				drain.setFlowDirection(2);
			}
			else {
				drain.setPipeType(PipeType.DRAIN_SOUTH);
				drain.setFlowDirection(1);
			}
			board[row][col] = drain;
		}
	}
		
	private void updateWaterFlow() {
		fluidCanFlow = true;
		Pipe current = waterPath.get(0);
		current.setStartFlow(true);
		cycles++;
		if(cycles % 8 == 0 && !current.isPipeFull()) {
			counter++;
			moveWater(current.getFlowDirection());	
		}
		if (counter > 63) {
			current.setStartFlow(false); 
			current.setPipeFull(true);
			pipesFilled = 1;
			Pipe next = waterPath.get(1);
			next.setStartFlow(true);
			if(cycles % 8 == 0 && !next.isPipeFull()) {
				counter++;
				moveWater(next.getFlowDirection());	
			}
			if (counter > 127) {
				next.setStartFlow(false); 
				next.setPipeFull(true);
				pipesFilled = 2;
				Pipe third = waterPath.get(2);
				third.setStartFlow(true);
				if(cycles % 8 == 0 && !third.isPipeFull()) {
					counter++;
					moveWater(third.getFlowDirection());	
				}

			}

		}
		
	}
	
	private void moveWater(int flowDirection) {
		switch(flowDirection) {
		case 1: //straight flowing up
			waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
			waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
			break;
		case 2: //straight flowing down
			waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
			waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
			break;
		case 3: //straight flowing left
			waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
			waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
			break;
		case 4: //straight flowing right 
			waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
			waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
			break;
		case 5: //elbow flow up then to left
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
			}
			break;
		case 6: //elbow flow up then to right
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
			}
			break;
		case 7: //elbow flow down then to left
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
			}
			break;
		case 8: //elbow flow down then to right 
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
			}
			break;
		case 9: //elbow flow right then up 
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
			}
			break;
		case 10: //elbow flow right then down 
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
			}
			break;
		case 11: //elbow flow left then up 
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
			}
			break;
		case 12: //elbow flow left then down
			if(counter <= 45) {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
			}
			else {
				waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
				waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
			}
			break;
		}
	}


	//0 no flow, 1 flowing from bottom to top, 2 flowing top to bottom, 3 flowing right to left, 4 flowing left to right
	//5 flowing bottom to left, 6 flowing bottom to right, 7 flowing top to left, 8 flowing top to right, 9 flowing left to top,
	//10 flowing left to bottom, 11 flowing right to top, 12 flowing right to bottom - see my shape diagram!
	private void updateWaterPath(Pipe source) {
		//flowing to top ie. bottom to top, left to top, right to top, cross bottom to top 
		if(source.getFlowDirection() == 1 || source.getFlowDirection() == 9 || source.getFlowDirection() == 11) {
			if(source.getRow() == 0) 
				return;
			//check above
			if(board[source.getRow() - 1][source.getCol()] == null) 
				return;
			PipeType nextPipeType = board[source.getRow() - 1][source.getCol()].getPipeType();
			if(!nextPipeType.pipehasBottom()) 
				return;
			else {
				//int dir = 0;
				Pipe nextPipe = board[source.getRow() - 1][source.getCol()];
				if(nextPipeType.pipehasTop()) {
					//bottom to top
					nextPipe.setFlowDirection(1);
					//dir = 1; 
				}
				else if(nextPipeType.pipehasLeft()) {
					//bottom to left
					nextPipe.setFlowDirection(5);
					//dir = 3;
				}
				else if(nextPipeType.pipehasRight()) {
					//bottom to right
					nextPipe.setFlowDirection(6);
					//dir = 2;
				}
				else {
					//is drain south
				}
				waterPath.add(nextPipe);				
				updateWaterPath(waterPath.get(waterPath.size() - 1));
			}
		}
		//flowing to bottom ie. top to bottom, left to bottom, right to bottom, cross top to bottom 
		else if (source.getFlowDirection() == 2 || source.getFlowDirection() == 10 || source.getFlowDirection() == 1) {
			if(source.getRow() == ROWS -1 )
				return;
			//check below
			if(board[source.getRow() + 1][source.getCol()] == null) 
				return;
			PipeType nextPipeType = board[source.getRow() + 1][source.getCol()].getPipeType();
			if(!nextPipeType.pipehasTop()) 
				return;
			else {
				Pipe nextPipe = board[source.getRow() + 1][source.getCol()];
				if(nextPipeType.pipehasBottom()) 
					//top to bottom
					nextPipe.setFlowDirection(2);
				else if(nextPipeType.pipehasLeft()) 
					//top to left
					nextPipe.setFlowDirection(7);
				else if(nextPipeType.pipehasRight()) 
					//top to right
					nextPipe.setFlowDirection(8);
				else {
					//is drain north
				}
				waterPath.add(nextPipe);
				updateWaterPath(waterPath.get(waterPath.size() - 1));
			}
		}
		//flowing to left ie right to left, bottom to left, top to left, cross right to left
		else if(source.getFlowDirection() == 3 || source.getFlowDirection() == 5 || source.getFlowDirection() == 7) {
			if(source.getCol() == 0) 
				return;
			//check left
			if(board[source.getRow()][source.getCol() - 1] == null) 
				return;			
			PipeType nextPipeType = board[source.getRow()][source.getCol() - 1].getPipeType();
			if(!nextPipeType.pipehasRight()) 
				return;
			else {
				Pipe nextPipe = board[source.getRow()][source.getCol() - 1];
				if(nextPipeType.pipehasLeft()) 
					//right to left
					nextPipe.setFlowDirection(3);
				else if(nextPipeType.pipehasBottom()) 
					//right to bottom
					nextPipe.setFlowDirection(12);
				else if(nextPipeType.pipehasTop()) 
					//right to top 
					nextPipe.setFlowDirection(11); 
				else {
					//is drain east
				}
				waterPath.add(nextPipe);
				updateWaterPath(waterPath.get(waterPath.size() - 1));
			}
		}
		//flowing to right ie. left to right, top to right, bottom to right
		else if(source.getFlowDirection() == 4 || source.getFlowDirection() == 6 || source.getFlowDirection() == 8)	{
			if(source.getCol() == COLS -1 )
				return;
			if(board[source.getRow()][source.getCol() + 1] == null) 
				return;
			//check right
			PipeType nextPipeType = board[source.getRow()][source.getCol() + 1].getPipeType();
			if(!nextPipeType.pipehasLeft()) 
				return;
			else {
				Pipe nextPipe = board[source.getRow()][source.getCol() + 1];
				if(nextPipeType.pipehasRight()) 
					//left to right
					nextPipe.setFlowDirection(4);
				else if(nextPipeType.pipehasBottom()) 
					//left to bottom
					nextPipe.setFlowDirection(10);
				else if(nextPipeType.pipehasTop()) 
					//left to top 
					nextPipe.setFlowDirection(9);
				else {
					//is drain west
				}
				waterPath.add(nextPipe);
				updateWaterPath(waterPath.get(waterPath.size() - 1));
			}
		}
	}
	
	private void setPipe(int row, int col) { 
		//new pipe
		if(board[row][col] == null) {
			Pipe newPipe = new Pipe(false, currentPipe.getId());
			newPipe.setPipeType(currentPipe.getPipeType());
			newPipe.setRow(row);
			newPipe.setCol(col);
			board[row][col] = newPipe;
			refreshWaterPath();
			updatePipeOptions();
		}
		else {
			//change existing
			if(board[row][col].getId() != 9 ) {
				if(board[row][col].getId() != 10) {
					Pipe current = board[row][col];
					if(!current.startFlow() && !current.isPipeFull() && current.getPipeType() != currentPipe.getPipeType()) {
						current.setPipeType(currentPipe.getPipeType());
						board[row][col] = current;
						refreshWaterPath();
						updatePipeOptions();
					}
				}
			}	
		}
	}
	private void refreshWaterPath() {
		if(!waterPath.isEmpty()) {
			if(waterPath.size() > 1 && !fluidCanFlow) {
				waterPath.subList(1, waterPath.size()).clear();
			}
			else {
				
			}
		}
		updateWaterPath(waterPath.get(0));
		pipeCount = waterPath.size();
	}
	
	private void updatePipeOptions() {
		currentPipe = option1Pipe;
		option1Pipe = option2Pipe;
		option2Pipe = option3Pipe;
		option3Pipe = pipeOptions[random.nextInt(pipeOptions.length)];
	}
	
	private boolean checkLost() {
		for (int i = 0; i < waterPath.size(); i++) {
			//check for flow started?
			return false;
		}
		return true;
	}
	
	private boolean checkWon() {
		for (int i = 0; i < waterPath.size(); i++) {
			PipeType type = waterPath.get(i).getPipeType();
			if(type != PipeType.DRAIN_NORTH || type != PipeType.DRAIN_SOUTH || type != PipeType.DRAIN_EAST || type != PipeType.DRAIN_WEST)
				return false;
		}
		return true;
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
		int mY = mouseCellY();
		int mX = mouseCellX();
		if(mY != -1 && mX != -1) {
			if(hasStarted && !lost || hasStarted && !won) {
				if(e.getButton() == 1) {
					
				}
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		int mY = mouseCellY();
		int mX = mouseCellX();
		if(mY != -1 && mX != -1) {			
			if(hasStarted && !lost || hasStarted && !won) {
				if(e.getButton() == 1) {
					setPipe(mY, mX);
				}
			}
			else if (!hasStarted && !lost) {
				hasStarted = true;
				startTime = System.nanoTime();
				//repeated here to get the first click
				if(e.getButton() == 1) {
					setPipe(mY, mX);
				}
			}
			else if(!hasStarted && !won) {
				hasStarted = true;
				startTime = System.nanoTime();
				//repeated here to get the first click
				if(e.getButton() == 1) {
					setPipe(mY, mX);
				}
			}
			else if(hasStarted && lost || hasStarted && won)
				hasStarted = false;
		}		
	}
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}	
	
	public int getHighestNumberPipes() {
		
		return 0;
	}
		
	//Getters
	public boolean hasLost() {
		return lost;
	}
	public void setLost(boolean lost) {
		//ie if not set to lost but you have lost ...
		if(!this.lost && lost) {
			leaderboard.addTopNumberPipes(getHighestNumberPipes());
			leaderboard.addTopPipesScore(scores.getCurrentScore());
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
			leaderboard.addTopNumberPipes(getHighestNumberPipes());
			leaderboard.addTopPipesScore(scores.getCurrentScore());
			leaderboard.addTopPipesTime(scores.getTime());
			leaderboard.saveTopScores();
		}
		this.won = won;
	}
	
	public ScoreManager getScores() {
		return scores;
	}
	public boolean hasStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	public Pipe[][] getBoard() {
		return board;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
