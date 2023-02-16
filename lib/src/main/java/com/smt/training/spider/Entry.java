package com.smt.training.spider;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/****
 * 
 * <b>Title:</b> Entry.java<br>
 * <b>Project:</b> com.smt.training.spider-lib<br>
 * <b>Description:</b>This class serves as an entry point for the application.
 * It largely handles process and procedure to extract requested HTML data from
 * a website.<br>
 * <b>Copyright:</b> Copyright (c) 2023<br>
 * <b>Company:</b> Silicon Mountain Technologies<br>
 * // *
 * 
 * @author George Clam
 * @version 1.0
 * @since Feb 08 2023
 * @updates:
 ****/

public class Entry {

	/**
	 * This method executes the application by running the control method. Will log
	 * when the application procedure has completed.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Entry spider = new Entry();
		spider.go();
		System.out.println("Reached the end.");
	}

	/**
	 * This is the controller method for the application. It will serve as a script
	 * for the application.
	 * 
	 * @throws IOException
	 */
	public void go() throws IOException {
		Set<String> hrefs = new HashSet<>();
		Set<String> processed = new HashSet<>();
		String adminPath = "/sb/admintool?cPage=index&actionId=WEB_SOCKET&organizationId=SMT";

		hrefs.addAll(crawl("/"));
		processed.add("/");

		while (!hrefs.isEmpty()) {
			for (String link : hrefs) {
				hrefs.addAll(crawl(link));
				processed.add(link);
			}
			hrefs.removeIf(processed::contains);
		}
		// Get Web Socket Page
		HTMLAcquisition x = new HTMLAcquisition(adminPath, "smt-stage.qa.siliconmtn.com");
		x.login();
		x.acquire();
	}

	/**
	 * This application utilizes helper methods to make an initial GET request to a
	 * website, saves the data from the response, and parses a-tags from the data.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private Set<String> crawl(String path) throws IOException {
		HTMLAcquisition html = new HTMLAcquisition(path, "smt-stage.qa.siliconmtn.com");
		html.acquire();
		File file = new File(HTMLWriter.FILE_PATH + "/home.html");
		return html.parseLinks(file);
	}
}