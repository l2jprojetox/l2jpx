package net.l2jpx.gameserver.communitybbs.BB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.communitybbs.Manager.PostBBSManager;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author Maktakien
 * @author ReynalDev
 */
public class Post
{
	private static final Logger LOGGER = Logger.getLogger(Post.class);
	
	private static final String INSERT_POST = "INSERT INTO posts (post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt) VALUES (?,?,?,?,?,?,?)";
	private static final String DELETE_POST = "DELETE FROM posts WHERE post_forum_id=? AND post_topic_id=?";
	private static final String SELECT_POST = "SELECT post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt FROM posts WHERE post_forum_id=? AND post_topic_id=? ORDER BY post_id ASC";
	private static final String UPDATE_POST_BY_POST_ID_AND_POST_FORUM_ID = "UPDATE posts SET post_txt=? WHERE post_id=? AND post_topic_id=? AND post_forum_id=?";
	
	public class CPost
	{
		public int postId;
		public String postOwner;
		public int postOwnerId;
		public long postDate;
		public int postTopicId;
		public int postForumId;
		public String postTxt;
	}
	
	private final List<CPost> post;
	
	public Post(final String postOwner, final int postOwnerID, final long date, final int tid, final int postForumID, final String txt)
	{
		post = new ArrayList<>();
		CPost cp = new CPost();
		cp.postId = 0;
		cp.postOwner = postOwner;
		cp.postOwnerId = postOwnerID;
		cp.postDate = date;
		cp.postTopicId = tid;
		cp.postForumId = postForumID;
		cp.postTxt = txt;
		post.add(cp);
		insertindb(cp);
	}
	
	public void insertindb(CPost cp)
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_POST))
		{
			statement.setInt(1, cp.postId);
			statement.setString(2, cp.postOwner);
			statement.setInt(3, cp.postOwnerId);
			statement.setLong(4, cp.postDate);
			statement.setInt(5, cp.postTopicId);
			statement.setInt(6, cp.postForumId);
			statement.setString(7, cp.postTxt);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("error while saving new Post to db " + e);
		}
	}
	
	public Post(Topic t)
	{
		post = new ArrayList<>();
		load(t);
	}
	
	public CPost getCPost(int id)
	{
		int i = 0;
		
		for (CPost cp : post)
		{
			if (i++ == id)
			{
				return cp;
			}
		}
		
		return null;
	}
	
	public void deleteme(Topic t)
	{
		PostBBSManager.getInstance().delPostByTopic(t);
		
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_POST))
		{
			statement.setInt(1, t.getForumID());
			statement.setInt(2, t.getID());
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
	}
	
	private void load(Topic t)
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_POST))
		{
			statement.setInt(1, t.getForumID());
			statement.setInt(2, t.getID());
			
			try(ResultSet result = statement.executeQuery())
			{
				while (result.next())
				{
					CPost cp = new CPost();
					cp.postId = Integer.parseInt(result.getString("post_id"));
					cp.postOwner = result.getString("post_owner_name");
					cp.postOwnerId = Integer.parseInt(result.getString("post_ownerid"));
					cp.postDate = Long.parseLong(result.getString("post_date"));
					cp.postTopicId = Integer.parseInt(result.getString("post_topic_id"));
					cp.postForumId = Integer.parseInt(result.getString("post_forum_id"));
					cp.postTxt = result.getString("post_txt");
					post.add(cp);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("data error on forum id" + t.getForumID() + " and topic id" + t.getID(), e);
		}
	}
	
	public void updatetxt(int postId)
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_POST_BY_POST_ID_AND_POST_FORUM_ID))
		{
			CPost cp = getCPost(postId);
			statement.setString(1, cp.postTxt);
			statement.setInt(2, cp.postId);
			statement.setInt(3, cp.postTopicId);
			statement.setInt(4, cp.postForumId);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("error while saving new Post to db", e);
		}
	}
}