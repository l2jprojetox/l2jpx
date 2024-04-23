package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.Config;
import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.Monster;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.skills.L2Skill;

public class Harvesters implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		if (!Config.ALLOW_MANOR)
			return;
		
		final WorldObject target = playable.getTarget();
		if (!(target instanceof Monster))
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Monster monster = (Monster) target;
		if (!monster.isDead())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(2098, 1);
		if (skill != null)
			playable.getAI().tryToCast(monster, skill);
	}
}