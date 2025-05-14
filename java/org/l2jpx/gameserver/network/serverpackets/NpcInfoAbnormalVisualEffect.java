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

import java.util.Set;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect extends ServerPacket
{
	private final Npc _npc;
	
	public NpcInfoAbnormalVisualEffect(Npc npc)
	{
		_npc = npc;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.NPC_INFO_ABNORMAL_VISUAL_EFFECT.writeId(this, buffer);
		buffer.writeInt(_npc.getObjectId());
		buffer.writeInt(_npc.getTransformationDisplayId());
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
		buffer.writeShort(abnormalVisualEffects.size());
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			buffer.writeShort(abnormalVisualEffect.getClientId());
		}
	}
}
