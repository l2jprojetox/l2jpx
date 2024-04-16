package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.AbnormalEffect;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

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