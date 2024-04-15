package com.px.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.handler.itemhandlers.BeastSoulShot;
import com.px.gameserver.handler.itemhandlers.BeastSpice;
import com.px.gameserver.handler.itemhandlers.BeastSpiritShot;
import com.px.gameserver.handler.itemhandlers.BlessedSpiritShot;
import com.px.gameserver.handler.itemhandlers.Book;
import com.px.gameserver.handler.itemhandlers.Calculator;
import com.px.gameserver.handler.itemhandlers.Elixir;
import com.px.gameserver.handler.itemhandlers.EnchantScrolls;
import com.px.gameserver.handler.itemhandlers.FishShots;
import com.px.gameserver.handler.itemhandlers.Harvester;
import com.px.gameserver.handler.itemhandlers.ItemSkills;
import com.px.gameserver.handler.itemhandlers.Keys;
import com.px.gameserver.handler.itemhandlers.Maps;
import com.px.gameserver.handler.itemhandlers.MercTicket;
import com.px.gameserver.handler.itemhandlers.PaganKeys;
import com.px.gameserver.handler.itemhandlers.PetFood;
import com.px.gameserver.handler.itemhandlers.Recipes;
import com.px.gameserver.handler.itemhandlers.RollingDice;
import com.px.gameserver.handler.itemhandlers.ScrollOfResurrection;
import com.px.gameserver.handler.itemhandlers.SeedHandler;
import com.px.gameserver.handler.itemhandlers.SevenSignsRecord;
import com.px.gameserver.handler.itemhandlers.SoulCrystals;
import com.px.gameserver.handler.itemhandlers.SoulShots;
import com.px.gameserver.handler.itemhandlers.SpecialXMas;
import com.px.gameserver.handler.itemhandlers.SpiritShot;
import com.px.gameserver.handler.itemhandlers.SummonItems;
import com.px.gameserver.model.item.kind.EtcItem;

public class ItemHandler
{
	private final Map<Integer, IItemHandler> _entries = new HashMap<>();
	
	protected ItemHandler()
	{
		registerHandler(new BeastSoulShot());
		registerHandler(new BeastSpice());
		registerHandler(new BeastSpiritShot());
		registerHandler(new BlessedSpiritShot());
		registerHandler(new Book());
		registerHandler(new Calculator());
		registerHandler(new Elixir());
		registerHandler(new EnchantScrolls());
		registerHandler(new FishShots());
		registerHandler(new Harvester());
		registerHandler(new ItemSkills());
		registerHandler(new Keys());
		registerHandler(new Maps());
		registerHandler(new MercTicket());
		registerHandler(new PaganKeys());
		registerHandler(new PetFood());
		registerHandler(new Recipes());
		registerHandler(new RollingDice());
		registerHandler(new ScrollOfResurrection());
		registerHandler(new SeedHandler());
		registerHandler(new SevenSignsRecord());
		registerHandler(new SoulShots());
		registerHandler(new SpecialXMas());
		registerHandler(new SoulCrystals());
		registerHandler(new SpiritShot());
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