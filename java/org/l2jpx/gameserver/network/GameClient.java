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
package org.l2jpx.gameserver.network;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.network.Buffer;
import org.l2jpx.commons.network.Client;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.LoginServerThread;
import org.l2jpx.gameserver.LoginServerThread.SessionKey;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.data.xml.SecondaryAuthData;
import org.l2jpx.gameserver.enums.CharacterDeleteFailType;
import org.l2jpx.gameserver.instancemanager.ItemCommissionManager;
import org.l2jpx.gameserver.instancemanager.MailManager;
import org.l2jpx.gameserver.instancemanager.MentorManager;
import org.l2jpx.gameserver.model.CharSelectInfoPackage;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2jpx.gameserver.network.serverpackets.AbnormalStatusUpdate;
import org.l2jpx.gameserver.network.serverpackets.AcquireSkillList;
import org.l2jpx.gameserver.network.serverpackets.ExAbnormalStatusUpdateFromTarget;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.network.serverpackets.SkillList;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.security.SecondaryPasswordAuth;
import org.l2jpx.gameserver.util.FloodProtectors;

/**
 * Represents a client connected on GameServer.
 * @author KenM
 */
public class GameClient extends Client<org.l2jpx.commons.network.Connection<GameClient>>
{
	private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());
	private static final Logger LOGGER_ACCOUNTING = Logger.getLogger("accounting");
	
	private final FloodProtectors _floodProtectors = new FloodProtectors(this);
	private final ReentrantLock _playerLock = new ReentrantLock();
	private ConnectionState _connectionState = ConnectionState.CONNECTED;
	private Encryption _encryption = null;
	private String _ip = "N/A";
	private String _accountName;
	private SessionKey _sessionKey;
	private Player _player;
	private SecondaryPasswordAuth _secondaryAuth;
	private ClientHardwareInfoHolder _hardwareInfo;
	private List<CharSelectInfoPackage> _charSlotMapping = null;
	private volatile boolean _isDetached = false;
	private boolean _isAuthedGG;
	private boolean _protocolOk;
	private int _protocolVersion;
	private int[][] _trace;
	
	public GameClient(org.l2jpx.commons.network.Connection<GameClient> connection)
	{
		super(connection);
		_ip = connection.getRemoteAddress();
	}
	
	@Override
	public void onConnected()
	{
		LOGGER_ACCOUNTING.finer("Client connected: " + _ip);
	}
	
	@Override
	public void onDisconnection()
	{
		LOGGER_ACCOUNTING.finer("Client disconnected: " + this);
		LoginServerThread.getInstance().sendLogout(_accountName);
		if ((_player == null) || !_player.isInOfflineMode())
		{
			Disconnection.of(this).onDisconnection();
		}
		_connectionState = ConnectionState.DISCONNECTED;
	}
	
	@Override
	public boolean encrypt(Buffer data, int offset, int size)
	{
		if (Config.PACKET_ENCRYPTION && (_encryption != null))
		{
			_encryption.encrypt(data, offset, size);
		}
		return true;
	}
	
	@Override
	public boolean decrypt(Buffer data, int offset, int size)
	{
		if (Config.PACKET_ENCRYPTION && (_encryption != null))
		{
			_encryption.decrypt(data, offset, size);
		}
		return true;
	}
	
	public void closeNow()
	{
		disconnect();
	}
	
	public void close(ServerPacket packet)
	{
		if (packet == null)
		{
			closeNow();
		}
		else
		{
			// Send the close packet.
			sendPacket(packet);
			
			// Wait for packet to be sent.
			ThreadPool.schedule(this::closeNow, 1000);
		}
	}
	
	public byte[] enableCrypt()
	{
		final byte[] key = BlowFishKeygen.getRandomKey();
		if (Config.PACKET_ENCRYPTION)
		{
			_encryption = new Encryption();
			_encryption.setKey(key);
		}
		return key;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void setPlayer(Player player)
	{
		_player = player;
	}
	
	public ReentrantLock getPlayerLock()
	{
		return _playerLock;
	}
	
	public FloodProtectors getFloodProtectors()
	{
		return _floodProtectors;
	}
	
	public void setGameGuardOk(boolean value)
	{
		_isAuthedGG = value;
	}
	
	public boolean isAuthedGG()
	{
		return _isAuthedGG;
	}
	
	public String getIp()
	{
		return _ip;
	}
	
	public void setAccountName(String accountName)
	{
		_accountName = accountName;
		if (SecondaryAuthData.getInstance().isEnabled())
		{
			_secondaryAuth = new SecondaryPasswordAuth(this);
		}
	}
	
	public String getAccountName()
	{
		return _accountName;
	}
	
	public void setSessionId(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}
	
	public SessionKey getSessionId()
	{
		return _sessionKey;
	}
	
	public void sendPacket(ServerPacket packet)
	{
		// Packet should never be null.
		if (packet == null)
		{
			LOGGER.warning(CommonUtil.getStackTrace(new Exception()));
			return;
		}
		
		// Prevent flood from class change.
		if ((_player != null) && _player.isChangingClass() && ((packet instanceof SkillList) || (packet instanceof AcquireSkillList) || (packet instanceof ExUserInfoAbnormalVisualEffect) || (packet instanceof AbnormalStatusUpdate) || (packet instanceof ExAbnormalStatusUpdateFromTarget)))
		{
			return;
		}
		
		// Send the packet data.
		writePacket(packet);
		
		// Run packet implementation.
		packet.runImpl(_player);
	}
	
	public void sendPacket(SystemMessageId systemMessageId)
	{
		sendPacket(new SystemMessage(systemMessageId));
	}
	
	public boolean isDetached()
	{
		return _isDetached;
	}
	
	public void setDetached(boolean value)
	{
		_isDetached = value;
	}
	
	/**
	 * Method to handle character deletion
	 * @param characterSlot
	 * @return a byte:
	 *         <li>-1: Error: No char was found for such charslot, caught exception, etc...
	 *         <li>0: character is not member of any clan, proceed with deletion
	 *         <li>1: character is member of a clan, but not clan leader
	 *         <li>2: character is clan leader
	 */
	public CharacterDeleteFailType markToDeleteChar(int characterSlot)
	{
		final int objectId = getObjectIdForSlot(characterSlot);
		if (objectId < 0)
		{
			return CharacterDeleteFailType.UNKNOWN;
		}
		
		if (MentorManager.getInstance().isMentor(objectId))
		{
			return CharacterDeleteFailType.MENTOR;
		}
		else if (MentorManager.getInstance().isMentee(objectId))
		{
			return CharacterDeleteFailType.MENTEE;
		}
		else if (ItemCommissionManager.getInstance().hasCommissionItems(objectId))
		{
			return CharacterDeleteFailType.COMMISSION;
		}
		else if (MailManager.getInstance().getMailsInProgress(objectId) > 0)
		{
			return CharacterDeleteFailType.MAIL;
		}
		else
		{
			final int clanId = CharInfoTable.getInstance().getClassIdById(objectId);
			if (clanId > 0)
			{
				final Clan clan = ClanTable.getInstance().getClan(clanId);
				if (clan != null)
				{
					if (clan.getLeaderId() == objectId)
					{
						return CharacterDeleteFailType.PLEDGE_MASTER;
					}
					return CharacterDeleteFailType.PLEDGE_MEMBER;
				}
			}
		}
		
		if (Config.DELETE_DAYS == 0)
		{
			deleteCharByObjId(objectId);
		}
		else
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps2 = con.prepareStatement("UPDATE characters SET deletetime=? WHERE charId=?"))
			{
				ps2.setLong(1, System.currentTimeMillis() + (Config.DELETE_DAYS * 86400000)); // 24*60*60*1000 = 86400000
				ps2.setInt(2, objectId);
				ps2.execute();
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, "Failed to update char delete time: ", e);
			}
		}
		
		LOGGER_ACCOUNTING.info("Delete, " + objectId + ", " + this);
		return CharacterDeleteFailType.NONE;
	}
	
	public void restore(int characterSlot)
	{
		final int objectId = getObjectIdForSlot(characterSlot);
		if (objectId < 0)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE charId=?"))
		{
			statement.setInt(1, objectId);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error restoring character.", e);
		}
		
		LOGGER_ACCOUNTING.info("Restore, " + objectId + ", " + this);
	}
	
	public static void deleteCharByObjId(int objectId)
	{
		if (objectId < 0)
		{
			return;
		}
		
		CharInfoTable.getInstance().removeName(objectId);
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_contacts WHERE charId=? OR contactId=?"))
			{
				ps.setInt(1, objectId);
				ps.setInt(2, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE charId=? OR friendId=?"))
			{
				ps.setInt(1, objectId);
				ps.setInt(2, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_hennas WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_macroses WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_quests WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_subclasses WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM heroes WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM olympiad_nobles WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variables WHERE id IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM seven_signs WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE owner_id=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM merchant_lease WHERE player_id=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_reco_bonus WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_instance_time WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM characters WHERE charId=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error deleting character.", e);
		}
	}
	
	public Player load(int characterSlot)
	{
		final int objectId = getObjectIdForSlot(characterSlot);
		if (objectId < 0)
		{
			return null;
		}
		
		Player player = World.getInstance().getPlayer(objectId);
		if (player != null)
		{
			// exploit prevention, should not happens in normal way
			if (player.isOnlineInt() == 1)
			{
				LOGGER.severe("Attempt of double login: " + player.getName() + "(" + objectId + ") " + _accountName);
			}
			
			if (player.getClient() != null)
			{
				Disconnection.of(player).defaultSequence(LeaveWorld.STATIC_PACKET);
			}
			else
			{
				player.storeMe();
				player.deleteMe();
			}
			
			return null;
		}
		
		player = Player.load(objectId);
		if (player == null)
		{
			LOGGER.severe("Could not restore in slot: " + characterSlot);
		}
		
		return player;
	}
	
	public void setCharSelection(List<CharSelectInfoPackage> characters)
	{
		_charSlotMapping = characters;
	}
	
	public CharSelectInfoPackage getCharSelection(int charslot)
	{
		if ((_charSlotMapping == null) || (charslot < 0) || (charslot >= _charSlotMapping.size()))
		{
			return null;
		}
		return _charSlotMapping.get(charslot);
	}
	
	public SecondaryPasswordAuth getSecondaryAuth()
	{
		return _secondaryAuth;
	}
	
	/**
	 * @param characterSlot
	 * @return
	 */
	private int getObjectIdForSlot(int characterSlot)
	{
		final CharSelectInfoPackage info = getCharSelection(characterSlot);
		if (info == null)
		{
			LOGGER.warning(toString() + " tried to delete Character in slot " + characterSlot + " but no characters exits at that slot.");
			return -1;
		}
		return info.getObjectId();
	}
	
	public void setConnectionState(ConnectionState connectionState)
	{
		_connectionState = connectionState;
	}
	
	public ConnectionState getConnectionState()
	{
		return _connectionState;
	}
	
	public void setProtocolVersion(int version)
	{
		_protocolVersion = version;
	}
	
	public int getProtocolVersion()
	{
		return _protocolVersion;
	}
	
	public boolean isProtocolOk()
	{
		return _protocolOk;
	}
	
	public void setProtocolOk(boolean value)
	{
		_protocolOk = value;
	}
	
	public void setClientTracert(int[][] tracert)
	{
		_trace = tracert;
	}
	
	public int[][] getTrace()
	{
		return _trace;
	}
	
	public Encryption getEncryption()
	{
		return _encryption;
	}
	
	/**
	 * @return the hardwareInfo
	 */
	public ClientHardwareInfoHolder getHardwareInfo()
	{
		return _hardwareInfo;
	}
	
	/**
	 * @param hardwareInfo
	 */
	public void setHardwareInfo(ClientHardwareInfoHolder hardwareInfo)
	{
		_hardwareInfo = hardwareInfo;
	}
	
	/**
	 * Produces the best possible string representation of this client.
	 */
	@Override
	public String toString()
	{
		try
		{
			final String ip = getIp();
			final ConnectionState state = getConnectionState();
			switch (state)
			{
				case DISCONNECTED:
				{
					if (_accountName != null)
					{
						return "[Account: " + _accountName + " - IP: " + (ip == null ? "disconnected" : ip) + "]";
					}
					return "[IP: " + (ip == null ? "disconnected" : ip) + "]";
				}
				case CONNECTED:
				{
					return "[IP: " + (ip == null ? "disconnected" : ip) + "]";
				}
				case AUTHENTICATED:
				{
					return "[Account: " + _accountName + " - IP: " + (ip == null ? "disconnected" : ip) + "]";
				}
				case ENTERING:
				case IN_GAME:
				{
					return "[Character: " + (_player == null ? "disconnected" : _player.getName() + "[" + _player.getObjectId() + "]") + " - Account: " + _accountName + " - IP: " + (ip == null ? "disconnected" : ip) + "]";
				}
				default:
				{
					throw new IllegalStateException("Missing state on switch.");
				}
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}
}
