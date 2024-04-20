package com.l2jpx.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jpx.gameserver.model.holder.EffectHolder;
import com.l2jpx.gameserver.skills.L2Skill;

public class AbnormalStatusUpdate extends L2GameServerPacket
{
	private final List<EffectHolder> _effects = new ArrayList<>();
	
	public AbnormalStatusUpdate()
	{
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x7f);
		
		writeH(_effects.size());
		for (EffectHolder holder : _effects)
		{
			writeD(holder.getId());
			writeH(holder.getValue());
			writeD((holder.getDuration() == -1) ? -1 : holder.getDuration() / 1000);
		}
	}
	
	public void addEffect(L2Skill skill, int duration)
	{
		_effects.add(new EffectHolder(skill, duration));
	}
}