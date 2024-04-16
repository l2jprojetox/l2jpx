package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectFlag;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.EtcStatusUpdate;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectCharmOfCourage extends AbstractEffect
{
	public EffectCharmOfCourage(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CHARM_OF_COURAGE;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player)
		{
			getEffected().broadcastPacket(new EtcStatusUpdate((Player) getEffected()));
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected() instanceof Player)
			getEffected().broadcastPacket(new EtcStatusUpdate((Player) getEffected()));
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.CHARM_OF_COURAGE.getMask();
	}
}