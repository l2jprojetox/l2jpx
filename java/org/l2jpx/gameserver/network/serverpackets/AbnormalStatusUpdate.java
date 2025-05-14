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

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class AbnormalStatusUpdate extends ServerPacket
{
	private final List<BuffInfo> _effects = new ArrayList<>();
	
	public void addSkill(BuffInfo info)
	{
		if (!info.getSkill().isHealingPotionSkill())
		{
			_effects.add(info);
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.ABNORMAL_STATUS_UPDATE.writeId(this, buffer);
		buffer.writeShort(_effects.size());
		for (BuffInfo info : _effects)
		{
			if ((info != null) && info.isInUse())
			{
				buffer.writeInt(info.getSkill().getDisplayId());
				buffer.writeShort(info.getSkill().getDisplayLevel());
				buffer.writeShort(info.getSkill().getSubLevel());
				buffer.writeInt(info.getSkill().getAbnormalType().getClientId());
				writeOptionalInt(info.getSkill().isAura() || info.getSkill().isToggle() ? -1 : info.getTime(), buffer);
			}
		}
	}
}
