package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.Config;
import com.l2jpx.gameserver.data.manager.CastleManorManager;
import com.l2jpx.gameserver.data.xml.MapRegionData;
import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.Monster;
import com.l2jpx.gameserver.model.holder.IntIntHolder;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.model.manor.Seed;
import com.l2jpx.gameserver.network.SystemMessageId;

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
		if (!monster.getTemplate().isSeedable())
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return;
		}
		
		if (monster.isDead() || monster.getSeedState().isSeeded())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
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
		
		monster.getSeedState().setSeeded(seed, playable.getObjectId());
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills != null)
		{
			if (skills[0] == null)
				return;
			
			playable.getAI().tryToCast(monster, skills[0].getSkill());
		}
	}
}