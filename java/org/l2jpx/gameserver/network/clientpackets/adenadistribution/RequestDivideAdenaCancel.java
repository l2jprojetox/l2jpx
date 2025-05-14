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
package org.l2jpx.gameserver.network.clientpackets.adenadistribution;

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;

/**
 * @author Sdw
 */
public class RequestDivideAdenaCancel extends ClientPacket
{
	private boolean _cancel;
	
	@Override
	protected void readImpl()
	{
		_cancel = readByte() == 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_cancel)
		{
			final AdenaDistributionRequest request = player.getRequest(AdenaDistributionRequest.class);
			for (Player p : request.getPlayers())
			{
				if (p != null)
				{
					p.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
					p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
					p.removeRequest(AdenaDistributionRequest.class);
				}
			}
		}
	}
}
