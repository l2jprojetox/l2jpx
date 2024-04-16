package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.FeedableBeast;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.skills.L2Skill;

public class BeastSpices implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;
		
		if (!(target instanceof FeedableBeast))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final L2Skill skill = item.getEtcItem().getSkills()[0].getSkill();
		if (skill != null)
			player.getAI().tryToCast(target, skill);
	}
}