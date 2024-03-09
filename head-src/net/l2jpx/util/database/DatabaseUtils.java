package net.l2jpx.util.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils
{
	
	public static void close(final PreparedStatement stmt)
	{
		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch (final SQLException e)
			{
			}
		}
	}
	
	public static void close(final ResultSet rs)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (final SQLException e)
			{
			}
		}
	}
	
}