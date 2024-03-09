package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2HennaInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;

/**
 * format cd
 */
public final class RequestHennaRemove extends L2GameClientPacket
{
	private int _symbolId;
	
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		for (int i = 1; i <= 3; i++)
		{
			L2HennaInstance henna = activeChar.getHennas(i);
			
			if (henna != null && henna.getSymbolId() == _symbolId)
			{
				if (activeChar.getAdena() >= (henna.getCancelFee()))
				{
					activeChar.getInventory().reduceAdena("Henna", henna.getCancelFee(), activeChar, activeChar.getLastFolkNPC());
					activeChar.removeHenna(i);
					break;
				}
				activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
		}
	}

	@Override
	public String getType()
	{
		return null;
	}

}