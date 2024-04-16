package com.px.gameserver.handler.skillhandlers;

import com.px.commons.random.Rnd;

import com.px.gameserver.data.manager.CastleManorManager;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.manor.Seed;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.skills.L2Skill;

public class Sow implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SOW
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (itemInstance == null || !(activeChar instanceof Player))
			return;
		
		final WorldObject object = targets[0];
		if (!(object instanceof Monster))
			return;
		
		final Player player = (Player) activeChar;
		final Monster target = (Monster) object;
		
		if (target.isDead())
			return;
		
		if (target.getSeedState().isSeeded())
		{
			player.sendPacket(SystemMessageId.THE_SEED_HAS_BEEN_SOWN);
			return;
		}
		
		final Seed seed = CastleManorManager.getInstance().getSeed(itemInstance.getItemId());
		if (seed == null)
			return;
		
		if (!calcSuccess(player, target, seed))
		{
			player.sendPacket(SystemMessageId.THE_SEED_WAS_NOT_SOWN);
			return;
		}
		
		target.getSeedState().setSeeded(player, seed);
		
		player.sendPacket(SystemMessageId.THE_SEED_WAS_SUCCESSFULLY_SOWN);
	}
	
	private static boolean calcSuccess(Player player, Creature target, Seed seed)
	{
		final int minlevelSeed = seed.getLevel() - 5;
		final int maxlevelSeed = seed.getLevel() + 5;
		
		final int targetLevel = target.getStatus().getLevel();
		
		int rate = (seed.isAlternative()) ? 20 : 90;
		
		// Apply a 5% penalty for each level difference, above 5, between target and seed levels.
		if (targetLevel < minlevelSeed)
			rate -= 5 * (minlevelSeed - targetLevel);
		
		if (targetLevel > maxlevelSeed)
			rate -= 5 * (targetLevel - maxlevelSeed);
		
		// Apply a 5% penalty for each level difference, above 5, between player and target levels.
		final int diff = Math.abs(player.getStatus().getLevel() - targetLevel);
		if (diff > 5)
			rate -= (diff - 5) * 5;
		
		// Success rate can't be lesser than 1%.
		return Rnd.get(100) < Math.max(1, rate);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}