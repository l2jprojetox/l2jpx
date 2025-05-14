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
package org.l2jpx.gameserver.network.serverpackets;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.sevensigns.SevenSigns;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * Changes the sky color depending on the outcome of the Seven Signs competition.
 * @author Tempy
 */
public class SSQInfo extends ServerPacket
{
	private int _state = 0;
	
	public SSQInfo()
	{
		final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
		if (SevenSigns.getInstance().isSealValidationPeriod())
		{
			if (compWinner == SevenSigns.CABAL_DAWN)
			{
				_state = 2;
			}
			else if (compWinner == SevenSigns.CABAL_DUSK)
			{
				_state = 1;
			}
		}
	}
	
	public SSQInfo(int state)
	{
		_state = state;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SSQ_INFO.writeId(this, buffer);
		buffer.writeShort(256 + _state);
	}
}
