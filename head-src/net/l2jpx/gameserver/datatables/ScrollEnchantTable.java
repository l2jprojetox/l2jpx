package net.l2jpx.gameserver.datatables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ReynalDev
 */
public class ScrollEnchantTable
{
	private static final List<Integer> SCROLL_ENCHANT_WEAPON = Arrays.asList(
		729, // Scroll: Enchant Weapon (Grade A)
		947, // Scroll: Enchant Weapon (Grade B)
		951, // Scroll: Enchant Weapon (Grade C)
		955, // Scroll: Enchant Weapon (Grade D)
		959 // Scroll: Enchant Weapon (Grade S)
	);
	
	private static final List<Integer> SCROLL_ENCHANT_ARMOR = Arrays.asList(
		730, // Scroll: Enchant Armor (Grade A)
		948, // Scroll: Enchant Armor (Grade B)
		952, // Scroll: Enchant Armor (Grade C)
		956, // Scroll: Enchant Armor (Grade D)
		960 // Scroll: Enchant Armor (Grade S)
	);
	
	private static final List<Integer> BLESSED_SCROLL_ENCHANT_WEAPON = Arrays.asList(
		6569, // Blessed Scroll: Enchant Weapon (Grade A)
		6571, // Blessed Scroll: Enchant Weapon (Grade B)
		6573, // Blessed Scroll: Enchant Weapon (Grade C)
		6575, // Blessed Scroll: Enchant Weapon (Grade D)
		6577 // Blessed Scroll: Enchant Weapon (Grade S)
	);
	
	private static final List<Integer> BLESSED_SCROLL_ENCHANT_ARMOR = Arrays.asList(
		6570, // Blessed Scroll: Enchant Armor (Grade A)
		6572, // Blessed Scroll: Enchant Armor (Grade B)
		6574, // Blessed Scroll: Enchant Armor (Grade C)
		6576, // Blessed Scroll: Enchant Armor (Grade D)
		6578 // Blessed Scroll: Enchant Armor (Grade S)
	);
	
	private static final List<Integer> BLESSED_SCROLLS = new ArrayList<>();
	
	static
	{
		for(int scrollId : BLESSED_SCROLL_ENCHANT_WEAPON)
		{
			BLESSED_SCROLLS.add(scrollId);
		}
		
		for(int scrollId : BLESSED_SCROLL_ENCHANT_ARMOR)
		{
			BLESSED_SCROLLS.add(scrollId);
		}
	}
	
	private static final List<Integer> CRYSTAL_SCROLL_ENCHANT_WEAPON = Arrays.asList(
		731, // Crystal Scroll: Enchant Weapon (Grade A)
		949, // Crystal Scroll: Enchant Weapon (Grade B)
		953, // Crystal Scroll: Enchant Weapon (Grade C)
		957, // Crystal Scroll: Enchant Weapon (Grade D)
		961 // Crystal Scroll: Enchant Weapon (Grade S)
	);
	
	private static final List<Integer> CRYSTAL_SCROLL_ENCHANT_ARMOR = Arrays.asList(
		732, // Crystal Scroll: Enchant Armor (Grade A)
		950, // Crystal Scroll: Enchant Armor (Grade B)
		954, // Crystal Scroll: Enchant Armor (Grade C)
		958, // Crystal Scroll: Enchant Armor (Grade D)
		962 // Crystal Scroll: Enchant Armor (Grade S)
	);
	
	private static final List<Integer> CRYSTAL_SCROLLS = new ArrayList<>();
	
	static
	{
		for(int scrollId : CRYSTAL_SCROLL_ENCHANT_WEAPON)
		{
			CRYSTAL_SCROLLS.add(scrollId);
		}
		
		for(int scrollId : CRYSTAL_SCROLL_ENCHANT_ARMOR)
		{
			CRYSTAL_SCROLLS.add(scrollId);
		}
	}
	
	private static final List<Integer> ALL_SCROLL_ENCHANTS = new ArrayList<>();
	
	static
	{
		for(int scrollId : SCROLL_ENCHANT_WEAPON)
		{
			ALL_SCROLL_ENCHANTS.add(scrollId);
		}
		
		for(int scrollId : SCROLL_ENCHANT_ARMOR)
		{
			ALL_SCROLL_ENCHANTS.add(scrollId);
		}
		
		for(int scrollId : BLESSED_SCROLL_ENCHANT_WEAPON)
		{
			ALL_SCROLL_ENCHANTS.add(scrollId);
		}
		
		for(int scrollId : BLESSED_SCROLL_ENCHANT_ARMOR)
		{
			ALL_SCROLL_ENCHANTS.add(scrollId);
		}
		
		for(int scrollId : CRYSTAL_SCROLL_ENCHANT_WEAPON)
		{
			ALL_SCROLL_ENCHANTS.add(scrollId);
		}
		
		for(int scrollId : CRYSTAL_SCROLL_ENCHANT_ARMOR)
		{
			ALL_SCROLL_ENCHANTS.add(scrollId);
		}
	}
	
	public static List<Integer> getScrollEnchantWeaponIds()
	{
		return SCROLL_ENCHANT_WEAPON;
	}
	
	public static List<Integer> getScrollEnchantArmorIds()
	{
		return SCROLL_ENCHANT_ARMOR;
	}
	
	public static List<Integer> getBlessedScrollEnchantWeaponIds()
	{
		return BLESSED_SCROLL_ENCHANT_WEAPON;
	}
	
	public static List<Integer> getBlessedScrollEnchantArmorIds()
	{
		return BLESSED_SCROLL_ENCHANT_ARMOR;
	}
	
	public static List<Integer> getCrystalScrollEnchantWeaponIds()
	{
		return CRYSTAL_SCROLL_ENCHANT_WEAPON;
	}
	
	public static List<Integer> getCrystalScrollEnchantArmorIds()
	{
		return CRYSTAL_SCROLL_ENCHANT_ARMOR;
	}
	
	public static List<Integer> getBlessedScrollEnchantIds()
	{
		return BLESSED_SCROLLS;
	}
	
	public static List<Integer> getCrystalScrollEnchantIds()
	{
		return CRYSTAL_SCROLLS;
	}
	
	/**
	 * @return Normal (weapon and armor), Blessed (weapon and armor) and Crystal (weapon and armor) scroll IDs
	 */
	public static List<Integer> getAllScrollEnchantIds()
	{
		return ALL_SCROLL_ENCHANTS;
	}
}
