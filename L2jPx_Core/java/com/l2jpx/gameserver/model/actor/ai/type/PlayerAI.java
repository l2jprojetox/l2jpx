package com.l2jpx.gameserver.model.actor.ai.type;

import com.l2jpx.commons.pool.ThreadPool;

import com.l2jpx.gameserver.data.manager.CursedWeaponManager;
import com.l2jpx.gameserver.enums.AiEventType;
import com.l2jpx.gameserver.enums.IntentionType;
import com.l2jpx.gameserver.enums.LootRule;
import com.l2jpx.gameserver.enums.items.ArmorType;
import com.l2jpx.gameserver.enums.items.EtcItemType;
import com.l2jpx.gameserver.enums.items.WeaponType;
import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.handler.ItemHandler;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Boat;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.model.actor.instance.StaticObject;
import com.l2jpx.gameserver.model.actor.instance.Walker;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.model.location.BoatEntrance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.ActionFailed;
import com.l2jpx.gameserver.network.serverpackets.AutoAttackStart;
import com.l2jpx.gameserver.network.serverpackets.ChairSit;
import com.l2jpx.gameserver.network.serverpackets.MoveToLocationInVehicle;
import com.l2jpx.gameserver.network.serverpackets.MoveToPawn;
import com.l2jpx.gameserver.network.serverpackets.StopMove;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;
import com.l2jpx.gameserver.taskmanager.ItemsOnGroundTaskManager;

public class PlayerAI extends PlayableAI
{
	public PlayerAI(Player player)
	{
		super(player);
	}
	
	@Override
	protected void onEvtArrived()
	{
		if (_currentIntention.getType() == IntentionType.MOVE_TO)
		{
			final Boat boat = _currentIntention.getBoat();
			if (boat != null)
			{
				final BoatEntrance closestEntrance = boat.getClosestEntrance(getActor().getPosition());
				
				getActor().getBoatPosition().set(closestEntrance.getInnerLocation());
				
				// Since we're close enough to the boat we just send client onboarding packet without any movement on the server.
				getActor().broadcastPacket(new MoveToLocationInVehicle(getActor(), boat, closestEntrance.getInnerLocation(), getActor().getPosition()));
			}
		}
		
		super.onEvtArrived();
	}
	
	@Override
	protected void onEvtArrivedBlocked()
	{
		if (_currentIntention.getType() == IntentionType.INTERACT)
		{
			clientActionFailed();
			
			final WorldObject target = _currentIntention.getTarget();
			if (getActor().getAI().canDoInteract(target))
			{
				getActor().broadcastPacket(new StopMove(getActor()));
				
				target.onInteract(getActor());
			}
			else
				super.onEvtArrivedBlocked();
			
			doIdleIntention();
		}
		else
			super.onEvtArrivedBlocked();
	}
	
