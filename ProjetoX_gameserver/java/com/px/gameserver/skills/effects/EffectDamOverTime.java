package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.Env;

public class EffectDamOverTime extends L2Effect
{
	public EffectDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
			return false;
		
		double damage = calc();
		if (damage >= getEffected().getCurrentHp())
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_HP));
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!getSkill().killByDOT())
			{
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (getEffected().getCurrentHp() <= 1)
					return true;
				
				damage = getEffected().getCurrentHp() - 1;
			}
		}
		getEffected().reduceCurrentHpByDOT(damage, getEffector(), getSkill());
		
		return true;
	}
}