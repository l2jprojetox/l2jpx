package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.model.actor.instance.L2SiegeSummonInstance;
import net.l2jpx.gameserver.network.serverpackets.MyTargetSelected;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author eX1steam L2JFrozen
 */
public class EffectTargetMe extends L2Effect
{
	public EffectTargetMe(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#getEffectType()
	 */
	@Override
	public EffectType getEffectType()
	{
		return EffectType.TARGET_ME;
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#onStart()
	 */
	@Override
	public void onStart()
	{
		if (getEffected() instanceof L2PlayableInstance)
		{
			if (getEffected() instanceof L2SiegeSummonInstance)
			{
				return;
			}
			
			if (getEffected().getTarget() != getEffector())
			{
				// Target is different - stop autoattack and break cast
				// getEffected().abortAttack();
				// getEffected().abortCast();
				getEffected().setTarget(getEffector());
				final MyTargetSelected my = new MyTargetSelected(getEffector().getObjectId(), 0);
				getEffected().sendPacket(my);
				getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getEffector());
		}
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		// nothing
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		// nothing
		return false;
	}
}
