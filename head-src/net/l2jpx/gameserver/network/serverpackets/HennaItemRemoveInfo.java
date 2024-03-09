package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.templates.L2Henna;

public class HennaItemRemoveInfo extends L2GameServerPacket
{
	private final L2PcInstance activeChar;
	private final L2Henna henna;
	
	public HennaItemRemoveInfo(L2Henna henna, L2PcInstance player)
	{
		this.henna = henna;
		activeChar = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xe6);
		writeD(henna.getSymbolId()); // symbol Id
		writeD(henna.getDyeId()); // item id of dye
		writeD(henna.getAmountDyeRequire() / 2); // amount of given dyes
		writeD(henna.getCancelFee()); // amount of required adenas
		writeD(1); // able to remove or not 0 is false and 1 is true
		writeD(activeChar.getAdena());
		
		writeD(activeChar.getINT()); // current INT
		writeC(activeChar.getINT() - henna.getStatINT()); // equip INT
		writeD(activeChar.getSTR()); // current STR
		writeC(activeChar.getSTR() - henna.getStatSTR()); // equip STR
		writeD(activeChar.getCON()); // current CON
		writeC(activeChar.getCON() - henna.getStatCON()); // equip CON
		writeD(activeChar.getMEN()); // current MEN
		writeC(activeChar.getMEN() - henna.getStatMEN()); // equip MEN
		writeD(activeChar.getDEX()); // current DEX
		writeC(activeChar.getDEX() - henna.getStatDEX()); // equip DEX
		writeD(activeChar.getWIT()); // current WIT
		writeC(activeChar.getWIT() - henna.getStatWIT()); // equip WIT
	}

	@Override
	public String getType()
	{
		return null;
	}
}