package com.smt.training.spider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/****
 * 
 * <b>Title:</b> FileWriter.java<br>
 * <b>Project:</b> com.smt.training.spider-lib<br>
 * <b>Description:</b>This class is used to write HTML data to a file in the
 * resources directory.<br>
 * <b>Copyright:</b> Copyright (c) 2023<br>
 * <b>Company:</b> Silicon Mountain Technologies<br>
 * 
 * @author George Clam
 * @version 1.0
 * @since Feb 06 2023
 * @updates:
 ****/

public class HTMLWriter {
	static final String FILE_PATH = "src/main/resources/Parsed HTML Files";

	/**
	 * This method verifies a path name and writes HTML data to a .html file saved
	 * to the resources directory.
	 * 
	 * @param html
	 * @param path
	 * @throws IOException
	 */
	public void writeToFile(String html, String path) throws IOException {
		String file = FILE_PATH + (("/".equals(path)) ? "/home.html" : path + ".html");
		File outputFile = new File(file);
		try (FileWriter writer = new FileWriter(outputFile)) {
			writer.write(html);
		}
	}
}
