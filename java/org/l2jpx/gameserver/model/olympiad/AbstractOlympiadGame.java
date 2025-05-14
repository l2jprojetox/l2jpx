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
package org.l2jpx.gameserver.model.olympiad;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.enums.PartyMessageType;
import org.l2jpx.gameserver.instancemanager.AntiFeedManager;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.FortManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExOlympiadMode;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.network.serverpackets.SkillCoolTime;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author godson, GodKratos, Pere, DS
 */
public abstract class AbstractOlympiadGame
{
	protected static final Logger LOGGER = Logger.getLogger(AbstractOlympiadGame.class.getName());
	protected static final Logger LOGGER_OLYMPIAD = Logger.getLogger("olympiad");
	
	protected static final String POINTS = "olympiad_points";
	protected static final String COMP_DONE = "competitions_done";
	protected static final String COMP_WON = "competitions_won";
	protected static final String COMP_LOST = "competitions_lost";
	protected static final String COMP_DRAWN = "competitions_drawn";
	protected static final String COMP_DONE_WEEK = "competitions_done_week";
	protected static final String COMP_DONE_WEEK_CLASSED = "competitions_done_week_classed";
	protected static final String COMP_DONE_WEEK_NON_CLASSED = "competitions_done_week_non_classed";
	protected static final String COMP_DONE_WEEK_TEAM = "competitions_done_week_team";
	
	protected long _startTime = 0;
	protected boolean _aborted = false;
	protected final int _stadiumId;
	
	protected AbstractOlympiadGame(int id)
	{
		_stadiumId = id;
	}
	
	public boolean isAborted()
	{
		return _aborted;
	}
	
	public int getStadiumId()
	{
		return _stadiumId;
	}
	
	protected boolean makeCompetitionStart()
	{
		_startTime = System.currentTimeMillis();
		return !_aborted;
	}
	
	protected void addPointsToParticipant(Participant par, int points)
	{
		par.updateStat(POINTS, points);
		final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_EARNED_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
		sm.addString(par.getName());
		sm.addInt(points);
		broadcastPacket(sm);
	}
	
	protected void removePointsFromParticipant(Participant par, int points)
	{
		par.updateStat(POINTS, -points);
		final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_LOST_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
		sm.addString(par.getName());
		sm.addInt(points);
		broadcastPacket(sm);
	}
	
