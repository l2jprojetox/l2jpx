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
 * Defines a factory interface for creating Client instances.<br>
 * This functional interface specifies a method to create a new Client using a given connection.
 * @param <T> The type of Client to be created, extending Client with a specified Connection type.
 * @author JoeAlisson
 */
@FunctionalInterface
public interface ClientFactory<T extends Client<Connection<T>>>
{
	/**
	 * Creates a new Client instance using the provided connection.<br>
	 * Implementations of this method should provide the logic to instantiate a specific Client type.
	 * @param connection The underlying connection to the client.
	 * @return A new instance of a client implementation.
	 */
	T create(Connection<T> connection);
}
