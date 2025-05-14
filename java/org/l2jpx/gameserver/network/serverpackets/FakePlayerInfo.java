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

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.data.xml.FakePlayerData;
import org.l2jpx.gameserver.enums.Sex;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.holders.FakePlayerHolder;
import org.l2jpx.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class FakePlayerInfo extends ServerPacket
{
	private final Npc _npc;
	private final int _objId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
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
	private final FakePlayerHolder _fpcHolder;
	private final Clan _clan;
	
	public FakePlayerInfo(Npc npc)
	{
		_npc = npc;
		_objId = npc.getObjectId();
		_x = npc.getX();
		_y = npc.getY();
		_z = npc.getZ();
		_heading = npc.getHeading();
		_mAtkSpd = npc.getMAtkSpd();
		_pAtkSpd = npc.getPAtkSpd();
		_attackSpeedMultiplier = (float) npc.getAttackSpeedMultiplier();
		_moveMultiplier = npc.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(npc.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(npc.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(npc.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(npc.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = npc.isFlying() ? _runSpd : 0;
		_flyWalkSpd = npc.isFlying() ? _walkSpd : 0;
		_fpcHolder = FakePlayerData.getInstance().getInfo(npc.getId());
		_clan = ClanTable.getInstance().getClan(_fpcHolder.getClanId());
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.CHAR_INFO.writeId(this, buffer);
		buffer.writeByte(0); // Grand Crusade
		buffer.writeInt(_x);
		buffer.writeInt(_y);
		buffer.writeInt(_z);
		buffer.writeInt(0); // vehicleId
		buffer.writeInt(_objId);
		buffer.writeString(_npc.getName());
		buffer.writeShort(_npc.getRace().ordinal());
		buffer.writeByte(_npc.getTemplate().getSex() == Sex.FEMALE);
		buffer.writeInt(_fpcHolder.getClassId());
		buffer.writeInt(0); // Inventory.PAPERDOLL_UNDER
		buffer.writeInt(_fpcHolder.getEquipHead());
		buffer.writeInt(_fpcHolder.getEquipRHand());
		buffer.writeInt(_fpcHolder.getEquipLHand());
		buffer.writeInt(_fpcHolder.getEquipGloves());
		buffer.writeInt(_fpcHolder.getEquipChest());
		buffer.writeInt(_fpcHolder.getEquipLegs());
		buffer.writeInt(_fpcHolder.getEquipFeet());
		buffer.writeInt(_fpcHolder.getEquipCloak());
		buffer.writeInt(_fpcHolder.getEquipRHand()); // dual hand
		buffer.writeInt(_fpcHolder.getEquipHair());
		buffer.writeInt(_fpcHolder.getEquipHair2());
		for (@SuppressWarnings("unused")
		final int slot : getPaperdollOrderAugument())
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		buffer.writeByte(_fpcHolder.getArmorEnchantLevel());
		for (@SuppressWarnings("unused")
		final int slot : getPaperdollOrderVisualId())
		{
			buffer.writeInt(0);
		}
		buffer.writeByte(_npc.getScriptValue()); // getPvpFlag()
		buffer.writeInt(_npc.getReputation());
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
		buffer.writeDouble(_npc.getCollisionRadius());
		buffer.writeDouble(_npc.getCollisionHeight());
		buffer.writeInt(_fpcHolder.getHair());
		buffer.writeInt(_fpcHolder.getHairColor());
		buffer.writeInt(_fpcHolder.getFace());
		buffer.writeString(_npc.getTemplate().getTitle());
		if (_clan != null)
		{
			buffer.writeInt(_clan.getId());
			buffer.writeInt(_clan.getCrestId());
			buffer.writeInt(_clan.getAllyId());
			buffer.writeInt(_clan.getAllyCrestId());
		}
		else
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		buffer.writeByte(1); // isSitting() ? 0 : 1 (at some initial tests it worked)
		buffer.writeByte(_npc.isRunning());
		buffer.writeByte(_npc.isInCombat());
		buffer.writeByte(_npc.isAlikeDead());
		buffer.writeByte(_npc.isInvisible());
		buffer.writeByte(0); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		buffer.writeByte(0); // getPrivateStoreType().getId()
		buffer.writeShort(0); // getCubics().size()
		// getCubics().keySet().forEach(packet::writeH);
		buffer.writeByte(0);
		buffer.writeByte(_npc.isInsideZone(ZoneId.WATER));
		buffer.writeShort(_fpcHolder.getRecommends());
		buffer.writeInt(0); // getMountNpcId() == 0 ? 0 : getMountNpcId() + 1000000
		buffer.writeInt(_fpcHolder.getClassId());
		buffer.writeInt(0);
		buffer.writeByte(_fpcHolder.getWeaponEnchantLevel()); // isMounted() ? 0 : _enchantLevel
		buffer.writeByte(_npc.getTeam().getId());
		buffer.writeInt(_clan != null ? _clan.getCrestLargeId() : 0);
		buffer.writeByte(_fpcHolder.getNobleLevel());
		buffer.writeByte(_fpcHolder.isHero());
		buffer.writeByte(_fpcHolder.isFishing());
		buffer.writeInt(_fpcHolder.getBaitLocationX());
		buffer.writeInt(_fpcHolder.getBaitLocationY());
		buffer.writeInt(_fpcHolder.getBaitLocationZ());
		buffer.writeInt(_fpcHolder.getNameColor());
		buffer.writeInt(_heading);
		buffer.writeByte(_fpcHolder.getPledgeStatus());
		buffer.writeShort(0); // getPledgeType()
		buffer.writeInt(_fpcHolder.getTitleColor());
		buffer.writeByte(0); // isCursedWeaponEquipped
		buffer.writeInt(0); // getAppearance().getVisibleClanId() > 0 ? getClan().getReputationScore() : 0
		buffer.writeInt(0); // getTransformationDisplayId()
		buffer.writeInt(_fpcHolder.getAgathionId());
		buffer.writeByte(0);
		buffer.writeInt(0); // getCurrentCp()
		buffer.writeInt(_npc.getMaxHp());
		buffer.writeInt((int) Math.round(_npc.getCurrentHp()));
		buffer.writeInt(_npc.getMaxMp());
		buffer.writeInt((int) Math.round(_npc.getCurrentMp()));
		buffer.writeByte(0);
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
		buffer.writeInt(abnormalVisualEffects.size() + (_npc.isInvisible() ? 1 : 0));
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			buffer.writeShort(abnormalVisualEffect.getClientId());
		}
		if (_npc.isInvisible())
		{
			buffer.writeShort(AbnormalVisualEffect.STEALTH.getClientId());
		}
		buffer.writeByte(0); // cocPlayer.getPosition()
		buffer.writeByte((_fpcHolder.getHair() > 0) || (_fpcHolder.getEquipHair2() > 0));
		buffer.writeByte(0); // Used Ability Points
	}
}
