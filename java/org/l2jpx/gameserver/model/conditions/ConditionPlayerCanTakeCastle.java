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
package org.l2jpx.gameserver.model.conditions;

import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * Player Can Take Castle condition implementation.
 * @author Adry_85
 */
public class ConditionPlayerCanTakeCastle extends Condition
{
	private final boolean _value;
	
	public ConditionPlayerCanTakeCastle(boolean value)
	{
		_value = value;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item)
	{
		if ((effector == null) || !effector.isPlayer())
		{
			return !_value;
		}
		
		final Player player = effector.getActingPlayer();
		boolean canTakeCastle = true;
		if (player.isAlikeDead() || player.isCursedWeaponEquipped() || !player.isClanLeader())
		{
			canTakeCastle = false;
		}
		
		final Castle castle = CastleManager.getInstance().getCastle(player);
		SystemMessage sm;
		if ((castle == null) || (castle.getResidenceId() <= 0) || !castle.getSiege().isInProgress() || (castle.getSiege().getAttackerClan(player.getClan()) == null))
		{
			sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addSkillName(skill);
			player.sendPacket(sm);
			canTakeCastle = false;
		}
		else if (!castle.getArtefacts().contains(effected))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canTakeCastle = false;
		}
		else if (!Util.checkIfInRange(200, player, effected, true) || (player.getZ() < effected.getZ()) || (Math.abs(player.getZ() - effected.getZ()) > 40))
		{
			player.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
			canTakeCastle = false;
		}
		return (_value == canTakeCastle);
	}
}
