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
import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.enums.MountType;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.enums.TeleportWhereType;
import org.l2jpx.gameserver.instancemanager.FortManager;
import org.l2jpx.gameserver.instancemanager.FortSiegeManager;
import org.l2jpx.gameserver.instancemanager.ZoneManager;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.model.siege.FortSiege;
import org.l2jpx.gameserver.model.siege.Siegable;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.zone.AbstractZoneSettings;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.model.zone.ZoneType;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * A siege zone
 * @author durgus
 */
public class SiegeZone extends ZoneType
{
	private static final int DISMOUNT_DELAY = 5;
	
	public SiegeZone(int id)
	{
		super(id);
		AbstractZoneSettings settings = ZoneManager.getSettings(getName());
		if (settings == null)
		{
			settings = new Settings();
		}
		setSettings(settings);
	}
	
	public class Settings extends AbstractZoneSettings
	{
		private int _siegableId = -1;
		private Siegable _siege = null;
		private boolean _isActiveSiege = false;
		
		protected Settings()
		{
		}
		
		public int getSiegeableId()
		{
			return _siegableId;
		}
		
		protected void setSiegeableId(int id)
		{
			_siegableId = id;
		}
		
		public Siegable getSiege()
		{
			return _siege;
		}
		
		public void setSiege(Siegable s)
		{
			_siege = s;
		}
		
		public boolean isActiveSiege()
		{
			return _isActiveSiege;
		}
		
		public void setActiveSiege(boolean value)
		{
			_isActiveSiege = value;
		}
		
		@Override
		public void clear()
		{
			_siegableId = -1;
			_siege = null;
			_isActiveSiege = false;
		}
	}
	
	@Override
	public Settings getSettings()
	{
		return (Settings) super.getSettings();
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
		{
			if (getSettings().getSiegeableId() != -1)
			{
				throw new IllegalArgumentException("Siege object already defined!");
			}
			getSettings().setSiegeableId(Integer.parseInt(value));
		}
		else if (name.equals("fortId"))
		{
			if (getSettings().getSiegeableId() != -1)
			{
				throw new IllegalArgumentException("Siege object already defined!");
			}
			getSettings().setSiegeableId(Integer.parseInt(value));
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (getSettings().isActiveSiege())
		{
			creature.setInsideZone(ZoneId.PVP, true);
			creature.setInsideZone(ZoneId.SIEGE, true);
			creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true); // FIXME: Custom ?
			
			if (creature.isPlayer())
			{
				final Player player = creature.getActingPlayer();
				if (player.isRegisteredOnThisSiegeField(getSettings().getSiegeableId()))
				{
					player.setInSiege(true); // in siege
					if (getSettings().getSiege().giveFame() && (getSettings().getSiege().getFameFrequency() > 0))
					{
						player.startFameTask(getSettings().getSiege().getFameFrequency() * 1000, getSettings().getSiege().getFameAmount());
					}
				}
				
				creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
				if (!Config.ALLOW_WYVERN_DURING_SIEGE && (player.getMountType() == MountType.WYVERN))
				{
					player.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
					player.enteredNoLanding(DISMOUNT_DELAY);
				}
				
				if (!Config.ALLOW_MOUNTS_DURING_SIEGE && player.isMounted())
				{
					player.dismount();
				}
				
				if (!Config.ALLOW_MOUNTS_DURING_SIEGE && player.isTransformed() && player.getTransformation().get().isRiding())
				{
					player.untransform();
				}
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.PVP, false);
		creature.setInsideZone(ZoneId.SIEGE, false);
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false); // FIXME: Custom ?
		if (getSettings().isActiveSiege() && creature.isPlayer())
		{
			final Player player = creature.getActingPlayer();
			creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
			if (player.getMountType() == MountType.WYVERN)
			{
				player.exitedNoLanding();
			}
			// Set pvp flag
			if (player.getPvpFlag() == 0)
			{
				player.startPvPFlag();
			}
		}
		if (creature.isPlayer())
		{
			final Player player = creature.getActingPlayer();
			player.stopFameTask();
			player.setInSiege(false);
			
			if ((getSettings().getSiege() instanceof FortSiege) && (player.getInventory().getItemByItemId(9819) != null))
			{
				// drop combat flag
				final Fort fort = FortManager.getInstance().getFortById(getSettings().getSiegeableId());
				if (fort != null)
				{
					FortSiegeManager.getInstance().dropCombatFlag(player, fort.getResidenceId());
				}
				else
				{
					final int slot = player.getInventory().getSlotFromItem(player.getInventory().getItemByItemId(9819));
					player.getInventory().unEquipItemInBodySlot(slot);
					player.destroyItem("CombatFlag", player.getInventory().getItemByItemId(9819), null, true);
				}
			}
			
			if (player.hasServitors())
			{
				player.getServitors().values().forEach(servitor ->
				{
					if (servitor.getRace() == Race.SIEGE_WEAPON)
					{
						servitor.abortAttack();
						servitor.abortCast();
						servitor.stopAllEffects();
						servitor.unSummon(player);
					}
				});
			}
		}
	}
	
	@Override
	public void onDieInside(Creature creature)
	{
		// debuff participants only if they die inside siege zone
		if (getSettings().isActiveSiege() && creature.isPlayer() && creature.getActingPlayer().isRegisteredOnThisSiegeField(getSettings().getSiegeableId()))
		{
			int level = 1;
			final BuffInfo info = creature.getEffectList().getBuffInfoBySkillId(5660);
			if (info != null)
			{
				level = Math.min(level + info.getSkill().getLevel(), 5);
			}
			
			final Skill skill = SkillData.getInstance().getSkill(5660, level);
			if (skill != null)
			{
				skill.applyEffects(creature, creature);
			}
		}
	}
	
	@Override
	public void onPlayerLogoutInside(Player player)
	{
		if (player.getClanId() != getSettings().getSiegeableId())
		{
			player.teleToLocation(TeleportWhereType.TOWN);
		}
	}
	
	public void updateZoneStatusForCharactersInside()
	{
		if (getSettings().isActiveSiege())
		{
			for (Creature creature : getCharactersInside())
			{
				if (creature != null)
				{
					onEnter(creature);
				}
			}
		}
		else
		{
			Player player;
			for (Creature creature : getCharactersInside())
			{
				if (creature == null)
				{
					continue;
				}
				
				creature.setInsideZone(ZoneId.PVP, false);
				creature.setInsideZone(ZoneId.SIEGE, false);
				creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
				
				if (creature.isPlayer())
				{
					player = creature.getActingPlayer();
					creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
					player.stopFameTask();
					if (player.getMountType() == MountType.WYVERN)
					{
						player.exitedNoLanding();
					}
				}
			}
		}
	}
	
	/**
	 * Sends a message to all players in this zone
	 * @param message
	 */
	public void announceToPlayers(String message)
	{
		for (Player player : getPlayersInside())
		{
			if (player != null)
			{
				player.sendMessage(message);
			}
		}
	}
	
	public int getSiegeObjectId()
	{
		return getSettings().getSiegeableId();
	}
	
	public boolean isActive()
	{
		return getSettings().isActiveSiege();
	}
	
	public void setActive(boolean value)
	{
		getSettings().setActiveSiege(value);
	}
	
	public void setSiegeInstance(Siegable siege)
	{
		getSettings().setSiege(siege);
	}
	
	/**
	 * Removes all foreigners from the zone
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		for (Player temp : getPlayersInside())
		{
			if (temp.getClanId() == owningClanId)
			{
				continue;
			}
			temp.teleToLocation(TeleportWhereType.TOWN);
		}
	}
}
