package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author ZaKaX (Ghost @ L2D)
 */
public class EffectClanGate extends L2Effect
{
	public EffectClanGate(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public void onStart()
	{
		getEffected().startAbnormalEffect(L2Character.ABNORMAL_EFFECT_MAGIC_CIRCLE);
		if (getEffected() instanceof L2PcInstance)
		{
			final L2Clan clan = ((L2PcInstance) getEffected()).getClan();
			if (clan != null)
			{
				final SystemMessage msg = new SystemMessage(SystemMessageId.COURT_MAGICIAN_CREATED_PORTAL);
				clan.broadcastToOnlineMembersExcept(msg, ((L2PcInstance) getEffected()));
			}
		}
		
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopAbnormalEffect(L2Character.ABNORMAL_EFFECT_MAGIC_CIRCLE);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CLAN_GATE;
	}
}