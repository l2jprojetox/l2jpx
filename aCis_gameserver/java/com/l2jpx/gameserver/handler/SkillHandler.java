package com.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.handler.skillhandlers.BalanceLife;
import com.l2jpx.gameserver.handler.skillhandlers.Blow;
import com.l2jpx.gameserver.handler.skillhandlers.Cancel;
import com.l2jpx.gameserver.handler.skillhandlers.CombatPointHeal;
import com.l2jpx.gameserver.handler.skillhandlers.Continuous;
import com.l2jpx.gameserver.handler.skillhandlers.CpDamPercent;
import com.l2jpx.gameserver.handler.skillhandlers.Craft;
import com.l2jpx.gameserver.handler.skillhandlers.Disablers;
import com.l2jpx.gameserver.handler.skillhandlers.DrainSoul;
import com.l2jpx.gameserver.handler.skillhandlers.Dummy;
import com.l2jpx.gameserver.handler.skillhandlers.Extractable;
import com.l2jpx.gameserver.handler.skillhandlers.Fishing;
import com.l2jpx.gameserver.handler.skillhandlers.FishingSkill;
import com.l2jpx.gameserver.handler.skillhandlers.GetPlayer;
import com.l2jpx.gameserver.handler.skillhandlers.GiveSp;
import com.l2jpx.gameserver.handler.skillhandlers.Harvest;
import com.l2jpx.gameserver.handler.skillhandlers.Heal;
import com.l2jpx.gameserver.handler.skillhandlers.HealPercent;
import com.l2jpx.gameserver.handler.skillhandlers.InstantJump;
import com.l2jpx.gameserver.handler.skillhandlers.ManaHeal;
import com.l2jpx.gameserver.handler.skillhandlers.Manadam;
import com.l2jpx.gameserver.handler.skillhandlers.Mdam;
import com.l2jpx.gameserver.handler.skillhandlers.Pdam;
import com.l2jpx.gameserver.handler.skillhandlers.Resurrect;
import com.l2jpx.gameserver.handler.skillhandlers.Sow;
import com.l2jpx.gameserver.handler.skillhandlers.Spoil;
import com.l2jpx.gameserver.handler.skillhandlers.StriderSiegeAssault;
import com.l2jpx.gameserver.handler.skillhandlers.SummonCreature;
import com.l2jpx.gameserver.handler.skillhandlers.SummonFriend;
import com.l2jpx.gameserver.handler.skillhandlers.Sweep;
import com.l2jpx.gameserver.handler.skillhandlers.TakeCastle;
import com.l2jpx.gameserver.handler.skillhandlers.Unlock;

public class SkillHandler
{
	private final Map<Integer, ISkillHandler> _entries = new HashMap<>();
	
	protected SkillHandler()
	{
		registerHandler(new BalanceLife());
		registerHandler(new Blow());
		registerHandler(new Cancel());
		registerHandler(new CombatPointHeal());
		registerHandler(new Continuous());
		registerHandler(new CpDamPercent());
		registerHandler(new Craft());
		registerHandler(new Disablers());
		registerHandler(new DrainSoul());
		registerHandler(new Dummy());
		registerHandler(new Extractable());
		registerHandler(new Fishing());
		registerHandler(new FishingSkill());
		registerHandler(new GetPlayer());
		registerHandler(new GiveSp());
		registerHandler(new Harvest());
		registerHandler(new Heal());
		registerHandler(new HealPercent());
		registerHandler(new InstantJump());
		registerHandler(new Manadam());
		registerHandler(new ManaHeal());
		registerHandler(new Mdam());
		registerHandler(new Pdam());
		registerHandler(new Resurrect());
		registerHandler(new Sow());
		registerHandler(new Spoil());
		registerHandler(new StriderSiegeAssault());
		registerHandler(new SummonFriend());
		registerHandler(new SummonCreature());
		registerHandler(new Sweep());
		registerHandler(new TakeCastle());
		registerHandler(new Unlock());
	}
	
	private void registerHandler(ISkillHandler handler)
	{
		for (SkillType t : handler.getSkillIds())
			_entries.put(t.ordinal(), handler);
	}
	
	public ISkillHandler getHandler(SkillType skillType)
	{
		return _entries.get(skillType.ordinal());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static SkillHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillHandler INSTANCE = new SkillHandler();
	}
}