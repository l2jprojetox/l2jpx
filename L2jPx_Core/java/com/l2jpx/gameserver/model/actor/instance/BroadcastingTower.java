package com.l2jpx.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import com.l2jpx.gameserver.data.manager.CastleManager;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.template.NpcTemplate;
import com.l2jpx.gameserver.model.olympiad.OlympiadManager;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.ItemList;

/**
 * An instance type extending {@link Folk}, used by Broadcasting Towers.<br>
 * <br>
 * Those NPCs allow {@link Player}s to spectate areas (sieges, olympiads).
 */
public final class BroadcastingTower extends Folk
{
	public BroadcastingTower(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("observe"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			final int x = Integer.parseInt(st.nextToken());
			final int y = Integer.parseInt(st.nextToken());
			final int z = Integer.parseInt(st.nextToken());
			final int cost = Integer.parseInt(st.nextToken());
			
			final boolean hasSummon = player.getSummon() != null;
			
			if (command.startsWith("observeSiege"))
			{
				// Summon check. Siege observe type got an appropriate message.
				if (hasSummon)
				{
					player.sendPacket(SystemMessageId.NO_OBSERVE_WITH_PET);
					return;
				}
				
				// Active siege must exist.
				if (CastleManager.getInstance().getActiveSiege(x, y, z) == null)
				{
					player.sendPacket(SystemMessageId.ONLY_VIEW_SIEGE);
					return;
				}
			}
			// Summon check for regular observe. No message on retail.
			else if (hasSummon)
				return;
			
			// Can't observe if under attack stance.
			if (player.isInCombat())
			{
				player.sendPacket(SystemMessageId.CANNOT_OBSERVE_IN_COMBAT);
				return;
			}
			
			// Olympiad registration check. No message on retail.
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
				return;
			
			// Adena check.
			if (!player.reduceAdena("Broadcast", cost, this, true))
				return;
			
			player.enterObserverMode(x, y, z);
			player.sendPacket(new ItemList(player, false));
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/observation/" + filename + ".htm";
	}
}