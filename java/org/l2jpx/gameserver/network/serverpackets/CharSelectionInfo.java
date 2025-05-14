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
package org.l2jpx.gameserver.network.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.data.xml.ExperienceData;
import org.l2jpx.gameserver.model.CharSelectInfoPackage;
import org.l2jpx.gameserver.model.VariationInstance;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.olympiad.Hero;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class CharSelectionInfo extends ServerPacket
{
	private static final Logger LOGGER = Logger.getLogger(CharSelectionInfo.class.getName());
	
	private static final int[] PAPERDOLL_ORDER_VISUAL_ID = new int[]
	{
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2,
	};
	
	private final String _loginName;
	private final int _sessionId;
	private int _activeId;
	private final List<CharSelectInfoPackage> _characterPackages;
	
	/**
	 * Constructor for CharSelectionInfo.
	 * @param loginName
	 * @param sessionId
	 */
	public CharSelectionInfo(String loginName, int sessionId)
	{
		_sessionId = sessionId;
		_loginName = loginName;
		_characterPackages = loadCharacterSelectInfo(_loginName);
		_activeId = -1;
	}
	
	public CharSelectionInfo(String loginName, int sessionId, int activeId)
	{
		_sessionId = sessionId;
		_loginName = loginName;
		_characterPackages = loadCharacterSelectInfo(_loginName);
		_activeId = activeId;
	}
	
	public List<CharSelectInfoPackage> getCharInfo()
	{
		return _characterPackages;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.CHARACTER_SELECTION_INFO.writeId(this, buffer);
		final int size = _characterPackages.size();
		buffer.writeInt(size); // Created character count
		buffer.writeInt(Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT); // Can prevent players from creating new characters (if 0); (if 1, the client will ask if chars may be created (0x13) Response: (0x0D) )
		buffer.writeByte(size == Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT); // if 1 can't create new char
		buffer.writeByte(1); // 0=can't play, 1=can play free until level 85, 2=100% free play
		buffer.writeInt(2); // if 1, Korean client
		buffer.writeByte(0); // Balthus Knights, if 1 suggests premium account
		long lastAccess = 0;
		if (_activeId == -1)
		{
			for (int i = 0; i < size; i++)
			{
				if (lastAccess < _characterPackages.get(i).getLastAccess())
				{
					lastAccess = _characterPackages.get(i).getLastAccess();
					_activeId = i;
				}
			}
		}
		for (int i = 0; i < size; i++)
		{
			final CharSelectInfoPackage charInfoPackage = _characterPackages.get(i);
			buffer.writeString(charInfoPackage.getName()); // Character name
			buffer.writeInt(charInfoPackage.getObjectId()); // Character ID
			buffer.writeString(_loginName); // Account name
			buffer.writeInt(_sessionId); // Account ID
			buffer.writeInt(0); // Clan ID
			buffer.writeInt(0); // Builder level
			buffer.writeInt(charInfoPackage.getSex()); // Sex
			buffer.writeInt(charInfoPackage.getRace()); // Race
			buffer.writeInt(charInfoPackage.getBaseClassId());
			buffer.writeInt(1); // GameServerName
			buffer.writeInt(charInfoPackage.getX());
			buffer.writeInt(charInfoPackage.getY());
			buffer.writeInt(charInfoPackage.getZ());
			buffer.writeDouble(charInfoPackage.getCurrentHp());
			buffer.writeDouble(charInfoPackage.getCurrentMp());
			buffer.writeLong(charInfoPackage.getSp());
			buffer.writeLong(charInfoPackage.getExp());
			buffer.writeDouble((float) (charInfoPackage.getExp() - ExperienceData.getInstance().getExpForLevel(charInfoPackage.getLevel())) / (ExperienceData.getInstance().getExpForLevel(charInfoPackage.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(charInfoPackage.getLevel()))); // High
			// Five
			buffer.writeInt(charInfoPackage.getLevel());
			buffer.writeInt(charInfoPackage.getReputation());
			buffer.writeInt(charInfoPackage.getPkKills());
			buffer.writeInt(charInfoPackage.getPvPKills());
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0); // Ertheia
			buffer.writeInt(0); // Ertheia
			for (int slot : getPaperdollOrder())
			{
				buffer.writeInt(charInfoPackage.getPaperdollItemId(slot));
			}
			for (int slot : getPaperdollOrderVisualId())
			{
				buffer.writeInt(charInfoPackage.getPaperdollItemVisualId(slot));
			}
			buffer.writeShort(charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_CHEST)); // Upper Body enchant level
			buffer.writeShort(charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_LEGS)); // Lower Body enchant level
			buffer.writeShort(charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_HEAD)); // Headgear enchant level
			buffer.writeShort(charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_GLOVES)); // Gloves enchant level
			buffer.writeShort(charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_FEET)); // Boots enchant level
			buffer.writeInt(charInfoPackage.getHairStyle());
			buffer.writeInt(charInfoPackage.getHairColor());
			buffer.writeInt(charInfoPackage.getFace());
			buffer.writeDouble(charInfoPackage.getMaxHp()); // Maximum HP
			buffer.writeDouble(charInfoPackage.getMaxMp()); // Maximum MP
			buffer.writeInt(charInfoPackage.getDeleteTimer() > 0 ? (int) ((charInfoPackage.getDeleteTimer() - System.currentTimeMillis()) / 1000) : 0);
			buffer.writeInt(charInfoPackage.getClassId());
			buffer.writeInt(i == _activeId);
			buffer.writeByte(charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_RHAND) > 127 ? 127 : charInfoPackage.getEnchantEffect(Inventory.PAPERDOLL_RHAND));
			buffer.writeInt(charInfoPackage.getAugmentation() != null ? charInfoPackage.getAugmentation().getOption1Id() : 0);
			buffer.writeInt(charInfoPackage.getAugmentation() != null ? charInfoPackage.getAugmentation().getOption2Id() : 0);
			// buffer.writeInt(charInfoPackage.getTransformId()); // Used to display Transformations
			buffer.writeInt(0); // Currently on retail when you are on character select you don't see your transformation.
			buffer.writeInt(0); // Pet NpcId
			buffer.writeInt(0); // Pet level
			buffer.writeInt(0); // Pet Food
			buffer.writeInt(0); // Pet Food Level
			buffer.writeDouble(0); // Current pet HP
			buffer.writeDouble(0); // Current pet MP
			buffer.writeInt(charInfoPackage.getVitalityPoints()); // Vitality
			buffer.writeInt((int) Config.RATE_VITALITY_EXP_MULTIPLIER * 100); // Vitality Percent
			buffer.writeInt(charInfoPackage.getVitalityItemsUsed()); // Remaining vitality item uses
			buffer.writeInt(charInfoPackage.getAccessLevel() != -100); // Char is active or not
			buffer.writeByte(charInfoPackage.isNoble());
			buffer.writeByte(Hero.getInstance().isHero(charInfoPackage.getObjectId()) ? 2 : 0); // Hero glow
			buffer.writeByte(charInfoPackage.isHairAccessoryEnabled()); // Show hair accessory if enabled
		}
	}
	
	private static List<CharSelectInfoPackage> loadCharacterSelectInfo(String loginName)
	{
		CharSelectInfoPackage charInfopackage;
		final List<CharSelectInfoPackage> characterList = new LinkedList<>();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY createDate"))
		{
			statement.setString(1, loginName);
			try (ResultSet charList = statement.executeQuery())
			{
				while (charList.next()) // fills the package
				{
					charInfopackage = restoreChar(charList);
					if (charInfopackage != null)
					{
						characterList.add(charInfopackage);
						
						// Disconnect offline trader.
						if (Config.OFFLINE_DISCONNECT_SAME_ACCOUNT)
						{
							final Player player = World.getInstance().getPlayer(charInfopackage.getObjectId());
							if ((player != null) && player.isInStoreMode())
							{
								Disconnection.of(player).storeMe().deleteMe();
								continue;
							}
						}
						
						// Disconnect offline play.
						if (Config.OFFLINE_PLAY_DISCONNECT_SAME_ACCOUNT)
						{
							final Player player = World.getInstance().getPlayer(charInfopackage.getObjectId());
							if ((player != null) && player.isOfflinePlay())
							{
								Disconnection.of(player).storeMe().deleteMe();
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore char info: " + e.getMessage(), e);
		}
		return characterList;
	}
	
	private static void loadCharacterSubclassInfo(CharSelectInfoPackage charInfopackage, int objectId, int activeClassId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT exp, sp, level, vitality_points FROM character_subclasses WHERE charId=? AND class_id=? ORDER BY charId"))
		{
			statement.setInt(1, objectId);
			statement.setInt(2, activeClassId);
			try (ResultSet charList = statement.executeQuery())
			{
				if (charList.next())
				{
					charInfopackage.setExp(charList.getLong("exp"));
					charInfopackage.setSp(charList.getLong("sp"));
					charInfopackage.setLevel(charList.getInt("level"));
					charInfopackage.setVitalityPoints(charList.getInt("vitality_points"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore char subclass info: " + e.getMessage(), e);
		}
	}
	
	private static CharSelectInfoPackage restoreChar(ResultSet chardata) throws Exception
	{
		final int objectId = chardata.getInt("charId");
		final String name = chardata.getString("char_name");
		
		// See if the char must be deleted
		final long deletetime = chardata.getLong("deletetime");
		if ((deletetime > 0) && (System.currentTimeMillis() > deletetime))
		{
			final Clan clan = ClanTable.getInstance().getClan(chardata.getInt("clanid"));
			if (clan != null)
			{
				clan.removeClanMember(objectId, 0);
			}
			GameClient.deleteCharByObjId(objectId);
			return null;
		}
		
		final CharSelectInfoPackage charInfopackage = new CharSelectInfoPackage(objectId, name);
		charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));
		charInfopackage.setLevel(chardata.getInt("level"));
		charInfopackage.setMaxHp(chardata.getInt("maxhp"));
		charInfopackage.setCurrentHp(chardata.getDouble("curhp"));
		charInfopackage.setMaxMp(chardata.getInt("maxmp"));
		charInfopackage.setCurrentMp(chardata.getDouble("curmp"));
		charInfopackage.setReputation(chardata.getInt("reputation"));
		charInfopackage.setPkKills(chardata.getInt("pkkills"));
		charInfopackage.setPvPKills(chardata.getInt("pvpkills"));
		charInfopackage.setFace(chardata.getInt("face"));
		charInfopackage.setHairStyle(chardata.getInt("hairstyle"));
		charInfopackage.setHairColor(chardata.getInt("haircolor"));
		charInfopackage.setSex(chardata.getInt("sex"));
		charInfopackage.setExp(chardata.getLong("exp"));
		charInfopackage.setSp(chardata.getLong("sp"));
		charInfopackage.setVitalityPoints(chardata.getInt("vitality_points"));
		charInfopackage.setClanId(chardata.getInt("clanid"));
		charInfopackage.setRace(chardata.getInt("race"));
		final int baseClassId = chardata.getInt("base_class");
		final int activeClassId = chardata.getInt("classid");
		charInfopackage.setX(chardata.getInt("x"));
		charInfopackage.setY(chardata.getInt("y"));
		charInfopackage.setZ(chardata.getInt("z"));
		final int faction = chardata.getInt("faction");
		if (faction == 1)
		{
			charInfopackage.setGood();
		}
		if (faction == 2)
		{
			charInfopackage.setEvil();
		}
		if (Config.MULTILANG_ENABLE)
		{
			String lang = chardata.getString("language");
			if (!Config.MULTILANG_ALLOWED.contains(lang))
			{
				lang = Config.MULTILANG_DEFAULT;
			}
			charInfopackage.setHtmlPrefix("data/lang/" + lang + "/");
		}
		// if is in subclass, load subclass exp, sp, level info
		if (baseClassId != activeClassId)
		{
			loadCharacterSubclassInfo(charInfopackage, objectId, activeClassId);
		}
		charInfopackage.setClassId(activeClassId);
		// Get the augmentation id for equipped weapon
		int weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
		if (weaponObjId < 1)
		{
			weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
		}
		if (weaponObjId > 0)
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT mineralId,option1,option2 FROM item_variations WHERE itemId=?"))
			{
				statement.setInt(1, weaponObjId);
				try (ResultSet result = statement.executeQuery())
				{
					if (result.next())
					{
						final int mineralId = result.getInt("mineralId");
						final int option1 = result.getInt("option1");
						final int option2 = result.getInt("option2");
						if ((option1 != -1) && (option2 != -1))
						{
							charInfopackage.setAugmentation(new VariationInstance(mineralId, option1, option2));
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Could not restore augmentation info: " + e.getMessage(), e);
			}
		}
		// Check if the base class is set to zero and also doesn't match with the current active class, otherwise send the base class ID. This prevents chars created before base class was introduced from being displayed incorrectly.
		if ((baseClassId == 0) && (activeClassId > 0))
		{
			charInfopackage.setBaseClassId(activeClassId);
		}
		else
		{
			charInfopackage.setBaseClassId(baseClassId);
		}
		charInfopackage.setDeleteTimer(deletetime);
		charInfopackage.setLastAccess(chardata.getLong("lastAccess"));
		charInfopackage.setNoble(chardata.getInt("nobless") == 1);
		return charInfopackage;
	}
	
	@Override
	public int[] getPaperdollOrderVisualId()
	{
		return PAPERDOLL_ORDER_VISUAL_ID;
	}
}
