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
package org.l2jpx.gameserver.model.zone.type;

import org.l2jpx.Config;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.model.zone.ZoneType;

/**
 * A Peace Zone
 * @author durgus
 */
public class PeaceZone extends ZoneType
{
	public PeaceZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (!isEnabled())
		{
			return;
		}
		
		if (creature.isPlayer())
		{
			final Player player = creature.getActingPlayer();
			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if ((player.getSiegeState() != 0) && (Config.PEACE_ZONE_MODE == 1))
			{
				return;
			}
		}
		
		if (Config.PEACE_ZONE_MODE != 2)
		{
			creature.setInsideZone(ZoneId.PEACE, true);
		}
		
		if (!getAllowStore())
		{
			creature.setInsideZone(ZoneId.NO_STORE, true);
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (Config.PEACE_ZONE_MODE != 2)
		{
			creature.setInsideZone(ZoneId.PEACE, false);
		}
		
		if (!getAllowStore())
		{
			creature.setInsideZone(ZoneId.NO_STORE, false);
		}
	}
	
	@Override
	public void setEnabled(boolean value)
	{
		super.setEnabled(value);
		if (value)
		{
			for (Player player : World.getInstance().getPlayers())
			{
				if ((player != null) && isInsideZone(player))
				{
					revalidateInZone(player);
					
					if (player.getPet() != null)
					{
						revalidateInZone(player.getPet());
					}
					
					for (Summon summon : player.getServitors().values())
					{
						revalidateInZone(summon);
					}
				}
			}
		}
		else
		{
			for (Creature creature : getCharactersInside())
			{
				if (creature != null)
				{
					removeCharacter(creature);
				}
			}
		}
	}
}
