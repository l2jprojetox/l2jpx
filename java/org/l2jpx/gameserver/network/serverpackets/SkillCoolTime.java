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

import java.util.LinkedList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.model.TimeStamp;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * Skill Cool Time server packet implementation.
 * @author KenM, Zoey76, Mobius
 */
public class SkillCoolTime extends ServerPacket
{
	private final List<TimeStamp> _reuseTimestamps = new LinkedList<>();
	
	public SkillCoolTime(Player player)
	{
		for (TimeStamp ts : player.getSkillReuseTimeStamps().values())
		{
			if (ts.hasNotPassed() && !SkillData.getInstance().getSkill(ts.getSkillId(), ts.getSkillLevel(), ts.getSkillSubLevel()).isNotBroadcastable())
			{
				_reuseTimestamps.add(ts);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SKILL_COOL_TIME.writeId(this, buffer);
		buffer.writeInt(_reuseTimestamps.size());
		for (TimeStamp ts : _reuseTimestamps)
		{
			final long reuse = ts.getReuse();
			final long remaining = ts.getRemaining();
			final int sharedReuseGroup = ts.getSharedReuseGroup();
			buffer.writeInt(sharedReuseGroup > 0 ? sharedReuseGroup : ts.getSkillId());
			buffer.writeInt(ts.getSkillLevel());
			buffer.writeInt((int) (reuse > 0 ? reuse : remaining) / 1000);
			buffer.writeInt((int) remaining / 1000);
		}
	}
}
