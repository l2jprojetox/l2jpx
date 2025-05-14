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
package custom.Transmog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.cache.HtmCache;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.enums.ChatType;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.Containers;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerItemAdd;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jpx.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jpx.gameserver.model.item.EtcItem;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.CreatureSay;
import org.l2jpx.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class Transmog extends AbstractNpcAI
{
	private static final int NPC = 900009;
	private static final Map<Integer, Map<Integer, Set<Integer>>> PLAYER_TRANSMOGS = new ConcurrentHashMap<>();
	private static final String LOAD_SQL = "SELECT itemId FROM character_transmogs WHERE owner=? ORDER BY itemId ASC;";
	private static final String SAVE_SQL = "REPLACE INTO character_transmogs (owner,itemId) VALUE (?,?)";
	
	private Transmog()
	{
		addStartNpc(NPC);
		addTalkId(NPC);
		addFirstTalkId(NPC);
		
		if (Config.ENABLE_TRANSMOG)
		{
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_ITEM_ADD, (OnPlayerItemAdd event) -> onPlayerItemAdd(event), this));
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGOUT, (OnPlayerLogout event) -> onPlayerLogout(event), this));
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("menu"))
		{
			return "900009-01.html";
		}
		else if (event.startsWith("rem"))
		{
			final String[] split = event.split("!");
			final String slot = split[1];
			final String page = split[2];
			if (isValidSlot(slot))
			{
				final Item item = player.getInventory().unEquipItemInBodySlot(getBodypart(slot));
				if (item != null)
				{
					final int transmogId = item.getTransmogId();
					if (transmogId > 0)
					{
						if (Config.TRANSMOG_REMOVE_COST > 0)
						{
							if (player.getAdena() < Config.TRANSMOG_REMOVE_COST)
							{
								player.getInventory().equipItem(item);
								player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_2);
								onEvent(slot + "!" + page, npc, player);
								return null;
							}
							
							player.reduceAdena("Transmog remove " + transmogId, Config.TRANSMOG_REMOVE_COST, player, true);
						}
						
						item.removeTransmog();
						player.sendMessage("You have removed your " + ItemData.getInstance().getTemplate(transmogId).getName() + " transmog.");
					}
					player.getInventory().equipItem(item);
					player.broadcastInfo();
				}
				else
				{
					player.sendMessage("No item equipped in this slot.");
				}
			}
			onEvent(slot + "!" + page, npc, player);
		}
		else if (event.startsWith("set"))
		{
			final String[] split = event.split("!");
			final String slot = split[1];
			final String page = split[2];
			final int transmogId = Integer.parseInt(split[3]);
			if (isValidSlot(slot))
			{
				final Integer bodypart = getBodypart(slot);
				final Item item = player.getInventory().unEquipItemInBodySlot(bodypart);
				if (item != null)
				{
					final ItemTemplate itemTemplate = ItemData.getInstance().getTemplate(transmogId);
					if (itemTemplate != null)
					{
						final Map<Integer, Set<Integer>> playerTransmogs = PLAYER_TRANSMOGS.getOrDefault(player.getObjectId(), Collections.emptyMap());
						final Set<Integer> itemIds = playerTransmogs.getOrDefault(bodypart, Collections.emptySet());
						if (itemIds.contains(transmogId))
						{
							if (Config.TRANSMOG_APPLY_COST > 0)
							{
								if (player.getAdena() < Config.TRANSMOG_APPLY_COST)
								{
									player.getInventory().equipItem(item);
									player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_2);
									onEvent(slot + "!" + page, npc, player);
									return null;
								}
								
								player.reduceAdena("Transmog apply " + transmogId, Config.TRANSMOG_APPLY_COST, player, true);
							}
							
							item.setTransmogId(transmogId);
							player.sendMessage("You have set your item transmog to " + itemTemplate.getName() + ".");
						}
					}
					player.getInventory().equipItem(item);
					player.broadcastInfo();
				}
				else
				{
					player.sendMessage("No item equipped in this slot.");
				}
			}
			onEvent(slot + "!" + page, npc, player);
		}
		else
		{
			final String[] split = event.split("!");
			final String slot = split[0];
			final int page = split.length > 1 ? Integer.parseInt(split[1]) : 1;
			
			final Map<Integer, Set<Integer>> playerTransmogs = PLAYER_TRANSMOGS.getOrDefault(player.getObjectId(), Collections.emptyMap());
			final Set<Integer> itemIds = playerTransmogs.getOrDefault(getBodypart(slot), Collections.emptySet());
			if (itemIds.isEmpty())
			{
				return "900009-02.html";
			}
			
			int total = 0;
			int counter = 0;
			final int pages = (int) Math.ceil((double) itemIds.size() / 16);
			final int maxItem = page * 16;
			final int minItem = maxItem - 16;
			String content = HtmCache.getInstance().getHtm(player, "data/scripts/custom/Transmog/900009-03.html");
			for (Integer itemId : itemIds)
			{
				final ItemTemplate itemTemplate = ItemData.getInstance().getTemplate(itemId);
				if (itemTemplate != null)
				{
					total++;
					if ((total < minItem) || (total > maxItem))
					{
						continue;
					}
					
					counter++;
					final String itemName = itemTemplate.getName();
					content = content.replace("%" + counter + "%", "<center><img src=\"" + itemTemplate.getIcon() + "\" width=32 height=32><br><a action=\"bypass Quest Transmog set!" + slot + "!" + page + "!" + itemTemplate.getDisplayId() + "\">" + itemName + "</a></center>");
				}
			}
			for (int i = 1; i < 17; i++)
			{
				content = content.replace("%" + i + "%", "");
			}
			content = content.replace("%title%", "Transmog (" + page + " of " + pages + ")");
			content = content.replace("%previous%", "<a action=\"bypass Quest Transmog " + slot + "!" + Math.max(1, (page - 1)) + "\">Previous</a>");
			content = content.replace("%next%", "<a action=\"bypass Quest Transmog " + slot + "!" + Math.min(pages, (page + 1)) + "\">Next</a>");
			content = content.replace("%remove%", "<a action=\"bypass Quest Transmog rem!" + slot + "!" + page + "\">Remove Transmog</a>");
			
			Util.sendCBHtml(player, content);
		}
		
		return super.onEvent(event, npc, player);
	}
	
	public void onPlayerItemAdd(OnPlayerItemAdd event)
	{
		final ItemTemplate itemTemplate = event.getItem().getTemplate();
		if ((itemTemplate instanceof EtcItem) || itemTemplate.isForNpc())
		{
			return;
		}
		
		final Integer bodypart = (int) itemTemplate.getBodyPart();
		if ((bodypart < ItemTemplate.SLOT_R_HAND) || (bodypart > ItemTemplate.SLOT_HAIRALL))
		{
			return;
		}
		
		if (Config.TRANSMOG_BANNED_ITEM_IDS.contains(itemTemplate.getId()))
		{
			return;
		}
		
		final Player player = event.getPlayer();
		final Integer playerObjectId = player.getObjectId();
		final Map<Integer, Set<Integer>> playerTransmogs = PLAYER_TRANSMOGS.getOrDefault(playerObjectId, new HashMap<>());
		final Set<Integer> itemIds = playerTransmogs.getOrDefault(bodypart, new HashSet<>());
		if (itemIds.add(itemTemplate.getDisplayId()))
		{
			playerTransmogs.putIfAbsent(bodypart, itemIds);
			PLAYER_TRANSMOGS.putIfAbsent(playerObjectId, playerTransmogs);
			player.sendPacket(new CreatureSay(null, ChatType.WHISPER, "[Transmog]", itemTemplate.getName() + " has been added to your appearance collection."));
		}
	}
	
	private void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		final String owner = Config.TRANSMOG_SHARE_ACCOUNT ? player.getAccountName() : String.valueOf(player.getObjectId());
		final Map<Integer, Set<Integer>> playerTransmogs = new HashMap<>();
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(LOAD_SQL))
		{
			statement.setString(1, owner);
			try (ResultSet result = statement.executeQuery())
			{
				while (result.next())
				{
					final int itemId = result.getInt(1);
					final ItemTemplate itemTemplate = ItemData.getInstance().getTemplate(itemId);
					if (itemTemplate != null)
					{
						final Integer bodypart = (int) itemTemplate.getBodyPart();
						final Set<Integer> itemIds = playerTransmogs.getOrDefault(bodypart, new HashSet<>());
						itemIds.add(itemId);
						playerTransmogs.putIfAbsent(bodypart, itemIds);
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.warning("Problem with Transmog: " + e.getMessage());
		}
		
		if (!playerTransmogs.isEmpty())
		{
			PLAYER_TRANSMOGS.put(player.getObjectId(), playerTransmogs);
		}
	}
	
	private void onPlayerLogout(OnPlayerLogout event)
	{
		final Player player = event.getPlayer();
		final Integer playerObjectId = player.getObjectId();
		final String owner = Config.TRANSMOG_SHARE_ACCOUNT ? player.getAccountName() : String.valueOf(player.getObjectId());
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SAVE_SQL))
		{
			for (Set<Integer> itemIds : PLAYER_TRANSMOGS.getOrDefault(playerObjectId, Collections.emptyMap()).values())
			{
				for (int itemId : itemIds)
				{
					statement.setString(1, owner);
					statement.setInt(2, itemId);
					statement.addBatch();
				}
			}
			statement.executeBatch();
		}
		catch (SQLException e)
		{
			LOGGER.warning("Problem with Transmog: " + e.getMessage());
		}
		
		PLAYER_TRANSMOGS.remove(playerObjectId);
	}
	
	private Integer getBodypart(String event)
	{
		switch (event)
		{
			case "R_HAND":
			{
				return ItemTemplate.SLOT_R_HAND;
			}
			case "L_HAND":
			{
				return ItemTemplate.SLOT_L_HAND;
			}
			case "GLOVES":
			{
				return ItemTemplate.SLOT_GLOVES;
			}
			case "CHEST":
			{
				return ItemTemplate.SLOT_CHEST;
			}
			case "LEGS":
			{
				return ItemTemplate.SLOT_LEGS;
			}
			case "FEET":
			{
				return ItemTemplate.SLOT_FEET;
			}
			case "BACK":
			{
				return ItemTemplate.SLOT_BACK;
			}
			case "LR_HAND":
			{
				return ItemTemplate.SLOT_LR_HAND;
			}
			case "FULL_ARMOR":
			{
				return ItemTemplate.SLOT_FULL_ARMOR;
			}
			case "HAIRALL":
			{
				return ItemTemplate.SLOT_HAIRALL;
			}
			case "HAIR2":
			{
				return ItemTemplate.SLOT_HAIR2;
			}
			case "HAIR":
			{
				return ItemTemplate.SLOT_HAIR;
			}
			default:
			{
				return -1;
			}
		}
	}
	
	private boolean isValidSlot(String slot)
	{
		switch (slot)
		{
			case "R_HAND":
			case "L_HAND":
			case "GLOVES":
			case "CHEST":
			case "LEGS":
			case "FEET":
			case "BACK":
			case "LR_HAND":
			case "FULL_ARMOR":
			case "HAIRALL":
			case "HAIR2":
			case "HAIR":
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Transmog();
	}
}
