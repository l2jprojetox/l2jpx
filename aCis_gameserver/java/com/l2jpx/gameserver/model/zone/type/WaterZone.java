package com.l2jpx.gameserver.model.zone.type;

import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.enums.actors.MoveType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.zone.type.subtype.ZoneType;
import com.l2jpx.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import com.l2jpx.gameserver.network.serverpackets.ServerObjectInfo;

/**
 * A zone extending {@link ZoneType}, used for the water behavior. {@link Player}s can drown if they stay too long below water line.
 */
public class WaterZone extends ZoneType
{
	public WaterZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.WATER, true);
		character.getMove().addMoveType(MoveType.SWIM);
		
		if (character instanceof Player)
			((Player) character).broadcastUserInfo();
		else if (character instanceof Npc)
		{
			for (Player player : character.getKnownType(Player.class))
			{
				if (character.getStatus().getMoveSpeed() == 0)
					player.sendPacket(new ServerObjectInfo((Npc) character, player));
				else
					player.sendPacket(new NpcInfo((Npc) character, player));
			}
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.WATER, false);
		character.getMove().removeMoveType(MoveType.SWIM);
		
		if (character instanceof Player)
			((Player) character).broadcastUserInfo();
		else if (character instanceof Npc)
		{
			for (Player player : character.getKnownType(Player.class))
			{
				if (character.getStatus().getMoveSpeed() == 0)
					player.sendPacket(new ServerObjectInfo((Npc) character, player));
				else
					player.sendPacket(new NpcInfo((Npc) character, player));
			}
		}
	}
	
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}