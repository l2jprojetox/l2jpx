package net.l2jpx.gameserver.communitybbs.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.communitybbs.BB.Forum;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.util.database.L2DatabaseFactory;

public class ForumsBBSManager extends BaseBBSManager
{
	private static final Logger LOGGER = Logger.getLogger(ForumsBBSManager.class);
	
	private static final String SELECT_FORUM_ID = "SELECT forum_id FROM forums WHERE forum_type=0";
	
	private final List<Forum> table = new CopyOnWriteArrayList<>();
	private static ForumsBBSManager instance;
	private int lastid = 1;
	
	public static ForumsBBSManager getInstance()
	{
		if (instance == null)
		{
			instance = new ForumsBBSManager();
		}
		return instance;
	}
	
	public ForumsBBSManager()
	{
		load();
	}
	
	public void addForum(final Forum ff)
	{
		if (ff == null)
		{
			return;
		}
		
		table.add(ff);
		
		if (ff.getID() > lastid)
		{
			lastid = ff.getID();
		}
	}
	
	private void load()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_FORUM_ID);
			ResultSet result = statement.executeQuery())
		{
			while (result.next())
			{
				Forum f = new Forum(result.getInt("forum_id"), null);
				addForum(f);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("data error on Forum (root)", e);
		}
	}
	
	public void initRoot()
	{
		for (final Forum f : table)
		{
			f.vload();
		}
		LOGGER.info("Loaded " + table.size() + " forums. Last forum id used: " + lastid);
	}
	
	@Override
	public void parsecmd(final String command, final L2PcInstance activeChar)
	{
		//
	}
	
	/**
	 * @param  Name
	 * @return
	 */
	public Forum getForumByName(final String Name)
	{
		for (final Forum f : table)
		{
			if (f.getName().equals(Name))
			{
				return f;
			}
		}
		
		return null;
	}
	
	/**
	 * @param  name
	 * @param  parent
	 * @param  type
	 * @param  perm
	 * @param  oid
	 * @return
	 */
	public Forum createNewForum(final String name, final Forum parent, final int type, final int perm, final int oid)
	{
		final Forum forum = new Forum(name, parent, type, perm, oid);
		forum.insertindb();
		return forum;
	}
	
	/**
	 * @return
	 */
	public int getANewID()
	{
		return ++lastid;
	}
	
	/**
	 * @param  idf
	 * @return
	 */
	public Forum getForumByID(final int idf)
	{
		for (final Forum f : table)
		{
			if (f.getID() == idf)
			{
				return f;
			}
		}
		return null;
	}
	
	@Override
	public void parsewrite(final String ar1, final String ar2, final String ar3, final String ar4, final String ar5, final L2PcInstance activeChar)
	{
		//
	}
}
