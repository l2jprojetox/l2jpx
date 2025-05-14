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

import java.util.LinkedList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Playable;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Luca Baldi
 */
public class RelationChanged extends ServerPacket
{
	// TODO: Enum
	public static final int RELATION_PARTY1 = 0x1; // party member
	public static final int RELATION_PARTY2 = 0x2; // party member
	public static final int RELATION_PARTY3 = 0x4; // party member
	public static final int RELATION_PARTY4 = 0x8; // party member (for information, see Player.getRelation())
	public static final int RELATION_PARTYLEADER = 0x10; // true if is party leader
	public static final int RELATION_HAS_PARTY = 0x20; // true if is in party
	public static final int RELATION_CLAN_MEMBER = 0x40; // true if is in clan
	public static final int RELATION_LEADER = 0x80; // true if is clan leader
	public static final int RELATION_CLAN_MATE = 0x100; // true if is in same clan
	public static final int RELATION_INSIEGE = 0x200; // true if in siege
	public static final int RELATION_ATTACKER = 0x400; // true when attacker
	public static final int RELATION_ALLY = 0x800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 0x1000; // true when red icon, doesn't matter with blue
	public static final int RELATION_DECLARED_WAR = 0x4000; // single sword
	public static final int RELATION_MUTUAL_WAR = 0x8000; // double swords
	public static final int RELATION_ALLY_MEMBER = 0x10000; // clan is in alliance
	public static final int RELATION_TERRITORY_WAR = 0x80000; // show Territory War icon
	// Masks
	public static final byte SEND_DEFAULT = 0x01;
	public static final byte SEND_ONE = 0x02;
	public static final byte SEND_MULTI = 0x04;
	
	protected static class Relation
	{
		int _objId;
		int _relation;
		boolean _autoAttackable;
		int _reputation;
		int _pvpFlag;
	}
	
	private Relation _singled;
	private final List<Relation> _multi;
	private byte _mask = 0x00;
	
	public RelationChanged(Playable activeChar, int relation, boolean autoattackable)
	{
		_mask |= SEND_ONE;
		_singled = new Relation();
		_singled._objId = activeChar.getObjectId();
		_singled._relation = relation;
		_singled._autoAttackable = autoattackable;
		_singled._reputation = activeChar.getReputation();
		_singled._pvpFlag = activeChar.getPvpFlag();
		_multi = null;
	}
	
	public RelationChanged()
	{
		_mask |= SEND_MULTI;
		_multi = new LinkedList<>();
	}
	
	public void addRelation(Playable activeChar, int relation, boolean autoattackable)
	{
		if (activeChar.isInvisible())
		{
			return;
		}
		final Relation r = new Relation();
		r._objId = activeChar.getObjectId();
		r._relation = relation;
		r._autoAttackable = autoattackable;
		r._reputation = activeChar.getReputation();
		r._pvpFlag = activeChar.getPvpFlag();
		_multi.add(r);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.RELATION_CHANGED.writeId(this, buffer);
		buffer.writeByte(_mask);
		if (_multi == null)
		{
			writeRelation(_singled, buffer);
		}
		else
		{
			buffer.writeShort(_multi.size());
			for (Relation r : _multi)
			{
				writeRelation(r, buffer);
			}
		}
	}
	
	private void writeRelation(Relation relation, WritableBuffer buffer)
	{
		buffer.writeInt(relation._objId);
		if ((_mask & SEND_DEFAULT) != SEND_DEFAULT)
		{
			buffer.writeInt(relation._relation);
			buffer.writeByte(relation._autoAttackable);
			buffer.writeInt(relation._reputation);
			buffer.writeByte(relation._pvpFlag);
		}
	}
}
