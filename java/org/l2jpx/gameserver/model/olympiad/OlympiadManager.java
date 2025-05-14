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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.CategoryType;
import org.l2jpx.gameserver.instancemanager.AntiFeedManager;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author DS
 */
public class OlympiadManager
{
	private final Set<Integer> _nonClassBasedRegisters = ConcurrentHashMap.newKeySet();
	private final Map<Integer, Set<Integer>> _classBasedRegisters = new ConcurrentHashMap<>();
	
	protected OlympiadManager()
	{
	}
	
	public static OlympiadManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public Set<Integer> getRegisteredNonClassBased()
	{
		return _nonClassBasedRegisters;
	}
	
	public Map<Integer, Set<Integer>> getRegisteredClassBased()
	{
		return _classBasedRegisters;
	}
	
	protected final List<Set<Integer>> hasEnoughRegisteredClassed()
	{
		List<Set<Integer>> result = null;
		for (Entry<Integer, Set<Integer>> classList : _classBasedRegisters.entrySet())
		{
			if ((classList.getValue() != null) && (classList.getValue().size() >= Config.OLYMPIAD_CLASSED))
			{
				if (result == null)
				{
					result = new ArrayList<>();
				}
				
				result.add(classList.getValue());
			}
		}
		return result;
	}
	
	protected final boolean hasEnoughRegisteredNonClassed()
	{
		return _nonClassBasedRegisters.size() >= Config.OLYMPIAD_NONCLASSED;
	}
	
	protected void clearRegistered()
	{
		_nonClassBasedRegisters.clear();
		_classBasedRegisters.clear();
		AntiFeedManager.getInstance().clear(AntiFeedManager.OLYMPIAD_ID);
	}
	
	public boolean isRegistered(Player noble)
	{
		return isRegistered(noble, noble, false);
	}
	
