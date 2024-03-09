package net.l2jpx.gameserver.communitybbs.BB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.l2jpx.gameserver.communitybbs.Manager.TopicBBSManager;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class Forum
{
	private static final Logger LOGGER = Logger.getLogger(Forum.class);
	
	private static final String SELECT_FORUMS = "SELECT forum_id,forum_name,forum_parent,forum_post,forum_type,forum_perm,forum_owner_id FROM forums WHERE forum_id=?";
	private static final String SELECT_TOPICS = "SELECT topic_id,topic_forum_id,topic_name,topic_date,topic_ownername,topic_ownerid,topic_type,topic_reply FROM topic WHERE topic_forum_id=? ORDER BY topic_id DESC";
	private static final String SELECT_FORUM_BY_FORUM_PARENT = "SELECT forum_id FROM forums WHERE forum_parent=?";
	private static final String INSERT_FORUM = "INSERT INTO forums (forum_id,forum_name,forum_parent,forum_post,forum_type,forum_perm,forum_owner_id) VALUES (?,?,?,?,?,?,?)";
	
	// type
	public static final int ROOT = 0;
	public static final int NORMAL = 1;
	public static final int CLAN = 2;
	public static final int MEMO = 3;
	public static final int MAIL = 4;
	// perm
	public static final int INVISIBLE = 0;
	public static final int ALL = 1;
	public static final int CLANMEMBERONLY = 2;
	public static final int OWNERONLY = 3;
	
	private final List<Forum> children;
	private final Map<Integer, Topic> topic;
	private final int forumId;
	private String forumName;
	// private int forumParent;
	private int forumType;
	private int forumPost;
	private int forumPerm;
	private final Forum fParent;
	private int ownerID;
	private boolean loaded = false;
	
	public Forum(final int Forumid, final Forum FParent)
	{
		forumId = Forumid;
		fParent = FParent;
		children = new ArrayList<>();
		topic = new HashMap<>();
	}
	
	public Forum(final String name, final Forum parent, final int type, final int perm, final int OwnerID)
	{
		forumName = name;
		forumId = ForumsBBSManager.getInstance().getANewID();
		// forumParent = parent.getID();
		forumType = type;
		forumPost = 0;
		forumPerm = perm;
		fParent = parent;
		ownerID = OwnerID;
		children = new ArrayList<>();
		topic = new HashMap<>();
		parent.children.add(this);
		ForumsBBSManager.getInstance().addForum(this);
		loaded = true;
	}
	
	private void load()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_FORUMS))
		{
			statement.setInt(1, forumId);
			
			try(ResultSet result = statement.executeQuery())
			{
				if (result.next())
				{
					forumName = result.getString("forum_name");
					// forumParent = Integer.parseInt(result.getString("forum_parent"));
					forumPost = Integer.parseInt(result.getString("forum_post"));
					forumType = Integer.parseInt(result.getString("forum_type"));
					forumPerm = Integer.parseInt(result.getString("forum_perm"));
					ownerID = Integer.parseInt(result.getString("forum_owner_id"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("data error on Forum id " + forumId, e);
		}
		
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_TOPICS))
		{
			statement.setInt(1, forumId);
			
			try(ResultSet result = statement.executeQuery())
			{
				while (result.next())
				{
					Topic t = new Topic(Topic.ConstructorType.RESTORE, Integer.parseInt(result.getString("topic_id")), Integer.parseInt(result.getString("topic_forum_id")), result.getString("topic_name"), Long.parseLong(result.getString("topic_date")), result.getString("topic_ownername"), Integer.parseInt(result.getString("topic_ownerid")), Integer.parseInt(result.getString("topic_type")), Integer.parseInt(result.getString("topic_reply")));
					topic.put(t.getID(), t);
					if (t.getID() > TopicBBSManager.getInstance().getMaxID(this))
					{
						TopicBBSManager.getInstance().setMaxID(t.getID(), this);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("data error on Forum id " + forumId, e);
		}
	}
	
	private void getChildren()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_FORUM_BY_FORUM_PARENT))
		{
			statement.setInt(1, forumId);

			try(ResultSet result = statement.executeQuery())
			{
				while (result.next())
				{
					Forum f = new Forum(result.getInt("forum_id"), this);
					children.add(f);
					ForumsBBSManager.getInstance().addForum(f);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("data error on Forum (children)", e);
		}
	}
	
	public int getTopicSize()
	{
		vload();
		return topic.size();
	}
	
	public Topic gettopic(final int j)
	{
		vload();
		return topic.get(j);
	}
	
	public void addtopic(final Topic t)
	{
		vload();
		topic.put(t.getID(), t);
	}
	
	public int getID()
	{
		return forumId;
	}
	
	public String getName()
	{
		vload();
		return forumName;
	}
	
	public int getType()
	{
		vload();
		return forumType;
	}
	
	public Forum getChildByName(final String name)
	{
		vload();
		
		for (final Forum f : children)
		{
			if (f == null || f.getName() == null)
			{
				continue;
			}
			
			if (f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}
	
	public void rmTopicByID(final int id)
	{
		topic.remove(id);
	}
	
	public void insertindb()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_FORUM))
		{
			statement.setInt(1, forumId);
			statement.setString(2, forumName);
			statement.setInt(3, fParent.getID());
			statement.setInt(4, forumPost);
			statement.setInt(5, forumType);
			statement.setInt(6, forumPerm);
			statement.setInt(7, ownerID);
			statement.executeUpdate();
			
		}
		catch (Exception e)
		{
			LOGGER.warn("error while saving new Forum to db", e);
		}
	}
	
	public void vload()
	{
		if (!loaded)
		{
			load();
			getChildren();
			loaded = true;
		}
	}
}
