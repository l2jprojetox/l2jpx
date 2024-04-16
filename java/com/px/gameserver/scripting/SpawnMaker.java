package com.px.gameserver.scripting;

import java.util.Arrays;

import com.px.Config;
import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.data.xml.DoorData;
import com.px.gameserver.enums.CabalType;
import com.px.gameserver.enums.MakerSpawnTime;
import com.px.gameserver.enums.PeriodType;
import com.px.gameserver.enums.SealType;
import com.px.gameserver.enums.SiegeStatus;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.instance.Door;
import com.px.gameserver.model.residence.castle.Castle;
import com.px.gameserver.model.residence.castle.Siege;
import com.px.gameserver.model.residence.clanhall.SiegableHall;
import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;
import com.px.gameserver.model.spawn.SpawnData;

public abstract class SpawnMaker
{
	private final String _makerName;
	
	public SpawnMaker(String name)
	{
		_makerName = name;
	}
	
	public String getMakerName()
	{
		return _makerName;
	}
	
	public void onStart(NpcMaker maker)
	{
	}
	
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
	}
	
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
	}
	
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
	}
	
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
	}
	
	public void onSiegeEvent(Siege siege, NpcMaker maker)
	{
	}
	
	public void onDoorEvent(Door door, NpcMaker maker)
	{
	}
	
	public void onTimer(String name, NpcMaker maker)
	{
	}
	
	public boolean checkHasSpawnCondition(NpcMaker maker)
	{
		final String eventName = maker.getEvent();
		if (eventName != null)
		{
			final PeriodType ssqPeriod = SevenSignsManager.getInstance().getCurrentPeriod();
			final CabalType cabalWon = SevenSignsManager.getInstance().getWinningCabal();
			final CabalType sealOwner1 = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
			final CabalType sealOwner2 = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
			
			switch (eventName)
			{
				case "ssq_seal1_none":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner1 == CabalType.NORMAL || sealOwner1 != cabalWon);
				
				case "ssq_seal1_dawn":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner1 == CabalType.DAWN && sealOwner1 == cabalWon);
				
				case "ssq_seal1_twilight":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner1 == CabalType.DUSK && sealOwner1 == cabalWon);
				
				case "ssq_seal2_none":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner2 == CabalType.NORMAL || sealOwner2 != cabalWon);
				
				case "ssq_seal2_dawn":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner2 == CabalType.DAWN && sealOwner2 == cabalWon);
				
				case "ssq_seal2_twilight":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner2 == CabalType.DUSK && sealOwner2 == cabalWon);
				
				case "ssq_event":
					return !(ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION);
				
				default:
					return !Arrays.stream(Config.SPAWN_EVENTS).anyMatch(eventName::equals);
			}
		}
		
		final MakerSpawnTime spawnTime = maker.getMakerSpawnTime();
		if (spawnTime == null)
			return false;
		
		final String[] spawnTimeParams = maker.getMakerSpawnTimeParams();
		if (spawnTimeParams == null)
			return false;
		
		SiegableHall sh = null;
		Castle cst = null;
		
		switch (spawnTime)
		{
			case AGIT_BR_START:
			case AGIT_FINAL_START:
				return true;
			
			case AGIT_DEF_START:
				sh = (SiegableHall) ClanHallManager.getInstance().getClanHall(Integer.parseInt(spawnTimeParams[0]));
				return !(sh.getSiegeStatus() == SiegeStatus.IN_PROGRESS && !sh.getSiege().wasPreviouslyOwned());
			
			case AGIT_ATK_START:
				sh = (SiegableHall) ClanHallManager.getInstance().getClanHall(Integer.parseInt(spawnTimeParams[0]));
				return !(sh.getSiegeStatus() == SiegeStatus.IN_PROGRESS && sh.getSiege().wasPreviouslyOwned());
			
			case SIEGE_START:
				cst = CastleManager.getInstance().getCastleById(Integer.parseInt(spawnTimeParams[0]));
				return !(cst.getSiegeZone().isActive() && cst.getOwnerId() == 0);
			
			case PC_SIEGE_START:
				cst = CastleManager.getInstance().getCastleById(Integer.parseInt(spawnTimeParams[0]));
				return !(cst.getSiegeZone().isActive() && cst.getOwnerId() > 0);
			
			case DOOR_OPEN:
				if (spawnTimeParams[0].equalsIgnoreCase("[kuruma_parent]"))
					return !DoorData.getInstance().getDoor(20210001).isOpened();
				
			default:
				return true;
		}
	}
}