	private boolean isRegistered(Player noble, Player player, boolean showMessage)
	{
		final Integer objId = noble.getObjectId();
		if (_nonClassBasedRegisters.contains(objId))
		{
			if (showMessage)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE);
				sm.addPcName(noble);
				player.sendPacket(sm);
			}
			return true;
		}
		
		final Set<Integer> classed = _classBasedRegisters.get(getClassGroup(noble));
		if ((classed != null) && classed.contains(objId))
		{
			if (showMessage)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST);
				sm.addPcName(noble);
				player.sendPacket(sm);
			}
			return true;
		}
		
		return false;
	}
	
	public boolean isRegisteredInComp(Player noble)
	{
		return isRegistered(noble, noble, false) || isInCompetition(noble, noble, false);
	}
	
	private boolean isInCompetition(Player noble, Player player, boolean showMessage)
	{
		if (!Olympiad._inCompPeriod)
		{
			return false;
		}
		
		AbstractOlympiadGame game;
		for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0;)
		{
			game = OlympiadGameManager.getInstance().getOlympiadTask(i).getGame();
			if (game == null)
			{
				continue;
			}
			
			if (game.containsParticipant(noble.getObjectId()))
			{
				if (!showMessage)
				{
					return true;
				}
				
				switch (game.getType())
				{
					case CLASSED:
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST);
						sm.addPcName(noble);
						player.sendPacket(sm);
						break;
					}
					case NON_CLASSED:
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE);
						sm.addPcName(noble);
						player.sendPacket(sm);
						break;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean registerNoble(Player player, CompetitionType type)
	{
		if (!Olympiad._inCompPeriod)
		{
			player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if (Olympiad.getInstance().getMillisToCompEnd() < 1200000)
		{
			player.sendPacket(SystemMessageId.PARTICIPATION_REQUESTS_ARE_NO_LONGER_BEING_ACCEPTED);
			return false;
		}
		
		final int charId = player.getObjectId();
		if (Olympiad.getInstance().getRemainingWeeklyMatches(charId) < 1)
		{
			player.sendPacket(SystemMessageId.THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_30);
			return false;
		}
		
		if (isRegistered(player, player, true) || isInCompetition(player, player, true))
		{
			return false;
		}
		
		StatSet statDat = Olympiad.getNobleStats(charId);
		if (statDat == null)
		{
			statDat = new StatSet();
			statDat.set(Olympiad.CLASS_ID, player.getBaseClass());
			statDat.set(Olympiad.CHAR_NAME, player.getName());
			statDat.set(Olympiad.POINTS, Olympiad.DEFAULT_POINTS);
			statDat.set(Olympiad.COMP_DONE, 0);
			statDat.set(Olympiad.COMP_WON, 0);
			statDat.set(Olympiad.COMP_LOST, 0);
			statDat.set(Olympiad.COMP_DRAWN, 0);
			statDat.set(Olympiad.COMP_DONE_WEEK, 0);
			statDat.set("to_save", true);
			Olympiad.addNobleStats(charId, statDat);
		}
		
		switch (type)
		{
			case CLASSED:
			{
				if (player.isRegisteredOnEvent())
				{
					player.sendMessage("You can't join olympiad while participating on an event.");
					return false;
				}
				
				if ((Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.OLYMPIAD_ID, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP))
				{
					final NpcHtmlMessage message = new NpcHtmlMessage(player.getLastHtmlActionOriginId());
					message.setFile(player, "data/html/mods/OlympiadIPRestriction.htm");
					message.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
					player.sendPacket(message);
					return false;
				}
				
				_classBasedRegisters.computeIfAbsent(getClassGroup(player), k -> ConcurrentHashMap.newKeySet()).add(charId);
				player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_FOR_THE_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_BATTLE);
				break;
			}
			case NON_CLASSED:
			{
				if (player.isRegisteredOnEvent())
				{
					player.sendMessage("You can't join olympiad while participating on an event.");
					return false;
				}
				
				if ((Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.OLYMPIAD_ID, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP))
				{
					final NpcHtmlMessage message = new NpcHtmlMessage(player.getLastHtmlActionOriginId());
					message.setFile(player, "data/html/mods/OlympiadIPRestriction.htm");
					message.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
					player.sendPacket(message);
					return false;
				}
				
				_nonClassBasedRegisters.add(charId);
				player.sendPacket(SystemMessageId.YOU_ARE_CURRENTLY_REGISTERED_FOR_A_1V1_CLASS_IRRELEVANT_MATCH);
				break;
			}
		}
		return true;
	}
	
	public boolean unRegisterNoble(Player noble)
	{
		if (!Olympiad._inCompPeriod)
		{
			noble.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if ((!noble.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !noble.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || (noble.getLevel() < 55)) // Classic noble equivalent check.
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.CHARACTER_C1_DOES_NOT_MEET_THE_CONDITIONS_ONLY_CHARACTERS_WHO_HAVE_CHANGED_TWO_OR_MORE_CLASSES_CAN_PARTICIPATE_IN_OLYMPIAD);
			sm.addString(noble.getName());
			noble.sendPacket(sm);
			return false;
		}
		
		if (!isRegistered(noble, noble, false))
		{
			noble.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_OLYMPIAD);
			return false;
		}
		
		if (isInCompetition(noble, noble, false))
		{
			return false;
		}
		
		final Integer objId = noble.getObjectId();
		if (_nonClassBasedRegisters.remove(objId))
		{
			if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0)
			{
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, noble);
			}
			
			noble.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
			return true;
		}
		
		final Set<Integer> classed = _classBasedRegisters.get(getClassGroup(noble));
		if ((classed != null) && classed.remove(objId))
		{
			if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0)
			{
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, noble);
			}
			
			noble.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
			return true;
		}
		
		return false;
	}
	
	public void removeDisconnectedCompetitor(Player player)
	{
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
		if ((task != null) && task.isGameStarted())
		{
			task.getGame().handleDisconnect(player);
		}
		
		final Integer objId = player.getObjectId();
		if (_nonClassBasedRegisters.remove(objId))
		{
			return;
		}
		
		_classBasedRegisters.getOrDefault(getClassGroup(player), Collections.emptySet()).remove(objId);
	}
	
	public int getCountOpponents()
	{
		return _nonClassBasedRegisters.size() + _classBasedRegisters.size();
	}
	
	private static class SingletonHolder
	{
		protected static final OlympiadManager INSTANCE = new OlympiadManager();
	}
	
	private int getClassGroup(Player player)
	{
		if (player.isInCategory(CategoryType.SIXTH_TIR_GROUP))
		{
			return 1001;
		}
		else if (player.isInCategory(CategoryType.SIXTH_SIGEL_GROUP))
		{
			return 1002;
		}
		else if (player.isInCategory(CategoryType.SIXTH_OTHEL_GROUP))
		{
			return 1003;
		}
		else if (player.isInCategory(CategoryType.SIXTH_FEOH_GROUP))
		{
			return 1004;
		}
		else if (player.isInCategory(CategoryType.SIXTH_IS_GROUP))
		{
			return 1005;
		}
		else if (player.isInCategory(CategoryType.SIXTH_EOLH_GROUP))
		{
			return 1006;
		}
		else if (player.isInCategory(CategoryType.SIXTH_WYNN_GROUP))
		{
			return 1007;
		}
		else if (player.isInCategory(CategoryType.SIXTH_YR_GROUP))
		{
			return 1008;
		}
		else if (player.isInCategory(CategoryType.ERTHEIA_FOURTH_CLASS_GROUP))
		{
			return 1009;
		}
		else
		{
			return player.getBaseClass();
		}
	}
}
