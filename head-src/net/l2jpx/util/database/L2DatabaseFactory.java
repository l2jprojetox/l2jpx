package net.l2jpx.util.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class L2DatabaseFactory
{
	private static final Logger LOGGER = Logger.getLogger(L2DatabaseFactory.class);
	private HikariDataSource source;
	protected static L2DatabaseFactory instance;
	
	public static L2DatabaseFactory getInstance()
	{
		if (instance == null)
		{
			instance = new L2DatabaseFactory();
			
			LOGGER.info("You are using DBMS: MariaDB");
			LOGGER.info("You are using JDBC Pool: HikariCP");
		}
		
		return instance;
	}
	
	public L2DatabaseFactory()
	{
		try
		{
			HikariConfig config = new HikariConfig();
			config.setDriverClassName("org.mariadb.jdbc.Driver");
			config.setJdbcUrl(Config.DATABASE_URL);
			config.setUsername(Config.DATABASE_USER);
			config.setPassword(Config.DATABASE_PASSWORD);
			
			// Typical configuration - Visit: https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
			// Also - http://assets.en.oreilly.com/1/event/21/Connector_J%20Performance%20Gems%20Presentation.pdf
			
			// Recommended
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("useServerPrepStmts", "true");
			
			// Optionals - Performance
			config.addDataSourceProperty("useLocalSessionState", "true");
			config.addDataSourceProperty("useLocalTransactionState", "true");
			config.addDataSourceProperty("rewriteBatchedStatements", "true");
			config.addDataSourceProperty("cacheResultSetMetadata", "true");
			config.addDataSourceProperty("cacheServerConfiguration", "true");
			config.addDataSourceProperty("useLocalSessionState", "true");
			config.addDataSourceProperty("elideSetAutoCommits", "true");
			config.addDataSourceProperty("maintainTimeStats", "false");
			
			source = new HikariDataSource(config);
		}
		catch (Exception e)
		{
			LOGGER.error("HikariCP: Could not init DB connection", e);
			System.exit(1);
		}
	}
	
	public final String safetyString(final String... whatToCheck)
	{
		// NOTE: Use brace as a safty precaution just incase name is a reserved word
		final char braceLeft;
		final char braceRight;
		
		braceLeft = '`';
		braceRight = '`';
		
		int length = 0;
		
		for (final String word : whatToCheck)
		{
			length += word.length() + 4;
		}
		
		final StringBuilder sbResult = new StringBuilder(length);
		
		for (final String word : whatToCheck)
		{
			if (sbResult.length() > 0)
			{
				sbResult.append(", ");
			}
			
			sbResult.append(braceLeft);
			sbResult.append(word);
			sbResult.append(braceRight);
		}
		
		return sbResult.toString();
	}
	
	
	public Connection getConnection()
	{
		Connection con = null;
		
		while (con == null && source != null)
		{
			try
			{
				con = source.getConnection();
			}
			catch (SQLException e)
			{
				LOGGER.error("L2DatabaseFactory.getConnection: Failed to establish connection ", e);
			}
		}
		
		return con;
	}
	
	public void shutdown()
	{
		try
		{
			// Close connection
			source.close();
			// release source
			source = null;
			
			LOGGER.info("L2DatabaseFactory shutted down!");
		}
		catch (Exception e)
		{
			LOGGER.error("L2DatabaseFactory.shutdown : Something went wrong", e);
		}
	}
}
