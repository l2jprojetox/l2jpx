package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.network.serverpackets.StatusUpdate;
import com.px.gameserver.skills.Env;

class EffectCombatPointHealOverTime extends L2Effect
{
	public EffectCombatPointHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.COMBAT_POINT_HEAL_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
			return false;
		
		double cp = getEffected().getCurrentCp();
		double maxcp = getEffected().getMaxCp();
		cp += calc();
		
		if (cp > maxcp)
			cp = maxcp;
		
		getEffected().setCurrentCp(cp);
		StatusUpdate sump = new StatusUpdate(getEffected());
		sump.addAttribute(StatusUpdate.CUR_CP, (int) cp);
		getEffected().sendPacket(sump);
		return true;
	}
}