	@Override
	protected void onEvtSatDown(WorldObject target)
	{
		if (_nextIntention.isBlank())
			doIdleIntention();
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtStoodUp()
	{
		if (getActor().getThroneId() != 0)
		{
			final WorldObject object = World.getInstance().getObject(getActor().getThroneId());
			if (object instanceof StaticObject)
				((StaticObject) object).setBusy(false);
			
			getActor().setThroneId(0);
		}
		
		if (_nextIntention.isBlank())
			doIdleIntention();
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtBowAttackReuse()
	{
		if (getActor().getAttackType() == WeaponType.BOW)
		{
			// Attacks can be scheduled while isAttackingNow
			if (_nextIntention.getType() == IntentionType.ATTACK)
			{
				doIntention(_nextIntention);
				return;
			}
			
			if (_currentIntention.getType() == IntentionType.ATTACK)
			{
				if (getActor().canKeepAttacking(_currentIntention.getFinalTarget()))
					notifyEvent(AiEventType.THINK, null, null);
				else
					doIdleIntention();
			}
		}
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		if (getActor().getTamedBeast() != null)
			getActor().getTamedBeast().getAI().notifyEvent(AiEventType.OWNER_ATTACKED, attacker, null);
		
		if (getActor().isSitting())
			doStandIntention();
		
		super.onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtCancel()
	{
		getActor().getCast().stop();
		getActor().getMove().cancelFollowTask();
		
		doIdleIntention();
	}
	
	@Override
	public synchronized void doActiveIntention()
	{
		doIdleIntention();
	}
	
	@Override
	public synchronized void tryToActive()
	{
		tryToIdle();
	}
	
	@Override
	protected void thinkActive()
	{
		thinkIdle();
	}
	
	@Override
	protected void thinkAttack()
	{
		if (getActor().denyAiAction() || getActor().isSitting())
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		if (isTargetLost(target))
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (getActor().getMove().maybeMoveToPawn(target, getActor().getStatus().getPhysicalAttackRange(), isShiftPressed))
		{
			if (isShiftPressed)
			{
				doIdleIntention();
				clientActionFailed();
			}
			
			return;
		}
		
		getActor().getMove().stop();
		
		if ((getActor().getAttackType() == WeaponType.BOW && getActor().getAttack().isBowCoolingDown()) || getActor().getAttack().isAttackingNow())
		{
			setNextIntention(_currentIntention);
			clientActionFailed();
			return;
		}
		
		if (!getActor().getAttack().canDoAttack(target))
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		getActor().getAttack().doAttack(target);
	}
	
	@Override
	protected void thinkCast()
	{
		if (getActor().denyAiAction() || getActor().getAllSkillsDisabled() || getActor().getCast().isCastingNow())
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		if (target == null)
		{
			doIdleIntention();
			return;
		}
		
		final L2Skill skill = _currentIntention.getSkill();
		if (isTargetLost(target, skill))
		{
			doIdleIntention();
			return;
		}
		
		if (!getActor().getCast().canAttemptCast(target, skill))
			return;
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (skill.getTargetType() == SkillTargetType.GROUND)
		{
			if (getActor().getMove().maybeMoveToLocation(getActor().getCast().getSignetLocation(), skill.getCastRange(), false, isShiftPressed))
			{
				if (isShiftPressed)
				{
					getActor().sendPacket(SystemMessageId.TARGET_TOO_FAR);
					doIdleIntention();
				}
				
				return;
			}
		}
		else
		{
			if (getActor().getMove().maybeMoveToPawn(target, skill.getCastRange(), isShiftPressed))
			{
				if (isShiftPressed)
				{
					getActor().sendPacket(SystemMessageId.TARGET_TOO_FAR);
					doIdleIntention();
				}
				
				return;
			}
		}
		
		if (skill.isToggle())
		{
			getActor().getMove().stop();
			getActor().getCast().doToggleCast(skill, target);
		}
		else
		{
			final boolean isCtrlPressed = _currentIntention.isCtrlPressed();
			final int itemObjectId = _currentIntention.getItemObjectId();
			
			if (!getActor().getCast().canDoCast(target, skill, isCtrlPressed, itemObjectId))
			{
				if (skill.nextActionIsAttack() && target.isAttackableWithoutForceBy(getActor()))
					doAttackIntention(target, isCtrlPressed, isShiftPressed);
				
				return;
			}
			
			if (skill.getHitTime() > 50)
				getActor().getMove().stop();
			
			if (skill.getSkillType() == SkillType.FUSION || skill.getSkillType() == SkillType.SIGNET_CASTTIME)
				getActor().getCast().doFusionCast(skill, target);
			else
				getActor().getCast().doCast(skill, target, _actor.getInventory().getItemByObjectId(itemObjectId));
		}
	}
	
	@Override
	protected void thinkFakeDeath()
	{
		if (getActor().denyAiAction() || getActor().isMounted())
		{
			clientActionFailed();
			return;
		}
		
		// Start fake death hidden in isCtrlPressed.
		if (_currentIntention.isCtrlPressed())
		{
			getActor().getMove().stop();
			getActor().startFakeDeath();
		}
		else
			getActor().stopFakeDeath(false);
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		final ItemInstance item = super.thinkPickUp();
		if (item == null)
			return null;
		
		synchronized (item)
		{
			if (!item.isVisible())
				return null;
			
			if (((getActor().isInParty() && getActor().getParty().getLootRule() == LootRule.ITEM_LOOTER) || !getActor().isInParty()) && !getActor().getInventory().validateCapacity(item))
			{
				getActor().sendPacket(SystemMessageId.SLOTS_FULL);
				return null;
			}
			
			if (getActor().getActiveTradeList() != null)
			{
				getActor().sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
				return null;
			}
			
			if (item.getOwnerId() != 0 && !getActor().isLooterOrInLooterParty(item.getOwnerId()))
			{
				if (item.getItemId() == 57)
					getActor().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
				else if (item.getCount() > 1)
					getActor().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item).addNumber(item.getCount()));
				else
					getActor().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item));
				
				return null;
			}
			
			if (item.hasDropProtection())
				item.removeDropProtection();
			
			item.pickupMe(getActor());
			
			ItemsOnGroundTaskManager.getInstance().remove(item);
		}
		
		if (item.getItemType() == EtcItemType.HERB)
		{
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(getActor(), item, false);
			
			item.destroyMe("Consume", getActor(), null);
		}
		else if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
		{
			getActor().addItem("Pickup", item, null, true);
		}
		else
		{
			if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType)
			{
				SystemMessage sm;
				if (item.getEnchantLevel() > 0)
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2_S3).addString(getActor().getName()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
				else
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2).addString(getActor().getName()).addItemName(item.getItemId());
				
