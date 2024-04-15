package com.px.gameserver.handler.itemhandlers;

import com.px.Config;
import com.px.gameserver.data.SkillTable;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;

public class Harvester implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		if (!Config.ALLOW_MANOR)
			return;
		
		if (!(playable.getTarget() instanceof Monster))
		{
			playable.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final Monster _target = (Monster) playable.getTarget();
		if (_target == null || !_target.isDead())
		{
			playable.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(2098, 1);
		if (skill != null)
			playable.useMagic(skill, false, false);
	}
}