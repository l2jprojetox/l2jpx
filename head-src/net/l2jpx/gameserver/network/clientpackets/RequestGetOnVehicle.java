package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.managers.BoatManager;
import net.l2jpx.gameserver.model.actor.instance.L2BoatInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.GetOnVehicle;
import net.l2jpx.util.Point3D;

public final class RequestGetOnVehicle extends L2GameClientPacket
{
	private int id, x, y, z;
	
	@Override
	protected void readImpl()
	{
		id = readD();
		x = readD();
		y = readD();
		z = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		final L2BoatInstance boat = BoatManager.getInstance().GetBoat(id);
		if (boat == null)
		{
			return;
		}
		
		final GetOnVehicle Gon = new GetOnVehicle(activeChar, boat, x, y, z);
		activeChar.setInBoatPosition(new Point3D(x, y, z));
		activeChar.getPosition().setXYZ(boat.getPosition().getX(), boat.getPosition().getY(), boat.getPosition().getZ());
		activeChar.broadcastPacket(Gon);
		activeChar.revalidateZone(true);
		
	}
	
	@Override
	public String getType()
	{
		return "[C] 5C GetOnVehicle";
	}
}
