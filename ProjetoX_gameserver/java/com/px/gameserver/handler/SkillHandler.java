package com.px.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.skillhandlers.BalanceLife;
import com.px.gameserver.handler.skillhandlers.Blow;
import com.px.gameserver.handler.skillhandlers.Cancel;
import com.px.gameserver.handler.skillhandlers.CombatPointHeal;
import com.px.gameserver.handler.skillhandlers.Continuous;
import com.px.gameserver.handler.skillhandlers.CpDamPercent;
import com.px.gameserver.handler.skillhandlers.Craft;
import com.px.gameserver.handler.skillhandlers.Disablers;
import com.px.gameserver.handler.skillhandlers.DrainSoul;
import com.px.gameserver.handler.skillhandlers.Dummy;
import com.px.gameserver.handler.skillhandlers.Extractable;
import com.px.gameserver.handler.skillhandlers.Fishing;
import com.px.gameserver.handler.skillhandlers.FishingSkill;
import com.px.gameserver.handler.skillhandlers.GetPlayer;
import com.px.gameserver.handler.skillhandlers.GiveSp;
import com.px.gameserver.handler.skillhandlers.Harvest;
import com.px.gameserver.handler.skillhandlers.Heal;
import com.px.gameserver.handler.skillhandlers.HealPercent;
import com.px.gameserver.handler.skillhandlers.InstantJump;
import com.px.gameserver.handler.skillhandlers.ManaHeal;
import com.px.gameserver.handler.skillhandlers.Manadam;
import com.px.gameserver.handler.skillhandlers.Mdam;
import com.px.gameserver.handler.skillhandlers.Pdam;
import com.px.gameserver.handler.skillhandlers.Resurrect;
import com.px.gameserver.handler.skillhandlers.Sow;
import com.px.gameserver.handler.skillhandlers.Spoil;
import com.px.gameserver.handler.skillhandlers.StrSiegeAssault;
import com.px.gameserver.handler.skillhandlers.SummonFriend;
import com.px.gameserver.handler.skillhandlers.Sweep;
import com.px.gameserver.handler.skillhandlers.TakeCastle;
import com.px.gameserver.handler.skillhandlers.Unlock;

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
		registerHandler(new StrSiegeAssault());
		registerHandler(new SummonFriend());
		registerHandler(new Sweep());
		registerHandler(new TakeCastle());
		registerHandler(new Unlock());
	}
	
	private void registerHandler(ISkillHandler handler)
	{
		for (L2SkillType t : handler.getSkillIds())
			_entries.put(t.ordinal(), handler);
	}
	
	public ISkillHandler getHandler(L2SkillType skillType)
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