package com.px.gameserver.handler.itemhandlers;

import com.px.Config;
import com.px.gameserver.data.SkillTable.FrequentSkill;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;

public class Harvesters implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!Config.ALLOW_MANOR || !(playable instanceof Player))
			return;
		
		final WorldObject target = playable.getTarget();
		if (!(target instanceof Creature))
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Creature creature = (Creature) target;
		if (!creature.isDead())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		playable.getAI().tryToCast(creature, FrequentSkill.HARVEST.getSkill());
	}
}