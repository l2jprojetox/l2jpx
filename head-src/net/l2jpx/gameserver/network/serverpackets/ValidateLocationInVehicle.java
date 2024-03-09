package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

public class ValidateLocationInVehicle extends L2GameServerPacket
{
	private int boat = 1343225858;
	private final int x;
	private final int y;
	private final int z;
	private final int heading;
	private final int playerObj;
	
	public ValidateLocationInVehicle(final L2Character player)
	{
		playerObj = player.getObjectId();
		x = player.getX();
		y = player.getY();
		z = player.getZ();
		heading = player.getHeading();
		boat = ((L2PcInstance) player).getBoat().getId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x73);
		writeD(playerObj);
		writeD(boat);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(heading);
	}
	
	@Override
	public String getType()
	{
		return "[S] 73 ValidateLocationInVehicle";
	}
}