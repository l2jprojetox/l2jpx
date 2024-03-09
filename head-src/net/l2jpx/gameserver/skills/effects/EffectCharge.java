package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.EtcStatusUpdate;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Env;

public class EffectCharge extends L2Effect
{
	public int numCharges;
	
	public EffectCharge(final Env env, final EffectTemplate template)
	{
		super(env, template);
		numCharges = 1;
		if (env.target instanceof L2PcInstance)
		{
			env.target.sendPacket(new EtcStatusUpdate((L2PcInstance) env.target));
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FORCE_HAS_INCREASED_TO_S1_LEVEL);
			sm.addNumber(numCharges);
			getEffected().sendPacket(sm);
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CHARGE;
	}
	
	@Override
	public boolean onActionTime()
	{
		// ignore
		return true;
	}
	
	@Override
	public int getLevel()
	{
		return numCharges;
	}
	
	public void addNumCharges(final int i)
	{
		numCharges = numCharges + i;
	}
}
