package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.skills.L2EffectFlag;
import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Folk;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.model.actor.instance.SiegeFlag;
import com.px.gameserver.model.actor.instance.SiegeSummon;
import com.px.gameserver.skills.Env;

/**
 * Implementation of the Fear Effect
 * @author littlecrow
 */
public class EffectFear extends L2Effect
{
	public static final int FEAR_RANGE = 500;
	
	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FEAR;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player && getEffector() instanceof Player)
		{
			switch (getSkill().getId())
			{
				case 1376:
				case 1169:
				case 65:
				case 1092:
				case 98:
				case 1272:
				case 1381:
				case 763:
					break;
				default:
					return false;
			}
		}
		
		if (getEffected() instanceof Folk || getEffected() instanceof SiegeFlag || getEffected() instanceof SiegeSummon)
			return false;
		
		if (getEffected().isAfraid())
			return false;
		
		getEffected().startFear();
		onActionTime();
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopFear(true);
	}
	
	@Override
	public boolean onActionTime()
	{
		if (!(getEffected() instanceof Pet))
			getEffected().setRunning();
		
		final int victimX = getEffected().getX();
		final int victimY = getEffected().getY();
		final int victimZ = getEffected().getZ();
		
		final int posX = victimX + (((victimX > getEffector().getX()) ? 1 : -1) * FEAR_RANGE);
		final int posY = victimY + (((victimY > getEffector().getY()) ? 1 : -1) * FEAR_RANGE);
		
		getEffected().getAI().setIntention(IntentionType.MOVE_TO, GeoEngine.getInstance().canMoveToTargetLoc(victimX, victimY, victimZ, posX, posY, victimZ));
		return true;
	}
	
	@Override
	public boolean onSameEffect(L2Effect effect)
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.FEAR.getMask();
	}
}