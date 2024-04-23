package com.l2jpx.gameserver.model.actor.ai.type;

import com.l2jpx.gameserver.enums.AiEventType;
import com.l2jpx.gameserver.enums.IntentionType;
import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.enums.items.ItemLocation;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.skills.L2Skill;

public abstract class PlayableAI extends CreatureAI
{
	public PlayableAI(Playable playable)
	{
		super(playable);
	}
	
	@Override
	protected void onEvtFinishedCasting()
	{
		if (_nextIntention.isBlank())
		{
			if (_currentIntention.getType() == IntentionType.CAST)
			{
				final L2Skill skill = _currentIntention.getSkill();
				final Creature target = _currentIntention.getFinalTarget();
				
				if (skill.nextActionIsAttack() && target.isAttackableWithoutForceBy(getActor()))
					doAttackIntention(target, _currentIntention.isCtrlPressed(), _currentIntention.isShiftPressed());
				else
					doActiveIntention();
			}
			else
				// TODO This occurs with skills that change the AI of the caster in callSkill->useSkill (eg. SoE)
				doActiveIntention();
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtFinishedAttack()
	{
		if (_nextIntention.isBlank())
		{
			if (getActor().canKeepAttacking(_currentIntention.getFinalTarget()))
				notifyEvent(AiEventType.THINK, null, null);
			else
				doActiveIntention();
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	public synchronized void tryToAttack(Creature target, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		// These situations are waited out regardless. Any Intention that is added is scheduled as nextIntention.
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.ATTACK))
		{
			getNextIntention().updateAsAttack(target, isCtrlPressed, isShiftPressed);
			clientActionFailed();
			return;
		}
		
		if (target instanceof Playable)
		{
			final Player targetPlayer = target.getActingPlayer();
			final Player actorPlayer = getActor().getActingPlayer();
			
			if (!target.isInsideZone(ZoneId.PVP))
			{
				if (targetPlayer.getProtectionBlessing() && (actorPlayer.getStatus().getLevel() - targetPlayer.getStatus().getLevel()) >= 10 && actorPlayer.getKarma() > 0)
				{
					actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					clientActionFailed();
					return;
				}
				
				if (actorPlayer.getProtectionBlessing() && (targetPlayer.getStatus().getLevel() - actorPlayer.getStatus().getLevel()) >= 10 && targetPlayer.getKarma() > 0)
				{
					actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					clientActionFailed();
					return;
				}
			}
			
			if (targetPlayer.isCursedWeaponEquipped() && actorPlayer.getStatus().getLevel() <= 20)
			{
				actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				clientActionFailed();
				return;
			}
			
			if (actorPlayer.isCursedWeaponEquipped() && targetPlayer.getStatus().getLevel() <= 20)
			{
				actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				clientActionFailed();
				return;
			}
		}
		
		doAttackIntention(target, isCtrlPressed, isShiftPressed);
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		clientActionFailed();
		
		if (getActor().denyAiAction() || getActor().isSitting())
		{
			doActiveIntention();
			return null;
		}
		
		final WorldObject target = World.getInstance().getObject(_currentIntention.getItemObjectId());
		if (!(target instanceof ItemInstance) || isTargetLost(target))
		{
			doActiveIntention();
			return null;
		}
		
		final ItemInstance item = (ItemInstance) target;
		if (item.getLocation() != ItemLocation.VOID)
		{
			doActiveIntention();
			return null;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (getActor().getMove().maybeMoveToLocation(target.getPosition(), 36, false, isShiftPressed))
		{
			if (isShiftPressed)
				doActiveIntention();
			
			return null;
		}
		
		doActiveIntention();
		
		return item;
	}
	
	@Override
	public Playable getActor()
	{
		return (Playable) _actor;
	}
}