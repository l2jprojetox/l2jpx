package com.px.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;

import com.px.commons.math.MathUtil;

import com.px.Config;
import com.px.gameserver.enums.TeleportMode;
import com.px.gameserver.model.actor.Boat;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.container.player.BoatInfo;
import com.px.gameserver.model.boat.BoatDock;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.location.Point2D;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.MoveToLocation;
import com.px.gameserver.network.serverpackets.MoveToLocationInVehicle;

public class MoveBackwardToLocation extends L2GameClientPacket
{
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	private int _moveMovement;
	
	@Override
	protected void readImpl()
	{
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		
		try
		{
			_moveMovement = readD(); // is 0 if cursor keys are used 1 if mouse is used
		}
		catch (BufferUnderflowException e)
		{
			if (Config.L2WALKER_PROTECTION)
			{
				final Player player = getClient().getPlayer();
				if (player != null)
					player.logout(false);
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final BoatInfo info = player.getBoatInfo();
		
		// Deny movement from arrow keys.
		if (_moveMovement == 0)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// If Player can't be controlled, forget it.
		if (player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// If Player can't move, forget it.
		if (player.getStatus().getMoveSpeed() == 0)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.CANT_MOVE_TOO_ENCUMBERED);
			return;
		}
		
		// Cancel enchant over movement.
		player.cancelActiveEnchant();
		
		// Correct targetZ from floor level to head level.
		_targetZ += player.getCollisionHeight();
		
		// If under teleport mode, teleport instead of tryToMove.
		switch (player.getTeleportMode())
		{
			case ONE_TIME:
				player.setTeleportMode(TeleportMode.NONE);
			case FULL_TIME:
				player.sendPacket(ActionFailed.STATIC_PACKET);
				player.teleportTo(_targetX, _targetY, _targetZ, 0);
				return;
		}
		
		// Generate a Location based on target coords.
		final Location targetLoc = new Location(_targetX, _targetY, _targetZ);
		
		// If we target past 9900 distance, forget it.
		if (!targetLoc.isIn3DRadius(_originX, _originY, _originZ, 9900))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final boolean isInBoat = info.isInBoat();
		
		info.setCanBoard(false);
		
		// If out of Boat, register a move Intention.
		if (!isInBoat)
		{
			if (player.tryToPassBoatEntrance(targetLoc))
				return;
			
			player.getAI().tryToMoveTo(targetLoc, null);
		}
		// Player is on the boat, we don't want to schedule a real movement until he gets out of it otherwise GeoEngine will be confused.
		else
		{
			final Boat boat = info.getBoat();
			// We want to set the real player heading though so it can be used during actual departure.
			player.getPosition().setHeading(MathUtil.calculateHeadingFrom(_originX, _originY, _targetX, _targetY));
			
			if (boat == null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final BoatDock dock = boat.getDock();
			if (dock == null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			Point2D boardingPoint = dock.getBoardingPoint(new Point2D(_originX, _originY), new Point2D(_targetX, _targetY), isInBoat);
			if (boardingPoint == null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Location pos = info.getBoatPosition();
			final int oX = pos.getX();
			final int oY = pos.getY();
			final int z = pos.getZ();
			
			final Point2D currPoint = dock.convertBoatToWorldCoordinates(oX, oY);
			
			if (currPoint.distance2D(boardingPoint) < 60)
			{
				// Just sending a client move packet so player will try to move towards exit.
				player.broadcastPacket(new MoveToLocation(player, new Location(boardingPoint.getX(), boardingPoint.getY(), -3624)));
				info.setBoatMovement(true);
				return;
			}
			
			boardingPoint = boat.getDock().convertWorldToBoatCoordinates(boardingPoint.getX(), boardingPoint.getY());
			
			final int tX = boardingPoint.getX();
			final int tY = boardingPoint.getY();
			
			player.getBoatInfo().getBoatPosition().set(tX, tY);
			player.broadcastPacket(new MoveToLocationInVehicle(player, boat, tX, tY, z, oX, oY, z));
		}
	}
}