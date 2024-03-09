package net.l2jpx.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.model.L2Attackable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.util.random.Rnd;

/**
 * @author littlecrow Implementation of the Confusion Effect
 */
final class EffectConfuseMob extends L2Effect
{
	
	public EffectConfuseMob(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSE_MOB_ONLY;
	}
	
	/** Notify started */
	@Override
	public void onStart()
	{
		getEffected().startConfused();
		onActionTime();
	}
	
	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopConfused(this);
	}
	
	@Override
	public boolean onActionTime()
	{
		List<L2Character> targetList = new ArrayList<>();
		
		// Getting the possible targets
		
		for (final L2Object obj : getEffected().getKnownList().getKnownObjects().values())
		{
			if (obj == null)
			{
				continue;
			}
			
			if (obj instanceof L2Attackable && obj != getEffected())
			{
				targetList.add((L2Character) obj);
			}
		}
		// if there is no target, exit function
		if (targetList.size() == 0)
		{
			return true;
		}
		
		// Choosing randomly a new target
		final int nextTargetIdx = Rnd.nextInt(targetList.size());
		final L2Object target = targetList.get(nextTargetIdx);
		
		// Attacking the target
		getEffected().setTarget(target);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		
		return true;
	}
}
