package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Player;

public class RecipeShopMsg extends L2GameServerPacket
{
	private final Player _player;
	
	public RecipeShopMsg(Player player)
	{
		_player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xdb);
		
		writeD(_player.getObjectId());
		writeS(_player.getManufactureList().getStoreName());
	}
}