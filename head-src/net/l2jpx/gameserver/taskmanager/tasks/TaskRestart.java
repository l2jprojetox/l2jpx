package net.l2jpx.gameserver.taskmanager.tasks;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.Shutdown;
import net.l2jpx.gameserver.taskmanager.Task;
import net.l2jpx.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author Layane
 */
public final class TaskRestart extends Task
{
	private static final Logger LOGGER = Logger.getLogger(TaskRestart.class);
	public static final String NAME = "restart";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(final ExecutedTask task)
	{
		LOGGER.info("[GlobalTask] Server Restart launched.");
		
		Shutdown.getInstance().startShutdown(null, Integer.parseInt(task.getParams()[2]), true);
	}
}