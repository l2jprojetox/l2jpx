package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.Config;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.BeginRotation;

public final class StartRotating extends L2GameClientPacket
{
	private int degree;
	private int side;
	
	@Override
	protected void readImpl()
	{
		degree = readD();
		side = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		if (!Config.ALLOW_USE_CURSOR_FOR_WALK)
		{
			getClient().getActiveChar().sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BeginRotation br = new BeginRotation(getClient().getActiveChar(), degree, side, 0);
		getClient().getActiveChar().broadcastPacket(br);
	}
	
	@Override
	public String getType()
	{
		return "[C] 4A StartRotating";
	}
}
