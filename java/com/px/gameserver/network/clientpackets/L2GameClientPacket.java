package com.px.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;

import com.px.commons.logging.CLogger;
import com.px.commons.mmocore.ReceivablePacket;

import com.px.Config;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.GameClient;
import com.px.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * Packets received by the gameserver from clients.
 */
public abstract class L2GameClientPacket extends ReceivablePacket<GameClient>
{
	protected static final CLogger LOGGER = new CLogger(L2GameClientPacket.class.getName());
	
	protected abstract void readImpl();
	
	protected abstract void runImpl();
	
	@Override
	protected boolean read()
	{
		if (Config.PACKET_HANDLER_DEBUG)
			LOGGER.info(getType());
		
		try
		{
			readImpl();
			return true;
		}
		catch (Exception e)
		{
			if (e instanceof BufferUnderflowException)
			{
				getClient().onBufferUnderflow();
				return false;
			}
			LOGGER.error("Failed reading {} for {}. ", e, getType(), getClient().toString());
		}
		return false;
	}
	
	@Override
	public void run()
	{
		try
		{
			runImpl();
			
			// Depending of the packet send, removes spawn protection
			if (triggersOnActionRequest())
			{
				final Player player = getClient().getPlayer();
				if (player != null && player.isSpawnProtected())
					player.onActionRequest();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed reading {} for {}. ", e, getType(), getClient().toString());
			
			if (this instanceof EnterWorld)
				getClient().closeNow();
		}
	}
	
	protected final void sendPacket(L2GameServerPacket gsp)
	{
		getClient().sendPacket(gsp);
	}
	
	/**
	 * @return A String with this packet name for debuging purposes
	 */
	public String getType()
	{
		return "[C] " + getClass().getSimpleName();
	}
	
	/**
	 * Overriden with true value on some packets that should disable spawn protection
	 * @return
	 */
	protected boolean triggersOnActionRequest()
	{
		return true;
	}
}