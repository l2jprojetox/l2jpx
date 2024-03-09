package net.l2jpx.gameserver.skills;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author ProGramMoS, eX1steam, L2JFrozen An Env object is just a class to pass parameters to a calculator such as L2PcInstance, L2ItemInstance, Initial value.
 */

public final class Env
{
	public L2Character player;
	public L2Character target;
	public L2ItemInstance item;
	public L2Skill skill;
	public double value;
	public double baseValue;
	public boolean skillMastery = false;
	private L2Character character;
	private L2Character targetCharacter;
	
	public L2Character getCharacter()
	{
		return character;
	}
	
	public L2PcInstance getPlayer()
	{
		return character == null ? null : character.getActingPlayer();
	}
	
	public L2Character getTarget()
	{
		return targetCharacter;
	}
}
