package com.pool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BoundedNonblockingPool < T > extends AbstractGenericPool < T >
{
	private int size;

	private Queue < T > objects;

	private ConnectionValidator < T > validator;
	private ObjectFactory < T > objectFactory;

	private Semaphore permits = new Semaphore(1);

	private volatile boolean shutdownCalled;

	public BoundedNonblockingPool(
			int size, 
			ConnectionValidator < T > validator, 
			ObjectFactory < T > objectFactory)
	{
		super();

		this.objectFactory = objectFactory;
		this.size = size;
		this.validator = validator;

		objects = new LinkedList < T >();

		initializeObjects();

		shutdownCalled = false;
	}


	@Override
	public T get()
	{
		T t = null;

		if(!shutdownCalled)
		{
			if(permits.tryAcquire())
			{
				t = objects.poll();
			}
		}
		else
		{
			throw new IllegalStateException(
					"Object pool already shutdown");
		}

		return t;
	}

	@Override
	public void shutdown()
	{
		shutdownCalled = true;

		clearResources();
	}

	private void clearResources()
	{
		for(T t : objects)
		{
			validator.invalidate(t);
		}
	}

	@Override
	protected void returnToPool(T t)
	{
		boolean added = objects.add(t);

		if(added)
		{
			permits.release();
		}
	}

	@Override
	protected void handleInvalidReturn(T t)
	{

	}

	@Override
	protected boolean isValid(T t)
	{
		return validator.isValid(t);
	}

	private void initializeObjects()
	{
		for(int i = 0; i < size; i++)
		{
			objects.add(objectFactory.createNew());
		}
	}
}

