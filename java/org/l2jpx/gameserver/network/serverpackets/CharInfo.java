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

import java.util.Set;

import org.l2jpx.Config;
import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jpx.gameserver.model.VariationInstance;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Decoy;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.interfaces.ILocational;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class CharInfo extends ServerPacket
{
	private static final int[] PAPERDOLL_ORDER = new int[]
	{
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_CLOAK,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2
	};
	
	private final Player _player;
	private final Clan _clan;
	private int _objId;
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	private final float _attackSpeedMultiplier;
	private int _enchantLevel = 0;
	private int _armorEnchant = 0;
	private int _vehicleId = 0;
	private final boolean _gmSeeInvis;
	
	public CharInfo(Player player, boolean gmSeeInvis)
	{
		_player = player;
		_objId = player.getObjectId();
		_clan = player.getClan();
		if ((_player.getVehicle() != null) && (_player.getInVehiclePosition() != null))
		{
			_x = _player.getInVehiclePosition().getX();
			_y = _player.getInVehiclePosition().getY();
			_z = _player.getInVehiclePosition().getZ();
			_vehicleId = _player.getVehicle().getObjectId();
		}
		else
		{
			_x = _player.getX();
			_y = _player.getY();
			_z = _player.getZ();
		}
		_heading = _player.getHeading();
		_mAtkSpd = _player.getMAtkSpd();
		_pAtkSpd = _player.getPAtkSpd();
		_attackSpeedMultiplier = (float) _player.getAttackSpeedMultiplier();
		_moveMultiplier = player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
		_enchantLevel = player.getInventory().getWeaponEnchant();
		_armorEnchant = player.getInventory().getArmorSetEnchant();
		_gmSeeInvis = gmSeeInvis;
	}
	
	public CharInfo(Decoy decoy, boolean gmSeeInvis)
	{
		this(decoy.getActingPlayer(), gmSeeInvis); // init
		_objId = decoy.getObjectId();
		_x = decoy.getX();
		_y = decoy.getY();
		_z = decoy.getZ();
		_heading = decoy.getHeading();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.CHAR_INFO.writeId(this, buffer);
		buffer.writeByte(0); // Grand Crusade
		buffer.writeInt(_x); // Confirmed
		buffer.writeInt(_y); // Confirmed
		buffer.writeInt(_z); // Confirmed
		buffer.writeInt(_vehicleId); // Confirmed
		buffer.writeInt(_objId); // Confirmed
		buffer.writeString(_player.getAppearance().getVisibleName()); // Confirmed
		buffer.writeShort(_player.getRace().ordinal()); // Confirmed
		buffer.writeByte(_player.getAppearance().isFemale()); // Confirmed
		buffer.writeInt(_player.getBaseTemplate().getClassId().getRootClassId().getId());
		
		for (int slot : getPaperdollOrder())
		{
			buffer.writeInt(_player.getInventory().getPaperdollItemDisplayId(slot)); // Confirmed
		}
		
		for (int slot : getPaperdollOrderAugument())
		{
			final VariationInstance augment = _player.getInventory().getPaperdollAugmentation(slot);
			buffer.writeInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
			buffer.writeInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
		}
		
		buffer.writeByte(_armorEnchant);
		
		for (int slot : getPaperdollOrderVisualId())
		{
			buffer.writeInt(_player.getInventory().getPaperdollItemVisualId(slot));
		}
		
		buffer.writeByte(_player.getPvpFlag());
		buffer.writeInt(_player.getReputation());
		buffer.writeInt(_mAtkSpd);
		buffer.writeInt(_pAtkSpd);
		buffer.writeShort(_runSpd);
		buffer.writeShort(_walkSpd);
		buffer.writeShort(_swimRunSpd);
		buffer.writeShort(_swimWalkSpd);
		buffer.writeShort(_flyRunSpd);
		buffer.writeShort(_flyWalkSpd);
		buffer.writeShort(_flyRunSpd);
		buffer.writeShort(_flyWalkSpd);
		buffer.writeDouble(_moveMultiplier);
		buffer.writeDouble(_attackSpeedMultiplier);
		buffer.writeDouble(_player.getCollisionRadius());
		buffer.writeDouble(_player.getCollisionHeight());
		buffer.writeInt(_player.getVisualHair());
		buffer.writeInt(_player.getVisualHairColor());
		buffer.writeInt(_player.getVisualFace());
		buffer.writeString(_gmSeeInvis ? "Invisible" : _player.getAppearance().getVisibleTitle());
		buffer.writeInt(_player.getAppearance().getVisibleClanId());
		buffer.writeInt(_player.getAppearance().getVisibleClanCrestId());
		buffer.writeInt(_player.getAppearance().getVisibleAllyId());
		buffer.writeInt(_player.getAppearance().getVisibleAllyCrestId());
		buffer.writeByte(!_player.isSitting()); // Confirmed
		buffer.writeByte(_player.isRunning()); // Confirmed
		buffer.writeByte(_player.isInCombat()); // Confirmed
		buffer.writeByte(!_player.isInOlympiadMode() && _player.isAlikeDead()); // Confirmed
		buffer.writeByte(_player.isInvisible());
		buffer.writeByte(_player.getMountType().ordinal()); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		buffer.writeByte(_player.getPrivateStoreType().getId()); // Confirmed
		
		buffer.writeShort(_player.getCubics().size()); // Confirmed
		_player.getCubics().keySet().forEach(buffer::writeShort);
		
		buffer.writeByte(_player.isInMatchingRoom()); // Confirmed
		buffer.writeByte(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
		buffer.writeShort(_player.getRecomHave()); // Confirmed
		buffer.writeInt(_player.getMountNpcId() == 0 ? 0 : _player.getMountNpcId() + 1000000);
		buffer.writeInt(_player.getClassId().getId()); // Confirmed
		buffer.writeInt(0); // TODO: Find me!
		buffer.writeByte(_player.isMounted() ? 0 : _enchantLevel); // Confirmed
		buffer.writeByte(_player.getTeam().getId()); // Confirmed
		buffer.writeInt(_player.getClanCrestLargeId());
		buffer.writeByte(_player.isNoble()); // Confirmed
		buffer.writeByte(_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA)); // Confirmed
		
		buffer.writeByte(_player.isFishing()); // Confirmed
		final ILocational baitLocation = _player.getFishing().getBaitLocation();
		if (baitLocation != null)
		{
			buffer.writeInt(baitLocation.getX()); // Confirmed
			buffer.writeInt(baitLocation.getY()); // Confirmed
			buffer.writeInt(baitLocation.getZ()); // Confirmed
		}
		else
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		
		buffer.writeInt(_player.getAppearance().getNameColor()); // Confirmed
		buffer.writeInt(_heading); // Confirmed
		buffer.writeByte(_player.getPledgeClass());
		buffer.writeShort(_player.getPledgeType());
		buffer.writeInt(_player.getAppearance().getTitleColor()); // Confirmed
		buffer.writeByte(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
		buffer.writeInt(_clan != null ? _clan.getReputationScore() : 0);
		buffer.writeInt(_player.getTransformationDisplayId()); // Confirmed
		buffer.writeInt(_player.getAgathionId()); // Confirmed
		buffer.writeByte(0); // nPvPRestrainStatus
		buffer.writeInt((int) Math.round(_player.getCurrentCp())); // Confirmed
		buffer.writeInt(_player.getMaxHp()); // Confirmed
		buffer.writeInt((int) Math.round(_player.getCurrentHp())); // Confirmed
		buffer.writeInt(_player.getMaxMp()); // Confirmed
		buffer.writeInt((int) Math.round(_player.getCurrentMp())); // Confirmed
		buffer.writeByte(0); // cBRLectureMark
		
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _player.getEffectList().getCurrentAbnormalVisualEffects();
		buffer.writeInt(abnormalVisualEffects.size() + (_gmSeeInvis ? 1 : 0)); // Confirmed
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			buffer.writeShort(abnormalVisualEffect.getClientId()); // Confirmed
		}
		if (_gmSeeInvis)
		{
			buffer.writeShort(AbnormalVisualEffect.STEALTH.getClientId());
		}
		
		buffer.writeByte(_player.isTrueHero() ? 100 : 0);
		buffer.writeByte(_player.isHairAccessoryEnabled()); // Hair accessory
		buffer.writeByte(_player.getAbilityPointsUsed()); // Used Ability Points
	}
	
	@Override
	public int[] getPaperdollOrder()
	{
		return PAPERDOLL_ORDER;
	}
}
