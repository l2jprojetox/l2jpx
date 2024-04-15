package com.px.gameserver.handler.skillhandlers;

import com.px.commons.math.MathUtil;
import com.px.commons.random.Rnd;

import com.px.gameserver.data.manager.ZoneManager;
import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.enums.items.WeaponType;
import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.itemcontainer.Inventory;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.zone.type.FishingZone;
import com.px.gameserver.network.SystemMessageId;

public class Fishing implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.FISHING
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		Player player = (Player) activeChar;
		
		// Cancels fishing
		if (player.isFishing())
		{
			player.getFishingStance().end(false);
			player.sendPacket(SystemMessageId.FISHING_ATTEMPT_CANCELLED);
			return;
		}
		
		// Fishing pole isn't equipped.
		if (player.getAttackType() != WeaponType.FISHINGROD)
		{
			player.sendPacket(SystemMessageId.FISHING_POLE_NOT_EQUIPPED);
			return;
		}
		
		// You can't fish while you are on boat
		if (player.isInBoat())
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_ON_BOAT);
			return;
		}
		
		if (player.isCrafting() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_WHILE_USING_RECIPE_BOOK);
			return;
		}
		
		// You can't fish in water
		if (player.isInsideZone(ZoneId.WATER))
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_UNDER_WATER);
			return;
		}
		
		// Check equipped baits.
		final ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (lure == null)
		{
			player.sendPacket(SystemMessageId.BAIT_ON_HOOK_BEFORE_FISHING);
			return;
		}
		
		final int rnd = Rnd.get(50) + 250;
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(player.getHeading()));
		
		final int x = player.getX() + (int) (Math.cos(radian) * rnd);
		final int y = player.getY() + (int) (Math.sin(radian) * rnd);
		
		boolean canFish = false;
		int z = 0;
		
		// Pick the fishing zone.
		final FishingZone zone = ZoneManager.getInstance().getZone(x, y, FishingZone.class);
		if (zone != null)
		{
			z = zone.getWaterZ();
			
			// Check if the height related to the bait location is above water level. If yes, it means the water isn't visible.
			if (GeoEngine.getInstance().canSeeTarget(player, new Location(x, y, z)) && GeoEngine.getInstance().getHeight(x, y, z) < z)
			{
				z += 10;
				canFish = true;
			}
		}
		
		// You can't fish here.
		if (!canFish)
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_HERE);
			return;
		}
		
		// Has enough bait, consume 1 and update inventory.
		if (!player.destroyItem("Consume", lure, 1, player, false))
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_BAIT);
			return;
		}
		
		// Start fishing.
		player.getFishingStance().start(x, y, z, lure);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}