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
package org.l2jpx.commons.network;

import java.nio.channels.CompletionHandler;

/**
 * Handles the completion of write operations for network clients.<br>
 * This class implements {@link CompletionHandler} to process the results of data writing to the client, ensuring proper handling of the write operation's conclusion.
 * @param <T> The type of Client associated with this write handler.
 * @author JoeAlisson
 */
class WriteHandler<T extends Client<Connection<T>>> implements CompletionHandler<Long, T>
{
	// private static final Logger LOGGER = Logger.getLogger(WriteHandler.class.getName());
	
	@Override
	public void completed(Long result, T client)
	{
		// Client probably disconnected.
		if (client == null)
		{
			return;
		}
		
		if (result < 0)
		{
			// LOGGER.warning("Couldn't send data to client " + client);
			if (client.isConnected())
			{
				client.disconnect();
			}
			return;
		}
		
		if ((result < client.getDataSentSize()) && (result > 0))
		{
			// LOGGER.info("Still " + result + " data to send. Trying to send");
			client.resumeSend(result);
		}
		else
		{
			client.finishWriting();
		}
	}
	
	@Override
	public void failed(Throwable e, T client)
	{
		// if (!(e instanceof IOException))
		// {
		// LOGGER.log(Level.WARNING, e.getMessage(), e);
		// }
		client.disconnect();
	}
}
