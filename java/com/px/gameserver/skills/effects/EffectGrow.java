package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.AbnormalEffect;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectGrow extends AbstractEffect
{
	public EffectGrow(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Npc)
		{
			Npc npc = (Npc) getEffected();
			npc.setCollisionRadius(npc.getCollisionRadius() * 1.19);
			
			getEffected().startAbnormalEffect(AbnormalEffect.GROW);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected() instanceof Npc)
		{
			Npc npc = (Npc) getEffected();
			npc.setCollisionRadius(npc.getTemplate().getCollisionRadius());
			
			getEffected().stopAbnormalEffect(AbnormalEffect.GROW);
		}
	}
}