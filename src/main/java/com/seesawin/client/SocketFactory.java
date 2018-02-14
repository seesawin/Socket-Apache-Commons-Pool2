package com.seesawin.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class SocketFactory extends BasePooledObjectFactory<Socket> {
	InetSocketAddress serverInfo = new InetSocketAddress("127.0.0.1", 10008);

	@Override
	public Socket create() throws IOException {
		System.out.println(">>>>> SocketFactory.create...");

		Socket socket = new Socket();
		try {
			socket.connect(serverInfo);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		PrintWriter pw;
		BufferedReader br = null;

		String respStr = "";
		try {
			try {
				pw = new PrintWriter(socket.getOutputStream(), true);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				System.out.println(">>>>> login ...");
				pw.println("login");

				respStr = br.readLine();
				System.out.println(">>>>> respone: " + respStr);

				System.out.println(">>>>> hand shake ...");
				pw.println("hand");

				respStr = br.readLine();
				System.out.println(">>>>> respone: " + respStr);

			} catch (IOException ioe) {
				System.out.println("error: " + ioe.getMessage());
			} finally {
				socket.setKeepAlive(true);
			}

		} catch (NoSuchElementException e) {
			System.out.println(e);
		} catch (IllegalStateException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}

		return socket;
	}

	/**
	 * Use the default PooledObject implementation.
	 */
	@Override
	public PooledObject<Socket> wrap(Socket buffer) {
		System.out.println(">>>>> SocketFactory.wrap...");
		return new DefaultPooledObject<Socket>(buffer);
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 */
	@Override
	public void passivateObject(PooledObject<Socket> pooledObject) {
		System.out.println(">>>>> SocketFactory.passivateObject...");
	}

	@Override
	public PooledObject<Socket> makeObject() throws Exception {
		System.out.println(">>>>> SocketFactory.makeObject...");
		return super.makeObject();
	}

	@Override
	public void destroyObject(PooledObject<Socket> p) throws Exception {
		System.out.println(">>>>> SocketFactory.destroyObject...");

		System.out.println(">>>>> A isClosed: " + p.getObject().isClosed());
		System.out.println(">>>>> A isConnected: " + p.getObject().isConnected());

		PrintWriter pw = null;
		BufferedReader br = null;

		pw = new PrintWriter(p.getObject().getOutputStream(), true);
		br = new BufferedReader(new InputStreamReader(p.getObject().getInputStream()));

		System.out.println("--------------- send bye to server ...");

		// socket will be closed by socket server after send this key word.
		pw.println("bye");

		String respStr = br.readLine();
		while (respStr != null) {
			// there is nothing returned from server, actually it depends on what you design
			System.out.println(">>>>> respStr : " + respStr);
			respStr = br.readLine();
		}

		System.out.println("--------------- leave the server ...");

		System.out.println(">>>>> B isClosed: " + p.getObject().isClosed());
		System.out.println(">>>>> B isConnected: " + p.getObject().isConnected());

		super.destroyObject(p);
	}

	@Override
	public boolean validateObject(PooledObject<Socket> p) {
		System.out.println(">>>>> SocketFactory.validateObject...");

		boolean b = p.getObject().isClosed();
		System.out.println(">>>>> socket isClosed: " + b);

		return super.validateObject(p);
	}

	@Override
	public void activateObject(PooledObject<Socket> p) throws Exception {
		System.out.println(">>>>> SocketFactory.activateObject...");
		super.activateObject(p);
	}

	// for all other methods, the no-op implementation
	// in BasePooledObjectFactory will suffice

}
