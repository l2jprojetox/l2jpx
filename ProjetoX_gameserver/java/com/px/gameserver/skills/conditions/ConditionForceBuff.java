package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.L2Effect;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.effects.EffectFusion;

/**
 * The Class ConditionForceBuff.
 * @author kombat, Forsaiken
 */
public class ConditionForceBuff extends Condition
{
	private static final short BATTLE_FORCE = 5104;
	private static final short SPELL_FORCE = 5105;
	
	private final byte[] _forces;
	
	/**
	 * Instantiates a new condition force buff.
	 * @param forces the forces
	 */
	public ConditionForceBuff(byte[] forces)
	{
		_forces = forces;
	}
	
	/**
	 * Test impl.
	 * @param env the env
	 * @return true, if successful
	 * @see com.px.gameserver.skills.conditions.Condition#testImpl(com.px.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (_forces[0] > 0)
		{
			L2Effect force = env.getCharacter().getFirstEffect(BATTLE_FORCE);
			if (force == null || ((EffectFusion) force)._effect < _forces[0])
				return false;
		}
		
		if (_forces[1] > 0)
		{
			L2Effect force = env.getCharacter().getFirstEffect(SPELL_FORCE);
			if (force == null || ((EffectFusion) force)._effect < _forces[1])
				return false;
		}
		return true;
	}
}