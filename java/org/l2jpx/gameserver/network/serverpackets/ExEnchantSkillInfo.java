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
import org.l2jpx.gameserver.data.xml.EnchantSkillGroupsData;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExEnchantSkillInfo extends ServerPacket
{
	private final Set<Integer> _routes;
	private final int _skillId;
	private final int _skillLevel;
	private final int _skillSubLevel;
	private final int _currentSubLevel;
	
	public ExEnchantSkillInfo(int skillId, int skillLevel, int skillSubLevel, int currentSubLevel)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
		_skillSubLevel = skillSubLevel;
		_currentSubLevel = currentSubLevel;
		_routes = EnchantSkillGroupsData.getInstance().getRouteForSkill(_skillId, _skillLevel);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ENCHANT_SKILL_INFO.writeId(this, buffer);
		buffer.writeInt(_skillId);
		buffer.writeShort(_skillLevel);
		buffer.writeShort(_skillSubLevel);
		buffer.writeInt((_skillSubLevel % 1000) != EnchantSkillGroupsData.MAX_ENCHANT_LEVEL);
		buffer.writeInt(_skillSubLevel > 1000);
		buffer.writeInt(_routes.size());
		_routes.forEach(route ->
		{
			final int routeId = route / 1000;
			final int currentRouteId = _skillSubLevel / 1000;
			final int subLevel = _currentSubLevel > 0 ? (route + (_currentSubLevel % 1000)) - 1 : route;
			buffer.writeShort(_skillLevel);
			buffer.writeShort(currentRouteId != routeId ? subLevel : Math.min(subLevel + 1, route + (EnchantSkillGroupsData.MAX_ENCHANT_LEVEL - 1)));
		});
	}
}
