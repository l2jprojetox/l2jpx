package com.l2jpx.loginserver.network.clientpackets;

import com.l2jpx.commons.logging.CLogger;
import com.l2jpx.commons.mmocore.ReceivablePacket;

import com.l2jpx.loginserver.network.LoginClient;

public abstract class L2LoginClientPacket extends ReceivablePacket<LoginClient>
{
	protected static final CLogger LOGGER = new CLogger(L2LoginClientPacket.class.getName());
	
	@Override
	protected final boolean read()
	{
		try
		{
			return readImpl();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed reading {}. ", e, getClass().getSimpleName());
			return false;
		}
	}
	
	protected abstract boolean readImpl();
}
