package com.px.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.handler.itemhandlers.BeastSoulShots;
import com.px.gameserver.handler.itemhandlers.BeastSpices;
import com.px.gameserver.handler.itemhandlers.BeastSpiritShots;
import com.px.gameserver.handler.itemhandlers.BlessedSpiritShots;
import com.px.gameserver.handler.itemhandlers.Books;
import com.px.gameserver.handler.itemhandlers.Calculators;
import com.px.gameserver.handler.itemhandlers.Elixirs;
import com.px.gameserver.handler.itemhandlers.EnchantScrolls;
import com.px.gameserver.handler.itemhandlers.FishShots;
import com.px.gameserver.handler.itemhandlers.Harvesters;
import com.px.gameserver.handler.itemhandlers.ItemSkills;
import com.px.gameserver.handler.itemhandlers.Keys;
import com.px.gameserver.handler.itemhandlers.Maps;
import com.px.gameserver.handler.itemhandlers.MercenaryTickets;
import com.px.gameserver.handler.itemhandlers.PaganKeys;
import com.px.gameserver.handler.itemhandlers.PetFoods;
import com.px.gameserver.handler.itemhandlers.Recipes;
import com.px.gameserver.handler.itemhandlers.RollingDices;
import com.px.gameserver.handler.itemhandlers.ScrollsOfResurrection;
import com.px.gameserver.handler.itemhandlers.Seeds;
import com.px.gameserver.handler.itemhandlers.SevenSignsRecords;
import com.px.gameserver.handler.itemhandlers.SoulCrystals;
import com.px.gameserver.handler.itemhandlers.SoulShots;
import com.px.gameserver.handler.itemhandlers.SpecialXMas;
import com.px.gameserver.handler.itemhandlers.SpiritShots;
import com.px.gameserver.handler.itemhandlers.SummonItems;
import com.px.gameserver.handler.itemhandlers.custom.CapsuleBox_System;
import com.px.gameserver.model.item.kind.EtcItem;

public class ItemHandler
{
	private final Map<Integer, IItemHandler> _entries = new HashMap<>();
	
	protected ItemHandler()
	{
		registerHandler(new BeastSoulShots());
		registerHandler(new BeastSpices());
		registerHandler(new BeastSpiritShots());
		registerHandler(new BlessedSpiritShots());
		registerHandler(new Books());
		registerHandler(new Calculators());
		registerHandler(new Elixirs());
		registerHandler(new EnchantScrolls());
		registerHandler(new FishShots());
		registerHandler(new Harvesters());
		registerHandler(new ItemSkills());
		registerHandler(new Keys());
		registerHandler(new Maps());
		registerHandler(new MercenaryTickets());
		registerHandler(new PaganKeys());
		registerHandler(new PetFoods());
		registerHandler(new Recipes());
		registerHandler(new RollingDices());
		registerHandler(new ScrollsOfResurrection());
		registerHandler(new Seeds());
		registerHandler(new SevenSignsRecords());
		registerHandler(new SoulShots());
		registerHandler(new SpecialXMas());
		registerHandler(new SoulCrystals());
		registerHandler(new SpiritShots());
		registerHandler(new SummonItems());
		registerHandler(new CapsuleBox_System());
	}
	
	private void registerHandler(IItemHandler handler)
	{
		_entries.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getHandler(EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _entries.get(item.getHandlerName().hashCode());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}