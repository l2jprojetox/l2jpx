package com.px.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.px.commons.data.StatSet;
import com.px.commons.data.xml.IXmlReader;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.enums.DropType;
import com.px.gameserver.enums.actors.NpcRace;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.PetDataEntry;
import com.px.gameserver.model.PrivateData;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.actor.template.PetTemplate;
import com.px.gameserver.model.item.DropCategory;
import com.px.gameserver.model.item.DropData;
import com.px.gameserver.model.memo.NpcMemo;
import com.px.gameserver.skills.L2Skill;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * Loads and stores {@link NpcTemplate}s.
 */
public class NpcData implements IXmlReader
{
	private final Map<Integer, NpcTemplate> _npcs = new HashMap<>();
	
	protected NpcData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/npcs");
		LOGGER.info("Loaded {} NPC templates.", _npcs.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "npc", npcNode ->
		{
			final NamedNodeMap attrs = npcNode.getAttributes();
			final int npcId = parseInteger(attrs, "id");
			final int templateId = attrs.getNamedItem("idTemplate") == null ? npcId : parseInteger(attrs, "idTemplate");
			final StatSet set = new StatSet();
			set.set("id", npcId);
			set.set("idTemplate", templateId);
			set.set("name", parseString(attrs, "name"));
			set.set("title", parseString(attrs, "title"));
			set.set("alias", parseString(attrs, "alias"));
			
			forEach(npcNode, "set", setNode ->
			{
				final NamedNodeMap setAttrs = setNode.getAttributes();
				set.set(parseString(setAttrs, "name"), parseString(setAttrs, "val"));
			});
			forEach(npcNode, "ai", aiNode ->
			{
				final NpcMemo aiParams = new NpcMemo();
				forEach(aiNode, "set", setNode ->
				{
					final NamedNodeMap setAttrs = setNode.getAttributes();
					aiParams.set(parseString(setAttrs, "name"), parseString(setAttrs, "val"));
				});
				set.set("aiParams", aiParams);
			});
			forEach(npcNode, "drops", dropsNode ->
			{
				final List<DropCategory> drops = new ArrayList<>();
				forEach(dropsNode, "category", categoryNode ->
				{
					final NamedNodeMap categoryAttrs = categoryNode.getAttributes();
					final DropCategory category = new DropCategory(parseEnum(categoryAttrs, DropType.class, "type"), parseDouble(categoryAttrs, "chance", 100.0));
					forEach(categoryNode, "drop", dropNode ->
					{
						final NamedNodeMap dropAttrs = dropNode.getAttributes();
						final DropData data = new DropData(parseInteger(dropAttrs, "itemid"), parseInteger(dropAttrs, "min"), parseInteger(dropAttrs, "max"), parseDouble(dropAttrs, "chance"));
						
						if (ItemData.getInstance().getTemplate(data.getItemId()) == null)
						{
							LOGGER.warn("Droplist data for undefined itemId: {}.", data.getItemId());
							return;
						}
						category.addDropData(data);
					});
					drops.add(category);
				});
				set.set("drops", drops);
			});
			forEach(npcNode, "privates", privatesNode ->
			{
				final List<PrivateData> privateData = new ArrayList<>();
				forEach(privatesNode, "private", privateNode -> privateData.add(new PrivateData(parseAttributes(privateNode))));
				set.set("privates", privateData);
			});
			forEach(npcNode, "petdata", petdataNode ->
			{
				final NamedNodeMap petdataAttrs = petdataNode.getAttributes();
				set.set("mustUsePetTemplate", true);
				set.set("food1", parseInteger(petdataAttrs, "food1"));
				set.set("food2", parseInteger(petdataAttrs, "food2"));
				set.set("autoFeedLimit", parseDouble(petdataAttrs, "autoFeedLimit"));
				set.set("hungryLimit", parseDouble(petdataAttrs, "hungryLimit"));
				set.set("unsummonLimit", parseDouble(petdataAttrs, "unsummonLimit"));
				
				final Map<Integer, PetDataEntry> entries = new HashMap<>();
				forEach(petdataNode, "stat", statNode ->
				{
					final StatSet petSet = parseAttributes(statNode);
					entries.put(petSet.getInteger("level"), new PetDataEntry(petSet));
				});
				set.set("petData", entries);
			});
			forEach(npcNode, "skills", skillsNode ->
			{
				final List<L2Skill> passives = new ArrayList<>();
				final Map<NpcSkillType, L2Skill> skills = new HashMap<>();
				
				forEach(skillsNode, "skill", skillNode ->
				{
					final NamedNodeMap skillAttrs = skillNode.getAttributes();
					final int skillId = parseInteger(skillAttrs, "id");
					final int level = parseInteger(skillAttrs, "level");
					
					// Attempt to check secondary skill ids related to NpcRace. DUMMY means no valid NpcRace was found, continue to investigate.
					final NpcRace nr = NpcRace.retrieveBySecondarySkillId(skillId);
					if (nr != null && nr != NpcRace.DUMMY)
					{
						set.set("race", nr);
						return;
					}
					
					// Test the primary NpcRace if not already set.
					if (skillId == L2Skill.SKILL_NPC_RACE && !set.containsKey("race"))
					{
						set.set("race", NpcRace.VALUES[level]);
						return;
					}
					
					// Allow the split of multiple types.
					for (String nstString : parseString(skillAttrs, "type").split(";"))
					{
						final L2Skill skill = SkillTable.getInstance().getInfo(skillId, level);
						if (skill == null)
						{
							LOGGER.warn("Invalid skill data {}-{} for : {}.", skillId, level, npcId);
							return;
						}
						
						// Add the L2Skill to either passives or skills container.
						final NpcSkillType nst = Enum.valueOf(NpcSkillType.class, nstString);
						if (nst == NpcSkillType.PASSIVE)
							passives.add(skill);
						else
							skills.put(nst, skill);
					}
				});
				set.set("passives", passives);
				set.set("skills", skills);
			});
			forEach(npcNode, "teachTo", teachToNode -> set.set("teachTo", parseString(teachToNode.getAttributes(), "classes")));
			
			_npcs.put(npcId, set.getBool("mustUsePetTemplate", false) ? new PetTemplate(set) : new NpcTemplate(set));
		}));
	}
	
	public void reload()
	{
		_npcs.clear();
		
		load();
	}
	
	public NpcTemplate getTemplate(int id)
	{
		return _npcs.get(id);
	}
	
	/**
	 * @param name : The name of the NPC to search.
	 * @return the {@link NpcTemplate} for a given name.
	 */
	public NpcTemplate getTemplateByName(String name)
	{
		return _npcs.values().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * Gets all {@link NpcTemplate}s matching the filter.
	 * @param filter : The Predicate filter used as a filter.
	 * @return a NpcTemplate list matching the given filter.
	 */
	public List<NpcTemplate> getTemplates(Predicate<NpcTemplate> filter)
	{
		return _npcs.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public Collection<NpcTemplate> getAllNpcs()
	{
		return _npcs.values();
	}
	
	public static NpcData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcData INSTANCE = new NpcData();
	}
}