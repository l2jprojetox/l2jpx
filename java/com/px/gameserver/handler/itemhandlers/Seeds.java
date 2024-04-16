package com.px.gameserver.handler.itemhandlers;

import com.px.Config;
import com.px.gameserver.data.manager.CastleManorManager;
import com.px.gameserver.data.xml.ManorAreaData;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.manor.ManorArea;
import com.px.gameserver.model.manor.Seed;
import com.px.gameserver.network.SystemMessageId;

public class Seeds implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!Config.ALLOW_MANOR || !(playable instanceof Player))
			return;
		
		final WorldObject target = playable.getTarget();
		if (!(target instanceof Monster))
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return;
		}
		
		final Monster monster = (Monster) target;
		final ManorArea area = ManorAreaData.getInstance().getManorArea(monster);
		if (!monster.getTemplate().isSeedable() || area == null)
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return;
		}
		
		final Seed seed = CastleManorManager.getInstance().getSeed(item.getItemId());
		if (seed == null)
			return;
		
		if (area.getCastleId() != seed.getCastleId())
		{
			playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return;
		}
		
		if (monster.isDead())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		if (monster.getSeedState().isSeeded())
		{
			playable.sendPacket(SystemMessageId.THE_SEED_HAS_BEEN_SOWN);
			return;
		}
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null || skills[0] == null)
			return;
		
		playable.getAI().tryToCast(monster, skills[0].getSkill(), false, false, item.getObjectId());
	}
}