/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import org.l2jpx.gameserver.data.xml.NpcData;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Vice, Zoey76
 */
public class FortLogistics extends Merchant
{
	private static final int[] SUPPLY_BOX_IDS =
	{
		35665,
		35697,
		35734,
		35766,
		35803,
		35834,
		35866,
		35903,
		35935,
		35973,
		36010,
		36042,
		36080,
		36117,
		36148,
		36180,
		36218,
		36256,
		36293,
		36325,
		36363
	};
	
	public FortLogistics(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.FortLogistics);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player.getLastFolkNPC().getObjectId() != getObjectId())
		{
			return;
		}
		
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken(); // Get actual command
		final boolean isMyLord = player.isClanLeader() && (player.getClan().getFortId() == (getFort() != null ? getFort().getResidenceId() : -1));
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		if (actualCommand.equalsIgnoreCase("rewards"))
		{
			if (isMyLord)
			{
				html.setFile(player, "data/html/fortress/logistics-rewards.htm");
				html.replace("%bloodoath%", String.valueOf(player.getClan().getBloodOathCount()));
			}
			else
			{
				html.setFile(player, "data/html/fortress/logistics-noprivs.htm");
			}
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("blood"))
		{
			if (isMyLord)
			{
				final int blood = player.getClan().getBloodOathCount();
				if (blood > 0)
				{
					player.addItem("Quest", 9910, blood, this, true);
					player.getClan().resetBloodOathCount();
					html.setFile(player, "data/html/fortress/logistics-blood.htm");
				}
				else
				{
					html.setFile(player, "data/html/fortress/logistics-noblood.htm");
				}
			}
			else
			{
				html.setFile(player, "data/html/fortress/logistics-noprivs.htm");
			}
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("supplylvl"))
		{
			if (getFort().getFortState() == 2)
			{
				if (player.isClanLeader())
				{
					html.setFile(player, "data/html/fortress/logistics-supplylvl.htm");
					html.replace("%supplylvl%", String.valueOf(getFort().getSupplyLeveL()));
				}
				else
				{
					html.setFile(player, "data/html/fortress/logistics-noprivs.htm");
				}
			}
			else
			{
				html.setFile(player, "data/html/fortress/logistics-1.htm"); // TODO: Missing HTML?
			}
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("supply"))
		{
			if (isMyLord)
			{
				if (getFort().getSiege().isInProgress())
				{
					html.setFile(player, "data/html/fortress/logistics-siege.htm");
				}
				else
				{
					final int level = getFort().getSupplyLeveL();
					if (level > 0)
					{
						// spawn box
						final NpcTemplate boxTemplate = NpcData.getInstance().getTemplate(SUPPLY_BOX_IDS[level - 1]);
						final Monster box = new Monster(boxTemplate);
						box.setCurrentHp(box.getMaxHp());
						box.setCurrentMp(box.getMaxMp());
						box.setHeading(0);
						box.spawnMe(getX() - 23, getY() + 41, getZ());
						getFort().setSupplyLeveL(0);
						getFort().saveFortVariables();
						
						html.setFile(player, "data/html/fortress/logistics-supply.htm");
					}
					else
					{
						html.setFile(player, "data/html/fortress/logistics-nosupply.htm");
					}
				}
			}
			else
			{
				html.setFile(player, "data/html/fortress/logistics-noprivs.htm");
			}
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		showMessageWindow(player, 0);
	}
	
	private void showMessageWindow(Player player, int value)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		String filename;
		if (value == 0)
		{
			filename = "data/html/fortress/logistics.htm";
		}
		else
		{
			filename = "data/html/fortress/logistics-" + value + ".htm";
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcId%", String.valueOf(getId()));
		if (getFort().getOwnerClan() != null)
		{
			html.replace("%clanname%", getFort().getOwnerClan().getName());
		}
		else
		{
			html.replace("%clanname%", "NPC");
		}
		player.sendPacket(html);
	}
	
	@Override
	public String getHtmlPath(int npcId, int value, Player player)
	{
		String pom = "";
		if (value == 0)
		{
			pom = "logistics";
		}
		else
		{
			pom = "logistics-" + value;
		}
		return "data/html/fortress/" + pom + ".htm";
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}