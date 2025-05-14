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

import org.l2jpx.gameserver.enums.SkillEnchantType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;

/**
 * @author -Wooden-
 */
public class RequestExEnchantSkillInfoDetail extends ClientPacket
{
	private SkillEnchantType _type;
	private int _skillId;
	private int _skillLevel;
	private int _skillSubLevel;
	
	@Override
	protected void readImpl()
	{
		_type = SkillEnchantType.values()[readInt()];
		_skillId = readInt();
		_skillLevel = readShort();
		_skillSubLevel = readShort();
	}
	
	@Override
	protected void runImpl()
	{
		if ((_skillId <= 0) || (_skillLevel <= 0) || (_skillSubLevel < 0))
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		player.sendPacket(new ExEnchantSkillInfoDetail(_type, _skillId, _skillLevel, _skillSubLevel, player));
	}
}
