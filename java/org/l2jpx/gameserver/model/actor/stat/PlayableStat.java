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
package org.l2jpx.gameserver.model.actor.stat;

import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.ExperienceData;
import org.l2jpx.gameserver.data.xml.PetDataTable;
import org.l2jpx.gameserver.data.xml.SkillTreeData;
import org.l2jpx.gameserver.model.actor.Playable;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.ClanMember;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayableExpChanged;
import org.l2jpx.gameserver.model.events.returns.TerminateReturn;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.model.variables.PlayerVariables;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExNewSkillToLearnByLevelUp;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class PlayableStat extends CreatureStat
{
	protected static final Logger LOGGER = Logger.getLogger(PlayableStat.class.getName());
	
	public PlayableStat(Playable player)
	{
		super(player);
	}
	
	public boolean addExp(long amount)
	{
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYABLE_EXP_CHANGED, getActiveChar()))
		{
			final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayableExpChanged(getActiveChar(), getExp(), getExp() + amount), getActiveChar(), TerminateReturn.class);
			if ((term != null) && term.terminate())
			{
				return false;
			}
		}
		
		if (((getExp() + amount) < 0) || ((amount > 0) && (getExp() == (getExpForLevel(getMaxLevel()) - 1))))
		{
			return true;
		}
		
		long value = amount;
		if ((getExp() + value) >= getExpForLevel(getMaxLevel()))
		{
			value = getExpForLevel(getMaxLevel()) - 1 - getExp();
		}
		
		final int oldLevel = getLevel();
		setExp(getExp() + value);
		byte minimumLevel = 1;
		if (getActiveChar().isPet())
		{
			// get minimum level from NpcTemplate
			minimumLevel = (byte) PetDataTable.getInstance().getPetMinLevel(((Pet) getActiveChar()).getTemplate().getId());
		}
		
		byte level = minimumLevel; // minimum level
		for (byte tmp = level; tmp <= getMaxLevel(); tmp++)
		{
			if (getExp() >= getExpForLevel(tmp))
			{
				continue;
			}
			level = --tmp;
			break;
		}
		
		if ((level != getLevel()) && (level >= minimumLevel))
		{
			addLevel((byte) (level - getLevel()));
		}
		
		byte newLevel = getLevel();
		if ((newLevel > oldLevel) && getActiveChar().isPlayer())
		{
			final Player player = getActiveChar().getActingPlayer();
			if (SkillTreeData.getInstance().hasAvailableSkills(player, player.getClassId()))
			{
				getActiveChar().sendPacket(ExNewSkillToLearnByLevelUp.STATIC_PACKET);
			}
			
			// Check last rewarded level - prevent reputation farming via deleveling
			int lastPledgedLevel = player.getVariables().getInt(PlayerVariables.LAST_PLEDGE_REPUTATION_LEVEL, 0);
			if (lastPledgedLevel < newLevel)
			{
				int leveledUpCount = newLevel - lastPledgedLevel;
				addReputationToClanBasedOnLevel(player, leveledUpCount);
				
				player.getVariables().set(PlayerVariables.LAST_PLEDGE_REPUTATION_LEVEL, (int) newLevel);
			}
		}
		
		return true;
	}
	
	public boolean removeExp(long amount)
	{
		long value = amount;
		if (((getExp() - value) < getExpForLevel(getLevel())) && (!Config.PLAYER_DELEVEL || (Config.PLAYER_DELEVEL && (getLevel() <= Config.DELEVEL_MINIMUM))))
		{
			value = getExp() - getExpForLevel(getLevel());
		}
		
		if ((getExp() - value) < 0)
		{
			value = getExp() - 1;
		}
		
		setExp(getExp() - value);
		byte minimumLevel = 1;
		if (getActiveChar().isPet())
		{
			// get minimum level from NpcTemplate
			minimumLevel = (byte) PetDataTable.getInstance().getPetMinLevel(((Pet) getActiveChar()).getTemplate().getId());
		}
		byte level = minimumLevel;
		for (byte tmp = level; tmp <= getMaxLevel(); tmp++)
		{
			if (getExp() >= getExpForLevel(tmp))
			{
				continue;
			}
			level = --tmp;
			break;
		}
		if ((level != getLevel()) && (level >= minimumLevel))
		{
			addLevel((byte) (level - getLevel()));
		}
		return true;
	}
	
	public boolean removeExpAndSp(long removeExp, long removeSp)
	{
		boolean expRemoved = false;
		boolean spRemoved = false;
		if (removeExp > 0)
		{
			expRemoved = removeExp(removeExp);
		}
		if (removeSp > 0)
		{
			spRemoved = removeSp(removeSp);
		}
		return expRemoved || spRemoved;
	}
	
	public boolean addLevel(byte amount)
	{
		byte value = amount;
		if ((getLevel() + value) > (getMaxLevel() - 1))
		{
			if (getLevel() < (getMaxLevel() - 1))
			{
				value = (byte) (getMaxLevel() - 1 - getLevel());
			}
			else
			{
				return false;
			}
		}
		
		final boolean levelIncreased = (getLevel() + value) > getLevel();
		value += getLevel();
		setLevel(value);
		
		// Sync up exp with current level
		if ((getExp() >= getExpForLevel(getLevel() + 1)) || (getExpForLevel(getLevel()) > getExp()))
		{
			setExp(getExpForLevel(getLevel()));
		}
		
		if (!levelIncreased && getActiveChar().isPlayer() && !getActiveChar().isGM() && Config.DECREASE_SKILL_LEVEL)
		{
			((Player) getActiveChar()).checkPlayerSkills();
		}
		
		if (!levelIncreased)
		{
			return false;
		}
		
		getActiveChar().getStatus().setCurrentHp(getActiveChar().getStat().getMaxHp());
		getActiveChar().getStatus().setCurrentMp(getActiveChar().getStat().getMaxMp());
		
		return true;
	}
	
	public boolean addSp(long amount)
	{
		if (amount < 0)
		{
			LOGGER.warning("wrong usage");
			return false;
		}
		
		final long currentSp = getSp();
		if (currentSp >= Config.MAX_SP)
		{
			return false;
		}
		
		long value = amount;
		if (currentSp > (Config.MAX_SP - value))
		{
			value = Config.MAX_SP - currentSp;
		}
		
		setSp(currentSp + value);
		return true;
	}
	
	public boolean removeSp(long amount)
	{
		final long currentSp = getSp();
		if (currentSp < amount)
		{
			setSp(getSp() - currentSp);
			return true;
		}
		setSp(getSp() - amount);
		return true;
	}
	
	public long getExpForLevel(int level)
	{
		return ExperienceData.getInstance().getExpForLevel(level);
	}
	
	@Override
	public Playable getActiveChar()
	{
		return (Playable) super.getActiveChar();
	}
	
	public int getMaxLevel()
	{
		return ExperienceData.getInstance().getMaxLevel();
	}
	
	@Override
	public int getPhysicalAttackRadius()
	{
		final Weapon weapon = getActiveChar().getActiveWeaponItem();
		return weapon != null ? weapon.getBaseAttackRadius() : super.getPhysicalAttackRadius();
	}
	
	@Override
	public int getPhysicalAttackAngle()
	{
		final Weapon weapon = getActiveChar().getActiveWeaponItem();
		return weapon != null ? weapon.getBaseAttackAngle() : super.getPhysicalAttackAngle();
	}
	
	private void addReputationToClanBasedOnLevel(Player player, int leveledUpCount)
	{
		Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (clan.getLevel() < 3) // When a character from clan level 3 or above increases its level, CRP are added
		{
			return;
		}
		
		int reputation = 0;
		for (int i = 0; i < leveledUpCount; i++)
		{
			int level = player.getLevel() - i;
			if ((level >= 20) && (level <= 25))
			{
				reputation += Config.LVL_UP_20_AND_25_REP_SCORE;
			}
			else if ((level >= 26) && (level <= 30))
			{
				reputation += Config.LVL_UP_26_AND_30_REP_SCORE;
			}
			else if ((level >= 31) && (level <= 35))
			{
				reputation += Config.LVL_UP_31_AND_35_REP_SCORE;
			}
			else if ((level >= 36) && (level <= 40))
			{
				reputation += Config.LVL_UP_36_AND_40_REP_SCORE;
			}
			else if ((level >= 41) && (level <= 45))
			{
				reputation += Config.LVL_UP_41_AND_45_REP_SCORE;
			}
			else if ((level >= 46) && (level <= 50))
			{
				reputation += Config.LVL_UP_46_AND_50_REP_SCORE;
			}
			else if ((level >= 51) && (level <= 55))
			{
				reputation += Config.LVL_UP_51_AND_55_REP_SCORE;
			}
			else if ((level >= 56) && (level <= 60))
			{
				reputation += Config.LVL_UP_56_AND_60_REP_SCORE;
			}
			else if ((level >= 61) && (level <= 65))
			{
				reputation += Config.LVL_UP_61_AND_65_REP_SCORE;
			}
			else if ((level >= 66) && (level <= 70))
			{
				reputation += Config.LVL_UP_66_AND_70_REP_SCORE;
			}
			else if ((level >= 71) && (level <= 75))
			{
				reputation += Config.LVL_UP_71_AND_75_REP_SCORE;
			}
			else if ((level >= 76) && (level <= 80))
			{
				reputation += Config.LVL_UP_76_AND_80_REP_SCORE;
			}
			else if ((level >= 81) && (level <= 120))
			{
				reputation += Config.LVL_UP_81_PLUS_REP_SCORE;
			}
		}
		
		if (reputation == 0)
		{
			return;
		}
		
		reputation = (int) Math.ceil(reputation * Config.LVL_OBTAINED_REP_SCORE_MULTIPLIER);
		
		clan.addReputationScore(reputation);
		
		for (ClanMember member : clan.getMembers())
		{
			if (member.isOnline())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION);
				sm.addInt(reputation);
				member.getPlayer().sendPacket(sm);
			}
		}
	}
}
