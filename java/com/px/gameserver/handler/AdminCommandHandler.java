package com.px.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.handler.admincommandhandlers.AdminAdmin;
import com.px.gameserver.handler.admincommandhandlers.AdminAlternativeSpawn;
import com.px.gameserver.handler.admincommandhandlers.AdminAnnouncements;
import com.px.gameserver.handler.admincommandhandlers.AdminBookmark;
import com.px.gameserver.handler.admincommandhandlers.AdminClanHall;
import com.px.gameserver.handler.admincommandhandlers.AdminCursedWeapon;
import com.px.gameserver.handler.admincommandhandlers.AdminDoor;
import com.px.gameserver.handler.admincommandhandlers.AdminEditChar;
import com.px.gameserver.handler.admincommandhandlers.AdminEffects;
import com.px.gameserver.handler.admincommandhandlers.AdminEnchant;
import com.px.gameserver.handler.admincommandhandlers.AdminFind;
import com.px.gameserver.handler.admincommandhandlers.AdminGeoEngine;
import com.px.gameserver.handler.admincommandhandlers.AdminInfo;
import com.px.gameserver.handler.admincommandhandlers.AdminItem;
import com.px.gameserver.handler.admincommandhandlers.AdminKnownlist;
import com.px.gameserver.handler.admincommandhandlers.AdminMaintenance;
import com.px.gameserver.handler.admincommandhandlers.AdminManage;
import com.px.gameserver.handler.admincommandhandlers.AdminManor;
import com.px.gameserver.handler.admincommandhandlers.AdminMovieMaker;
import com.px.gameserver.handler.admincommandhandlers.AdminOlympiad;
import com.px.gameserver.handler.admincommandhandlers.AdminPetition;
import com.px.gameserver.handler.admincommandhandlers.AdminPledge;
import com.px.gameserver.handler.admincommandhandlers.AdminPolymorph;
import com.px.gameserver.handler.admincommandhandlers.AdminPunish;
import com.px.gameserver.handler.admincommandhandlers.AdminReload;
import com.px.gameserver.handler.admincommandhandlers.AdminSiege;
import com.px.gameserver.handler.admincommandhandlers.AdminSkill;
import com.px.gameserver.handler.admincommandhandlers.AdminSpawn;
import com.px.gameserver.handler.admincommandhandlers.AdminSummon;
import com.px.gameserver.handler.admincommandhandlers.AdminTarget;
import com.px.gameserver.handler.admincommandhandlers.AdminTeleport;
import com.px.gameserver.handler.admincommandhandlers.AdminTest;
import com.px.gameserver.handler.admincommandhandlers.AdminZone;

public class AdminCommandHandler
{
	private final Map<Integer, IAdminCommandHandler> _entries = new HashMap<>();
	
	protected AdminCommandHandler()
	{
		registerHandler(new AdminAdmin());
		registerHandler(new AdminAlternativeSpawn());
		registerHandler(new AdminAnnouncements());
		registerHandler(new AdminBookmark());
		registerHandler(new AdminClanHall());
		registerHandler(new AdminCursedWeapon());
		registerHandler(new AdminDoor());
		registerHandler(new AdminEditChar());
		registerHandler(new AdminEffects());
		registerHandler(new AdminEnchant());
		registerHandler(new AdminFind());
		registerHandler(new AdminGeoEngine());
		registerHandler(new AdminInfo());
		registerHandler(new AdminItem());
		registerHandler(new AdminKnownlist());
		registerHandler(new AdminMaintenance());
		registerHandler(new AdminManage());
		registerHandler(new AdminManor());
		registerHandler(new AdminMovieMaker());
		registerHandler(new AdminOlympiad());
		registerHandler(new AdminPetition());
		registerHandler(new AdminPledge());
		registerHandler(new AdminPolymorph());
		registerHandler(new AdminPunish());
		registerHandler(new AdminReload());
		registerHandler(new AdminSiege());
		registerHandler(new AdminSkill());
		registerHandler(new AdminSpawn());
		registerHandler(new AdminSummon());
		registerHandler(new AdminTarget());
		registerHandler(new AdminTeleport());
		registerHandler(new AdminTest());
		registerHandler(new AdminZone());
	}
	
	private void registerHandler(IAdminCommandHandler handler)
	{
		for (String id : handler.getAdminCommandList())
			_entries.put(id.hashCode(), handler);
	}
	
	public IAdminCommandHandler getHandler(String adminCommand)
	{
		String command = adminCommand;
		
		if (adminCommand.indexOf(" ") != -1)
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		
		return _entries.get(command.hashCode());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
	}
}