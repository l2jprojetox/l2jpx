package net.l2jpx.loginserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.loginserver.L2LoginClient;
import net.l2jpx.netcore.ReceivablePacket;

/**
 * @author ProGramMoS
 */

public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient>
{
	private static Logger LOGGER = Logger.getLogger(L2LoginClientPacket.class);
	
	@Override
	protected final boolean read()
	{
		try
		{
			return readImpl();
		}
		catch (final Exception e)
		{
			LOGGER.error("ERROR READING: " + this.getClass().getSimpleName(), e);
			return false;
		}
	}
	
	protected abstract boolean readImpl();
}
