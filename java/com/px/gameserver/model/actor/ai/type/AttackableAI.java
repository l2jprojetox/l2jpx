package com.px.gameserver.model.actor.ai.type;

import java.util.concurrent.ScheduledFuture;

import com.px.commons.pool.ThreadPool;
import com.px.commons.random.Rnd;
import com.px.commons.util.ArraysUtil;

import com.px.Config;
import com.px.gameserver.data.manager.CursedWeaponManager;
import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.enums.EventHandler;
import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.items.ItemLocation;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.World;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.entity.CursedWeapon;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.scripting.Quest;

public class AttackableAI<T extends Attackable> extends NpcAI<T>
{
	private ScheduledFuture<?> _wanderTask;
	
	public AttackableAI(T attackable)
	{
		super(attackable);
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		if (_actor.denyAiAction())
			return null;
		
		final WorldObject target = World.getInstance().getObject(_currentIntention.getItemObjectId());
		if (!(target instanceof ItemInstance) || isTargetLost(target))
			return null;
		
		final ItemInstance item = (ItemInstance) target;
		if (item.getLocation() != ItemLocation.VOID)
			return null;
		
		if (_actor.getMove().maybeMoveToLocation(target.getPosition(), 36, false, false))
			return null;
		
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.PICKED_ITEM))
			quest.onPickedItem(_actor, item);
		
		final CursedWeapon cw = CursedWeaponManager.getInstance().getCursedWeapon(item.getItemId());
		if (cw != null)
			cw.endOfLife();
		else
			item.decayMe();
		
		clearCurrentDesire();
		
		return item;
	}
	
	@Override
	protected void thinkWander()
	{
		// If the current intention isn't to wander, do nothing.
		if (_currentIntention.getType() != IntentionType.WANDER)
			return;
		
		_actor.setWalkOrRun(false);
		
		// If already moving, do nothing.
		if (_actor.isMoving())
			return;
		
		if (_lastDesire == null || _lastDesire.getType() != IntentionType.WANDER)
		{
			if (_wanderTask != null)
				_wanderTask.cancel(false);
			
			// Return to home if too far.
			if (_actor.returnHome())
				return;
			
			// Random walk otherwise.
			_actor.moveFromSpawnPointUsingRandomOffset((int) _actor.getStatus().getRealMoveSpeed(true) * 3);
		}
		else
		{
			_wanderTask = ThreadPool.schedule(() ->
			{
				if (_wanderTask != null)
				{
					_wanderTask.cancel(false);
					_wanderTask = null;
				}
				
				if (Config.RANDOM_WALK_RATE > 0 && Rnd.get(100) < Config.RANDOM_WALK_RATE)
				{
					if (!_actor.isMoving() && _currentIntention.getType() == IntentionType.WANDER)
					{
						// Return to home if too far.
						if (_actor.returnHome())
							return;
						
						// Random walk otherwise.
						_actor.moveFromSpawnPointUsingRandomOffset((int) _actor.getStatus().getRealMoveSpeed(true) * 3);
					}
				}
				else
					thinkWander();
			}, _currentIntention.getTimer() * 1000);
		}
	}
	
	@Override
	protected void onEvtFinishedAttackBow()
	{
		// Attackables that use a bow do not do anything until the attack is fully reused (equivalent of the Player red gauge bar).
	}
	
	@Override
	protected void onEvtBowAttackReuse()
	{
		if (_nextIntention.isBlank())
			notifyEvent(AiEventType.THINK, null, null);
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtArrived()
	{
		if (_currentIntention.getType() == IntentionType.MOVE_TO)
		{
			if (_actor.isReturningToSpawnPoint())
				_actor.setIsReturningToSpawnPoint(false);
		}
		
		super.onEvtArrived();
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		_actor.addAttacker(attacker);
		
		super.onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		// Retrieve ATTACKED scripts.
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.ATTACKED))
			quest.onAttacked(_actor, target, aggro, null);
		
		// Party aggro (minion/master).
		if (_actor.isMaster() || _actor.hasMaster())
		{
			// Retrieve PARTY_ATTACKED scripts associated to the master.
			for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
				quest.onPartyAttacked(_actor, _actor, target, aggro);
			
			// Call the event for the master.
			final Npc master = _actor.getMaster();
			if (master != null && !master.isDead())
			{
				// Retrieve PARTY_ATTACKED scripts associated to the master.
				for (Quest quest : master.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
					quest.onPartyAttacked(_actor, master, target, aggro);
			}
			
			// Call the event for all minions.
			for (Npc minion : _actor.getMinions())
			{
				if (minion.isDead())
					continue;
				
				// Retrieve PARTY_ATTACKED scripts associated to the minions.
				for (Quest quest : minion.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
					quest.onPartyAttacked(_actor, minion, target, aggro);
			}
		}
		
		// Social aggro.
		final String[] actorClans = _actor.getTemplate().getClans();
		if (actorClans != null && _actor.getTemplate().getClanRange() > 0)
		{
			// Retrieve scripts associated to called Attackable and notify the clan call.
			for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.CLAN_ATTACKED))
				quest.onClanAttacked(_actor, _actor, target, aggro, null);
			
			for (final Attackable called : _actor.getKnownTypeInRadius(Attackable.class, _actor.getTemplate().getClanRange()))
			{
				// Called is dead or caller is the same as called.
				if (called.isDead() || called != _actor)
					continue;
				
				// Caller clan doesn't correspond to the called clan.
				if (!ArraysUtil.contains(actorClans, called.getTemplate().getClans()))
					continue;
				
				// Called ignores that type of caller id.
				if (ArraysUtil.contains(called.getTemplate().getIgnoredIds(), _actor.getNpcId()))
					continue;
				
				// Check if the Attackable is in the LoS of the caller.
				if (!GeoEngine.getInstance().canSeeTarget(_actor, called))
					continue;
				
				// Retrieve scripts associated to called Attackable and notify the clan call.
				for (Quest quest : called.getTemplate().getEventQuests(EventHandler.CLAN_ATTACKED))
					quest.onClanAttacked(_actor, called, target, aggro, null);
			}
		}
	}
	
	/**
	 * This method holds behavioral information on which Intentions are scheduled and which are cast immediately.
	 * <ul>
	 * <li>All possible intentions are scheduled for AttackableAI.</li>
	 * </ul>
	 * @param oldIntention : The {@link IntentionType} to test against.
	 * @param newIntention : The {@link IntentionType} to test.
	 * @return True if the {@link IntentionType} set as parameter can be sheduled after this {@link IntentionType}, otherwise cast it immediately.
	 */
	@Override
	public boolean canScheduleAfter(IntentionType oldIntention, IntentionType newIntention)
	{
		return false;
	}
}