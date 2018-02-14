package com.seesawin.client;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class Main {
	ObjectPool<Socket> pool = null;

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

		// true : keep sockets alive
		boolean clearPool = false;

		System.out.println(">>>>> start first job...");

		// in this demo, there are 4 threads which use only 2 shared socket
		MyThread1 thread1 = main.new MyThread1();
		MyThread1 thread2 = main.new MyThread1();
		MyThread1 thread3 = main.new MyThread1();
		MyThread1 thread4 = main.new MyThread1();

		int sleep = 0;

		thread1.start();
		main.sleep(sleep);

		thread2.start();
		main.sleep(sleep);

		thread3.start();
		main.sleep(sleep);

		thread4.start();
		main.sleep(sleep);

		long inteval = 5000;
		main.sleep(inteval);
		
		if (clearPool) {
			main.cleanPool();
		}

		System.out.println(">>>>> start second job...");

		MyThread1 thread5 = main.new MyThread1();
		MyThread1 thread6 = main.new MyThread1();
		MyThread1 thread7 = main.new MyThread1();
		MyThread1 thread8 = main.new MyThread1();

		thread5.start();
		main.sleep(sleep);

		thread6.start();
		main.sleep(sleep);

		thread7.start();
		main.sleep(sleep);

		thread8.start();
		main.sleep(sleep);
		
		main.sleep(inteval);
		
		if (clearPool) {
			main.cleanPool();
		}
		
		main.sleep(inteval);
		
		System.out.println("end...");

	}

	class MyThread1 extends Thread {
		@Override
		public void run() {
			System.out.println("Main ---------------- in thread " + ++count);
			SocketUtil readerUtil = new SocketUtil(pool);

			try {
				readerUtil.sendToServer("frankClient_" + count);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void sleep(long inteval) {
		System.out.println("sleeping...");

		try {
			Thread.sleep(inteval);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void cleanPool() {
		System.out.println("cleanPool...");
		// clear all the sockets in the pool that will invoke the destroyObject method of the SocketFactory.
		// if you want to keep sockets alive, don't call this method.
		try {
			pool.clear();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
