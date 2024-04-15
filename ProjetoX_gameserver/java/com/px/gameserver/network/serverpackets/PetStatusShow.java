package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Summon;

/**
 * @author Yme
 */
public class PetStatusShow extends L2GameServerPacket
{
	private final int _summonType;
	
	public PetStatusShow(Summon summon)
	{
		_summonType = summon.getSummonType();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xB0);
		writeD(_summonType);
	}
}