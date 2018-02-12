package com.seesawin.client;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class SocketPreconnectPool<T> extends GenericObjectPool<T> {

	public SocketPreconnectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig config) {
		super(factory, config);
		// TODO Auto-generated constructor stub
	}

	@Override
	public T borrowObject() throws Exception {
		System.out.println(">>>>> SocketPreconnectPool.borrowObject...");
		return super.borrowObject();
	}

	@Override
	public void returnObject(T obj) {
		System.out.println(">>>>> SocketPreconnectPool.returnObject...");
		super.returnObject(obj);
	}

	@Override
	public void addObject() throws Exception {
		System.out.println(">>>>> SocketPreconnectPool.addObject...");
		super.addObject();
	}

}