				getActor().broadcastPacketInRadius(sm, 1400);
			}
			
			if (getActor().isInParty())
				getActor().getParty().distributeItem(getActor(), item);
			else if (item.getItemId() == 57 && getActor().getInventory().getAdenaInstance() != null)
			{
				getActor().addAdena("Pickup", item.getCount(), null, true);
				item.destroyMe("Pickup", getActor(), null);
			}
			else
				getActor().addItem("Pickup", item, null, true);
		}
		
		ThreadPool.schedule(() -> getActor().setIsParalyzed(false), 200);
		getActor().setIsParalyzed(true);
		
		return item;
	}
	
	@Override
	protected void thinkInteract()
	{
		clientActionFailed();
		
		if (getActor().denyAiAction() || getActor().isSitting() || getActor().isFlying())
		{
			doIdleIntention();
			return;
		}
		
		final WorldObject target = _currentIntention.getTarget();
		if (isTargetLost(target))
		{
			doIdleIntention();
			return;
		}
		
		if (!getActor().getAI().canAttemptInteract())
		{
			doIdleIntention();
			return;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (getActor().getMove().maybeMoveToPawn(target, 100, isShiftPressed))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return;
		}
		
		if (!getActor().getAI().canDoInteract(target))
		{
			doIdleIntention();
			return;
		}
		
		if (target instanceof Walker)
			getActor().broadcastPacket(new StopMove(getActor()));
		else
		{
			getActor().getPosition().setHeadingTo(target);
			getActor().broadcastPacket(new MoveToPawn(_actor, target, Npc.INTERACTION_DISTANCE));
		}
		
		target.onInteract(getActor());
		
		doIdleIntention();
	}
	
	@Override
	protected void thinkSit()
	{
		if (getActor().denyAiAction() || getActor().isSitting() || getActor().isOperating() || getActor().isMounted())
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		getActor().getMove().stop();
		
		// sitDown sends the ChangeWaitType packet, which MUST precede the ChairSit packet (sent in this function) in order to properly sit on the throne.
		getActor().sitDown();
		
		final WorldObject target = _currentIntention.getTarget();
		final boolean isThrone = target instanceof StaticObject && ((StaticObject) target).getType() == 1;
		if (isThrone && !((StaticObject) target).isBusy() && getActor().isIn3DRadius(target, Npc.INTERACTION_DISTANCE))
		{
			getActor().setThroneId(target.getObjectId());
			
			((StaticObject) target).setBusy(true);
			getActor().broadcastPacket(new ChairSit(getActor().getObjectId(), ((StaticObject) target).getStaticObjectId()));
		}
	}
	
	@Override
	protected void thinkStand()
	{
		// no need to getActor().isOperating() here, because it is included in the Player overriden denyAiAction
		if (getActor().denyAiAction() || !getActor().isSitting() || getActor().isMounted())
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		if (getActor().isFakeDeath())
			getActor().stopFakeDeath(true);
		else
			getActor().standUp();
	}
	
	@Override
	protected void thinkUseItem()
	{
		final ItemInstance itemToTest = getActor().getInventory().getItemByObjectId(_currentIntention.getItemObjectId());
		if (itemToTest == null)
			return;
		
		// Equip or unequip the related ItemInstance.
		getActor().useEquippableItem(itemToTest, false);
		
		// Resolve previous intention.
		if (_previousIntention.getType() != IntentionType.CAST && _previousIntention.getType() != IntentionType.USE_ITEM)
			doIntention(_previousIntention);
	}
	
	@Override
	public boolean canAttemptInteract()
	{
		if (getActor().isOperating() || getActor().isProcessingTransaction())
			return false;
		
		return true;
	}
	
	@Override
	public boolean canDoInteract(WorldObject target)
	{
		// Can't interact in shop mode, or during a transaction or a request.
		if (getActor().isOperating() || getActor().isProcessingTransaction())
			return false;
		
		// Can't interact if regular distance doesn't match.
		return target.isIn3DRadius(getActor(), Npc.INTERACTION_DISTANCE);
	}
	
	@Override
	public void startAttackStance()
	{
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(getActor()))
		{
			final Summon summon = getActor().getSummon();
			if (summon != null)
				summon.broadcastPacket(new AutoAttackStart(summon.getObjectId()));
			
			getActor().broadcastPacket(new AutoAttackStart(getActor().getObjectId()));
		}
		
		AttackStanceTaskManager.getInstance().add(getActor());
	}
	
	@Override
	public void clientActionFailed()
	{
		getActor().sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public Player getActor()
	{
		return (Player) _actor;
	}
}