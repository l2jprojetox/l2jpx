package net.l2jpx.gameserver.taskmanager.tasks;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.UserInfo;
import net.l2jpx.gameserver.taskmanager.Task;
import net.l2jpx.gameserver.taskmanager.TaskManager;
import net.l2jpx.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.l2jpx.gameserver.taskmanager.TaskTypes;

/**
 * @author Layane
 */
public class TaskRecom extends Task
{
	private static final Logger LOGGER = Logger.getLogger(TaskRecom.class);
	private static final String NAME = "sp_recommendations";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(final ExecutedTask task)
	{
		for (final L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			player.restartRecom();
			player.sendPacket(new UserInfo(player));
		}
		LOGGER.info("[GlobalTask] Restart Recommendation launched.");
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "13:00:00", "");
	}
}