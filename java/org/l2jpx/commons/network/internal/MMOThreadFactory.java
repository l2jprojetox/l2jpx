/*
 * Copyright © 2019-2021 Async-mmocore
 *
 * This file is part of the Async-mmocore project.
 *
 * Async-mmocore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Async-mmocore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.commons.network.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory used for creating threads for MMO server tasks.<br>
 * This factory assigns custom names and priorities to the threads it creates, aiding in identification and management.
 * @author JoeAlisson
 */
public class MMOThreadFactory implements ThreadFactory
{
	private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
	
	private final AtomicInteger _threadNumber = new AtomicInteger(1);
	private final String _namePrefix;
	private final int _priority;
	
	public MMOThreadFactory(String name, int priority)
	{
		_namePrefix = name + "-MMO-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
		_priority = priority;
	}
	
	@Override
	public Thread newThread(Runnable r)
	{
		final Thread thread = new Thread(null, r, _namePrefix + _threadNumber.getAndIncrement(), 0);
		thread.setPriority(_priority);
		thread.setDaemon(false);
		return thread;
	}
}
