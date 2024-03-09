package net.l2jpx.util;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * little 'hack' :)
 * @author ProGramMoS
 */
public final class CloseUtil
{
	private final static Logger LOGGER = Logger.getLogger(CloseUtil.class);
	
	public static void close(Connection con)
	{
		if (con != null)
		{
			try
			{
				con.close();
				con = null;
			}
			catch (final Throwable e)
			{
				LOGGER.error(e);
			}
		}
	}
	
}
