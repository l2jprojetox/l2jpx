package com.px.gameserver.handler.itemhandlers;

import com.px.Config;
import com.px.gameserver.data.manager.CastleManorManager;
import com.px.gameserver.data.xml.MapRegionData;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.manor.Seed;
import com.px.gameserver.network.SystemMessageId;

public class SeedHandler implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!Config.ALLOW_MANOR || !(playable instanceof Player))
			return;
		
		final WorldObject tgt = playable.getTarget();
		if (!(tgt instanceof Monster) || !((Monster) tgt).getTemplate().isSeedable())
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return;
		}
		
		final Monster target = (Monster) tgt;
		if (target.isDead() || target.isSeeded())
		{
			playable.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final Seed seed = CastleManorManager.getInstance().getSeed(item.getItemId());
		if (seed == null)
			return;
		
		if (seed.getCastleId() != MapRegionData.getInstance().getAreaCastle(playable.getX(), playable.getY()))
		{
			playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return;
		}
		
		target.setSeeded(seed, playable.getObjectId());
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills != null)
		{
			if (skills[0] == null)
				return;
			
			playable.useMagic(skills[0].getSkill(), false, false);
		}
	}
}