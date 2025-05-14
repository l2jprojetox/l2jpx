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

import java.util.List;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.data.xml.FakePlayerData;
import org.l2jpx.gameserver.data.xml.InitialEquipmentData;
import org.l2jpx.gameserver.data.xml.InitialShortcutData;
import org.l2jpx.gameserver.data.xml.PlayerTemplateData;
import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.data.xml.SkillTreeData;
import org.l2jpx.gameserver.enums.ClassId;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.SkillLearn;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jpx.gameserver.model.actor.stat.PlayerStat;
import org.l2jpx.gameserver.model.actor.templates.PlayerTemplate;
import org.l2jpx.gameserver.model.events.Containers;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerCreate;
import org.l2jpx.gameserver.model.item.PlayerItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.serverpackets.CharCreateFail;
import org.l2jpx.gameserver.network.serverpackets.CharCreateOk;
import org.l2jpx.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2jpx.gameserver.util.Util;

public class CharacterCreate extends ClientPacket
{
	protected static final Logger LOGGER_ACCOUNTING = Logger.getLogger("accounting");
	
	// cSdddddddddddd
	private String _name;
	private boolean _isFemale;
	private int _classId;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
		readInt(); // race
		_isFemale = readInt() != 0;
		_classId = readInt();
		readInt(); // _int
		readInt(); // _str
		readInt(); // _con
		readInt(); // _men
		readInt(); // _dex
		readInt(); // _wit
		_hairStyle = (byte) readInt();
		_hairColor = (byte) readInt();
		_face = (byte) readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		
		// Last Verified: May 30, 2009 - Gracia Final - Players are able to create characters with names consisting of as little as 1,2,3 letter/number combinations.
		if ((_name.length() < 1) || (_name.length() > 16))
		{
			client.sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
			return;
		}
		
		if (Config.FORBIDDEN_NAMES.length > 0)
		{
			for (String st : Config.FORBIDDEN_NAMES)
			{
				if (_name.toLowerCase().contains(st.toLowerCase()))
				{
					client.sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
					return;
				}
			}
		}
		
		if (FakePlayerData.getInstance().getProperName(_name) != null)
		{
			client.sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
			return;
		}
		
		// Last Verified: May 30, 2009 - Gracia Final
		if (!Util.isAlphaNumeric(_name) || !isValidName(_name))
		{
			client.sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
			return;
		}
		
