package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Player;

public class RecipeShopMsg extends L2GameServerPacket
{
	private final Player _activeChar;
	
	public RecipeShopMsg(Player player)
	{
		_activeChar = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xdb);
		writeD(_activeChar.getObjectId());
		writeS(_activeChar.getCreateList().getStoreName());
	}
}