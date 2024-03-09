package net.l2jpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author ReynalDev
 */
public class L2Properties extends Properties
{
	private static final long serialVersionUID = -4599023842346938325L;
	protected static final Logger LOGGER = Logger.getLogger(L2Properties.class);
	private String filePath;
	
	public L2Properties()
	{
	}
	
	public L2Properties(String path)
	{
		filePath = path;
		
		try(InputStream is = new FileInputStream(new File(filePath)))
		{
			load(is);
		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
	}
	
	@Override
	public String getProperty(String key, String defaultValue)
	{
		String property = super.getProperty(key, defaultValue);
		
		if (property == null)
		{
			LOGGER.error("L2Properties.getProperty(key, defaultValue): " + (filePath != null ? filePath : "") + " missing property for key - " + key);
			return defaultValue;
		}
		
		return property.trim();
	}
}