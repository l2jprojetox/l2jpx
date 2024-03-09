package net.l2jpx.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;

import net.l2jpx.gameserver.handler.itemhandlers.custom.QuestHelp;
import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.GameServer;
import net.l2jpx.gameserver.handler.itemhandlers.BeastSoulShot;
import net.l2jpx.gameserver.handler.itemhandlers.BeastSpice;
import net.l2jpx.gameserver.handler.itemhandlers.BeastSpiritShot;
import net.l2jpx.gameserver.handler.itemhandlers.BlessedSpiritShot;
import net.l2jpx.gameserver.handler.itemhandlers.Book;
import net.l2jpx.gameserver.handler.itemhandlers.BreakingArrow;
import net.l2jpx.gameserver.handler.itemhandlers.CharChangePotions;
import net.l2jpx.gameserver.handler.itemhandlers.ChestKey;
import net.l2jpx.gameserver.handler.itemhandlers.ChristmasTree;
import net.l2jpx.gameserver.handler.itemhandlers.CrystalCarol;
import net.l2jpx.gameserver.handler.itemhandlers.Crystals;
import net.l2jpx.gameserver.handler.itemhandlers.CustomPotions;
import net.l2jpx.gameserver.handler.itemhandlers.EnchantScrolls;
import net.l2jpx.gameserver.handler.itemhandlers.EnergyStone;
import net.l2jpx.gameserver.handler.itemhandlers.ExtractableItems;
import net.l2jpx.gameserver.handler.itemhandlers.Firework;
import net.l2jpx.gameserver.handler.itemhandlers.FishShots;
import net.l2jpx.gameserver.handler.itemhandlers.Harvester;
import net.l2jpx.gameserver.handler.itemhandlers.HeroCustomItem;
import net.l2jpx.gameserver.handler.itemhandlers.JackpotSeed;
import net.l2jpx.gameserver.handler.itemhandlers.MOSKey;
import net.l2jpx.gameserver.handler.itemhandlers.MapForestOfTheDead;
import net.l2jpx.gameserver.handler.itemhandlers.Maps;
import net.l2jpx.gameserver.handler.itemhandlers.MercTicket;
import net.l2jpx.gameserver.handler.itemhandlers.MysteryPotion;
import net.l2jpx.gameserver.handler.itemhandlers.Nectar;
import net.l2jpx.gameserver.handler.itemhandlers.NobleCustomItem;
import net.l2jpx.gameserver.handler.itemhandlers.PaganKeys;
import net.l2jpx.gameserver.handler.itemhandlers.Potions;
import net.l2jpx.gameserver.handler.itemhandlers.Recipes;
import net.l2jpx.gameserver.handler.itemhandlers.Remedy;
import net.l2jpx.gameserver.handler.itemhandlers.RollingDice;
import net.l2jpx.gameserver.handler.itemhandlers.ScrollOfEscape;
import net.l2jpx.gameserver.handler.itemhandlers.ScrollOfResurrection;
import net.l2jpx.gameserver.handler.itemhandlers.Scrolls;
import net.l2jpx.gameserver.handler.itemhandlers.Seed;
import net.l2jpx.gameserver.handler.itemhandlers.SevenSignsRecord;
import net.l2jpx.gameserver.handler.itemhandlers.SoulCrystals;
import net.l2jpx.gameserver.handler.itemhandlers.SoulShots;
import net.l2jpx.gameserver.handler.itemhandlers.SpecialXMas;
import net.l2jpx.gameserver.handler.itemhandlers.SpiritShot;
import net.l2jpx.gameserver.handler.itemhandlers.SummonItems;
import net.l2jpx.gameserver.handler.itemhandlers.custom.FullBuffScroll;
import net.l2jpx.gameserver.handler.itemhandlers.custom.MonsterSummon;

