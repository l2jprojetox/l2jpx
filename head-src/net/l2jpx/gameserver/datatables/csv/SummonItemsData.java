package net.l2jpx.gameserver.datatables.csv;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.model.L2SummonItem;

/**
* @author FBIagent
* @author ReynalDev 
*/
public class SummonItemsData
{
	private static final Logger LOGGER = Logger.getLogger(SummonItemsData.class);
	private static SummonItemsData instance;
	private final Map<Integer, L2SummonItem> summonitems;
	
	public static SummonItemsData getInstance()
	{
		if (instance == null)
		{
			instance = new SummonItemsData();
		}
		
		return instance;
	}
	
	public SummonItemsData()
	{
		summonitems = new HashMap<>();
		
		try (Scanner s = new Scanner(new File(Config.DATAPACK_ROOT + "/data/csv/summon_items.csv")))
		{
			int lineCount = 0;
			
			while (s.hasNextLine())
			{
				lineCount++;
				
				String line = s.nextLine();
				
				if (line.startsWith("#"))
				{
					continue;
				}
				else if (line.equals(""))
				{
					continue;
				}
				
				final String[] lineSplit = line.split(";");
				line = null;
				
				boolean ok = true;
				int itemID = 0, npcID = 0;
				byte summonType = 0;
				
				try
				{
					itemID = Integer.parseInt(lineSplit[0]);
					npcID = Integer.parseInt(lineSplit[1]);
					summonType = Byte.parseByte(lineSplit[2]);
				}
				catch (Exception e)
				{
					LOGGER.error("Summon items data: Error in line " + lineCount + " -> incomplete/invalid data or wrong seperator!");
					LOGGER.error("Line: " + line);
					LOGGER.error(e);
					ok = false;
				}
				
				if (!ok)
				{
					continue;
				}
				
				L2SummonItem summonitem = new L2SummonItem(itemID, npcID, summonType);
				summonitems.put(itemID, summonitem);
				summonitem = null;
			}
			
		}
		catch (Exception e)
		{
			LOGGER.info("SummonItemsData.SummonItemsData : Can not find summon_items.csv file in gameserver/data/csv/ folder. ");
		}
		
		LOGGER.info("Summon items data: Loaded " + summonitems.size() + " summon items.");
	}
	
	public L2SummonItem getSummonItem(final int itemId)
	{
		return summonitems.get(itemId);
	}
	
	public int[] itemIDs()
	{
		final int size = summonitems.size();
		final int[] result = new int[size];
		int i = 0;
		
		for (final L2SummonItem si : summonitems.values())
		{
			result[i] = si.getItemId();
			i++;
		}
		return result;
	}
}
