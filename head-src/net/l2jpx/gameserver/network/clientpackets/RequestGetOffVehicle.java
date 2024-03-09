package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.managers.BoatManager;
import net.l2jpx.gameserver.model.actor.instance.L2BoatInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.GetOffVehicle;

/**
 * @author Maktakien
 */
public final class RequestGetOffVehicle extends L2GameClientPacket
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
		final GetOffVehicle Gon = new GetOffVehicle(activeChar, boat, x, y, z);
		activeChar.broadcastPacket(Gon);
	}
	
	@Override
	public String getType()
	{
		return "[S] 5d GetOffVehicle";
	}
	
}
