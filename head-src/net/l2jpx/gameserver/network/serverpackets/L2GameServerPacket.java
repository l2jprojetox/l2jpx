package net.l2jpx.gameserver.network.serverpackets;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.network.L2GameClient;
import net.l2jpx.netcore.SendablePacket;

/**
 * @author ProGramMoS
 * @author ReynalDev
 */
public abstract class L2GameServerPacket extends SendablePacket<L2GameClient>
{
	private static final Logger LOGGER = Logger.getLogger(L2GameServerPacket.class);
	
	@Override
	protected void write()
	{
		try
		{
			writeImpl();
		}
		catch (Exception e)
		{
			LOGGER.error("Client: " + getClient().toString() + " - Failed writing: " + getType(), e);
		}
	}
	
	public void runImpl()
	{
		
	}
	
	protected abstract void writeImpl();
	
	/**
	 * @return A String with this packet name for debuging purposes
	 */
	public abstract String getType();
}
