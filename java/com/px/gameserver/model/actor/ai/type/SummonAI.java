package com.px.gameserver.model.actor.ai.type;

import com.px.gameserver.data.manager.CursedWeaponManager;
import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.items.ArmorType;
import com.px.gameserver.enums.items.EtcItemType;
import com.px.gameserver.enums.items.WeaponType;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.handler.ItemHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.AutoAttackStart;
import com.px.gameserver.network.serverpackets.AutoAttackStop;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.taskmanager.AttackStanceTaskManager;
import com.px.gameserver.taskmanager.ItemsOnGroundTaskManager;

public class SummonAI extends PlayableAI<Summon>
{
	private volatile boolean _followOwner = true;
	
	public SummonAI(Summon summon)
	{
		super(summon);
	}
	
	@Override
	protected void thinkIdle()
	{
		if (_followOwner)
			doFollowIntention(_actor.getOwner(), false);
		// Don't call doIdleIntention directly to avoid self-call.
		else
		{
			prepareIntention();
			
			// Feed related values.
			_currentIntention.updateAsIdle();
			
			_actor.getMove().stop();
		}
	}
	
	@Override
	protected void onEvtTeleported()
	{
		_followOwner = true;
		doFollowIntention(_actor.getOwner(), false);
	}
	
	@Override
	protected void onEvtFinishedCasting()
	{
		if (_nextIntention.isBlank())
		{
			if (_previousIntention.getType() == IntentionType.ATTACK)
				doIntention(_previousIntention);
			else
				doIdleIntention();
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	public void onEvtAttacked(Creature attacker)
	{
		super.onEvtAttacked(attacker);
		
		_actor.getMove().avoidAttack(attacker);
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
		super.onEvtEvaded(attacker);
		
		_actor.getMove().avoidAttack(attacker);
	}
	
	@Override
	protected void thinkAttack()
	{
		if (_actor.denyAiAction())
		{
			doIdleIntention();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		if (isTargetLost(target))
		{
			doIdleIntention();
			return;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (_actor.getMove().maybeStartOffensiveFollow(target, _actor.getStatus().getPhysicalAttackRange()))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return;
		}
		
		_actor.getMove().stop();
		
		if (!_actor.getAttack().canAttack(target))
		{
			doIdleIntention();
			return;
		}
		
		_actor.getAttack().doAttack(target);
	}
	
	@Override
	protected void thinkFollow()
	{
		if (_actor.denyAiAction() || _actor.isMovementDisabled())
			return;
		
		final Creature target = _currentIntention.getFinalTarget();
		if (_actor == target)
			return;
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (isShiftPressed)
			return;
		
		_actor.getMove().maybeStartFriendlyFollow(target, 70);
	}
	
	@Override
	protected void thinkInteract()
	{
		final WorldObject target = _currentIntention.getTarget();
		if (isTargetLost(target))
		{
			doIdleIntention();
			return;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (_actor.getMove().maybeMoveToLocation(target.getPosition(), _actor.getStatus().getPhysicalAttackRange(), true, isShiftPressed))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return;
		}
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		final ItemInstance item = super.thinkPickUp();
		if (item == null)
			return null;
		
		if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
		{
			_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item.getItemId()));
			return null;
		}
		
		if (item.getItem().getItemType() == EtcItemType.ARROW || item.getItem().getItemType() == EtcItemType.SHOT)
		{
			_actor.getOwner().sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return null;
		}
		
		synchronized (item)
		{
			if (!item.isVisible())
				return null;
			
			if (!_actor.getInventory().validateCapacity(item))
			{
				_actor.getOwner().sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return null;
			}
			
			if (item.getOwnerId() != 0 && !_actor.getOwner().isLooterOrInLooterParty(item.getOwnerId()))
			{
				if (item.getItemId() == 57)
					_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
				else if (item.getCount() > 1)
					_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item.getItemId()).addNumber(item.getCount()));
				else
					_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item.getItemId()));
				
				return null;
			}
			
			if (item.hasDropProtection())
				item.removeDropProtection();
			
			item.pickupMe(_actor);
			
			ItemsOnGroundTaskManager.getInstance().remove(item);
		}
		
		if (item.getItemType() == EtcItemType.HERB)
		{
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(_actor, item, false);
			
			item.destroyMe("Consume", _actor.getOwner(), null);
			_actor.getStatus().broadcastStatusUpdate();
		}
		else
		{
			if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType)
			{
				SystemMessage sm;
				if (item.getEnchantLevel() > 0)
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PET_PICKED_UP_S2_S3).addCharName(_actor.getOwner()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
				else
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PET_PICKED_UP_S2).addCharName(_actor.getOwner()).addItemName(item.getItemId());
				
				_actor.getOwner().broadcastPacketInRadius(sm, 1400);
			}
			
			if (_actor.getOwner().isInParty())
				_actor.getOwner().getParty().distributeItem(_actor.getOwner(), item, _actor);
			else
				_actor.addItem("Pickup", item, null, true);
		}
		
		return item;
	}
	
	@Override
	public void startAttackStance()
	{
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(_actor.getOwner()))
		{
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
			_actor.getOwner().broadcastPacket(new AutoAttackStart(_actor.getOwner().getObjectId()));
		}
		
		AttackStanceTaskManager.getInstance().add(_actor.getOwner());
	}
	
	@Override
	public void stopAttackStance()
	{
		_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
	}
	
	public void switchFollowStatus()
	{
		setFollowStatus(!_followOwner);
	}
	
	@Override
	public void setFollowStatus(boolean state)
	{
		_followOwner = state;
		
		if (_followOwner)
			tryToFollow(_actor.getOwner(), false);
		else
			tryToIdle();
	}
	
	@Override
	public boolean getFollowStatus()
	{
		return _followOwner;
	}
	
	@Override
	public boolean isTargetLost(WorldObject object)
	{
		final boolean isTargetLost = super.isTargetLost(object);
		if (isTargetLost)
			setFollowStatus(true);
		
		return isTargetLost;
	}
	
	@Override
	public boolean isTargetLost(WorldObject object, L2Skill skill)
	{
		final boolean isTargetLost = super.isTargetLost(object, skill);
		if (isTargetLost)
			setFollowStatus(true);
		
		return isTargetLost;
	}
}