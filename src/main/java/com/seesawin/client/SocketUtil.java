package com.seesawin.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.ObjectPool;

public class SocketUtil {
	private ObjectPool<Socket> pool;

	public SocketUtil(ObjectPool<Socket> pool) {
		this.pool = pool;
	}

	// data transfer
	public String sendToServer(String request) throws Exception {

		Socket connectionSocket = null;
		PrintWriter pw;
		BufferedReader br = null;

		String respStr = "";

		try {
			try {
				System.out.println("--------------- send to server ...");
				connectionSocket = getConnection();

				String soInfo = connectionSocket.toString();
				System.out.println(">>>>>>>>>>>>>>>> soInfo : " + soInfo);

				pw = new PrintWriter(connectionSocket.getOutputStream(), true);
				br = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

				pw.println(request);

				// assume the response is always just a line
				respStr = br.readLine();
				System.out.println(">>>>> respStr : " + respStr);

				System.out.println("--------------- leave the server ...");

			} catch (IOException ioe) {
				System.out.println("error: " + ioe.getMessage());
				return null;
			} finally {
				connectionSocket.setKeepAlive(true);
			}

			return respStr.toString();

		} catch (NoSuchElementException e) {
			System.out.println(e);
		} catch (IllegalStateException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				long start = System.currentTimeMillis();

				releaseConnection(connectionSocket);

				System.out.println("RETURN spend::" + (System.currentTimeMillis() - start));
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		return null;
	}

	// get socket
	private Socket getConnection() throws Exception {

		int actives = pool.getNumActive();
		int idle = pool.getNumIdle();
		System.out.println("SocketUtil.getConnection before now actives : " + actives + ", idle : " + idle);

		System.out.println(">>>>> borrowObject start");
		Socket connectionSocket = pool.borrowObject();
		System.out.println(">>>>> borrowObject end");

		actives = pool.getNumActive();
		idle = pool.getNumIdle();
		System.out.println("SocketUtil.getConnection after now actives : " + actives + ", idle : " + idle);

		return connectionSocket;
	}

	// return socket
	private Socket releaseConnection(Socket connectionSocket) throws Exception {

		int actives = pool.getNumActive();
		int idle = pool.getNumIdle();
		System.out.println("SocketUtil.releaseConnection before now actives : " + actives + ", idle : " + idle);

		System.out.println(">>>>> returnObject start");
		pool.returnObject(connectionSocket);
		System.out.println(">>>>> returnObject end");

		actives = pool.getNumActive();
		idle = pool.getNumIdle();
		System.out.println("SocketUtil.releaseConnection after now actives : " + actives + ", idle : " + idle);

		return connectionSocket;
	}
}
