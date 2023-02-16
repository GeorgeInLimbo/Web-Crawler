package com.smt.training.spider;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/****
 * <b>Title:</b> HTMLParser.java<br>
 * <b>Project:</b> com.smt.training.spider-lib<br>
 * <b>Description:</b>This class extracts data from a reader, whether it be HTML
 * data or HTTP Responses. <br>
 * <b>Copyright:</b> Copyright (c) 2023<br>
 * <b>Company:</b> Silicon Mountain Technologies<br>
 * 
 * @author George Clam
 * @version 1.0
 * @since Feb 06 2023
 * @updates:
 ****/

public class DataParser {
	Set<String> hrefs = new HashSet<>();
	Set<String> cookies = new HashSet<>();

	/**
	 * This method uses J-Soup to parse <a[href]> data from a .html file, adds them
	 * to a HashSet, and removes unhelpful links.
	 * 
	 * @param file - This file will be parsed for a-tags.
	 * @return - A Set of parsed links from a given text file.
	 * @throws IOException
	 */
	public Set<String> parseLinks(File file) throws IOException {
		Document document = Jsoup.parse(file);
		document.select("a").forEach(x -> hrefs.add(x.attr("href")));
		hrefs.removeIf(href -> !href.startsWith("/")); // Removes links that are not paths to web page
		return hrefs;
	}

	/**
	 * This method provides a list of cookies that are specifically formatted for an
	 * HTTP request header.
	 * 
	 * @return - Returns a formatted string of all cookies extracted from an HTTP
	 *         request.
	 * @throws IOException
	 */
	public String retrieveCookies() throws IOException {
		StringBuilder build = new StringBuilder();
		for (String cookie : cookies) {
			build.append(cookie);
		}
		return build.toString();
	}
}