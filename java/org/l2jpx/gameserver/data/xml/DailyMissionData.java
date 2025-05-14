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
package org.l2jpx.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.enums.ClassId;
import org.l2jpx.gameserver.model.DailyMissionDataHolder;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemHolder;

/**
 * @author Sdw, Mobius
 */
public class DailyMissionData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(DailyMissionData.class.getName());
	
	private final Map<Integer, List<DailyMissionDataHolder>> _dailyMissionRewards = new LinkedHashMap<>();
	private final List<DailyMissionDataHolder> _dailyMissionData = new ArrayList<>();
	private boolean _isAvailable;
	
	protected DailyMissionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_dailyMissionRewards.clear();
		parseDatapackFile("data/DailyMission.xml");
		
		_dailyMissionData.clear();
		for (List<DailyMissionDataHolder> missionList : _dailyMissionRewards.values())
		{
			_dailyMissionData.addAll(missionList);
		}
		
		_isAvailable = !_dailyMissionRewards.isEmpty();
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _dailyMissionRewards.size() + " one day rewards.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "reward", rewardNode ->
		{
			final StatSet set = new StatSet(parseAttributes(rewardNode));
			final List<ItemHolder> items = new ArrayList<>(1);
			forEach(rewardNode, "items", itemsNode -> forEach(itemsNode, "item", itemNode ->
			{
				final int itemId = parseInteger(itemNode.getAttributes(), "id");
				final int itemCount = parseInteger(itemNode.getAttributes(), "count");
				items.add(new ItemHolder(itemId, itemCount));
			}));
			
			set.set("items", items);
			
			final List<ClassId> classRestriction = new ArrayList<>(1);
			forEach(rewardNode, "classId", classRestrictionNode -> classRestriction.add(ClassId.getClassId(Integer.parseInt(classRestrictionNode.getTextContent()))));
			set.set("classRestriction", classRestriction);
			
			// Initial values in case handler doesn't exist
			set.set("handler", "");
			set.set("params", StatSet.EMPTY_STATSET);
			
			// Parse handler and parameters
			forEach(rewardNode, "handler", handlerNode ->
			{
				set.set("handler", parseString(handlerNode.getAttributes(), "name"));
				
				final StatSet params = new StatSet();
				set.set("params", params);
				forEach(handlerNode, "param", paramNode -> params.set(parseString(paramNode.getAttributes(), "name"), paramNode.getTextContent()));
			});
			
			final DailyMissionDataHolder holder = new DailyMissionDataHolder(set);
			_dailyMissionRewards.computeIfAbsent(holder.getId(), k -> new ArrayList<>()).add(holder);
		}));
	}
	
	public Collection<DailyMissionDataHolder> getDailyMissionData()
	{
		return _dailyMissionData;
	}
	
	public Collection<DailyMissionDataHolder> getDailyMissionData(Player player)
	{
		final List<DailyMissionDataHolder> missionData = new LinkedList<>();
		for (DailyMissionDataHolder mission : _dailyMissionData)
		{
			if (mission.isDisplayable(player))
			{
				missionData.add(mission);
			}
		}
		return missionData;
	}
	
	public Collection<DailyMissionDataHolder> getDailyMissionData(int id)
	{
		return _dailyMissionRewards.get(id);
	}
	
	public boolean isAvailable()
	{
		return _isAvailable;
	}
	
	/**
	 * Gets the single instance of DailyMissionData.
	 * @return single instance of DailyMissionData
	 */
	public static DailyMissionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DailyMissionData INSTANCE = new DailyMissionData();
	}
}
