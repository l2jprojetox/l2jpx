package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.sevensigns.SevenSigns;
import net.l2jpx.gameserver.network.serverpackets.SSQStatus;

/**
 * Seven Signs Record Update Request packet type id 0xc7 format: cc
 * @author Tempy
 */
public final class RequestSSQStatus extends L2GameClientPacket
{
	private int page;
	
	@Override
	protected void readImpl()
	{
		page = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) && page == 4)
		{
			return;
		}
		
		final SSQStatus ssqs = new SSQStatus(activeChar, page);
		activeChar.sendPacket(ssqs);
	}
	
	@Override
	public String getType()
	{
		return "[C] C7 RequestSSQStatus";
	}
}
