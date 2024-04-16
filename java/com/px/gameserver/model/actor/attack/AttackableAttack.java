package com.px.gameserver.model.actor.attack;

import com.px.gameserver.enums.EventHandler;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.scripting.Quest;

/**
 * This class groups all attack data related to a {@link Creature}.
 */
public class AttackableAttack extends CreatureAttack<Attackable>
{
	public AttackableAttack(Attackable actor)
	{
		super(actor);
	}
	
	@Override
	public boolean canAttack(Creature target)
	{
		if (!super.canAttack(target))
			return false;
		
		if (target.isFakeDeath())
			return false;
		
		return true;
	}
	
	@Override
	protected void onFinishedAttackBow(Creature mainTarget)
	{
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.ATTACK_FINISHED))
			quest.onAttackFinished(_actor, mainTarget);
		
		super.onFinishedAttackBow(mainTarget);
	}
	
	@Override
	protected void onFinishedAttack(Creature mainTarget)
	{
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.ATTACK_FINISHED))
			quest.onAttackFinished(_actor, mainTarget);
		
		super.onFinishedAttack(mainTarget);
	}
}