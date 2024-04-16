package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.enums.SiegeSide;
import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Door;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.residence.castle.Siege;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class StriderSiegeAssault implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.STRIDER_SIEGE_ASSAULT
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		if (!check(player, targets[0], skill))
			return;
		
		final Door door = (Door) targets[0];
		if (door.isAlikeDead())
			return;
		
		final boolean isCrit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(activeChar));
		final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);
		final ShieldDefense sDef = Formulas.calcShldUse(activeChar, door, skill, isCrit);
		
		final int damage = (int) Formulas.calcPhysicalSkillDamage(activeChar, door, skill, sDef, isCrit, ss);
		if (damage > 0)
		{
			activeChar.sendDamageMessage(door, damage, false, false, false);
			door.reduceCurrentHp(damage, activeChar, skill);
		}
		else
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
		
		activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @param target : The {@link WorldObject} to test.
	 * @param skill : The {@link L2Skill} to test.
	 * @return True if the {@link Player} can cast the {@link L2Skill} on the {@link WorldObject}.
	 */
	public static boolean check(Player player, WorldObject target, L2Skill skill)
	{
		SystemMessage sm = null;
		
		if (!player.isRiding())
			sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill);
		else if (!(target instanceof Door))
			sm = SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET);
		else
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(player);
			if (siege == null || !siege.checkSide(player.getClan(), SiegeSide.ATTACKER))
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill);
		}
		
		if (sm != null)
			player.sendPacket(sm);
		
		return sm == null;
	}
}