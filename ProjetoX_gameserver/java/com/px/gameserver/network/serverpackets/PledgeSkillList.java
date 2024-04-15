package com.px.gameserver.network.serverpackets;

import java.util.Collection;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.pledge.Clan;

public class PledgeSkillList extends L2GameServerPacket
{
	private final Clan _clan;
	
	public PledgeSkillList(Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	protected void writeImpl()
	{
		Collection<L2Skill> skills = _clan.getClanSkills().values();
		
		writeC(0xfe);
		writeH(0x39);
		
		writeD(skills.size());
		
		for (L2Skill sk : skills)
		{
			writeD(sk.getId());
			writeD(sk.getLevel());
		}
	}
}