public class ItemHandler
{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class);
	
	private static ItemHandler instance;
	
	private final Map<Integer, IItemHandler> dataTable;
	
	/**
	 * Create ItemHandler if doesn't exist and returns ItemHandler
	 * @return ItemHandler
	 */
	public static ItemHandler getInstance()
	{
		if (instance == null)
		{
			instance = new ItemHandler();
		}
		
		return instance;
	}
	
	/**
	 * Returns the number of elements contained in datatable
	 * @return int : Size of the datatable
	 */
	public int size()
	{
		return dataTable.size();
	}
	
	/**
	 * Constructor of ItemHandler
	 */
	private ItemHandler()
	{
		dataTable = new TreeMap<>();
		registerItemHandler(new ScrollOfEscape());
		registerItemHandler(new ScrollOfResurrection());
		registerItemHandler(new SoulShots());
		registerItemHandler(new SpiritShot());
		registerItemHandler(new BlessedSpiritShot());
		registerItemHandler(new BeastSoulShot());
		registerItemHandler(new BeastSpiritShot());
		registerItemHandler(new ChestKey());
		registerItemHandler(new CustomPotions());
		registerItemHandler(new PaganKeys());
		registerItemHandler(new Maps());
		registerItemHandler(new MapForestOfTheDead());
		registerItemHandler(new Potions());
		registerItemHandler(new Recipes());
		registerItemHandler(new RollingDice());
		registerItemHandler(new MysteryPotion());
		registerItemHandler(new EnchantScrolls());
		registerItemHandler(new EnergyStone());
		registerItemHandler(new Book());
		registerItemHandler(new Remedy());
		registerItemHandler(new Scrolls());
		registerItemHandler(new CrystalCarol());
		registerItemHandler(new SoulCrystals());
		registerItemHandler(new SevenSignsRecord());
		registerItemHandler(new CharChangePotions());
		registerItemHandler(new Firework());
		registerItemHandler(new Seed());
		registerItemHandler(new Harvester());
		registerItemHandler(new MercTicket());
		registerItemHandler(new Nectar());
		registerItemHandler(new FishShots());
		registerItemHandler(new ExtractableItems());
		registerItemHandler(new SpecialXMas());
		registerItemHandler(new SummonItems());
		registerItemHandler(new BeastSpice());
		registerItemHandler(new JackpotSeed());
		registerItemHandler(new MonsterSummon());
		registerItemHandler(new FullBuffScroll());
		registerItemHandler(new QuestHelp());
		
		if (Config.NOBLE_CUSTOM_ITEMS)
		{
			registerItemHandler(new NobleCustomItem());
		}
		
		if (Config.HERO_CUSTOM_ITEMS)
		{
			registerItemHandler(new HeroCustomItem());
		}
		
		registerItemHandler(new MOSKey());
		registerItemHandler(new BreakingArrow());
		registerItemHandler(new ChristmasTree());
		registerItemHandler(new Crystals());
		LOGGER.info("ItemHandler: Loaded " + dataTable.size() + " handlers.");
	}
	
	/**
	 * Adds handler of item type in <I>datatable</I>.<BR>
	 * <BR>
	 * <B><I>Concept :</I></U><BR>
	 * This handler is put in <I>datatable</I> Map &lt;Integer ; IItemHandler &gt; for each ID corresponding to an item type (existing in classes of package itemhandlers) sets as key of the Map.
	 * @param handler (IItemHandler)
	 */
	public void registerItemHandler(final IItemHandler handler)
	{
		// Get all ID corresponding to the item type of the handler
		final int[] ids = handler.getItemIds();
		
		// Add handler for each ID found
		for (final int id : ids)
		{
			dataTable.put(id, handler);
		}
	}
	
	/**
	 * Returns the handler of the item
	 * @param  itemId : int designating the itemID
	 * @return        IItemHandler
	 */
	public IItemHandler getItemHandler(final int itemId)
	{
		return dataTable.get(itemId);
	}
}
