package net.l2jpx.gameserver.communitybbs.BB;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.communitybbs.Manager.TopicBBSManager;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class Topic
{
	private static final Logger LOGGER = Logger.getLogger(Topic.class);
	
	private static final String INSERT_TOPIC = "INSERT INTO topic (topic_id,topic_forum_id,topic_name,topic_date,topic_ownername,topic_ownerid,topic_type,topic_reply) VALUES (?,?,?,?,?,?,?,?)";
	private static final String DELETE_TOPIC_BY_TOPIC_ID_AND_TOPIC_FORUM_ID = "DELETE FROM topic WHERE topic_id=? AND topic_forum_id=?";
	
	public static final int MORMAL = 0;
	public static final int MEMO = 1;
	
	private final int id;
	private final int forumId;
	private final String topicName;
	private final long date;
	private final String ownerName;
	private final int ownerId;
	private final int type;
	private final int cReply;
	
	public Topic(final ConstructorType ct, final int id, final int fid, final String name, final long date, final String oname, final int oid, final int type, final int Creply)
	{
		this.id = id;
		forumId = fid;
		topicName = name;
		this.date = date;
		ownerName = oname;
		ownerId = oid;
		this.type = type;
		cReply = Creply;
		TopicBBSManager.getInstance().addTopic(this);
		
		if (ct == ConstructorType.CREATE)
		{
			insertindb();
		}
	}
	
	public void insertindb()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_TOPIC))
		{
			statement.setInt(1, id);
			statement.setInt(2, forumId);
			statement.setString(3, topicName);
			statement.setLong(4, date);
			statement.setString(5, ownerName);
			statement.setInt(6, ownerId);
			statement.setInt(7, type);
			statement.setInt(8, cReply);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("error while saving new Topic to db", e);
		}
	}
	
	public void deleteme(Forum forum)
	{
		TopicBBSManager.getInstance().delTopic(this);
		forum.rmTopicByID(getID());
		
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_TOPIC_BY_TOPIC_ID_AND_TOPIC_FORUM_ID))
		{
			statement.setInt(1, getID());
			statement.setInt(2, forum.getID());
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
	}
	
	public enum ConstructorType
	{
		RESTORE,
		CREATE
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getForumID()
	{
		return forumId;
	}
	
	public String getName()
	{
		return topicName;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}
	
	public long getDate()
	{
		return date;
	}
}
