package net.l2jpx.util;

import org.apache.log4j.Logger;

/**
 * @author ReynalDev
 */
public class JVM
{
	private static final Logger LOGGER = Logger.getLogger(JVM.class);
	
	public static boolean isUsingOpenJDK11()
	{
		if(System.getProperty("java.vm.name").contains("OpenJDK") && Integer.parseInt(System.getProperty("java.specification.version")) >= 11)
		{
			return true;
		}
		
		LOGGER.warn("Looks like you are not using OpenJDK 11, please make sure is installed in your machine.");
		LOGGER.warn("Guide to install Java OpenJDK 11: https://www.youtube.com/watch?v=Cr_mwn67kFs");
		return false;
	}
}
