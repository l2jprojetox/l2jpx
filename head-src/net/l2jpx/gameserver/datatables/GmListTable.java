package net.l2jpx.gameserver.datatables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.L2GameServerPacket;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class GmListTable
{
	protected static final Logger LOGGER = Logger.getLogger(GmListTable.class);
	private static GmListTable instance;
	
	// L2PcInstance (GM) and Boolean (if is hidden or not)
	private Map<L2PcInstance, Boolean> gmList;
	
	private GmListTable()
	{
		gmList = new ConcurrentHashMap<>();
	}
	
	public static GmListTable getInstance()
	{
		if (instance == null)
		{
			instance = new GmListTable();
		}
		
		return instance;
	}
	
	public static void reload()
	{
		instance = null;
		getInstance();
	}
	
	public List<L2PcInstance> getAllGms(boolean includeHidden)
	{
		if (includeHidden)
		{
			return gmList.keySet().stream().collect(Collectors.toList());
		}
		
		List<L2PcInstance> notHiddenGMs = new ArrayList<>();
		
		gmList.forEach((gmPlayer, hidden) ->
		{
			if (!hidden)
			{
				notHiddenGMs.add(gmPlayer);
			}
		});
		
		return notHiddenGMs;
	}
	
	/**
	 * Add a L2PcInstance player to the Set gmList
	 * @param player
	 * @param hidden
	 */
	public void addGm(L2PcInstance player, boolean hidden)
	{
		gmList.put(player, hidden);
	}
	
	public void deleteGm(final L2PcInstance player)
	{
		gmList.remove(player);
	}
	
	/**
	 * GM will be displayed on clients gmlist
	 * @param player
	 */
	public void showGm(L2PcInstance player)
	{
		gmList.put(player, false);
	}
	
	/**
	 * GM will no longer be displayed on clients gmlist
	 * @param player
	 */
	public void hideGm(L2PcInstance player)
	{
		gmList.put(player, true);
	}
	
	public void sendListToPlayer(L2PcInstance player)
	{
		if (player.isGM())
		{
			List<L2PcInstance> gmPlayers = getAllGms(true);
			
			SystemMessage sm = new SystemMessage(SystemMessageId.GM_LIST_HEAD);
			player.sendPacket(sm);
			
			for (L2PcInstance gmPlayer : gmPlayers)
			{
				SystemMessage sm1 = new SystemMessage(SystemMessageId.GM_S1);
				sm1.addString(gmPlayer.getName());
				player.sendPacket(sm1);
			}
		}
		else
		{
			List<L2PcInstance> gmPlayers = getAllGms(false);
			
			if (gmPlayers == null || gmPlayers.isEmpty())
			{
				SystemMessage sm2 = new SystemMessage(SystemMessageId.THERE_ARE_NOT_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
				player.sendPacket(sm2);
			}
			else
			{
				for (L2PcInstance gmPlayer : gmPlayers)
				{
					SystemMessage sm1 = new SystemMessage(SystemMessageId.GM_S1);
					sm1.addString(gmPlayer.getName());
					player.sendPacket(sm1);
				}
			}
		}
	}
	
	public static void broadcastToGMs(L2GameServerPacket packet)
	{
		for (L2PcInstance gm : getInstance().getAllGms(true))
		{
			if (gm != null)
			{
				gm.sendPacket(packet);
			}
		}
	}
	
	public static void broadcastMessageToGMs(String message)
	{
		for (L2PcInstance gm : getInstance().getAllGms(true))
		{
			if (gm != null)
			{
				gm.sendPacket(SystemMessage.sendString(message));
			}
		}
	}
}
