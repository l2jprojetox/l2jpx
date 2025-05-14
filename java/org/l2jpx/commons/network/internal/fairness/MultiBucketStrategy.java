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
package org.l2jpx.commons.network.internal.fairness;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.l2jpx.commons.network.Client;

/**
 * Implements a fairness strategy using multiple buckets.<br>
 * This strategy distributes clients across a number of buckets and cycles through them to achieve fairness.
 * @author JoeAlisson
 */
public class MultiBucketStrategy implements FairnessStrategy
{
	private final Queue<Client<?>>[] _readyBuckets;
	private final int _fairnessBuckets;
	private int _nextOffer;
	private int _nextPoll;
	
	@SuppressWarnings("unchecked")
	MultiBucketStrategy(int fairnessBuckets)
	{
		_readyBuckets = new ConcurrentLinkedQueue[fairnessBuckets];
		_fairnessBuckets = fairnessBuckets;
		for (int i = 0; i < fairnessBuckets; i++)
		{
			_readyBuckets[i] = new ConcurrentLinkedQueue<>();
		}
	}
	
	@Override
	public void doNextAction(Client<?> client, Consumer<Client<?>> action)
	{
		final int offer = _nextOffer++ % _fairnessBuckets;
		Queue<Client<?>> nextBucket = _readyBuckets[offer];
		nextBucket.offer(client);
		
		final int poll = _nextPoll++ % _fairnessBuckets;
		
		nextBucket = _readyBuckets[poll];
		final Client<?> next = nextBucket.poll();
		if (next != null)
		{
			action.accept(next);
		}
	}
}
