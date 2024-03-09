package net.l2jpx.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.handler.skillhandlers.BalanceLife;
import net.l2jpx.gameserver.handler.skillhandlers.BeastFeed;
import net.l2jpx.gameserver.handler.skillhandlers.Blow;
import net.l2jpx.gameserver.handler.skillhandlers.Charge;
import net.l2jpx.gameserver.handler.skillhandlers.ClanGate;
import net.l2jpx.gameserver.handler.skillhandlers.CombatPointHeal;
import net.l2jpx.gameserver.handler.skillhandlers.Continuous;
import net.l2jpx.gameserver.handler.skillhandlers.CpDam;
import net.l2jpx.gameserver.handler.skillhandlers.Craft;
import net.l2jpx.gameserver.handler.skillhandlers.DeluxeKey;
import net.l2jpx.gameserver.handler.skillhandlers.Disablers;
import net.l2jpx.gameserver.handler.skillhandlers.DrainSoul;
import net.l2jpx.gameserver.handler.skillhandlers.Fishing;
import net.l2jpx.gameserver.handler.skillhandlers.FishingSkill;
import net.l2jpx.gameserver.handler.skillhandlers.GetPlayer;
import net.l2jpx.gameserver.handler.skillhandlers.Harvest;
import net.l2jpx.gameserver.handler.skillhandlers.Heal;
import net.l2jpx.gameserver.handler.skillhandlers.ManaHeal;
import net.l2jpx.gameserver.handler.skillhandlers.Manadam;
import net.l2jpx.gameserver.handler.skillhandlers.Mdam;
import net.l2jpx.gameserver.handler.skillhandlers.Pdam;
import net.l2jpx.gameserver.handler.skillhandlers.Recall;
import net.l2jpx.gameserver.handler.skillhandlers.Resurrect;
import net.l2jpx.gameserver.handler.skillhandlers.SiegeFlag;
import net.l2jpx.gameserver.handler.skillhandlers.Sow;
import net.l2jpx.gameserver.handler.skillhandlers.Spoil;
import net.l2jpx.gameserver.handler.skillhandlers.StrSiegeAssault;
import net.l2jpx.gameserver.handler.skillhandlers.SummonFriend;
import net.l2jpx.gameserver.handler.skillhandlers.SummonTreasureKey;
import net.l2jpx.gameserver.handler.skillhandlers.Sweep;
import net.l2jpx.gameserver.handler.skillhandlers.TakeCastle;
import net.l2jpx.gameserver.handler.skillhandlers.Unlock;
import net.l2jpx.gameserver.handler.skillhandlers.ZakenPlayer;
import net.l2jpx.gameserver.handler.skillhandlers.ZakenSelf;
import net.l2jpx.gameserver.model.L2Skill.SkillType;

/**
 * @author ReynalDev
 */
public class SkillHandler
{
	private static final Logger LOGGER = Logger.getLogger(SkillHandler.class);
	
	private static SkillHandler instance;
	
	private final Map<SkillType, ISkillHandler> dataTable;
	
	public static SkillHandler getInstance()
	{
		if (instance == null)
		{
			instance = new SkillHandler();
		}
		
		return instance;
	}
	
	private SkillHandler()
	{
		dataTable = new TreeMap<>();
		registerSkillHandler(new Blow());
		registerSkillHandler(new Pdam());
		registerSkillHandler(new Mdam());
		registerSkillHandler(new CpDam());
		registerSkillHandler(new Manadam());
		registerSkillHandler(new Heal());
		registerSkillHandler(new CombatPointHeal());
		registerSkillHandler(new ManaHeal());
		registerSkillHandler(new BalanceLife());
		registerSkillHandler(new Charge());
		registerSkillHandler(new ClanGate());
		registerSkillHandler(new Continuous());
		registerSkillHandler(new Resurrect());
		registerSkillHandler(new Spoil());
		registerSkillHandler(new Sweep());
		registerSkillHandler(new StrSiegeAssault());
		registerSkillHandler(new SummonFriend());
		registerSkillHandler(new SummonTreasureKey());
		registerSkillHandler(new Disablers());
		registerSkillHandler(new Recall());
		registerSkillHandler(new SiegeFlag());
		registerSkillHandler(new TakeCastle());
		registerSkillHandler(new Unlock());
		registerSkillHandler(new DrainSoul());
		registerSkillHandler(new Craft());
		registerSkillHandler(new Fishing());
		registerSkillHandler(new FishingSkill());
		registerSkillHandler(new BeastFeed());
		registerSkillHandler(new DeluxeKey());
		registerSkillHandler(new Sow());
		registerSkillHandler(new Harvest());
		registerSkillHandler(new GetPlayer());
		registerSkillHandler(new ZakenPlayer());
		registerSkillHandler(new ZakenSelf());
		
		LOGGER.info("SkillHandler: Loaded " + dataTable.size() + " handlers.");
	}
	
	public void registerSkillHandler(ISkillHandler handler)
	{
		SkillType[] types = handler.getSkillIds();
		
		for (SkillType t : types)
		{
			dataTable.put(t, handler);
		}
	}
	
	public ISkillHandler getSkillHandler(SkillType skillType)
	{
		return dataTable.get(skillType);
	}
	
	public int size()
	{
		return dataTable.size();
	}
}