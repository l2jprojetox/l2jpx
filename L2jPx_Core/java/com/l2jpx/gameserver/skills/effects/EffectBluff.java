package com.l2jpx.gameserver.skills.effects;

import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.instance.Folk;
import com.l2jpx.gameserver.model.actor.instance.SiegeSummon;
import com.l2jpx.gameserver.network.serverpackets.StartRotation;
import com.l2jpx.gameserver.network.serverpackets.StopRotation;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectBluff extends AbstractEffect
{
	public EffectBluff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLUFF;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof SiegeSummon || getEffected() instanceof Folk || getEffected().isRaidRelated() || (getEffected() instanceof Npc && ((Npc) getEffected()).getNpcId() == 35062))
			return false;
		
		getEffected().broadcastPacket(new StartRotation(getEffected().getObjectId(), getEffected().getHeading(), 1, 65535));
		getEffected().broadcastPacket(new StopRotation(getEffected().getObjectId(), getEffector().getHeading(), 65535));
		getEffected().getPosition().setHeading(getEffector().getHeading());
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}