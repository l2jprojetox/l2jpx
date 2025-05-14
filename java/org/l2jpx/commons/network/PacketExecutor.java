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

/**
 * Defines a functional interface for executing incoming network packets.<br>
 * Implementations should handle the processing of packets, ideally offloading long-running or blocking operations to separate threads.
 * @param <T> The type of Client associated with the packet to be executed.
 * @author JoeAlisson
 */
@FunctionalInterface
public interface PacketExecutor<T extends Client<Connection<T>>>
{
	/**
	 * Executes the provided packet.<br>
	 * It is highly recommended to execute long-running or blocking code in another thread to avoid network processing delays.
	 * @param packet The packet to be executed.
	 */
	void execute(ReadablePacket<T> packet);
}
