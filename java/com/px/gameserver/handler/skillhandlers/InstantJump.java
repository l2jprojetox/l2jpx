package com.px.gameserver.handler.skillhandlers;

import com.px.commons.math.MathUtil;

import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.serverpackets.ValidateLocation;
import com.px.gameserver.skills.L2Skill;

public class InstantJump implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.INSTANT_JUMP
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		Creature target = (Creature) targets[0];
		
		int px = target.getX();
		int py = target.getY();
		double ph = MathUtil.convertHeadingToDegree(target.getHeading());
		
		ph += 180;
		
		if (ph > 360)
			ph -= 360;
		
		ph = (Math.PI * ph) / 180;
		
		int x = (int) (px + (25 * Math.cos(ph)));
		int y = (int) (py + (25 * Math.sin(ph)));
		int z = target.getZ();
		
		// Abort attack, cast and move.
		activeChar.abortAll(false);
		
		// Teleport the actor.
		activeChar.setXYZ(x, y, z);
		activeChar.broadcastPacket(new ValidateLocation(activeChar));
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}