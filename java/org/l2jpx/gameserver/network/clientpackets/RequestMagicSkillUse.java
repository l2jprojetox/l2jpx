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

import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.skill.CommonSkill;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;

public class RequestMagicSkillUse extends ClientPacket
{
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_magicId = readInt(); // Identifier of the used skill
		_ctrlPressed = readInt() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = readByte() != 0; // True if Shift pressed
	}
	
	@Override
	protected void runImpl()
	{
		// Get the current Player of the player
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Get the level of the used skill
		Skill skill = player.getKnownSkill(_magicId);
		if (skill == null)
		{
			if ((_magicId == CommonSkill.HAIR_ACCESSORY_SET.getId()) //
				|| ((_magicId > 1565) && (_magicId < 1570))) // subClass change SkillTree
			{
				skill = SkillData.getInstance().getSkill(_magicId, 1);
			}
			else
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				// if (_magicId > 0)
				// {
				// PacketLogger.warning("Skill Id " + _magicId + " not found in player: " + player);
				// }
				return;
			}
		}
		
		// Skill is blocked from player use.
		if (skill.isBlockActionUseSkill())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Avoid Use of Skills in AirShip.
		if (player.isInAirShip())
		{
			player.sendPacket(SystemMessageId.THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.onActionRequest();
		
		player.useMagic(skill, null, _ctrlPressed, _shiftPressed);
	}
}
