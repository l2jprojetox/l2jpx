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
package org.l2jpx.gameserver.network.clientpackets;

import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.data.xml.AdminData;
import org.l2jpx.gameserver.data.xml.SecondaryAuthData;
import org.l2jpx.gameserver.instancemanager.AntiFeedManager;
import org.l2jpx.gameserver.instancemanager.PunishmentManager;
import org.l2jpx.gameserver.model.CharSelectInfoPackage;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.Containers;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerSelect;
import org.l2jpx.gameserver.model.events.returns.TerminateReturn;
import org.l2jpx.gameserver.model.punishment.PunishmentAffect;
import org.l2jpx.gameserver.model.punishment.PunishmentType;
import org.l2jpx.gameserver.model.variables.PlayerVariables;
import org.l2jpx.gameserver.network.ConnectionState;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.serverpackets.CharSelected;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jpx.gameserver.network.serverpackets.SSQInfo;
import org.l2jpx.gameserver.network.serverpackets.ServerClose;

/**
 * @version $Revision: 1.5.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterSelect extends ClientPacket
{
	protected static final Logger LOGGER_ACCOUNTING = Logger.getLogger("accounting");
	
	// cd
	private int _charSlot;
	
	@SuppressWarnings("unused")
	private int _unk1; // new in C4
	@SuppressWarnings("unused")
	private int _unk2; // new in C4
	@SuppressWarnings("unused")
	private int _unk3; // new in C4
	@SuppressWarnings("unused")
	private int _unk4; // new in C4
	
	@Override
	protected void readImpl()
	{
		_charSlot = readInt();
		_unk1 = readShort();
		_unk2 = readInt();
		_unk3 = readInt();
		_unk4 = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		if (!client.getFloodProtectors().canSelectCharacter())
		{
			return;
		}
		
		if (SecondaryAuthData.getInstance().isEnabled() && !client.getSecondaryAuth().isAuthed())
		{
			client.getSecondaryAuth().openDialog();
			return;
		}
		
		// We should always be able to acquire the lock
		// But if we can't lock then nothing should be done (i.e. repeated packet)
		if (client.getPlayerLock().tryLock())
		{
			try
			{
				// should always be null
				// but if not then this is repeated packet and nothing should be done here
				if (client.getPlayer() == null)
				{
					final CharSelectInfoPackage info = client.getCharSelection(_charSlot);
					if (info == null)
					{
						return;
					}
					
					// Disconnect offline trader.
					final Player player = World.getInstance().getPlayer(info.getObjectId());
					if (player != null)
					{
						Disconnection.of(player).storeMe().deleteMe();
					}
					
					// Banned?
					if (PunishmentManager.getInstance().hasPunishment(info.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN) //
						|| PunishmentManager.getInstance().hasPunishment(client.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.BAN) //
						|| PunishmentManager.getInstance().hasPunishment(client.getIp(), PunishmentAffect.IP, PunishmentType.BAN))
					{
						client.close(ServerClose.STATIC_PACKET);
						return;
					}
					
					// Selected character is banned (compatibility with previous versions).
					if (info.getAccessLevel() < 0)
					{
						client.close(ServerClose.STATIC_PACKET);
						return;
					}
					
					if ((Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddClient(AntiFeedManager.GAME_ID, client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP))
					{
						final NpcHtmlMessage msg = new NpcHtmlMessage();
						msg.setFile(null, "data/html/mods/IPRestriction.htm");
						msg.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)));
						client.sendPacket(msg);
						return;
					}
					
					if (Config.FACTION_SYSTEM_ENABLED && Config.FACTION_BALANCE_ONLINE_PLAYERS)
					{
						if (info.isGood() && (World.getInstance().getAllGoodPlayers().size() >= (World.getInstance().getAllEvilPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
						{
							final NpcHtmlMessage msg = new NpcHtmlMessage();
							msg.setFile(null, "data/html/mods/Faction/ExceededOnlineLimit.htm");
							msg.replace("%more%", Config.FACTION_GOOD_TEAM_NAME);
							msg.replace("%less%", Config.FACTION_EVIL_TEAM_NAME);
							client.sendPacket(msg);
							return;
						}
						if (info.isEvil() && (World.getInstance().getAllEvilPlayers().size() >= (World.getInstance().getAllGoodPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
						{
							final NpcHtmlMessage msg = new NpcHtmlMessage();
							msg.setFile(null, "data/html/mods/Faction/ExceededOnlineLimit.htm");
							msg.replace("%more%", Config.FACTION_EVIL_TEAM_NAME);
							msg.replace("%less%", Config.FACTION_GOOD_TEAM_NAME);
							client.sendPacket(msg);
							return;
						}
					}
					
					// load up character from disk
					final Player cha = client.load(_charSlot);
					if (cha == null)
					{
						return; // handled in GameClient
					}
					
					CharInfoTable.getInstance().addName(cha);
					
					// Prevent instant disappear of invisible GMs on login.
					if (cha.isGM() && Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_invisible", cha.getAccessLevel()))
					{
						cha.setInvisible(true);
					}
					
					// Restore player location.
					final PlayerVariables vars = cha.getVariables();
					final String restore = vars.getString(PlayerVariables.RESTORE_LOCATION, "");
					if (!restore.isEmpty())
					{
						final String[] split = restore.split(";");
						cha.setXYZ(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
						vars.remove(PlayerVariables.RESTORE_LOCATION);
					}
					
					cha.setClient(client);
					client.setPlayer(cha);
					cha.setOnlineStatus(true, true);
					
					if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_SELECT, Containers.Players()))
					{
						final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerSelect(cha, cha.getObjectId(), cha.getName(), client), Containers.Players(), TerminateReturn.class);
						if ((terminate != null) && terminate.terminate())
						{
							Disconnection.of(cha).defaultSequence(LeaveWorld.STATIC_PACKET);
							return;
						}
					}
					
					client.sendPacket(new SSQInfo());
					client.setConnectionState(ConnectionState.ENTERING);
					client.sendPacket(new CharSelected(cha, client.getSessionId().playOkID1));
				}
			}
			finally
			{
				client.getPlayerLock().unlock();
			}
			
			LOGGER_ACCOUNTING.info("Logged in, " + client);
		}
	}
}
