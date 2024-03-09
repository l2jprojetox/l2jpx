package net.l2jpx.gameserver.datatables.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.idfactory.IdFactory;
import net.l2jpx.gameserver.model.actor.instance.L2StaticObjectInstance;

public class StaticObjectsTable
{
	private static Logger LOGGER = Logger.getLogger(StaticObjectsTable.class);
	private static StaticObjectsTable instance;
	private final Map<Integer, L2StaticObjectInstance> staticObjects;
	
	public static StaticObjectsTable getInstance()
	{
		if (instance == null)
		{
			instance = new StaticObjectsTable();
		}
		
		return instance;
	}
	
	public StaticObjectsTable()
	{
		staticObjects = new HashMap<>();
		parseData();
		LOGGER.info("StaticObject: Loaded " + staticObjects.size() + " StaticObject Templates.");
	}
	
	private void parseData()
	{
		File doorData = new File(Config.DATAPACK_ROOT, "data/csv/staticobjects.csv");
		
		try (FileReader reader = new FileReader(doorData);
			BufferedReader buff = new BufferedReader(reader);
			LineNumberReader lnr = new LineNumberReader(buff))
		{
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
				{
					continue;
				}
				
				L2StaticObjectInstance obj = parse(line);
				staticObjects.put(obj.getStaticObjectId(), obj);
				obj = null;
			}
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error("StaticObjects.parseData : staticobjects.csv file is missing in gameserver/data/csv/ folder");
		}
		catch (Exception e)
		{
			LOGGER.error("StaticObjects.parseData :  Error while creating StaticObjects table.", e);
		}
	}
	
	public static L2StaticObjectInstance parse(final String line)
	{
		StringTokenizer st = new StringTokenizer(line, ";");
		
		st.nextToken(); // Pass over static object name (not used in server)
		
		final int id = Integer.parseInt(st.nextToken());
		final int x = Integer.parseInt(st.nextToken());
		final int y = Integer.parseInt(st.nextToken());
		final int z = Integer.parseInt(st.nextToken());
		final int type = Integer.parseInt(st.nextToken());
		final String texture = st.nextToken();
		final int map_x = Integer.parseInt(st.nextToken());
		final int map_y = Integer.parseInt(st.nextToken());
		
		st = null;
		
		final L2StaticObjectInstance obj = new L2StaticObjectInstance(IdFactory.getInstance().getNextId());
		obj.setType(type);
		obj.setStaticObjectId(id);
		obj.setXYZ(x, y, z);
		obj.setMap(texture, map_x, map_y);
		obj.spawnMe();
		
		return obj;
	}
}
