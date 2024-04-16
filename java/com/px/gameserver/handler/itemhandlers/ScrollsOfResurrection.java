package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.L2Skill;

public class ScrollsOfResurrection implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final WorldObject obj = playable.getTarget();
		if (!(obj instanceof Creature))
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Creature target = (Creature) obj;
		
		if (target.isDead())
		{
			final Player targetPlayer;
			if (target instanceof Player)
				targetPlayer = (Player) target;
			else
				targetPlayer = null;
			
			final Pet targetPet;
			if (target instanceof Pet)
				targetPet = (Pet) target;
			else
				targetPet = null;
			
			if (targetPlayer != null || targetPet != null)
			{
				final Player player = (Player) playable;
				if (targetPlayer != null)
				{
					// Check if the target isn't in a active siege zone.
					if (targetPlayer.isInsideZone(ZoneId.SIEGE) && targetPlayer.getSiegeState() == 0)
					{
						playable.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE));
						return;
					}
					
					// Check if the target is in a festival.
					if (targetPlayer.isFestivalParticipant())
					{
						playable.sendMessage("You may not resurrect participants in a festival.");
						return;
					}
					
					if (targetPlayer.isReviveRequested())
					{
						if (targetPlayer.isRevivingPet())
							player.sendPacket(SystemMessageId.CANNOT_RES_MASTER);
						else
							player.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED);
						
						return;
					}
				}
				else if (targetPet != null)
				{
					if (targetPet.getOwner() != player)
					{
						if (targetPet.getOwner().isReviveRequested())
						{
							if (targetPet.getOwner().isRevivingPet())
								player.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED);
							else
								player.sendPacket(SystemMessageId.CANNOT_RES_PET2);
							
							return;
						}
					}
				}
			}
		}
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
		{
			LOGGER.warn("{} doesn't have any registered skill for handler.", item.getName());
			return;
		}
		
		for (IntIntHolder skillInfo : skills)
		{
			if (skillInfo == null)
				continue;
			
			final L2Skill itemSkill = skillInfo.getSkill();
			if (itemSkill == null)
				continue;
			
			// Scroll consumption is made on skill call, not on item call.
			playable.getAI().tryToCast(target, itemSkill);
		}
	}
}