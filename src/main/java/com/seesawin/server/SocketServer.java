package com.seesawin.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

public class SocketServer {
	public static Vector<Thread> threadPool = new Vector<Thread>();

	public static void main(String[] args) {
		int i = 1;
		try {
			System.out.println("start server");

			ServerSocket serverSocket = new ServerSocket(10008);

			for (;;) {
				Socket newClientSocket = serverSocket.accept();
				new SocketServer_Thread(newClientSocket, i, threadPool).start();
				i++;
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
		}
	}
}

class SocketServer_Thread extends Thread {
	private Socket clientSocket;
	private int counter;

	// It's a common poll, every thread use this pool to get new information
	private Vector<Thread> threadPool;

	public SocketServer_Thread(Socket clientSocket, int c, Vector<Thread> threadPool) {
		this.clientSocket = clientSocket;
		this.counter = c;
		this.threadPool = threadPool;
	}

	public void connectionStarted() {
		threadPool.add(this);
		System.out.println("ID : " + this.counter + ", Connection Started");
	}

	public void connectionEnded() {
		threadPool.remove(this);
		System.out.println("ID : " + counter + ", Connection Ended");
	}

	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// add this thread in the pool
			connectionStarted();

			boolean done = false;

			// this thread always listen the request
			while (!done) {
				for (Enumeration<Thread> enumeration = threadPool.elements(); enumeration.hasMoreElements();) {

					SocketServer_Thread tempThread = (SocketServer_Thread) enumeration.nextElement();
					Socket tempSocket = tempThread.clientSocket;

					int tempCounter = tempThread.counter;

					// only handle the same thread's information
					if (this.counter == tempCounter) {
						// block this thread until it recevid new request
						String request = br.readLine();

						String radom = getRandomString();
						String info = tempSocket.toString();
						StringBuilder response = new StringBuilder();
						PrintWriter pw = null;

						try {
							pw = new PrintWriter(tempSocket.getOutputStream(), true /* autoFlush */);

							// the business logic ....
							if ("bye".equals(request)) {
								done = true;
								break;
							} else if ("login".equals(request)) {
								response.append("login success!");
							} else if ("hand".equals(request)) {
								response.append("shake OK!___");
							} else {
								response.append("transfer___");
							}

							response.append("___socket info= " + info);
							response.append("___request info= " + request);
							response.append("___counter:tempCounter= " + counter + ":" + tempCounter);
							response.append("___token=: " + radom);

							String respStr = response.toString();
							pw.println(respStr);
							System.out.println("response : " + respStr);

						} catch (Exception ex) {
							System.out.println("Exception : " + ex);
						}
					} else {
						// System.out.println("NOT CURRENT THREAD!___" + "counter : " + counter + ", tempCounter : " + tempCounter);
					}
				}
			}

			// close the socket's connection
			clientSocket.close();
		} catch (Exception e) {
			System.out.println("Exception : " + e);
		}

		// remove this thread from pool after connection is closed
		connectionEnded();
	}

	public static String getRandomString() {
		char seeds[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
		int strLen = (int) Math.round(Math.random() * 10) + 5;
		char randStr[] = new char[strLen];
		for (int i = 0; i < randStr.length; i++) {
			randStr[i] = seeds[(int) Math.round(Math.random() * (seeds.length - 1))];
		}
		String returnStr = new String(randStr);
		return returnStr;
	}
}
