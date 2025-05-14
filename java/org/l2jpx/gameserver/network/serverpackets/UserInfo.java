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

import org.l2jpx.Config;
import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.ExperienceData;
import org.l2jpx.gameserver.enums.UserInfoType;
import org.l2jpx.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType>
{
	private Player _player;
	private int _relation;
	private int _runSpd;
	private int _walkSpd;
	private int _swimRunSpd;
	private int _swimWalkSpd;
	private final int _flRunSpd = 0;
	private final int _flWalkSpd = 0;
	private int _flyRunSpd;
	private int _flyWalkSpd;
	private double _moveMultiplier;
	private int _enchantLevel;
	private int _armorEnchant;
	private String _title;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00
	};
	private int _initSize = 5;
	
	public UserInfo(Player player)
	{
		this(player, true);
	}
	
	public UserInfo(Player player, boolean addAll)
	{
		if (!player.isSubclassLocked()) // Changing class.
		{
			_player = player;
			_relation = calculateRelation(player);
			_moveMultiplier = player.getMovementSpeedMultiplier();
			_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
			_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
			_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
			_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
			_flyRunSpd = player.isFlying() ? _runSpd : 0;
			_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
			_enchantLevel = player.getInventory().getWeaponEnchant();
			_armorEnchant = player.getInventory().getArmorSetEnchant();
			_title = player.getTitle();
			
			if (player.isGM() && player.isInvisible())
			{
				_title = "[Invisible]";
			}
			
			if (addAll)
			{
				addComponentType(UserInfoType.values());
			}
		}
	}
	
	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}
	
	@Override
	protected void onNewMaskAdded(UserInfoType component)
	{
		calcBlockSize(component);
	}
	
	private void calcBlockSize(UserInfoType type)
	{
		switch (type)
		{
			case BASIC_INFO:
			{
				_initSize += type.getBlockLength() + (_player.getAppearance().getVisibleName().length() * 2);
				break;
			}
			case CLAN:
			{
				_initSize += type.getBlockLength() + (_title.length() * 2);
				break;
			}
			default:
			{
				_initSize += type.getBlockLength();
				break;
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (_player == null)
		{
			return;
		}
		
		ServerPackets.USER_INFO.writeId(this, buffer);
		buffer.writeInt(_player.getObjectId());
		buffer.writeInt(_initSize);
		buffer.writeShort(23);
		buffer.writeBytes(_masks);
		if (containsMask(UserInfoType.RELATION))
		{
			buffer.writeInt(_relation);
		}
		if (containsMask(UserInfoType.BASIC_INFO))
		{
			buffer.writeShort(16 + (_player.getAppearance().getVisibleName().length() * 2));
			buffer.writeSizedString(_player.getName());
			buffer.writeByte(_player.isGM());
			buffer.writeByte(_player.getRace().ordinal());
			buffer.writeByte(_player.getAppearance().isFemale());
			buffer.writeInt(_player.getBaseTemplate().getClassId().getRootClassId().getId());
			buffer.writeInt(_player.getClassId().getId());
			buffer.writeByte(_player.getLevel());
		}
		if (containsMask(UserInfoType.BASE_STATS))
		{
			buffer.writeShort(18);
			buffer.writeShort(_player.getSTR());
			buffer.writeShort(_player.getDEX());
			buffer.writeShort(_player.getCON());
			buffer.writeShort(_player.getINT());
			buffer.writeShort(_player.getWIT());
			buffer.writeShort(_player.getMEN());
			buffer.writeShort(0);
			buffer.writeShort(0);
		}
		if (containsMask(UserInfoType.MAX_HPCPMP))
		{
			buffer.writeShort(14);
			buffer.writeInt(_player.getMaxHp());
			buffer.writeInt(_player.getMaxMp());
			buffer.writeInt(_player.getMaxCp());
		}
		if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP))
		{
			buffer.writeShort(38);
			buffer.writeInt((int) Math.round(_player.getCurrentHp()));
			buffer.writeInt((int) Math.round(_player.getCurrentMp()));
			buffer.writeInt((int) Math.round(_player.getCurrentCp()));
			buffer.writeLong(_player.getSp());
			buffer.writeLong(_player.getExp());
			buffer.writeDouble((float) (_player.getExp() - ExperienceData.getInstance().getExpForLevel(_player.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_player.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_player.getLevel())));
		}
		if (containsMask(UserInfoType.ENCHANTLEVEL))
		{
			buffer.writeShort(4);
			buffer.writeByte(_enchantLevel);
			buffer.writeByte(_armorEnchant);
		}
		if (containsMask(UserInfoType.APPAREANCE))
		{
			buffer.writeShort(15);
			buffer.writeInt(_player.getVisualHair());
			buffer.writeInt(_player.getVisualHairColor());
			buffer.writeInt(_player.getVisualFace());
			buffer.writeByte(_player.isHairAccessoryEnabled());
		}
		if (containsMask(UserInfoType.STATUS))
		{
			buffer.writeShort(6);
			buffer.writeByte(_player.getMountType().ordinal());
			buffer.writeByte(_player.getPrivateStoreType().getId());
			buffer.writeByte(_player.hasDwarvenCraft() || (_player.getSkillLevel(248) > 0));
			buffer.writeByte(0);
		}
		if (containsMask(UserInfoType.STATS))
		{
			buffer.writeShort(56);
			buffer.writeShort(_player.getActiveWeaponItem() != null ? 40 : 20);
			buffer.writeInt(_player.getPAtk());
			buffer.writeInt(_player.getPAtkSpd());
			buffer.writeInt(_player.getPDef());
			buffer.writeInt(_player.getEvasionRate());
			buffer.writeInt(_player.getAccuracy());
			buffer.writeInt(_player.getCriticalHit());
			buffer.writeInt(_player.getMAtk());
			buffer.writeInt(_player.getMAtkSpd());
			buffer.writeInt(_player.getPAtkSpd()); // Seems like atk speed - 1
			buffer.writeInt(_player.getMagicEvasionRate());
			buffer.writeInt(_player.getMDef());
			buffer.writeInt(_player.getMagicAccuracy());
			buffer.writeInt(_player.getMCriticalHit());
		}
		if (containsMask(UserInfoType.ELEMENTALS))
		{
			buffer.writeShort(14);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
		}
		if (containsMask(UserInfoType.POSITION))
		{
			buffer.writeShort(18);
			buffer.writeInt(_player.getX());
			buffer.writeInt(_player.getY());
			buffer.writeInt(_player.getZ());
			buffer.writeInt(_player.isInVehicle() ? _player.getVehicle().getObjectId() : 0);
		}
		if (containsMask(UserInfoType.SPEED))
		{
			buffer.writeShort(18);
			buffer.writeShort(_runSpd);
			buffer.writeShort(_walkSpd);
			buffer.writeShort(_swimRunSpd);
			buffer.writeShort(_swimWalkSpd);
			buffer.writeShort(_flRunSpd);
			buffer.writeShort(_flWalkSpd);
			buffer.writeShort(_flyRunSpd);
			buffer.writeShort(_flyWalkSpd);
		}
		if (containsMask(UserInfoType.MULTIPLIER))
		{
			buffer.writeShort(18);
			buffer.writeDouble(_moveMultiplier);
			buffer.writeDouble(_player.getAttackSpeedMultiplier());
		}
		if (containsMask(UserInfoType.COL_RADIUS_HEIGHT))
		{
			buffer.writeShort(18);
			buffer.writeDouble(_player.getCollisionRadius());
			buffer.writeDouble(_player.getCollisionHeight());
		}
		if (containsMask(UserInfoType.ATK_ELEMENTAL))
		{
			buffer.writeShort(5);
			buffer.writeByte(0);
			buffer.writeShort(0);
		}
		if (containsMask(UserInfoType.CLAN))
		{
			buffer.writeShort(32 + (_title.length() * 2));
			buffer.writeSizedString(_title);
			buffer.writeShort(_player.getPledgeType());
			buffer.writeInt(_player.getClanId());
			buffer.writeInt(_player.getClanCrestLargeId());
			buffer.writeInt(_player.getClanCrestId());
			buffer.writeInt(_player.getClanPrivileges().getBitmask());
			buffer.writeByte(_player.isClanLeader());
			buffer.writeInt(_player.getAllyId());
			buffer.writeInt(_player.getAllyCrestId());
			buffer.writeByte(_player.isInMatchingRoom());
		}
		if (containsMask(UserInfoType.SOCIAL))
		{
			buffer.writeShort(22);
			buffer.writeByte(_player.getPvpFlag());
			buffer.writeInt(_player.getReputation()); // Reputation
			buffer.writeByte(_player.isNoble());
			buffer.writeByte(_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA));
			buffer.writeByte(_player.getPledgeClass());
			buffer.writeInt(_player.getPkKills());
			buffer.writeInt(_player.getPvpKills());
			buffer.writeShort(_player.getRecomLeft());
			buffer.writeShort(_player.getRecomHave());
		}
		if (containsMask(UserInfoType.VITA_FAME))
		{
			buffer.writeShort(15);
			buffer.writeInt(_player.getVitalityPoints());
			buffer.writeByte(0); // Vita Bonus
			buffer.writeInt(_player.getFame());
			buffer.writeInt(_player.getRaidbossPoints());
		}
		if (containsMask(UserInfoType.SLOTS))
		{
			buffer.writeShort(9);
			buffer.writeByte(_player.getInventory().getTalismanSlots()); // Confirmed
			buffer.writeByte(_player.getInventory().getBroochJewelSlots()); // Confirmed
			buffer.writeByte(_player.getTeam().getId()); // Confirmed
			buffer.writeByte(0); // (1 = Red, 2 = White, 3 = White Pink) dotted ring on the floor
			buffer.writeByte(0);
			buffer.writeByte(0);
			buffer.writeByte(0);
		}
		if (containsMask(UserInfoType.MOVEMENTS))
		{
			buffer.writeShort(4);
			buffer.writeByte(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
			buffer.writeByte(_player.isRunning());
		}
		if (containsMask(UserInfoType.COLOR))
		{
			buffer.writeShort(10);
			buffer.writeInt(_player.getAppearance().getNameColor());
			buffer.writeInt(_player.getAppearance().getTitleColor());
		}
		if (containsMask(UserInfoType.INVENTORY_LIMIT))
		{
			buffer.writeShort(9);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(_player.getInventoryLimit());
			buffer.writeByte(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
		}
		if (containsMask(UserInfoType.TRUE_HERO))
		{
			buffer.writeShort(9);
			buffer.writeInt(0);
			buffer.writeShort(0);
			buffer.writeByte(_player.isTrueHero() ? 100 : 0);
		}
	}
	
	private int calculateRelation(Player player)
	{
		int relation = 0;
		final Party party = player.getParty();
		final Clan clan = player.getClan();
		if (party != null)
		{
			relation |= 0x08; // Party member
			if (party.getLeader() == _player)
			{
				relation |= 0x10; // Party leader
			}
		}
		if (clan != null)
		{
			relation |= 0x20; // Clan member
			if (clan.getLeaderId() == player.getObjectId())
			{
				relation |= 0x40; // Clan leader
			}
		}
		if (player.isInSiege())
		{
			relation |= 0x80; // In siege
		}
		return relation;
	}
}
