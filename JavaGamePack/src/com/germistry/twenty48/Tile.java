package com.germistry.twenty48;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.germistry.main.Game;
import com.germistry.utils.DrawUtils;

public class Tile {

	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static final int SLIDE_SPEED = 30;
	//for rounded rectangles 
	public static final int ARC_WIDTH = 10;
	public static final int ARC_HEIGHT = 10;
	
	private int value;
	private BufferedImage tileImage;
	private Color background;
	private Color foreground;
	private Font font;
	private int x, y;
	
	private Point slideTo;
	private boolean canCombine = true;
	
	private boolean firstAnimation = true;
	private double scaleFirst = 0.1;
	private BufferedImage firstImage;
	
	private boolean combineAnimation = false;
	private double scaleCombine = 1.2;
	private BufferedImage combineImage;
	
	
	public Tile(int value, int x, int y) {
		this.value = value;
		this.x = x;
		this.y = y;
		slideTo = new Point(x, y);
		tileImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		firstImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		combineImage = new BufferedImage(WIDTH * 2, HEIGHT * 2, BufferedImage.TYPE_INT_ARGB);
		drawImage();
	}
	
	public void update() {
		if(firstAnimation) {
			AffineTransform transform = new AffineTransform();
			//centering 
			transform.translate(WIDTH / 2 - scaleFirst * WIDTH /2, HEIGHT / 2 - scaleFirst * WIDTH / 2);
			//scaling
			transform.scale(scaleFirst, scaleFirst);
			//drawing
			Graphics2D g2d = (Graphics2D)firstImage.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(new Color(0,0,0,0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage, transform, null);
			//add to scalefirst, dispose & set first animation to false if the scale factor reaches 1
			scaleFirst += 0.1;
			g2d.dispose();
			if (scaleFirst >= 1) firstAnimation = false;
		}
		else if(combineAnimation) {
			AffineTransform transform = new AffineTransform();
			//centering 
			transform.translate(WIDTH / 2 - scaleCombine * WIDTH /2, HEIGHT / 2 - scaleCombine * WIDTH / 2);
			//scaling
			transform.scale(scaleCombine, scaleCombine);
			//drawing
			Graphics2D g2d = (Graphics2D)combineImage.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0,0,0,0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage, transform, null);
			//add to scalecombine, dispose & set combine animation to false if the scale factor reaches 1
			scaleCombine -= 0.05;
			g2d.dispose();
			if (scaleCombine <= 1) combineAnimation = false;
		}
	}
	
	public void render(Graphics2D g) {
		if(firstAnimation) {
			g.drawImage(firstImage, x, y, null);
		}
		else if (combineAnimation) {
			g.drawImage(combineImage, (int)(x + WIDTH / 2 - scaleCombine * WIDTH / 2), 
					(int)(y + HEIGHT / 2 - scaleCombine * HEIGHT / 2), null);
		}
		else {
			g.drawImage(tileImage, x, y, null);
		}
		
	}
	
	private void drawImage() {
		Graphics2D g = (Graphics2D)tileImage.getGraphics();
		switch (value) {
			case 2: 
				background = new Color(0xE9E9E9);
				foreground = new Color(0x000000);
				break;
			case 4: 
				background = new Color(0xDADB64);
				foreground = new Color(0x000000);
				break;
			case 8: 
				background = new Color(0xB9DB64);
				foreground = new Color(0xFFFFFF);
				break;
			case 16: 
				background = new Color(0x83EF58);
				foreground = new Color(0xFFFFFF);
				break;
			case 32: 
				background = new Color(0x66BF45);
				foreground = new Color(0xFFFFFF);
				break;
				
			case 64:
				background = new Color(0x66BF9C);
				foreground = new Color(0xFFFFFF);
				break;
				
			case 128:
				background = new Color(0x66BFFF);
				foreground = new Color(0xFFFFFF);
				break;
				
			case 256:
				background = new Color(0x0094FF);
				foreground = new Color(0xFFFFFF);
				break;
			case 512:
				background = new Color(0xC5AFFF);
				foreground = new Color(0xFFFFFF);
				break;
			case 1024:
				background = new Color(0x9A75FF);
				foreground = new Color(0xFFFFFF);
				break;
			case 2048:
				background = new Color(0x4300FF);
				foreground = new Color(0xFFFFFF);
				break;
			default:
				background = Color.black;
				foreground = Color.white;
		}
		
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(background);
		g.fillRoundRect(0, 0, WIDTH, HEIGHT, ARC_WIDTH, ARC_HEIGHT);
		
		g.setColor(foreground);
		if(value <= 64) {
			font = Game.mainBold.deriveFont(36f);
		}
		else {
			font = Game.mainBold.deriveFont(32f);
		}
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);	
		int drawX = WIDTH / 2 - DrawUtils.getMessageWidth("" + value, font, g) / 2;
		int drawY = HEIGHT / 2 + DrawUtils.getMessageHeight("" + value, font, g) / 2;
		
		g.drawString("" + value, drawX, drawY);
		g.dispose();
	}
	
	//getters & setters
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		drawImage();
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

	public Point getSlideTo() {
		return slideTo;
	}

	public void setSlideTo(Point slideTo) {
		this.slideTo = slideTo;
	}

	public boolean canCombine() {
		return canCombine;
	}

	public void setCanCombine(boolean canCombine) {
		this.canCombine = canCombine;
	}

	public boolean isCombineAnimation() {
		return combineAnimation;
	}

	public void setCombineAnimation(boolean combineAnimation) {
		this.combineAnimation = combineAnimation;
		if(combineAnimation) scaleCombine = 1.3;
	}
	
}
