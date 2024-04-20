package com.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.handler.targethandlers.TargetAlly;
import com.l2jpx.gameserver.handler.targethandlers.TargetArea;
import com.l2jpx.gameserver.handler.targethandlers.TargetAreaCorpseMob;
import com.l2jpx.gameserver.handler.targethandlers.TargetAreaSummon;
import com.l2jpx.gameserver.handler.targethandlers.TargetAura;
import com.l2jpx.gameserver.handler.targethandlers.TargetAuraUndead;
import com.l2jpx.gameserver.handler.targethandlers.TargetBehindAura;
import com.l2jpx.gameserver.handler.targethandlers.TargetClan;
import com.l2jpx.gameserver.handler.targethandlers.TargetCorpseAlly;
import com.l2jpx.gameserver.handler.targethandlers.TargetCorpseMob;
import com.l2jpx.gameserver.handler.targethandlers.TargetCorpsePet;
import com.l2jpx.gameserver.handler.targethandlers.TargetCorpsePlayer;
import com.l2jpx.gameserver.handler.targethandlers.TargetEnemySummon;
import com.l2jpx.gameserver.handler.targethandlers.TargetFrontArea;
import com.l2jpx.gameserver.handler.targethandlers.TargetFrontAura;
import com.l2jpx.gameserver.handler.targethandlers.TargetGround;
import com.l2jpx.gameserver.handler.targethandlers.TargetHoly;
import com.l2jpx.gameserver.handler.targethandlers.TargetOne;
import com.l2jpx.gameserver.handler.targethandlers.TargetOwnerPet;
import com.l2jpx.gameserver.handler.targethandlers.TargetParty;
import com.l2jpx.gameserver.handler.targethandlers.TargetPartyMember;
import com.l2jpx.gameserver.handler.targethandlers.TargetPartyOther;
import com.l2jpx.gameserver.handler.targethandlers.TargetSelf;
import com.l2jpx.gameserver.handler.targethandlers.TargetSummon;
import com.l2jpx.gameserver.handler.targethandlers.TargetUndead;
import com.l2jpx.gameserver.handler.targethandlers.TargetUnlockable;

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
		registerHandler(new TargetEnemySummon());
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