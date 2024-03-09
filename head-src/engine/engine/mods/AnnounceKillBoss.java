package engine.engine.mods;

import net.l2jpx.gameserver.model.actor.instance.L2GrandBossInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.model.actor.instance.L2RaidBossInstance;
import net.l2jpx.gameserver.network.clientpackets.Say2;

import engine.data.properties.ConfigData;
import engine.engine.AbstractMod;
import engine.holders.objects.CharacterHolder;
import engine.util.Util;
import engine.util.UtilMessage;

/**
 * @author fissban
 */
public class AnnounceKillBoss extends AbstractMod
{
	public AnnounceKillBoss()
	{
		registerMod(ConfigData.ENABLE_AnnounceKillBoss);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!Util.areObjectType(L2PlayableInstance.class, killer))
		{
			return;
		}
		
		if (Util.areObjectType(L2RaidBossInstance.class, victim))
		{
			UtilMessage.toAllOnline(Say2.TELL, ConfigData.ANNOUNCE_KILL_BOSS.replace("%s1", killer.getInstance().getActingPlayer().getName()).replace("%s2", victim.getInstance().getName()));
			return;
		}
		
		if (Util.areObjectType(L2GrandBossInstance.class, victim))
		{
			UtilMessage.toAllOnline(Say2.TELL, ConfigData.ANNOUNCE_KILL_GRANDBOSS.replace("%s1", killer.getInstance().getActingPlayer().getName()).replace("%s2", victim.getInstance().getName()));
			return;
		}
	}
}
