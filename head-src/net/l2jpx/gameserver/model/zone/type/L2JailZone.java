package net.l2jpx.gameserver.model.zone.type;

import net.l2jpx.Config;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.zone.L2ZoneType;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.thread.ThreadPoolManager;

/**
 * A jail zone
 * @author durgus update Harpun
 */
public class L2JailZone extends L2ZoneType
{
	public L2JailZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			player.setInsideZone(L2Character.ZONE_JAIL, true);
			player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
			if (Config.JAIL_IS_PVP)
			{
				player.setInsideZone(L2Character.ZONE_PVP, true);
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE));
			}
			else
			{
				player.setInsideZone(L2Character.ZONE_PEACE, true);
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			player.setInsideZone(L2Character.ZONE_JAIL, false);
			player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);
			if (Config.JAIL_IS_PVP)
			{
				player.setInsideZone(L2Character.ZONE_PVP, false);
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE));
			}
			else
			{
				player.setInsideZone(L2Character.ZONE_PEACE, false);
			}
			
			if (player.isInJail())
			{
				// when a player wants to exit jail even if he is still jailed, teleport him back to jail
				player.sendMessage("You can't cheat your way out of here. You must wait until your jail time is over.");
				ThreadPoolManager.getInstance().scheduleGeneral(() ->
				{
					player.teleToLocation(-114356, -249645, -2984);
				}, 2000);
			}
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
}