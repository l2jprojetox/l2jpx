package com.l2jpx.gameserver.skills.effects;

import com.l2jpx.gameserver.enums.skills.AbnormalEffect;
import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.pledge.Clan;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectClanGate extends AbstractEffect
{
	public EffectClanGate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startAbnormalEffect(AbnormalEffect.MAGIC_CIRCLE);
		
		if (getEffected() instanceof Player)
		{
			final Clan clan = ((Player) getEffected()).getClan();
			if (clan != null)
				clan.broadcastToMembersExcept(((Player) getEffected()), SystemMessage.getSystemMessage(SystemMessageId.COURT_MAGICIAN_CREATED_PORTAL));
		}
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopAbnormalEffect(AbnormalEffect.MAGIC_CIRCLE);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CLAN_GATE;
	}
}