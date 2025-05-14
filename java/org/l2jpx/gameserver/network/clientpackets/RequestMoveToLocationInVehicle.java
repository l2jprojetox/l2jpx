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

import org.l2jpx.Config;
import org.l2jpx.gameserver.instancemanager.BoatManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Boat;
import org.l2jpx.gameserver.model.item.type.WeaponType;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.MoveToLocationInVehicle;
import org.l2jpx.gameserver.network.serverpackets.StopMoveInVehicle;

public class RequestMoveToLocationInVehicle extends ClientPacket
{
	private int _boatId;
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	
	@Override
	protected void readImpl()
	{
		_boatId = readInt(); // objectId of boat
		_targetX = readInt();
		_targetY = readInt();
		_targetZ = readInt();
		_originX = readInt();
		_originY = readInt();
		_originZ = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !player.isGM() && (player.getNotMoveUntil() > System.currentTimeMillis()))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ))
		{
			player.sendPacket(new StopMoveInVehicle(player, _boatId));
			return;
		}
		
		if (player.isAttackingNow() && (player.getActiveWeaponItem() != null) && (player.getActiveWeaponItem().getItemType() == WeaponType.BOW))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isSitting() || player.isMovementDisabled())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.hasSummon())
		{
			player.sendPacket(SystemMessageId.YOU_SHOULD_RELEASE_YOUR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isTransformed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		Boat boat;
		if (player.isInBoat())
		{
			boat = player.getBoat();
			if (boat.getObjectId() != _boatId)
			{
				boat = BoatManager.getInstance().getBoat(_boatId);
				player.setVehicle(boat);
			}
		}
		else
		{
			boat = BoatManager.getInstance().getBoat(_boatId);
			player.setVehicle(boat);
		}
		
		final Location pos = new Location(_targetX, _targetY, _targetZ);
		final Location originPos = new Location(_originX, _originY, _originZ);
		player.setInVehiclePosition(pos);
		player.broadcastPacket(new MoveToLocationInVehicle(player, pos, originPos));
	}
}
