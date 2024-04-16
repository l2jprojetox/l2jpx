package com.px.gameserver.skills.effects;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.skills.EffectFlag;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.ai.type.NpcAI;
import com.px.gameserver.model.actor.ai.type.PlayableAI;
import com.px.gameserver.model.actor.instance.Chest;
import com.px.gameserver.model.actor.instance.Door;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectConfusion extends AbstractEffect
{
	public EffectConfusion(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSION;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player)
			return true;
		
		// Abort move.
		getEffected().getMove().stop();
		
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
		
		// Find a random target from known Attackables (without doors nor chests) and Playables.
		final Creature target = Rnd.get(getEffected().getKnownType(Creature.class, wo -> (wo instanceof Attackable || wo instanceof Playable) && wo != getEffected() && !(wo instanceof Door || wo instanceof Chest) && wo.distance2D(getEffected()) <= 1000));
		if (target == null)
			return true;
		
		if (getEffected() instanceof Playable)
			((PlayableAI<?>) getEffected().getAI()).tryToAttack(target, false, false);
		else
			((NpcAI<?>) getEffected().getAI()).addAttackDesire(target, Integer.MAX_VALUE);
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
		
		if (getEffected() instanceof Playable)
			((PlayableAI<?>) getEffected().getAI()).tryToFollow(getEffected().getActingPlayer(), false);
		else
			((NpcAI<?>) getEffected().getAI()).getAggroList().stopHate(((NpcAI<?>) getEffected().getAI()).getAggroList().getMostHatedCreature());
		
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.CONFUSED.getMask();
	}
}