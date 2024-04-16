package com.px.gameserver.handler.admincommandhandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import com.px.commons.data.Pagination;
import com.px.commons.lang.StringUtil;

import com.px.gameserver.handler.IAdminCommandHandler;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.GameClient;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminFind implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_find"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.startsWith("admin_find"))
		{
			final int paramCount = st.countTokens();
			if (paramCount < 2)
			{
				listPlayers(player, 1, "");
				return;
			}
			
			final String param = st.nextToken();
			final String nameIpOrPage = st.nextToken();
			final String search = (paramCount >= 3) ? st.nextToken().toLowerCase() : "";
			
			switch (param)
			{
				case "player":
					try
					{
						if (StringUtil.isDigit(nameIpOrPage))
							listPlayers(player, Integer.parseInt(nameIpOrPage), search);
						else
							listPlayers(player, 1, nameIpOrPage);
					}
					catch (Exception e)
					{
						player.sendMessage("Usage: //find player name");
						listPlayers(player, 1, "");
					}
					break;
				
				case "ip":
					try
					{
						listPlayersPerIp(player, nameIpOrPage);
					}
					catch (Exception e)
					{
						player.sendMessage("Usage: //find ip 111.222.333.444");
						listPlayers(player, 1, "");
					}
					break;
				
				case "account":
					try
					{
						listPlayersPerAccount(player, nameIpOrPage);
					}
					catch (Exception e)
					{
						player.sendMessage("Usage: //find account name");
						listPlayers(player, 1, "");
					}
					break;
				
				case "dualbox":
					try
					{
						final int multibox = Integer.parseInt(nameIpOrPage);
						if (multibox < 1)
						{
							player.sendMessage("Usage: //find dualbox [number > 0]");
							return;
						}
						
						listDualbox(player, multibox);
					}
					catch (Exception e)
					{
						listDualbox(player, 2);
					}
					break;
				
				default:
					listPlayers(player, 1, "");
					break;
			}
		}
	}
	
	/**
	 * Find all {@link Player}s and paginate them, then send back the results to the {@link Player}.
	 * @param player : The {@link Player} to send back results.
	 * @param page : The page to show.
	 * @param search : The {@link String} used as search.
	 */
	private static void listPlayers(Player player, int page, String search)
	{
		// Generate data.
		final Pagination<Player> list = new Pagination<>(World.getInstance().getPlayers().stream(), page, PAGE_LIMIT_12, p -> p.getName().toLowerCase().contains(search));
		list.append("<html><body>");
		
		list.generateSearch("bypass admin_find player", 45);
		list.append("<br1><table width=280 height=26><tr><td width=140>Name</td><td width=120>Class</td><td width=20>Lvl</td></tr></table>");
		
		for (Player targetPlayer : list)
		{
			list.append(((list.indexOf(targetPlayer) % 2) == 0 ? "<table width=280 height=22 bgcolor=000000><tr>" : "<table width=280><tr>"));
			list.append("<td width=140><a action=\"bypass -h admin_debug ", targetPlayer.getName(), "\">", targetPlayer.getName(), "</a></td><td width=120>", targetPlayer.getTemplate().getClassName(), "</td><td width=20>", targetPlayer.getStatus().getLevel(), "</td>");
			list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		}
		list.generateSpace(22);
		list.generatePages("bypass admin_find player %page% " + search);
		list.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(list.getContent());
		player.sendPacket(html);
	}
	
	/**
	 * List all {@link Player}s attached to an IP and send results to the {@link Player} set as parameter.
	 * @param player : The {@link Player} who requested the action.
	 * @param ipAdress : The {@link String} used as tested IP.
	 * @throws IllegalArgumentException if the IP is malformed.
	 */
	private static void listPlayersPerIp(Player player, String ipAdress) throws IllegalArgumentException
	{
		boolean findDisconnected = false;
		
		if (ipAdress.equals("disconnected"))
			findDisconnected = true;
		else
		{
			if (!ipAdress.matches("^(?:(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))$"))
				throw new IllegalArgumentException("Malformed IPv4 number");
		}
		
		int charactersFound = 0;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/ipfind.htm");
		
		final StringBuilder sb = new StringBuilder(1000);
		for (Player worldPlayer : World.getInstance().getPlayers())
		{
			final GameClient client = worldPlayer.getClient();
			if (client.isDetached())
			{
				if (!findDisconnected)
					continue;
			}
			else
			{
				if (findDisconnected)
					continue;
				
				if (!client.getConnection().getInetAddress().getHostAddress().equals(ipAdress))
					continue;
			}
			
			StringUtil.append(sb, "<tr><td><a action=\"bypass -h admin_debug ", worldPlayer.getName(), "\">", worldPlayer.getName(), "</a></td><td>", worldPlayer.getTemplate().getClassName(), "</td><td>", worldPlayer.getStatus().getLevel(), "</td></tr>");
			
			if (charactersFound++ > 20)
				break;
		}
		
		if (charactersFound > 20)
			html.replace("%number%", "more than 20");
		else
			html.replace("%number%", charactersFound);
		
		html.replace("%ip%", ipAdress);
		html.replace("%results%", sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * List all characters names attached to an ONLINE {@link Player} name and send results to the {@link Player} set as parameter.
	 * @param player : The {@link Player} who requested the action.
	 * @param name : The {@link String} name to test.
	 */
	private static void listPlayersPerAccount(Player player, String name)
	{
		final Player worldPlayer = World.getInstance().getPlayer(name);
		if (worldPlayer == null)
		{
			player.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/accountinfo.htm");
		html.replace("%name%", name);
		html.replace("%characters%", String.join("<br1>", worldPlayer.getAccountChars().values()));
		html.replace("%account%", worldPlayer.getAccountName());
		player.sendPacket(html);
	}
	
	/**
	 * Test multiboxing {@link Player}s and send results to the {@link Player} set as parameter.
	 * @param player : The {@link Player} who requested the action.
	 * @param multibox : The tested value to trigger multibox.
	 */
	private static void listDualbox(Player player, int multibox)
	{
		final Map<String, List<Player>> ips = new HashMap<>();
		final Map<String, Integer> dualboxIPs = new HashMap<>();
		
		for (Player worldPlayer : World.getInstance().getPlayers())
		{
			final GameClient client = worldPlayer.getClient();
			if (client == null || client.isDetached())
				continue;
			
			final String ip = client.getConnection().getInetAddress().getHostAddress();
			
			final List<Player> list = ips.computeIfAbsent(ip, k -> new ArrayList<>());
			list.add(worldPlayer);
			
			if (list.size() >= multibox)
			{
				Integer count = dualboxIPs.get(ip);
				if (count == null)
					dualboxIPs.put(ip, multibox);
				else
					dualboxIPs.put(ip, count++);
			}
		}
		
		final List<String> keys = dualboxIPs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).map(Map.Entry::getKey).collect(Collectors.toList());
		
		final StringBuilder sb = new StringBuilder();
		for (String dualboxIP : keys)
			StringUtil.append(sb, "<a action=\"bypass -h admin_find ip ", dualboxIP, "\">", dualboxIP, " (", dualboxIPs.get(dualboxIP), ")</a><br1>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/dualbox.htm");
		html.replace("%multibox%", multibox);
		html.replace("%results%", sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}