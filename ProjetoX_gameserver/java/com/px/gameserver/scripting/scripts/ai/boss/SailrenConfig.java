package com.px.gameserver.scripting.scripts.ai.boss;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.px.commons.config.ExProperties;

/**
 * @author WaN202
 */
public class SailrenConfig
{
	protected static final Logger _log = Logger.getLogger(SailrenConfig.class.getName());
	public static final String SAILREN_FILE = "./config/boss/Sailren.properties";
	
	public static String FWA_FIXTIMEPATTERNOFSAILREN;
	public static int WAIT_TIME_SAILREN;
	
	public static int Sailren_ItemTakeId;
	public static int Sailren_ItemTakeCont;
	
	
	public static void ini()
	{
		final ExProperties sailren = load(SAILREN_FILE);
		
		FWA_FIXTIMEPATTERNOFSAILREN = sailren.getProperty("SailrenRespawnTimePattern", "");
		WAIT_TIME_SAILREN = sailren.getProperty("SailrenWaitTime", 5) * 60000;
		Sailren_ItemTakeId = sailren.getProperty("SailrenConsumeItemId", 1);
		Sailren_ItemTakeCont = sailren.getProperty("SailrenItemCont", 1);
		
	}
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}
	
	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}
		
		return result;
	}
}
