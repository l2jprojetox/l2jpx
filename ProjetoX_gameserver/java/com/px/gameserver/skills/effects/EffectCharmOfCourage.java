package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.L2EffectFlag;
import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.EtcStatusUpdate;
import com.px.gameserver.skills.Env;

/**
 * @author nBd
 */
public class EffectCharmOfCourage extends L2Effect
{
	public EffectCharmOfCourage(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.CHARMOFCOURAGE;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player)
		{
			getEffected().broadcastPacket(new EtcStatusUpdate((Player) getEffected()));
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected() instanceof Player)
			getEffected().broadcastPacket(new EtcStatusUpdate((Player) getEffected()));
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.CHARM_OF_COURAGE.getMask();
	}
}