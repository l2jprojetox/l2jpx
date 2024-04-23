package com.l2jpx.gameserver.model.actor.attack;

import com.l2jpx.gameserver.enums.ScriptEventType;
import com.l2jpx.gameserver.model.actor.Attackable;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.scripting.Quest;

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
	public boolean canDoAttack(Creature target)
	{
		if (!super.canDoAttack(target))
			return false;
		
		if (target.isFakeDeath())
			return false;
		
		return true;
	}
	
	@Override
	public boolean doAttack(Creature target)
	{
		final boolean isHit = super.doAttack(target);
		if (isHit)
		{
			// Bypass behavior if the victim isn't a player
			final Player victim = target.getActingPlayer();
			if (victim != null)
			{
				for (Quest quest : _actor.getTemplate().getEventQuests(ScriptEventType.ON_ATTACK_ACT))
					quest.notifyAttackAct(_actor, victim);
			}
		}
		return isHit;
	}
}