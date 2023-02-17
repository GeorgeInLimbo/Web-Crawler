package com.smt.training.spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/****
 * 
 * <b>Title:</b> SslConnector.java<br>
 * <b>Project:</b> com.smt.training.spider-lib<br>
 * <b>Description:</b>This class manages the socket connection to a server and
 * processes GET and POST requests. To instantiate the class, the user must
 * provide a file path and a server host.<br>
 * <b>Copyright:</b> Copyright (c) 2023<br>
 * <b>Company:</b> Silicon Mountain Technologies<br>
 * 
 * @author George Clam
 * @version 1.0
 * @since Feb 08 2023
 * @updates:
 ****/

public class HTMLAcquisition {
	Logger logr = Logger.getLogger(HTMLAcquisition.class.getName());
	private String path;
	private String host;
	private DataParser parse;

	/**
	 * This is a controller for the class which accepts values for a file path and a
	 * server host.
	 * 
	 * @param path - This is the file path that will be used to have any data
	 *             retrieved saved to the resources directory.
	 * @param host - This parameter will represent the server host that is being
	 *             utilized by the socket.
	 */
	HTMLAcquisition(String path, String host) {
		this.path = path;
		this.host = host;
		parse = new DataParser();
	}

	/**
	 * This method serves to establish a socket connection to a given server host,
	 * and manages procedure for making a get request and writing any information
	 * from the data stream to a file in the resources directory.
	 */
	public void acquire() {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		HTMLWriter writeFile = new HTMLWriter();

		try (SSLSocket socket = (SSLSocket) factory.createSocket(host, 443)) {
			logr.info("SSL Socket connection created.");
			logr.info("Connected: " + socket.isConnected());
			logr.info("Is Input Shutdown: " + socket.isInputShutdown());

			makeGETRequest(socket, path, host);
			String response = readStream(socket);
			writeFile.writeToFile(response, path.replace("?", "").replace("=", "").replace("&", ""));
		} catch (IOException e) {
			logr.log(Level.WARNING, "Error occurred in establishing a connection with the host.", e);
		}
	}

	/**
	 * This method establishes a reader for the data stream, saves that data to a
	 * StringBuilder and returns a string after all data has been read.
	 * 
	 * @param socket - SSLSocket connection for the Input Stream to latch onto.
	 * @return - Returns a String of data read from the data stream.
	 * @throws IOException
	 */
	private String readStream(SSLSocket socket) throws IOException {
		logr.info("reading stream");
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		StringBuilder build = new StringBuilder();
		String inData;
		while ((inData = in.readLine()) != null) {
			build.append(inData + "\r\n");
			logr.info(inData);
		}
		return build.toString();
	}

	/**
	 * This method makes a GET request and includes cookies if any where extracted
	 * from the website so that any extraction can be completed in a single session.
	 * 
	 * @param socket - An SSLSocket for the Output Stream to latch onto.
	 * @param path   - Utilizes a path for the GET request header.
	 * @param host   - provides the name of the server host for the GET request
	 *               header.
	 * @throws IOException
	 */
	private void makeGETRequest(SSLSocket socket, String path, String host) throws IOException {
		logr.info("New data stream created.");
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.writeBytes("GET " + path + " HTTP/1.0\r\n");
		out.writeBytes("Host: " + host + "\r\n");

		String cookieString = parse.retrieveCookies();
		if (parse.cookies != null && !parse.cookies.isEmpty()) {
			logr.info("COOKIES!: " + cookieString);
			out.writeBytes("Cookie: " + cookieString);
		}
		out.writeBytes("\r\n");
		out.flush();
	}

	// Review how this method is implemented
	public Set<String> parseLinks(File file) throws IOException {
		return parse.parseLinks(file);
	}

	/**
	 * This method logs into the admin page of the Web Crescendo and makes a POST
	 * request, placing credentials for access in the request body. It then extracts
	 * cookies from the HTTP response.
	 * 
	 * @throws IOException
	 */
	public void login() throws IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		HTMLWriter writeFile = new HTMLWriter();
		StringBuilder build = new StringBuilder();
		String credentials = "requestType=reqBuild&pmid=ADMIN_LOGIN&emailAddress=<>&password=<>&l=";

		try (SSLSocket socket = (SSLSocket) factory.createSocket(host, 443)) {
			socket.startHandshake();
			logr.info("SSL Socket connection created.");

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			logr.info("Data stream created.");

			// HTTP Request Header
			writer.write("POST " + path + " HTTP/1.0\r\n");
			writer.write("Host: " + host + "\r\n");
			writer.write("Content-Length: " + credentials.length() + "\r\n");
			writer.write("Content-Type: application/x-www-form-urlencoded\r\n");
			writer.write("\r\n");
			// HTTP Request Body
			writer.write(credentials + "\r\n");
			writer.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String html = "";

			while ((html = reader.readLine()) != null) { // Get info from HTTP response
				build.append(html + "\r\n");
				if (html.startsWith("Set-Cookie")) { // Obtain cookies from HTTP Response header
					parse.cookies.add(html.substring(12, (html.indexOf(";") + 1)));
				}
			}
			writeFile.writeToFile(build.toString(), "/staging-home");
			writer.close();
			reader.close();

		} catch (IOException e) {
			logr.log(Level.WARNING, "There was an error in establishing an SSL Socket connection.", e);
		}
	}
}
