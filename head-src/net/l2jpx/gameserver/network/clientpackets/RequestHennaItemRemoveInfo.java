package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.datatables.xml.HennaTable;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.HennaItemRemoveInfo;
import net.l2jpx.gameserver.templates.L2Henna;

public final class RequestHennaItemRemoveInfo extends L2GameClientPacket
{
	private int symbolId;
	
	@Override
	protected void readImpl()
	{
		symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		L2Henna template = HennaTable.getInstance().getTemplate(symbolId);
		if (template == null)
			return;
		
		activeChar.sendPacket(new HennaItemRemoveInfo(template, activeChar));
	}

	@Override
	public String getType()
	{
		return null;
	}
}