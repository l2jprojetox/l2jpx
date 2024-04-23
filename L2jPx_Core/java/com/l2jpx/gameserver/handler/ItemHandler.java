package com.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jpx.gameserver.handler.itemhandlers.BeastSoulShots;
import com.l2jpx.gameserver.handler.itemhandlers.BeastSpices;
import com.l2jpx.gameserver.handler.itemhandlers.BeastSpiritShots;
import com.l2jpx.gameserver.handler.itemhandlers.BlessedSpiritShots;
import com.l2jpx.gameserver.handler.itemhandlers.Books;
import com.l2jpx.gameserver.handler.itemhandlers.Calculators;
import com.l2jpx.gameserver.handler.itemhandlers.Elixirs;
import com.l2jpx.gameserver.handler.itemhandlers.EnchantScrolls;
import com.l2jpx.gameserver.handler.itemhandlers.FishShots;
import com.l2jpx.gameserver.handler.itemhandlers.Harvesters;
import com.l2jpx.gameserver.handler.itemhandlers.ItemSkills;
import com.l2jpx.gameserver.handler.itemhandlers.Keys;
import com.l2jpx.gameserver.handler.itemhandlers.Maps;
import com.l2jpx.gameserver.handler.itemhandlers.MercenaryTickets;
import com.l2jpx.gameserver.handler.itemhandlers.PaganKeys;
import com.l2jpx.gameserver.handler.itemhandlers.PetFoods;
import com.l2jpx.gameserver.handler.itemhandlers.Recipes;
import com.l2jpx.gameserver.handler.itemhandlers.RollingDices;
import com.l2jpx.gameserver.handler.itemhandlers.ScrollsOfResurrection;
import com.l2jpx.gameserver.handler.itemhandlers.Seeds;
import com.l2jpx.gameserver.handler.itemhandlers.SevenSignsRecords;
import com.l2jpx.gameserver.handler.itemhandlers.SoulCrystals;
import com.l2jpx.gameserver.handler.itemhandlers.SoulShots;
import com.l2jpx.gameserver.handler.itemhandlers.SpecialXMas;
import com.l2jpx.gameserver.handler.itemhandlers.SpiritShots;
import com.l2jpx.gameserver.handler.itemhandlers.SummonItems;
import com.l2jpx.gameserver.model.item.kind.EtcItem;

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