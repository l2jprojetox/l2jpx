package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.FeedableBeast;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;

public class BeastSpice implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (!(activeChar.getTarget() instanceof FeedableBeast))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		int skillId = 0;
		switch (item.getItemId())
		{
			case 6643:
				skillId = 2188;
				break;
			case 6644:
				skillId = 2189;
				break;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
		if (skill != null)
			activeChar.useMagic(skill, false, false);
	}
}