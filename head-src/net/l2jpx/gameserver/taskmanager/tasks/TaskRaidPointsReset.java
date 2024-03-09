package net.l2jpx.gameserver.taskmanager.tasks;

import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.managers.RaidBossPointsManager;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.taskmanager.Task;
import net.l2jpx.gameserver.taskmanager.TaskManager;
import net.l2jpx.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.l2jpx.gameserver.taskmanager.TaskTypes;

public class TaskRaidPointsReset extends Task
{
	private static final Logger LOGGER = Logger.getLogger(TaskRaidPointsReset.class);
	public static final String NAME = "raid_points_reset";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(final ExecutedTask task)
	{
		String playerName = "";
		final Calendar cal = Calendar.getInstance();
		
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
		{
			// reward clan reputation points
			final Map<Integer, Integer> rankList = RaidBossPointsManager.getRankList();
			for (final L2Clan c : ClanTable.getInstance().getClans())
			{
				for (final Map.Entry<Integer, Integer> entry : rankList.entrySet())
				{
					final L2Object obj = L2World.getInstance().findObject(entry.getKey());
					if (obj instanceof L2PcInstance)
					{
						playerName = ((L2PcInstance) obj).getName();
					}
					if (entry.getValue() <= 100 && c.isMember(playerName))
					{
						int reputation = 0;
						switch (entry.getValue())
						{
							case 1:
								reputation = Config.RAID_RANKING_1ST;
								break;
							case 2:
								reputation = Config.RAID_RANKING_2ND;
								break;
							case 3:
								reputation = Config.RAID_RANKING_3RD;
								break;
							case 4:
								reputation = Config.RAID_RANKING_4TH;
								break;
							case 5:
								reputation = Config.RAID_RANKING_5TH;
								break;
							case 6:
								reputation = Config.RAID_RANKING_6TH;
								break;
							case 7:
								reputation = Config.RAID_RANKING_7TH;
								break;
							case 8:
								reputation = Config.RAID_RANKING_8TH;
								break;
							case 9:
								reputation = Config.RAID_RANKING_9TH;
								break;
							case 10:
								reputation = Config.RAID_RANKING_10TH;
								break;
							default:
								if (entry.getValue() <= 50)
								{
									reputation = Config.RAID_RANKING_UP_TO_50TH;
								}
								else
								{
									reputation = Config.RAID_RANKING_UP_TO_100TH;
								}
								break;
						}
						c.setReputationScore(c.getReputationScore() + reputation, true);
					}
				}
			}
			
			RaidBossPointsManager.cleanUp();
			LOGGER.info("[GlobalTask] Raid Points Reset launched.");
		}
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "00:10:00", "");
	}
}