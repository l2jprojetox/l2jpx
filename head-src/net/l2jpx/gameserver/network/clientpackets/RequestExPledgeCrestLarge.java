package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.cache.CrestCache;
import net.l2jpx.gameserver.network.serverpackets.ExPledgeCrestLarge;

/**
 * Fomat : chd c: (id) 0xD0 h: (subid) 0x10 d: the crest id This is a trigger
 * @author -Wooden-
 */
public final class RequestExPledgeCrestLarge extends L2GameClientPacket
{
	private int crestId;
	
	@Override
	protected void readImpl()
	{
		crestId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final byte[] data = CrestCache.getInstance().getPledgeCrestLarge(crestId);
		
		if (data != null)
		{
			final ExPledgeCrestLarge pcl = new ExPledgeCrestLarge(crestId, data);
			sendPacket(pcl);
		}
		
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:10 RequestExPledgeCrestLarge";
	}
	
}
