package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Player;

public class PartySmallWindowUpdate extends L2GameServerPacket
{
	private final Player _player;
	
	public PartySmallWindowUpdate(Player player)
	{
		_player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x52);
		writeD(_player.getObjectId());
		writeS(_player.getName());
		writeD((int) _player.getStatus().getCp());
		writeD(_player.getStatus().getMaxCp());
		writeD((int) _player.getStatus().getHp());
		writeD(_player.getStatus().getMaxHp());
		writeD((int) _player.getStatus().getMp());
		writeD(_player.getStatus().getMaxMp());
		writeD(_player.getStatus().getLevel());
		writeD(_player.getClassId().getId());
	}
}