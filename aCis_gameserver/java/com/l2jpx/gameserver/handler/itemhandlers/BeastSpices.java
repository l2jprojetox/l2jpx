package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.FeedableBeast;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.skills.L2Skill;

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