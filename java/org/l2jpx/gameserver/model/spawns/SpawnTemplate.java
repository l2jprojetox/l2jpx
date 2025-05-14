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
package org.l2jpx.gameserver.model.spawns;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.l2jpx.gameserver.instancemanager.QuestManager;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.interfaces.IParameterized;
import org.l2jpx.gameserver.model.interfaces.ITerritorized;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.zone.type.BannedSpawnTerritory;
import org.l2jpx.gameserver.model.zone.type.SpawnTerritory;

/**
 * @author UnAfraid
 */
public class SpawnTemplate implements Cloneable, ITerritorized, IParameterized<StatSet>
{
	private final String _name;
	private final String _ai;
	private final boolean _spawnByDefault;
	private final File _file;
	private List<SpawnTerritory> _territories;
	private List<BannedSpawnTerritory> _bannedTerritories;
	private final List<SpawnGroup> _groups = new ArrayList<>();
	private StatSet _parameters;
	
	public SpawnTemplate(StatSet set, File file)
	{
		this(set.getString("name", null), set.getString("ai", null), set.getBoolean("spawnByDefault", true), file);
	}
	
	private SpawnTemplate(String name, String ai, boolean spawnByDefault, File file)
	{
		_name = name;
		_ai = ai;
		_spawnByDefault = spawnByDefault;
		_file = file;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getAI()
	{
		return _ai;
	}
	
	public boolean isSpawningByDefault()
	{
		return _spawnByDefault;
	}
	
	public File getFile()
	{
		return _file;
	}
	
	@Override
	public void addTerritory(SpawnTerritory territory)
	{
		if (_territories == null)
		{
			_territories = new ArrayList<>();
		}
		_territories.add(territory);
	}
	
	@Override
	public List<SpawnTerritory> getTerritories()
	{
		return _territories != null ? _territories : Collections.emptyList();
	}
	
	@Override
	public void addBannedTerritory(BannedSpawnTerritory territory)
	{
		if (_bannedTerritories == null)
		{
			_bannedTerritories = new ArrayList<>();
		}
		_bannedTerritories.add(territory);
	}
	
	@Override
	public List<BannedSpawnTerritory> getBannedTerritories()
	{
		return _bannedTerritories != null ? _bannedTerritories : Collections.emptyList();
	}
	
	public void addGroup(SpawnGroup group)
	{
		_groups.add(group);
	}
	
	public List<SpawnGroup> getGroups()
	{
		return _groups;
	}
	
	public List<SpawnGroup> getGroupsByName(String name)
	{
		final List<SpawnGroup> result = new ArrayList<>();
		for (SpawnGroup group : _groups)
		{
			if ((group.getName() != null) && group.getName().equalsIgnoreCase(name))
			{
				result.add(group);
			}
		}
		return result;
	}
	
	@Override
	public StatSet getParameters()
	{
		return _parameters;
	}
	
	@Override
	public void setParameters(StatSet parameters)
	{
		_parameters = parameters;
	}
	
	public void notifyEvent(Consumer<Quest> event)
	{
		if (_ai != null)
		{
			final Quest script = QuestManager.getInstance().getQuest(_ai);
			if (script != null)
			{
				event.accept(script);
			}
		}
	}
	
	public void spawn(Predicate<SpawnGroup> groupFilter, Instance instance)
	{
		for (SpawnGroup group : _groups)
		{
			if (groupFilter.test(group))
			{
				group.spawnAll(instance);
			}
		}
	}
	
	public void spawnAll()
	{
		spawnAll(null);
	}
	
	public void spawnAll(Instance instance)
	{
		spawn(SpawnGroup::isSpawningByDefault, instance);
	}
	
	public void notifyActivate()
	{
		notifyEvent(script -> script.onSpawnActivate(this));
	}
	
	public void spawnAllIncludingNotDefault(Instance instance)
	{
		_groups.forEach(group -> group.spawnAll(instance));
	}
	
	public void despawn(Predicate<SpawnGroup> groupFilter)
	{
		for (SpawnGroup group : _groups)
		{
			if (groupFilter.test(group))
			{
				group.despawnAll();
			}
		}
		notifyEvent(script -> script.onSpawnDeactivate(this));
	}
	
	public void despawnAll()
	{
		_groups.forEach(SpawnGroup::despawnAll);
		notifyEvent(script -> script.onSpawnDeactivate(this));
	}
	
	@Override
	public SpawnTemplate clone()
	{
		final SpawnTemplate template = new SpawnTemplate(_name, _ai, _spawnByDefault, _file);
		
		// Clone parameters
		template.setParameters(_parameters);
		
		// Clone banned territories
		for (BannedSpawnTerritory territory : getBannedTerritories())
		{
			template.addBannedTerritory(territory);
		}
		
		// Clone territories
		for (SpawnTerritory territory : getTerritories())
		{
			template.addTerritory(territory);
		}
		
		// Clone groups
		for (SpawnGroup group : _groups)
		{
			template.addGroup(group.clone());
		}
		
		return template;
	}
}
