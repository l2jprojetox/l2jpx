package com.px.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.skills.L2EffectFlag;
import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.instance.Chest;
import com.px.gameserver.skills.Env;

/**
 * This effect changes the target of the victim. It adds some random aggro aswell to force the monster to keep attacking. As the added aggro is random, the victim can often change of target.<br>
 * <br>
 * Only others mobs can fill the aggroList of the victim. For a more generic use, consider using EffectConfusion.
 * @author littlecrow, Tryskell
 */
public class EffectConfuseMob extends L2Effect
{
	public EffectConfuseMob(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.CONFUSE_MOB_ONLY;
	}
	
	/** Notify started */
	@Override
	public boolean onStart()
	{
		getEffected().startConfused();
		onActionTime();
		return true;
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
		List<Creature> targetList = new ArrayList<>();
		
		// Getting the possible targets
		for (Attackable obj : getEffected().getKnownType(Attackable.class))
		{
			// Only attackable NPCs are put in the list.
			if (!(obj instanceof Chest))
				targetList.add(obj);
		}
		
		// if there is no target, exit function
		if (targetList.isEmpty())
			return true;
		
		// Choosing randomly a new target
		WorldObject target = Rnd.get(targetList);
		
		// Attacking the target
		getEffected().setTarget(target);
		getEffected().getAI().setIntention(IntentionType.ATTACK, target);
		
		// Add aggro to that target aswell. The aggro power is random.
		int aggro = (5 + Rnd.get(5)) * getEffector().getLevel();
		((Attackable) getEffected()).addDamageHate((Creature) target, 0, aggro);
		
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.CONFUSED.getMask();
	}
}