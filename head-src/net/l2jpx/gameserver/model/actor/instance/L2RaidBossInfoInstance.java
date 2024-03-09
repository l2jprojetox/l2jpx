package net.l2jpx.gameserver.model.actor.instance;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.NpcTable;
import net.l2jpx.gameserver.managers.GrandBossManager;
import net.l2jpx.gameserver.managers.RaidBossSpawnManager;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.gameserver.templates.StatsSet;

public class L2RaidBossInfoInstance extends L2NpcInstance
{
	private final static Logger LOGGER = Logger.getLogger(L2RaidBossInfoInstance.class);
	
	public L2RaidBossInfoInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/raidbossinfo/" + pom + ".htm";
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("raidInfo"))
		{
			StringBuilder sb = new StringBuilder();
			sb.append("<html><title>Raid Boss Info</title><body><br><center>");
			sb.append("<img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\"><br><br>");
			
			for (int boss : Config.RAID_INFO_IDS_LIST)
			{
				String name = "";
				L2NpcTemplate template = NpcTable.getInstance().getTemplate(boss);
				
				if (template != null)
				{
					name = template.getName();
				}
				else
				{
					LOGGER.warn("L2RaidBossInfoInstance.onBypassFeedback : bypass 'raidInfo' Raid Boss with ID " + boss + " is not defined into NpcTable");
					continue;
				}
				
				StatsSet bossStatsSet = null;
				long respawnTime = 0;
				
				if (NpcTable.getInstance().getTemplate(boss).type.equals("L2RaidBoss"))
				{
					bossStatsSet = RaidBossSpawnManager.getInstance().getStatsSet(boss);
					
					if (bossStatsSet != null)
					{
						respawnTime = bossStatsSet.getLong("respawnTime");
					}
				}
				else if (NpcTable.getInstance().getTemplate(boss).type.equals("L2GrandBoss"))
				{
					bossStatsSet = GrandBossManager.getInstance().getStatsSet(boss);
					
					if (bossStatsSet != null)
					{
						respawnTime = bossStatsSet.getLong("respawn_time");
					}
				}
				else
				{
					continue;
				}
				
				long now = System.currentTimeMillis();
				
				if (respawnTime <= now)
				{
					sb.append("<font color=\"00C3FF\">" + name + "</font>: <font color=\"9CC300\">Alive</font><br1>");
				}
				else
				{
					long diffInMiliseconds = respawnTime - now;
					
					long days = TimeUnit.DAYS.convert(diffInMiliseconds, TimeUnit.MILLISECONDS);
					diffInMiliseconds -= TimeUnit.DAYS.toMillis(days);
					
					long hours = TimeUnit.HOURS.convert(diffInMiliseconds, TimeUnit.MILLISECONDS);
					diffInMiliseconds -= TimeUnit.HOURS.toMillis(hours);
					
					long minutes = TimeUnit.MINUTES.convert(diffInMiliseconds, TimeUnit.MILLISECONDS);
					diffInMiliseconds -= TimeUnit.MINUTES.toMillis(minutes);
					
					long seconds = TimeUnit.SECONDS.convert(diffInMiliseconds, TimeUnit.MILLISECONDS);
					
					sb.append("<font color=\"00C3FF\">" + name + "</font> ");
					sb.append("Respawn in: ");
					sb.append("<font color=\"32C332\">");
					
					if(days > 0)
					{
						sb.append(days + "d ");
					}
					
					if(hours > 0)
					{
						sb.append(hours + "h ");
					}
					
					if(minutes > 0)
					{
						sb.append(minutes + "m ");
					}
					
					if(seconds > 0)
					{
						sb.append(seconds + "s.");
					}
					
					sb.append("</font><br1>");
				}
			}
			
			sb.append("<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
			sb.append("</center></body></html>");
			
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
