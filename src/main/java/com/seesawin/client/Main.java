package com.seesawin.client;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class Main {
	SocketPreconnectPool<Socket> pool = null;
	GenericObjectPoolConfig config = new GenericObjectPoolConfig();
	private int count = 0;

	Main() {
		// set the max and shared sockets which offer multi threads to use
		config.setMaxTotal(2);
		
		// config.setMinIdle(2);
		// config.setMaxWaitMillis(1);
		// config.setSoftMinEvictableIdleTimeMillis(0);
		// config.setTestOnReturn(true);
		// config.setTestOnBorrow(true);

		pool = new SocketPreconnectPool<Socket>(new SocketFactory(), config);
	}

	public static void main(String[] args) {

		Main main = new Main();

		// in this demo, there are 4 threads which use only 2 shared socket
		MyThread1 thread1 = main.new MyThread1();
		MyThread1 thread2 = main.new MyThread1();
		MyThread1 thread3 = main.new MyThread1();
		MyThread1 thread4 = main.new MyThread1();

		thread1.start();

		int sleep = 0;

		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread2.start();
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread3.start();

		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread4.start();
	}

	class MyThread1 extends Thread {
		@Override
		public void run() {
			System.out.println("Main ---------------- in thread " + ++count);
			SocketUtil readerUtil = new SocketUtil(pool);

			String result = null;
			try {
				result = readerUtil.sendToServer("frankClient");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
