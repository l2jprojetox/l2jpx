package com.l2jpx.gameserver.skills.effects;

import com.l2jpx.gameserver.enums.skills.EffectFlag;
import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectSilentMove extends AbstractEffect
{
	public EffectSilentMove(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SILENT_MOVE;
	}
	
	@Override
	public boolean onActionTime()
	{
		// Only cont skills shouldn't end
		if (getSkill().getSkillType() != SkillType.CONT)
			return false;
		
		if (getEffected().isDead())
			return false;
		
		if (getTemplate().getValue() > getEffected().getStatus().getMp())
		{
			getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}
		
		getEffected().getStatus().reduceMp(getTemplate().getValue());
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.SILENT_MOVE.getMask();
	}
}