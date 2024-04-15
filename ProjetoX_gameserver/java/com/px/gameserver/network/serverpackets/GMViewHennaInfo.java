package com.px.gameserver.network.serverpackets;

import java.util.List;

import com.px.gameserver.enums.actors.HennaType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.player.HennaList;
import com.px.gameserver.model.item.Henna;

public class GMViewHennaInfo extends L2GameServerPacket
{
	private final HennaList _hennaList;
	
	public GMViewHennaInfo(Player activeChar)
	{
		_hennaList = activeChar.getHennaList();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xea);
		
		writeC(_hennaList.getStat(HennaType.INT));
		writeC(_hennaList.getStat(HennaType.STR));
		writeC(_hennaList.getStat(HennaType.CON));
		writeC(_hennaList.getStat(HennaType.MEN));
		writeC(_hennaList.getStat(HennaType.DEX));
		writeC(_hennaList.getStat(HennaType.WIT));
		
		writeD(_hennaList.getMaxSize());
		
		final List<Henna> hennas = _hennaList.getHennas();
		writeD(hennas.size());
		for (Henna h : hennas)
		{
			writeD(h.getSymbolId());
			writeD(_hennaList.canBeUsedBy(h) ? h.getSymbolId() : 0);
		}
	}
}