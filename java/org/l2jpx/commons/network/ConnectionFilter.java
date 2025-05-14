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

import java.nio.channels.AsynchronousSocketChannel;

/**
 * Defines a filter for incoming network connections.<br>
 * This functional interface is used to determine whether an incoming connection should be accepted or rejected.
 * @author JoeAlisson
 */
@FunctionalInterface
public interface ConnectionFilter
{
	/**
	 * Determines if a given connection channel should be accepted.<br>
	 * Implementations of this method should include logic to evaluate the acceptability of an incoming connection.
	 * @param channel The AsynchronousSocketChannel to be evaluated.
	 * @return true if the channel is acceptable, false otherwise.
	 */
	boolean accept(AsynchronousSocketChannel channel);
}
