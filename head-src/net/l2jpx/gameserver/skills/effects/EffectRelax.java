package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Env;

class EffectRelax extends L2Effect
{
	public EffectRelax(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RELAXING;
	}
	
	/** Notify started */
	@Override
	public void onStart()
	{
		
		if (getEffected() instanceof L2PcInstance)
		{
			setRelax(true);
			((L2PcInstance) getEffected()).sitDown();
		}
		else
		{
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
		}
		super.onStart();
	}
	
	@Override
	public void onExit()
	{
		setRelax(false);
		super.onExit();
	}
	
	@Override
	public boolean onActionTime()
	{
		boolean retval = true;
		if (getEffected().isDead())
		{
			retval = false;
		}
		
		if (getEffected() instanceof L2PcInstance)
		{
			if (!((L2PcInstance) getEffected()).isSitting())
			{
				retval = false;
			}
		}
		
		if (getEffected().getCurrentHp() + 1 > getEffected().getMaxHp())
		{
			if (getSkill().isToggle())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Fully rested. Effect of " + getSkill().getName() + " has been removed.");
				getEffected().sendPacket(sm);
				// if (getEffected() instanceof L2PcInstance)
				// ((L2PcInstance)getEffected()).standUp();
				retval = false;
			}
		}
		
		final double manaDam = calc();
		
		if (manaDam > getEffected().getCurrentMp())
		{
			if (getSkill().isToggle())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP);
				getEffected().sendPacket(sm);
				// if (getEffected() instanceof L2PcInstance)
				// ((L2PcInstance)getEffected()).standUp();
				retval = false;
			}
		}
		
		if (!retval)
		{
			setRelax(retval);
		}
		else
		{
			getEffected().reduceCurrentMp(manaDam);
		}
		
		return retval;
	}
	
	private void setRelax(final boolean val)
	{
		if (getEffected() instanceof L2PcInstance)
		{
			((L2PcInstance) getEffected()).setRelax(val);
		}
	}
}
