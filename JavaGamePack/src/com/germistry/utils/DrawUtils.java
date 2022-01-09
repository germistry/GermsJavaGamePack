package com.germistry.utils;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class DrawUtils {

	private DrawUtils() {}
	
	public static int getMessageWidth(String message, Font font, Graphics2D g) {
		g.setFont(font); 
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(message, g);
		return (int)bounds.getWidth();
	}
	
	public static int getMessageWidth(String message, Font messageFont, Font finalFont, Graphics2D g) {
		g.setFont(messageFont); 
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(message, g);
		g.setFont(finalFont);
		return (int)bounds.getWidth();
	}
	
	public static int getMessageHeight(String message, Font font, Graphics2D g) {
		g.setFont(font);
		if(message.length() == 0) return 0;
		TextLayout tl = new TextLayout(message, font, g.getFontRenderContext());
		return (int)tl.getBounds().getHeight();
	}
	
	public static String formatTime(long millis) {
		String formattedTime;
		
		String hoursFormat = "";
		int hours = (int)(millis / 3600000);
		if(hours >= 1) {
			millis -= hours * 3600000;
			if(hours < 10) {
				hoursFormat = "0" + hours;
			}
			else {
				hoursFormat = "" + hours;
			}
			hoursFormat += ":";
		}

		String minuteFormat;
		int minutes = (int)(millis / 60000);
		if(minutes >= 1) {
			millis -= minutes * 60000;
			if(minutes < 10) {
				minuteFormat = "0" + minutes;
			}
			else {
				minuteFormat = "" + minutes;
			}
		}
		else {
			minuteFormat = "00";
		}
		
		String secondFormat;
		int seconds = (int)(millis / 1000);
		if(seconds >= 1) {
			millis -= seconds * 1000;
			if(seconds < 10) {
				secondFormat = "0" + seconds;
			}
			else {
				secondFormat = "" + seconds;
			}
		}
		else {
			secondFormat = "00";
		}
		
		String milliFormat;
		if(millis > 99) {
			milliFormat = "" + millis;
		}
		else if (millis > 9) {
			milliFormat = "0" + millis;
		}
		else {
			milliFormat = "00" + millis;
		}
		
		formattedTime = hoursFormat + minuteFormat + ":" + secondFormat + ":" + milliFormat;
		return formattedTime;
	}
	public static String formatTimeToSeconds(long millis) {
		String formattedTime;
		
		String hoursFormat = "";
		int hours = (int)(millis / 3600000);
		if(hours >= 1) {
			millis -= hours * 3600000;
			if(hours < 10) {
				hoursFormat = "0" + hours;
			}
			else {
				hoursFormat = "" + hours;
			}
			hoursFormat += ":";
		}

		String minuteFormat;
		int minutes = (int)(millis / 60000);
		if(minutes >= 1) {
			millis -= minutes * 60000;
			if(minutes < 10) {
				minuteFormat = "0" + minutes;
			}
			else {
				minuteFormat = "" + minutes;
			}
		}
		else {
			minuteFormat = "00";
		}
		
		String secondFormat;
		int seconds = (int)(millis / 1000);
		if(seconds >= 1) {
			millis -= seconds * 1000;
			if(seconds < 10) {
				secondFormat = "0" + seconds;
			}
			else {
				secondFormat = "" + seconds;
			}
		}
		else {
			secondFormat = "00";
		}
		
		formattedTime = hoursFormat + minuteFormat + ":" + secondFormat;
		return formattedTime;
	}
}
