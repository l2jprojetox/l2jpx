/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.model.actor.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.l2jpx.gameserver.data.xml.TeleporterData;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.enums.TeleportType;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.holders.TeleporterQuestRecommendationHolder;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.teleporter.TeleportHolder;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * @author NightMarez
 */
public class Teleporter extends Npc
{
	private static final Logger LOGGER = Logger.getLogger(Teleporter.class.getName());
	
	private static final Map<Integer, List<TeleporterQuestRecommendationHolder>> QUEST_RECOMENDATIONS = new HashMap<>();
	// static
	// {
	// QUEST_RECOMENDATIONS.put(30848, new ArrayList<>());
	// QUEST_RECOMENDATIONS.get(30848).add(new TeleporterQuestRecommendationHolder(30848, "Q00561_BasicMissionHarnakUndergroundRuins", new int[]{-1}, "30848-Q561-Q562"));
	// QUEST_RECOMENDATIONS.get(30848).add(new TeleporterQuestRecommendationHolder(30848, "Q00562_BasicMissionAltarOfEvil", new int[]{-1}, "30848-561-562"));
	// }
	
	public Teleporter(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.Teleporter);
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster() || super.isAutoAttackable(attacker);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		// Process bypass
		final StringTokenizer st = new StringTokenizer(command, " ");
		switch (st.nextToken())
		{
			case "showNoblesSelect":
			{
				sendHtmlMessage(player, "data/html/teleporter/" + (player.isNoble() ? "nobles_select" : "not_nobles") + ".htm");
				break;
			}
			case "showTeleports":
			{
				final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.NORMAL.name();
				final TeleportHolder holder = TeleporterData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warning(player + " requested show teleports for list with name " + listName + " at NPC " + getId() + "!");
					return;
				}
				holder.showTeleportList(player, this);
				break;
			}
			case "showTeleportsHunting":
			{
				final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.HUNTING.name();
				final TeleportHolder holder = TeleporterData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warning(player + " requested show teleports for hunting list with name " + listName + " at NPC " + getId() + "!");
					return;
				}
				holder.showTeleportList(player, this);
				break;
			}
			case "teleport":
			{
				// Check for required count of params.
				if (st.countTokens() != 2)
				{
					LOGGER.warning(player + " send unhandled teleport command: " + command);
					return;
				}
				
				final String listName = st.nextToken();
				final TeleportHolder holder = TeleporterData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warning(player + " requested unknown teleport list: " + listName + " for npc: " + getId() + "!");
					return;
				}
				holder.doTeleport(player, this, parseNextInt(st, -1));
				break;
			}
			case "chat":
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException | NumberFormatException ignored)
				{
				}
				showChatWindow(player, val);
				break;
			}
			default:
			{
				super.onBypassFeedback(player, command);
			}
		}
	}
	
	private int parseNextInt(StringTokenizer st, int defaultVal)
	{
		if (st.hasMoreTokens())
		{
			final String token = st.nextToken();
			if (Util.isDigit(token))
			{
				return Integer.parseInt(token);
			}
		}
		return defaultVal;
	}
	
	@Override
	public String getHtmlPath(int npcId, int value, Player player)
	{
		String pom;
		if (value == 0)
		{
			pom = String.valueOf(npcId);
			if ((player != null) && QUEST_RECOMENDATIONS.containsKey(npcId))
			{
				CHECK: for (TeleporterQuestRecommendationHolder rec : QUEST_RECOMENDATIONS.get(npcId))
				{
					final QuestState qs = player.getQuestState(rec.getQuestName());
					if ((qs != null) && qs.isStarted())
					{
						for (int cond : rec.getConditions())
						{
							if ((cond == -1) || qs.isCond(cond))
							{
								pom = rec.getHtml();
								break CHECK;
							}
						}
					}
				}
			}
		}
		else
		{
			pom = (npcId + "-" + value);
		}
		return "data/html/teleporter/" + pom + ".htm";
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		// Teleporter isn't on castle ground
		if (CastleManager.getInstance().getCastle(this) == null)
		{
			super.showChatWindow(player);
			return;
		}
		
		// Teleporter is on castle ground
		String filename = "data/html/teleporter/castleteleporter-no.htm";
		if ((player.getClan() != null) && (getCastle().getOwnerId() == player.getClanId())) // Clan owns castle
		{
			filename = getHtmlPath(getId(), 0, player); // Owner message window
		}
		else if (getCastle().getSiege().isInProgress()) // Teleporter is busy due siege
		{
			filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
		}
		sendHtmlMessage(player, filename);
	}
	
	private void sendHtmlMessage(Player player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
}
