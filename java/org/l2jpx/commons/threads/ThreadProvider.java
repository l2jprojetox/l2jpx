package org.l2jpx.commons.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory implementation that allows setting a thread name prefix, priority, and daemon status when creating new threads.
 * @author Pantelis Andrianakis
 * @since October 18th 2022
 */
public class ThreadProvider implements ThreadFactory
{
	private final AtomicInteger _id = new AtomicInteger();
	private final String _prefix;
	private final int _priority;
	private final boolean _daemon;
	
	/**
	 * Creates a new ThreadProvider with the specified prefix, normal thread priority, and non-daemon threads.
	 * @param prefix the prefix to be used for thread names
	 */
	public ThreadProvider(String prefix)
	{
		this(prefix, ThreadPriority.PRIORITY_5, false);
	}
	
	/**
	 * Creates a new ThreadProvider with the specified prefix and daemon status, and normal thread priority.
	 * @param prefix the prefix to be used for thread names
	 * @param daemon whether the threads should be daemon threads
	 */
	public ThreadProvider(String prefix, boolean daemon)
	{
		this(prefix, ThreadPriority.PRIORITY_5, daemon);
	}
	
	/**
	 * Creates a new ThreadProvider with the specified prefix and priority, and non-daemon threads.
	 * @param prefix the prefix to be used for thread names
	 * @param priority the priority of the threads
	 */
	public ThreadProvider(String prefix, ThreadPriority priority)
	{
		this(prefix, priority, false);
	}
	
	/**
	 * Creates a new ThreadProvider with the specified prefix, priority, and daemon status.
	 * @param prefix the prefix to be used for thread names
	 * @param priority the priority of the threads
	 * @param daemon whether the threads should be daemon threads
	 */
	public ThreadProvider(String prefix, ThreadPriority priority, boolean daemon)
	{
		_prefix = prefix + " ";
		_priority = priority.getId();
		_daemon = daemon;
	}
	
	/**
	 * Creates a new Thread with the specified Runnable object and with the properties defined in this ThreadProvider.
	 * @param runnable the object whose run method is invoked when this thread is started
	 * @return the created Thread
	 */
	@Override
	public Thread newThread(Runnable runnable)
	{
		final Thread thread = new Thread(runnable, _prefix + _id.incrementAndGet());
		thread.setPriority(_priority);
		thread.setDaemon(_daemon);
		return thread;
	}
}