	/**
	 * Function return null if player passed all checks or SystemMessage with reason for broadcast to opponent(s).
	 * @param player
	 * @return
	 */
	protected static SystemMessage checkDefaulted(Player player)
	{
		if ((player == null) || !player.isOnline())
		{
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		
		if ((player.getClient() == null) || player.getClient().isDetached())
		{
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		
		// safety precautions
		if (player.inObserverMode())
		{
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		
		SystemMessage sm;
		if (player.isDead())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			sm.addPcName(player);
			player.sendPacket(sm);
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		if (player.isSubClassActive())
		{
			sm = new SystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGED_YOUR_CLASS_TO_SUBCLASS);
			sm.addPcName(player);
			player.sendPacket(sm);
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		if (player.isCursedWeaponEquipped())
		{
			sm = new SystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_THE_OWNER_OF_S2_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			sm.addPcName(player);
			sm.addItemName(player.getCursedWeaponEquippedId());
			player.sendPacket(sm);
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		if (!player.isInventoryUnder90(true))
		{
			sm = new SystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_AS_THE_INVENTORY_WEIGHT_SLOT_IS_FILLED_BEYOND_80);
			sm.addPcName(player);
			player.sendPacket(sm);
			return new SystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
		}
		
		return null;
	}
	
	protected static boolean portPlayerToArena(Participant par, Location loc, int id, Instance instance)
	{
		final Player player = par.getPlayer();
		if ((player == null) || !player.isOnline())
		{
			return false;
		}
		
		try
		{
			player.setLastLocation();
			if (player.isSitting())
			{
				player.standUp();
			}
			player.setTarget(null);
			
			player.setOlympiadGameId(id);
			player.setInOlympiadMode(true);
			player.setOlympiadStart(false);
			player.setOlympiadSide(par.getSide());
			player.teleToLocation(loc, instance);
			player.sendPacket(new ExOlympiadMode(2));
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	protected void removals(Player player, boolean removeParty)
	{
		try
		{
			if (player == null)
			{
				return;
			}
			
			// Remove Buffs
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			player.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
			
			// Remove Clan Skills
			final Clan clan = player.getClan();
			if (clan != null)
			{
				clan.removeSkillEffects(player);
				if (clan.getCastleId() > 0)
				{
					final Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
					if (castle != null)
					{
						castle.removeResidentialSkills(player);
					}
				}
				if (clan.getFortId() > 0)
				{
					final Fort fort = FortManager.getInstance().getFortByOwner(clan);
					if (fort != null)
					{
						fort.removeResidentialSkills(player);
					}
				}
			}
			// Abort casting if player casting
			player.abortAttack();
			player.abortCast();
			
			// Force the character to be visible
			player.setInvisible(false);
			
			// Heal Player fully
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			
			// Remove Summon's Buffs
			if (player.hasSummon())
			{
				final Summon pet = player.getPet();
				if (pet != null)
				{
					pet.unSummon(player);
				}
				
				player.getServitors().values().forEach(s ->
				{
					s.stopAllEffectsExceptThoseThatLastThroughDeath();
					s.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
					s.abortAttack();
					s.abortCast();
				});
			}
			
			// stop any cubic that has been given by other player.
			player.stopCubicsByOthers();
			
			// Remove player from his party
			if (removeParty)
			{
				final Party party = player.getParty();
				if (party != null)
				{
					party.removePartyMember(player, PartyMessageType.EXPELLED);
				}
			}
			// Remove Agathion
			if (player.getAgathionId() > 0)
			{
				player.setAgathionId(0);
				player.broadcastUserInfo();
			}
			
			player.checkItemRestriction();
			
			// enable skills with cool time <= 15 minutes
			for (Skill skill : player.getAllSkills())
			{
				if (skill.getReuseDelay() <= 900000)
				{
					player.enableSkill(skill);
				}
			}
			
			player.sendSkillList();
			player.sendPacket(new SkillCoolTime(player));
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected void cleanEffects(Player player)
	{
		try
		{
			// prevent players kill each other
			player.setOlympiadStart(false);
			player.setTarget(null);
			player.abortAttack();
			player.abortCast();
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			
			if (player.isDead())
			{
				player.setDead(false);
			}
			
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			player.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
			player.clearSouls();
			player.clearCharges();
			if (player.getAgathionId() > 0)
			{
				player.setAgathionId(0);
			}
			final Summon pet = player.getPet();
			if ((pet != null) && !pet.isDead())
			{
				pet.setTarget(null);
				pet.abortAttack();
				pet.abortCast();
				pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				pet.stopAllEffectsExceptThoseThatLastThroughDeath();
				pet.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
			}
			
			for (Summon s : player.getServitors().values())
			{
				if (!s.isDead())
				{
					s.setTarget(null);
					s.abortAttack();
					s.abortCast();
					s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
					s.stopAllEffectsExceptThoseThatLastThroughDeath();
					s.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
				}
			}
			
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.getStatus().startHpMpRegeneration();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected void playerStatusBack(Player player)
	{
		try
		{
			if (player.isTransformed())
			{
				player.untransform();
			}
			
			player.setInOlympiadMode(false);
			player.setOlympiadStart(false);
			player.setOlympiadSide(-1);
			player.setOlympiadGameId(-1);
			player.sendPacket(new ExOlympiadMode(0));
			
			// Add Clan Skills
			final Clan clan = player.getClan();
			if (clan != null)
			{
				clan.addSkillEffects(player);
				if (clan.getCastleId() > 0)
				{
					final Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
					if (castle != null)
					{
						castle.giveResidentialSkills(player);
					}
				}
				if (clan.getFortId() > 0)
				{
					final Fort fort = FortManager.getInstance().getFortByOwner(clan);
					if (fort != null)
					{
						fort.giveResidentialSkills(player);
					}
				}
				player.sendSkillList();
			}
			
			// heal again after adding clan skills
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.getStatus().startHpMpRegeneration();
			
			if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0)
			{
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, player);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "playerStatusBack()", e);
		}
	}
	
	protected void portPlayerBack(Player player)
	{
		if (player == null)
		{
			return;
		}
		final Location loc = player.getLastLocation();
		if (loc != null)
		{
			player.setIsPendingRevive(false);
			player.teleToLocation(loc, null);
			player.unsetLastLocation();
		}
	}
	
	public static void rewardParticipant(Player player, List<ItemHolder> list)
	{
		if ((player == null) || !player.isOnline() || (list == null))
		{
			return;
		}
		
		try
		{
			final InventoryUpdate iu = new InventoryUpdate();
			list.forEach(holder ->
			{
				final Item item = player.getInventory().addItem("Olympiad", holder.getId(), holder.getCount(), player, null);
				if (item == null)
				{
					return;
				}
				
				iu.addModifiedItem(item);
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
				sm.addItemName(item);
				sm.addLong(holder.getCount());
				player.sendPacket(sm);
			});
			player.sendInventoryUpdate(iu);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	public abstract CompetitionType getType();
	
	public abstract String[] getPlayerNames();
	
	public abstract boolean containsParticipant(int playerId);
	
	public abstract void sendOlympiadInfo(Creature creature);
	
	public abstract void broadcastOlympiadInfo(OlympiadStadium stadium);
	
	protected abstract void broadcastPacket(ServerPacket packet);
	
	protected abstract boolean needBuffers();
	
	protected abstract boolean checkDefaulted();
	
	protected abstract void removals();
	
	protected abstract boolean portPlayersToArena(List<Location> spawns, Instance instance);
	
	protected abstract void cleanEffects();
	
	protected abstract void portPlayersBack();
	
	protected abstract void playersStatusBack();
	
	protected abstract void clearPlayers();
	
	protected abstract void handleDisconnect(Player player);
	
	protected abstract void resetDamage();
	
	protected abstract void addDamage(Player player, int damage);
	
	protected abstract boolean checkBattleStatus();
	
	protected abstract boolean haveWinner();
	
	protected abstract void validateWinner(OlympiadStadium stadium);
	
	protected abstract int getDivider();
	
	protected abstract void healPlayers();
	
	protected abstract void untransformPlayers();
	
	protected abstract void makePlayersInvul();
	
	protected abstract void removePlayersInvul();
}
