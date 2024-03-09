package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.datatables.xml.HennaTable;
import net.l2jpx.gameserver.model.actor.instance.L2HennaInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.HennaEquipList;

/**
 * RequestHennaList - 0xba
 * @author Tempy
 */
public final class RequestHennaList extends L2GameClientPacket
{
	// This is just a trigger packet...
	@SuppressWarnings("unused")
	private int unknown;
	
	@Override
	protected void readImpl()
	{
		unknown = readD(); // ??
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		final L2HennaInstance[] henna = HennaTable.getInstance().getAvailableHenna(activeChar.getClassId());
		final HennaEquipList he = new HennaEquipList(activeChar, henna);
		activeChar.sendPacket(he);
	}
	
	@Override
	public String getType()
	{
		return "[C] ba RequestHennaList";
	}
}
