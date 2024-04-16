package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.enums.skills.AbnormalEffect;
import com.px.gameserver.enums.skills.EffectFlag;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectPetrification extends AbstractEffect
{
	public EffectPetrification(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PETRIFICATION;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startAbnormalEffect(AbnormalEffect.HOLD_2);
		
		// Abort attack, cast and move.
		getEffected().abortAll(false);
		
		getEffected().setInvul(true);
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopAbnormalEffect(AbnormalEffect.HOLD_2);
		
		if (!(getEffected() instanceof Player))
			getEffected().getAI().notifyEvent(AiEventType.THINK, null, null);
		
		getEffected().setInvul(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.PARALYZED.getMask();
	}
}