		if ((_face > 2) || (_face < 0))
		{
			PacketLogger.warning("Character Creation Failure: Character face " + _face + " is invalid. Possible client hack. " + client);
			client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if ((_hairStyle < 0) || (!_isFemale && (_hairStyle > 4)) || (_isFemale && (_hairStyle > 6)))
		{
			PacketLogger.warning("Character Creation Failure: Character hair style " + _hairStyle + " is invalid. Possible client hack. " + client);
			client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if ((_hairColor > 3) || (_hairColor < 0))
		{
			PacketLogger.warning("Character Creation Failure: Character hair color " + _hairColor + " is invalid. Possible client hack. " + client);
			client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		Player newChar = null;
		PlayerTemplate template = null;
		
		/*
		 * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
		 */
		synchronized (CharInfoTable.getInstance())
		{
			if ((CharInfoTable.getInstance().getAccountCharacterCount(client.getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0))
			{
				client.sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
				return;
			}
			else if (CharInfoTable.getInstance().doesCharNameExist(_name))
			{
				client.sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
				return;
			}
			
			template = PlayerTemplateData.getInstance().getTemplate(_classId);
			if ((template == null) || (ClassId.getClassId(_classId).level() > 0))
			{
				client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
				return;
			}
			
			// Custom Feature: Disallow a race to be created.
			// Example: Humans can not be created if AllowHuman = False in Custom.properties
			switch (template.getRace())
			{
				case HUMAN:
				{
					if (!Config.ALLOW_HUMAN)
					{
						client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
						return;
					}
					break;
				}
				case ELF:
				{
					if (!Config.ALLOW_ELF)
					{
						client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
						return;
					}
					break;
				}
				case DARK_ELF:
				{
					if (!Config.ALLOW_DARKELF)
					{
						client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
						return;
					}
					break;
				}
				case ORC:
				{
					if (!Config.ALLOW_ORC)
					{
						client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
						return;
					}
					break;
				}
				case DWARF:
				{
					if (!Config.ALLOW_DWARF)
					{
						client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
						return;
					}
					break;
				}
			}
			newChar = Player.create(template, client.getAccountName(), _name, new PlayerAppearance(_face, _hairColor, _hairStyle, _isFemale));
		}
		
		// HP and MP are at maximum and CP is zero by default.
		newChar.setCurrentHp(newChar.getMaxHp());
		newChar.setCurrentMp(newChar.getMaxMp());
		// newChar.setMaxLoad(template.getBaseLoad());
		client.sendPacket(CharCreateOk.STATIC_PACKET);
		
		initNewChar(client, newChar);
		
		LOGGER_ACCOUNTING.info("Created new character, " + newChar + ", " + client);
	}
	
	private static boolean isValidName(String text)
	{
		return Config.CHARNAME_TEMPLATE_PATTERN.matcher(text).matches();
	}
	
	private void initNewChar(GameClient client, Player newChar)
	{
		World.getInstance().addObject(newChar);
		
		if (Config.STARTING_ADENA > 0)
		{
			newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
		}
		
		final PlayerTemplate template = newChar.getTemplate();
		if (Config.CUSTOM_STARTING_LOC)
		{
			final Location createLoc = new Location(Config.CUSTOM_STARTING_LOC_X, Config.CUSTOM_STARTING_LOC_Y, Config.CUSTOM_STARTING_LOC_Z);
			newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
		}
		else if (Config.FACTION_SYSTEM_ENABLED)
		{
			newChar.setXYZInvisible(Config.FACTION_STARTING_LOCATION.getX(), Config.FACTION_STARTING_LOCATION.getY(), Config.FACTION_STARTING_LOCATION.getZ());
		}
		else
		{
			final Location createLoc = template.getCreationPoint();
			newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
		}
		newChar.setTitle("");
		
		if (Config.ENABLE_VITALITY)
		{
			newChar.setVitalityPoints(Math.min(Config.STARTING_VITALITY_POINTS, PlayerStat.MAX_VITALITY_POINTS), true);
		}
		if (Config.STARTING_LEVEL > 1)
		{
			newChar.getStat().addLevel((byte) (Config.STARTING_LEVEL - 1));
		}
		if (Config.STARTING_SP > 0)
		{
			newChar.getStat().addSp(Config.STARTING_SP);
		}
		
		final List<PlayerItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(newChar.getClassId());
		if (initialItems != null)
		{
			for (PlayerItemTemplate ie : initialItems)
			{
				final Item item = newChar.getInventory().addItem("Init", ie.getId(), ie.getCount(), newChar, null);
				if (item == null)
				{
					PacketLogger.warning("Could not create item during char creation: itemId " + ie.getId() + ", amount " + ie.getCount() + ".");
					continue;
				}
				
				if (item.isEquipable() && ie.isEquipped())
				{
					newChar.getInventory().equipItem(item);
				}
			}
		}
		
		for (SkillLearn skill : SkillTreeData.getInstance().getAvailableSkills(newChar, newChar.getClassId(), false, true))
		{
			newChar.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
		}
		
		// Register all shortcuts for actions, skills and items for this new character.
		InitialShortcutData.getInstance().registerAllShortcuts(newChar);
		
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_CREATE, Containers.Players()))
		{
			EventDispatcher.getInstance().notifyEvent(new OnPlayerCreate(newChar, newChar.getObjectId(), newChar.getName(), client), Containers.Players());
		}
		
		newChar.setOnlineStatus(true, false);
		Disconnection.of(client, newChar).storeMe().deleteMe();
		
		final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.setCharSelection(cl.getCharInfo());
	}
}
