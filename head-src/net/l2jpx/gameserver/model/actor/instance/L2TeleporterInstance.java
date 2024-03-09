package net.l2jpx.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.TeleportLocationTable;
import net.l2jpx.gameserver.managers.CastleManager;
import net.l2jpx.gameserver.managers.GrandBossManager;
import net.l2jpx.gameserver.managers.SiegeManager;
import net.l2jpx.gameserver.managers.TownManager;
import net.l2jpx.gameserver.model.L2TeleportLocation;
import net.l2jpx.gameserver.model.zone.type.L2BossZone;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.L2NpcTemplate;

/**
 * @author NightMarez
 * @author ReynalDev
 */
public class L2TeleporterInstance extends L2FolkInstance
{
	private static final int COND_ALL_FALSE = 0;
	private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	private static final int COND_OWNER = 2;
	private static final int COND_REGULAR = 3;
	
	public L2TeleporterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (player == null)
		{
			return;
		}
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("You are not allowed to use a teleport while registered in olympiad game.");
			return;
		}
		
		int condition = validateCondition(player);
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("goto"))
		{
			int npcId = getTemplate().npcId;
			
			switch (npcId)
			{
				case 31095: //
				case 31096: //
				case 31097: //
				case 31098: // Enter Necropolises
				case 31099: //
				case 31100: //
				case 31101: //
				case 31102: //
				
				case 31114: //
				case 31115: //
				case 31116: // Enter Catacombs
				case 31117: //
				case 31118: //
				case 31119: //
					player.setIsIn7sDungeon(true);
					break;
				case 31103: //
				case 31104: //
				case 31105: //
				case 31106: // Exit Necropolises
				case 31107: //
				case 31108: //
				case 31109: //
				case 31110: //
				
				case 31120: //
				case 31121: //
				case 31122: // Exit Catacombs
				case 31123: //
				case 31124: //
				case 31125: //
					player.setIsIn7sDungeon(false);
					break;
			}
			
			if (st.countTokens() <= 0)
			{
				return;
			}
			
			int whereTo = Integer.parseInt(st.nextToken());
			
			if (condition == COND_REGULAR)
			{
				doTeleport(player, whereTo);
				return;
			}
			else if (condition == COND_OWNER)
			{
				int minPrivilegeLevel = 0; // NOTE: Replace 0 with highest level when privilege level is implemented
				if (st.countTokens() >= 1)
				{
					minPrivilegeLevel = Integer.parseInt(st.nextToken());
				}
				
				if (10 >= minPrivilegeLevel)
				{
					doTeleport(player, whereTo);
				}
				else
				{
					player.sendMessage("You don't have the sufficient access level to teleport there.");
				}
				
				return;
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/teleporter/" + pom + ".htm";
	}
	
	@Override
	public void showChatWindow(L2PcInstance player)
	{
		String filename = "data/html/teleporter/castleteleporter-no.htm";
		
		int condition = validateCondition(player);
		if (condition == COND_REGULAR)
		{
			super.showChatWindow(player);
			return;
		}
		else if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
			{
				filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
			}
			else if (condition == COND_OWNER)
			{
				filename = getHtmlPath(getNpcId(), 0); // Owner message window
			}
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
	
	private void doTeleport(L2PcInstance player, int teleportLocationId)
	{
		L2TeleportLocation teleportLocation = TeleportLocationTable.getInstance().getTemplate(teleportLocationId);
		
		if (teleportLocation != null)
		{
			// you cannot teleport to village that is in siege
			if (!SiegeManager.getInstance().is_teleport_to_siege_allowed() && SiegeManager.getInstance().getSiege(teleportLocation.getLocX(), teleportLocation.getLocY(), teleportLocation.getLocZ()) != null && !player.isNoble())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE));
				return;
			}
			else if (!SiegeManager.getInstance().is_teleport_to_siege_town_allowed() && TownManager.getInstance().townHasCastleInSiege(teleportLocation.getLocX(), teleportLocation.getLocY()) && !player.isNoble())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE));
				return;
			}
			else if (!player.isGM() && !Config.FLAGED_PLAYER_CAN_USE_GK && player.getPvpFlag() > 0)
			{
				player.sendMessage("Don't run from PvP! You will be able to use the teleporter only after your flag is gone.");
				return;
			}
			else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0) // karma
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Go away, you're not welcome here.");
				player.sendPacket(sm);
				return;
			}
			else if (teleportLocation.isForNoble() && !player.isNoble())
			{
				String filename = "data/html/teleporter/nobleteleporter-no.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (player.isAlikeDead())
			{
				player.sendMessage("You can't use teleport when you are dead.");
				return;
			}
			else if (player.isSitting())
			{
				player.sendMessage("You can't use teleport when you are sitting.");
				return;
			}
			else if (teleportLocation.getTeleId() == 9982 && teleportLocation.getTeleId() == 9983 && teleportLocation.getTeleId() == 9984 && getNpcId() == 30483 && player.getLevel() >= Config.CRUMA_TOWER_LEVEL_RESTRICT)
			{
				// Chars level XX can't enter in Cruma Tower. Retail: level 56 and above
				int maxlvl = Config.CRUMA_TOWER_LEVEL_RESTRICT;
				
				String filename = "data/html/teleporter/30483-biglvl.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%allowedmaxlvl%", "" + maxlvl + "");
				player.sendPacket(html);
				return;
			}
			// Lilith and Anakim have BossZone, so players must be allowed to enter
			else if (teleportLocation.getTeleId() == 450)
			{
				L2BossZone zone = GrandBossManager.getInstance().getZone(teleportLocation.getLocX(), teleportLocation.getLocY(), teleportLocation.getLocZ());
				zone.allowPlayerEntry(player, 300);
				player.teleToLocation(teleportLocation.getLocX(), teleportLocation.getLocY(), teleportLocation.getLocZ(), true);
			}
			else if(!teleportLocation.isForNoble() && player.getLevel() <= Config.ALT_TELEPORT_FREE_UNTIL_LEVEL)
			{
				player.teleToLocation(teleportLocation.getLocX(), teleportLocation.getLocY(), teleportLocation.getLocZ(), true);
			}
			else if (!teleportLocation.isForNoble() && player.destroyItemByItemId("Teleport", teleportLocation.getItemId(), teleportLocation.getPrice(), this, false))
			{
				player.setTelepotItemInformatio(teleportLocation.getItemId(), teleportLocation.getPrice());
				player.teleToLocation(teleportLocation.getLocX(), teleportLocation.getLocY(), teleportLocation.getLocZ(), true);
			}
			else if (teleportLocation.isForNoble() && player.destroyItemByItemId("Noble Teleport", teleportLocation.getItemId(), teleportLocation.getPrice(), this, false))
			{
				player.setTelepotItemInformatio(teleportLocation.getItemId(), teleportLocation.getPrice());
				player.teleToLocation(teleportLocation.getLocX(), teleportLocation.getLocY(), teleportLocation.getLocZ(), true);
			}
		}
		else
		{
			LOGGER.warn("No teleport destination with id:" + teleportLocationId);
		}
	}
	
	private int validateCondition(L2PcInstance player)
	{
		if (CastleManager.getInstance().getCastleIndex(this) < 0)
		{
			return COND_REGULAR; // Regular access
		}
		else if (getCastle().getSiege().getIsInProgress())
		{
			return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
		}
		else if (player.getClan() != null) // Teleporter is on castle ground and player is in a clan
		{
			if (getCastle().getOwnerId() == player.getClanId())
			{
				return COND_OWNER; // Owner
			}
		}
		
		return COND_ALL_FALSE;
	}
}
