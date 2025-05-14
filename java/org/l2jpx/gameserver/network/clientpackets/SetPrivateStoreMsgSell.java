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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.Config;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2jpx.gameserver.util.Util;

/**
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreMsgSell extends ClientPacket
{
	private static final int MAX_MSG_LENGTH = 29;
	
	private String _storeMsg;
	
	@Override
	protected void readImpl()
	{
		_storeMsg = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || (player.getSellList() == null))
		{
			return;
		}
		
		if ((_storeMsg != null) && (_storeMsg.length() > MAX_MSG_LENGTH))
		{
			Util.handleIllegalPlayerAction(player, player + " tried to overflow private store sell message", Config.DEFAULT_PUNISH);
			return;
		}
		
		player.getSellList().setTitle(_storeMsg);
		player.sendPacket(new PrivateStoreMsgSell(player));
	}
}
