package com.germistry.utils;

import java.io.File;

public class FileUtils {

	private static String workingDirectory;
	private static String folder = "/germistry/";
	
	private FileUtils() {}
	
	public static String filePath() {
		
		String OS = (System.getProperty("os.name")).toUpperCase();
		if(OS.contains("WIN")) {
			workingDirectory = System.getenv("AppData");
		}
		else {
			workingDirectory = System.getProperty("user.home");
		}
		File f = new File(workingDirectory + File.separator + folder);
		if(!f.exists()) {
			f.mkdir(); 
		}
		return new File(workingDirectory + File.separator + folder).getAbsolutePath();
	}
	
}
