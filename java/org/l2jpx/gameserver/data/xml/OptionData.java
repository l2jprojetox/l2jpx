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
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.handler.EffectHandler;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.options.Options;
import org.l2jpx.gameserver.model.options.OptionSkillHolder;
import org.l2jpx.gameserver.model.options.OptionSkillType;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * @author UnAfraid
 */
public class OptionData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(OptionData.class.getName());
	
	private static Options[] _options;
	private static Map<Integer, Options> _optionMap = new ConcurrentHashMap<>();
	
	protected OptionData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		parseDatapackDirectory("data/stats/augmentation/options", false);
		
		_options = new Options[Collections.max(_optionMap.keySet()) + 1];
		for (Entry<Integer, Options> option : _optionMap.entrySet())
		{
			_options[option.getKey()] = option.getValue();
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _optionMap.size() + " options.");
		_optionMap.clear();
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "option", optionNode ->
		{
			final int id = parseInteger(optionNode.getAttributes(), "id");
			final Options option = new Options(id);
			
			forEach(optionNode, IXmlReader::isNode, innerNode ->
			{
				switch (innerNode.getNodeName())
				{
					case "effects":
					{
						forEach(innerNode, "effect", effectNode ->
						{
							final String name = parseString(effectNode.getAttributes(), "name");
							final StatSet params = new StatSet();
							forEach(effectNode, IXmlReader::isNode, paramNode -> params.set(paramNode.getNodeName(), SkillData.getInstance().parseValue(paramNode, true, false, Collections.emptyMap())));
							option.addEffect(EffectHandler.getInstance().getHandlerFactory(name).apply(params));
						});
						break;
					}
					case "active_skill":
					{
						final int skillId = parseInteger(innerNode.getAttributes(), "id");
						final int skillLevel = parseInteger(innerNode.getAttributes(), "level");
						final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
						if (skill != null)
						{
							option.addActiveSkill(skill);
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find skill " + skillId + "(" + skillLevel + ") used by option " + id + ".");
						}
						break;
					}
					case "passive_skill":
					{
						final int skillId = parseInteger(innerNode.getAttributes(), "id");
						final int skillLevel = parseInteger(innerNode.getAttributes(), "level");
						final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
						if (skill != null)
						{
							option.addPassiveSkill(skill);
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find skill " + skillId + "(" + skillLevel + ") used by option " + id + ".");
						}
						break;
					}
					case "attack_skill":
					{
						final int skillId = parseInteger(innerNode.getAttributes(), "id");
						final int skillLevel = parseInteger(innerNode.getAttributes(), "level");
						final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
						if (skill != null)
						{
							option.addActivationSkill(new OptionSkillHolder(skill, parseDouble(innerNode.getAttributes(), "chance"), OptionSkillType.ATTACK));
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find skill " + skillId + "(" + skillLevel + ") used by option " + id + ".");
						}
						break;
					}
					case "magic_skill":
					{
						final int skillId = parseInteger(innerNode.getAttributes(), "id");
						final int skillLevel = parseInteger(innerNode.getAttributes(), "level");
						final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
						if (skill != null)
						{
							option.addActivationSkill(new OptionSkillHolder(skill, parseDouble(innerNode.getAttributes(), "chance"), OptionSkillType.MAGIC));
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find skill " + skillId + "(" + skillLevel + ") used by option " + id + ".");
						}
						break;
					}
					case "critical_skill":
					{
						final int skillId = parseInteger(innerNode.getAttributes(), "id");
						final int skillLevel = parseInteger(innerNode.getAttributes(), "level");
						final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
						if (skill != null)
						{
							option.addActivationSkill(new OptionSkillHolder(skill, parseDouble(innerNode.getAttributes(), "chance"), OptionSkillType.CRITICAL));
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find skill " + skillId + "(" + skillLevel + ") used by option " + id + ".");
						}
						break;
					}
				}
			});
			_optionMap.put(option.getId(), option);
		}));
	}
	
	public Options getOptions(int id)
	{
		if (_options.length > id)
		{
			return _options[id];
		}
		return null;
	}
	
	/**
	 * Gets the single instance of OptionsData.
	 * @return single instance of OptionsData
	 */
	public static OptionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OptionData INSTANCE = new OptionData();
	}
}
