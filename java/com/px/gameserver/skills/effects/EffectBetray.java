package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectFlag;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.model.actor.ai.type.PlayableAI;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectBetray extends AbstractEffect
{
	public EffectBetray(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BETRAY;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(getEffected() instanceof Summon))
			return false;
		
		final Player player = getEffected().getActingPlayer();
		if (player == null)
			return false;
		
		((PlayableAI<?>) getEffected().getAI()).tryToAttack(player, false, false);
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (!(getEffected() instanceof Summon))
			return;
		
		final Player player = getEffected().getActingPlayer();
		if (player == null)
			return;
		
		((PlayableAI<?>) getEffected().getAI()).tryToFollow(player, false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.BETRAYED.getMask();
	}
}