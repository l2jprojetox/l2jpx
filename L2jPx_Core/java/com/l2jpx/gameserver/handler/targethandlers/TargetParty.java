package com.l2jpx.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.handler.ITargetHandler;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.model.group.Party;
import com.l2jpx.gameserver.skills.L2Skill;

public class TargetParty implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.PARTY;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		final List<Creature> list = new ArrayList<>();
		list.add(caster);
		
		final Player player = caster.getActingPlayer();
		if (caster instanceof Summon && skill.addCharacter(caster, player, false))
			list.add(player);
		else if (caster instanceof Player && skill.addSummon(caster, player, false))
			list.add(player.getSummon());
		
		final Party party = caster.getParty();
		if (party != null)
		{
			for (Player member : party.getMembers())
			{
				if (member == player)
					continue;
				
				if (skill.addCharacter(caster, member, false))
					list.add(member);
				
				if (skill.addSummon(caster, member, false))
					list.add(member.getSummon());
			}
		}
		
		return list.toArray(new Creature[list.size()]);
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return caster;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		return true;
	}
}