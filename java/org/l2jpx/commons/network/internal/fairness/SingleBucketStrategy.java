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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.l2jpx.commons.network.Client;

/**
 * Implements a single bucket strategy for fairness.<br>
 * This strategy uses a single queue to manage clients and ensures fair processing of actions by cycling through them in order.
 * @author JoeAlisson
 */
public class SingleBucketStrategy implements FairnessStrategy
{
	private final ConcurrentLinkedQueue<Client<?>> _readyClients = new ConcurrentLinkedQueue<>();
	
	@Override
	public void doNextAction(Client<?> client, Consumer<Client<?>> action)
	{
		_readyClients.offer(client);
		final Client<?> next = _readyClients.poll();
		if (next != null)
		{
			action.accept(next);
		}
	}
}
