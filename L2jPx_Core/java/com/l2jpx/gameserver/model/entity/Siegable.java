package com.l2jpx.gameserver.model.entity;

import java.util.Calendar;
import java.util.List;

import com.l2jpx.gameserver.enums.SiegeSide;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.pledge.Clan;

public interface Siegable
{
	public void startSiege();
	
	public void endSiege();
	
	public List<Clan> getAttackerClans();
	
	public List<Clan> getDefenderClans();
	
	public boolean checkSide(Clan clan, SiegeSide type);
	
	public boolean checkSides(Clan clan, SiegeSide... types);
	
	public boolean checkSides(Clan clan);
	
	public Npc getFlag(Clan clan);
	
	public Calendar getSiegeDate();
}