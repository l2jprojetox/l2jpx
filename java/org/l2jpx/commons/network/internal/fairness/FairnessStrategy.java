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

import java.util.function.Consumer;

import org.l2jpx.commons.network.Client;

/**
 * Defines a strategy for managing fairness in executing actions across clients.<br>
 * Implementations of this interface will determine how actions are distributed fairly among clients.
 * @author JoeAlisson
 */
interface FairnessStrategy
{
	/**
	 * Executes an action for a client in a manner that ensures fairness across all clients.
	 * @param client The client for which the action is to be executed.
	 * @param action The action to be performed, represented as a Consumer of Client.
	 */
	void doNextAction(Client<?> client, Consumer<Client<?>> action);
}
