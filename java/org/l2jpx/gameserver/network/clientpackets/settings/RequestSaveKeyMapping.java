/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.network.clientpackets.settings;

import org.l2jpx.Config;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.variables.PlayerVariables;
import org.l2jpx.gameserver.network.ConnectionState;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;

/**
 * Request Save Key Mapping client 
 * @author Mobius
 */
public class RequestSaveKeyMapping extends ClientPacket
{
	public static final String SPLIT_VAR = "	";
	
	private byte[] _uiKeyMapping;
	
	@Override
	protected void readImpl()
	{
		final int dataSize = readInt();
		if (dataSize > 0)
		{
			_uiKeyMapping = readBytes(dataSize);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (!Config.STORE_UI_SETTINGS || //
			(player == null) || //
			(_uiKeyMapping == null) || //
			(getClient().getConnectionState() != ConnectionState.IN_GAME))
		{
			return;
		}
		
		String uiKeyMapping = "";
		for (Byte b : _uiKeyMapping)
		{
			uiKeyMapping += b + SPLIT_VAR;
		}
		player.getVariables().set(PlayerVariables.UI_KEY_MAPPING, uiKeyMapping);
	}
}
