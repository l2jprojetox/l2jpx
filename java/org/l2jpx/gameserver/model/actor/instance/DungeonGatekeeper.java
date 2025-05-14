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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.sevensigns.SevenSigns;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;

public class DungeonGatekeeper extends Npc
{
	/**
	 * Creates a dungeon gatekeeper.
	 * @param template the dungeon gatekeeper NPC template
	 */
	public DungeonGatekeeper(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.DungeonGatekeeper);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken(); // Get actual command
		String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
		final int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
		final int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
		final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
		final boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
		final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
		if (actualCommand.startsWith("necro"))
		{
			boolean canPort = true;
			if (isSealValidationPeriod)
			{
				if ((compWinner == SevenSigns.CABAL_DAWN) && ((playerCabal != SevenSigns.CABAL_DAWN) || (sealAvariceOwner != SevenSigns.CABAL_DAWN)))
				{
					player.sendPacket(SystemMessageId.ONLY_A_LORD_OF_DAWN_MAY_USE_THIS);
					canPort = false;
				}
				else if ((compWinner == SevenSigns.CABAL_DUSK) && ((playerCabal != SevenSigns.CABAL_DUSK) || (sealAvariceOwner != SevenSigns.CABAL_DUSK)))
				{
					player.sendPacket(SystemMessageId.ONLY_A_REVOLUTIONARY_OF_DUSK_MAY_USE_THIS);
					canPort = false;
				}
				else if ((compWinner == SevenSigns.CABAL_NULL) && (playerCabal != SevenSigns.CABAL_NULL))
				{
					canPort = true;
				}
				else if (playerCabal == SevenSigns.CABAL_NULL)
				{
					canPort = false;
				}
			}
			else
			{
				if (playerCabal == SevenSigns.CABAL_NULL)
				{
					canPort = false;
				}
			}
			
			if (!canPort)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				filename += "necro_no.htm";
				html.setFile(player, filename);
				player.sendPacket(html);
			}
			else
			{
				doTeleport(player, Integer.parseInt(st.nextToken()));
				player.setIn7sDungeon(true);
			}
		}
		else if (actualCommand.startsWith("cata"))
		{
			boolean canPort = true;
			if (isSealValidationPeriod)
			{
				if ((compWinner == SevenSigns.CABAL_DAWN) && ((playerCabal != SevenSigns.CABAL_DAWN) || (sealGnosisOwner != SevenSigns.CABAL_DAWN)))
				{
					player.sendPacket(SystemMessageId.ONLY_A_LORD_OF_DAWN_MAY_USE_THIS);
					canPort = false;
				}
				else if ((compWinner == SevenSigns.CABAL_DUSK) && ((playerCabal != SevenSigns.CABAL_DUSK) || (sealGnosisOwner != SevenSigns.CABAL_DUSK)))
				{
					player.sendPacket(SystemMessageId.ONLY_A_REVOLUTIONARY_OF_DUSK_MAY_USE_THIS);
					canPort = false;
				}
				else if ((compWinner == SevenSigns.CABAL_NULL) && (playerCabal != SevenSigns.CABAL_NULL))
				{
					canPort = true;
				}
				else if (playerCabal == SevenSigns.CABAL_NULL)
				{
					canPort = false;
				}
			}
			else
			{
				if (playerCabal == SevenSigns.CABAL_NULL)
				{
					canPort = false;
				}
			}
			
			if (!canPort)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				filename += "cata_no.htm";
				html.setFile(player, filename);
				player.sendPacket(html);
			}
			else
			{
				doTeleport(player, Integer.parseInt(st.nextToken()));
				player.setIn7sDungeon(true);
			}
		}
		else if (actualCommand.startsWith("exit"))
		{
			doTeleport(player, Integer.parseInt(st.nextToken()));
			player.setIn7sDungeon(false);
		}
		else if (actualCommand.startsWith("goto"))
		{
			doTeleport(player, Integer.parseInt(st.nextToken()));
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	private void doTeleport(Player player, int value)
	{
		final List<Object> locationDetails = TeleportLocations.getTeleportLocation(value);
		if (locationDetails != null)
		{
			int locX = (int) locationDetails.get(1);
			int locY = (int) locationDetails.get(2);
			int locZ = (int) locationDetails.get(3);
			
			// Use the location details as needed.
			if (player.isAlikeDead())
			{
				return;
			}
			
			player.teleToLocation(locX, locY, locZ);
		}
		else
		{
			LOGGER.warning("No teleport destination with id:" + value);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String getHtmlPath(int npcId, int value, Player player)
	{
		String pom = "";
		if (value == 0)
		{
			pom = Integer.toString(npcId);
		}
		else
		{
			pom = npcId + "-" + value;
		}
		
		return "data/html/teleporter/" + pom + ".htm";
	}
	
	public class TeleportLocations
	{
		private static final Map<Integer, List<Object>> teleportLocationMap = new HashMap<>();
		static
		{
			addTeleportLocation(250, "Cat Heretics Entrance", 42514, 143917, -5385, 0, false, 57);
			addTeleportLocation(251, "Cat Heretics Exit", 42514, 143917, -5385, 0, false, 57);
			addTeleportLocation(252, "Cat Branded Entrance", 46217, 170290, -4983, 0, false, 57);
			addTeleportLocation(253, "Cat Branded Exit", 45770, 170299, -4985, 0, false, 57);
			addTeleportLocation(254, "Cat Apostate Entrance", -20230, -250780, -8168, 0, false, 57);
			addTeleportLocation(255, "Cat Apostate Exit", 77225, 78362, -5119, 0, false, 57);
			addTeleportLocation(256, "Cat Witch Entrance", 140404, 79678, -5431, 0, false, 57);
			addTeleportLocation(257, "Cat Witch Exit", 139965, 79678, -5433, 0, false, 57);
			addTeleportLocation(258, "Cat DarkOmen Entrance", -19500, 13508, -4905, 0, false, 57);
			addTeleportLocation(259, "Cat DarkOmen Exit", -19931, 13502, -4905, 0, false, 57);
			addTeleportLocation(260, "Cat ForbiddenPath Entrance", 12521, -248481, -9585, 0, false, 57);
			addTeleportLocation(261, "Cat ForbiddenPath Exit", 113429, 84540, -6545, 0, false, 57);
			addTeleportLocation(262, "Necro Sacrifice Entrance", -41570, 209785, -5089, 0, false, 57);
			addTeleportLocation(263, "Necro Sacrifice Exit", -41567, 209292, -5091, 0, false, 57);
			addTeleportLocation(264, "Necro Pilgrims Entrance", 45251, 123890, -5415, 0, false, 57);
			addTeleportLocation(265, "Necro Pilgrims Exit", 45250, 124366, -5417, 0, false, 57);
			addTeleportLocation(266, "Necro Worshippers Entrance", 111273, 174015, -5417, 0, false, 57);
			addTeleportLocation(267, "Necro Worshippers Exit", 110818, 174010, -5443, 0, false, 57);
			addTeleportLocation(268, "Necro Patriots Entrance", -21726, 77385, -5177, 0, false, 57);
			addTeleportLocation(269, "Necro Patriots Exit", -22197, 77369, -5177, 0, false, 57);
			addTeleportLocation(270, "Necro Ascetics Entrance", -52254, 79103, -4743, 0, false, 57);
			addTeleportLocation(271, "Necro Ascetics Exit", -52716, 79106, -4745, 0, false, 57);
			addTeleportLocation(272, "Necro Martyrs Entrance", 118308, 132800, -4833, 0, false, 57);
			addTeleportLocation(273, "Necro Martyrs Exit", 117793, 132810, -4835, 0, false, 57);
			addTeleportLocation(274, "Necro Saints Entrance", 83000, 209213, -5443, 0, false, 57);
			addTeleportLocation(275, "Necro Saints Exit", 82608, 209225, -5443, 0, false, 57);
			addTeleportLocation(276, "Necro Disciples Entrance", 172251, -17605, -4903, 0, false, 57);
			addTeleportLocation(277, "Necro Disciples Exit", 171902, -17595, -4905, 0, false, 57);
		}
		
		private static void addTeleportLocation(int id, String description, int locX, int locY, int locZ, int price, boolean forNoble, int itemId)
		{
			final List<Object> locationDetails = new ArrayList<>();
			locationDetails.add(description);
			locationDetails.add(locX);
			locationDetails.add(locY);
			locationDetails.add(locZ);
			locationDetails.add(price);
			locationDetails.add(forNoble);
			locationDetails.add(itemId);
			teleportLocationMap.put(id, locationDetails);
		}
		
		public static List<Object> getTeleportLocation(int id)
		{
			return teleportLocationMap.get(id);
		}
	}
}