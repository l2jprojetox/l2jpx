package com.px.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.handler.targethandlers.TargetAlly;
import com.px.gameserver.handler.targethandlers.TargetArea;
import com.px.gameserver.handler.targethandlers.TargetAreaCorpseMob;
import com.px.gameserver.handler.targethandlers.TargetAreaSummon;
import com.px.gameserver.handler.targethandlers.TargetAura;
import com.px.gameserver.handler.targethandlers.TargetAuraUndead;
import com.px.gameserver.handler.targethandlers.TargetBehindAura;
import com.px.gameserver.handler.targethandlers.TargetClan;
import com.px.gameserver.handler.targethandlers.TargetCorpseAlly;
import com.px.gameserver.handler.targethandlers.TargetCorpseMob;
import com.px.gameserver.handler.targethandlers.TargetCorpsePet;
import com.px.gameserver.handler.targethandlers.TargetCorpsePlayer;
import com.px.gameserver.handler.targethandlers.TargetFrontArea;
import com.px.gameserver.handler.targethandlers.TargetFrontAura;
import com.px.gameserver.handler.targethandlers.TargetGround;
import com.px.gameserver.handler.targethandlers.TargetHoly;
import com.px.gameserver.handler.targethandlers.TargetOne;
import com.px.gameserver.handler.targethandlers.TargetOwnerPet;
import com.px.gameserver.handler.targethandlers.TargetParty;
import com.px.gameserver.handler.targethandlers.TargetPartyMember;
import com.px.gameserver.handler.targethandlers.TargetPartyOther;
import com.px.gameserver.handler.targethandlers.TargetSelf;
import com.px.gameserver.handler.targethandlers.TargetSummon;
import com.px.gameserver.handler.targethandlers.TargetUndead;
import com.px.gameserver.handler.targethandlers.TargetUnlockable;

public class TargetHandler
{
	private final Map<SkillTargetType, ITargetHandler> _entries = new HashMap<>();
	
	protected TargetHandler()
	{
		registerHandler(new TargetAlly());
		registerHandler(new TargetArea());
		registerHandler(new TargetAreaCorpseMob());
		registerHandler(new TargetAreaSummon());
		registerHandler(new TargetAura());
		registerHandler(new TargetAuraUndead());
		registerHandler(new TargetBehindAura());
		registerHandler(new TargetClan());
		registerHandler(new TargetCorpseAlly());
		registerHandler(new TargetCorpseMob());
		registerHandler(new TargetCorpsePet());
		registerHandler(new TargetCorpsePlayer());
		registerHandler(new TargetFrontArea());
		registerHandler(new TargetFrontAura());
		registerHandler(new TargetGround());
		registerHandler(new TargetHoly());
		registerHandler(new TargetOne());
		registerHandler(new TargetOwnerPet());
		registerHandler(new TargetParty());
		registerHandler(new TargetPartyMember());
		registerHandler(new TargetPartyOther());
		registerHandler(new TargetSelf());
		registerHandler(new TargetSummon());
		registerHandler(new TargetUndead());
		registerHandler(new TargetUnlockable());
	}
	
	private void registerHandler(ITargetHandler handler)
	{
		_entries.put(handler.getTargetType(), handler);
	}
	
	public ITargetHandler getHandler(SkillTargetType targetType)
	{
		return _entries.get(targetType);
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static TargetHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TargetHandler INSTANCE = new TargetHandler();
	}
}