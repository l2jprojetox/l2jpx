package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.MagicSkillUse;
import com.px.gameserver.network.serverpackets.SystemMessage;

public class BeastSoulShot implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (playable == null)
			return;
		
		final Player player = playable.getActingPlayer();
		if (player == null)
			return;
		
		if (playable instanceof Summon)
		{
			player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
			return;
		}
		
		final Summon summon = player.getSummon();
		if (summon == null)
		{
			player.sendPacket(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return;
		}
		
		if (summon.isDead())
		{
			player.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
			return;
		}
		
		// SoulShots are already active.
		if (summon.isChargedShot(ShotType.SOULSHOT))
			return;
		
		// If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
		if (!player.destroyItemWithoutTrace("Consume", item.getObjectId(), summon.getSoulShotsPerHit(), null, false))
		{
			if (!player.disableAutoShot(item.getItemId()))
				player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
			
			return;
		}
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addItemName(item.getItemId()));
		summon.setChargedShot(ShotType.SOULSHOT, true);
		player.broadcastPacketInRadius(new MagicSkillUse(summon, summon, 2033, 1, 0, 0), 600);
	}
}