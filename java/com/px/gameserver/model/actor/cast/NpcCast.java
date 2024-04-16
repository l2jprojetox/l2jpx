package com.px.gameserver.model.actor.cast;

import com.px.commons.pool.ThreadPool;

import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.enums.EventHandler;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;

/**
 * This class groups all cast data related to a {@link Npc}.
 */
public class NpcCast extends CreatureCast<Npc>
{
	public NpcCast(Npc actor)
	{
		super(actor);
	}
	
	@Override
	protected final void onMagicHitTimer()
	{
		// Content was cleaned meantime, simply return doing nothing.
		if (!isCastingNow())
			return;
		
		final double mpConsume = _actor.getStatus().getMpConsume(_skill);
		if (mpConsume > 0)
		{
			if (mpConsume > _actor.getStatus().getMp())
			{
				stop();
				return;
			}
			
			_actor.getStatus().reduceMp(mpConsume);
		}
		
		final double hpConsume = _skill.getHpConsume();
		if (hpConsume > 0)
		{
			if (hpConsume > _actor.getStatus().getHp())
			{
				stop();
				return;
			}
			
			_actor.getStatus().reduceHp(hpConsume, _actor, true);
		}
		
		callSkill(_skill, _targets, _item);
		
		_castTask = ThreadPool.schedule(this::onMagicFinalizer, _coolTime + 250);
	}
	
	@Override
	protected void notifyCastFinishToAI(boolean isInterrupted)
	{
		// Stop the current Desire.
		_actor.getAI().clearCurrentDesire();
		
		// Notify the actor with USE_SKILL_FINISHED EventHandler.
		final Creature target = (_targets != null && _targets.length > 0) ? _targets[0] : _target;
		final Player player = (target == null) ? null : target.getActingPlayer();
		
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.USE_SKILL_FINISHED))
			quest.onUseSkillFinished(_actor, player, _skill, !isInterrupted);
		
		if (!isInterrupted)
			_actor.getAI().notifyEvent(AiEventType.FINISHED_CASTING, null, null);
	}
}