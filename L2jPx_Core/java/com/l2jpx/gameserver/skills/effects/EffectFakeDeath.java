package com.l2jpx.gameserver.skills.effects;

import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectFakeDeath extends AbstractEffect
{
	public EffectFakeDeath(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.FAKE_DEATH;
	}
	
	@Override
	public boolean onStart()
	{
		final Player player = (Player) getEffected();
		player.startFakeDeath();
		return true;
	}
	
	@Override
	public void onExit()
	{
		final Player player = (Player) getEffected();
		player.stopFakeDeath(true);
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
			return false;
		
		if (getTemplate().getValue() > getEffected().getStatus().getMp())
		{
			getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}
		
		getEffected().getStatus().reduceMp(getTemplate().getValue());
		return true;
	}
}