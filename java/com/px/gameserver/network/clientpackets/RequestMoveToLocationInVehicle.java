package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.xml.BoatData;
import com.px.gameserver.model.actor.Boat;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.container.player.BoatInfo;
import com.px.gameserver.model.boat.BoatDock;
import com.px.gameserver.model.location.Point2D;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.MoveToLocationInVehicle;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private int _boatId;
	private int _tX;
	private int _tY;
	private int _tZ;
	private int _oX;
	private int _oY;
	private int _oZ;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_tX = readD();
		_tY = readD();
		_tZ = readD();
		_oX = readD();
		_oY = readD();
		_oZ = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isSittingNow() || player.isSitting() || player.isStandingNow())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BoatInfo info = player.getBoatInfo();
		
		info.setBoatMovement(false);
		
		if (_tX == _oX && _tY == _oY && _tZ == _oZ)
		{
			info.stopMoveInVehicle(_boatId);
			return;
		}
		
		final boolean isInBoat = info.isInBoat();
		
		Boat boat = info.getBoat();
		if (boat == null)
		{
			boat = BoatData.getInstance().getBoat(_boatId);
			if (boat == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (boat.isMoving())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (info.canBoard())
			{
				moveToLocationInVehicle(player, boat);
				return;
			}
			
			final BoatDock dock = boat.getDock();
			if (dock == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Point2D conv = dock.convertBoatToWorldCoordinates(_tX, _tY);
			Point2D point = dock.getBoardingPoint(player.getPosition(), conv, isInBoat);
			if (point == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			point = boat.getDock().getAdjustedBoardingPoint(player.getPosition(), conv, isInBoat);
			
			player.moveToBoatEntrance(point, boat);
		}
		else
		{
			if (boat.getObjectId() != _boatId)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			moveToLocationInVehicle(player, boat);
		}
	}
	
	private void moveToLocationInVehicle(final Player player, Boat boat)
	{
		player.getBoatInfo().getBoatPosition().set(_tX, _tY, _tZ);
		player.broadcastPacket(new MoveToLocationInVehicle(player, boat, _tX, _tY, _tZ, _oX, _oY, _oZ));
	}
}