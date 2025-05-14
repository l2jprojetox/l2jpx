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

import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.FortSiegeManager;
import org.l2jpx.gameserver.instancemanager.SiegeGuardManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;

public class RequestPetGetItem extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.hasPet())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Item item = (Item) World.getInstance().findObject(_objectId);
		if (item == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Castle castle = CastleManager.getInstance().getCastle(item);
		if ((castle != null) && (SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getResidenceId(), item.getId()) != null))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (FortSiegeManager.getInstance().isCombat(item.getId()))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Pet pet = player.getPet();
		if (pet.isDead() || pet.isControlBlocked())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (pet.isUncontrollable())
		{
			player.sendPacket(SystemMessageId.WHEN_YOUR_PET_S_HUNGER_GAUGE_IS_AT_0_YOU_CANNOT_USE_YOUR_PET);
			return;
		}
		
		pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
	}
}
