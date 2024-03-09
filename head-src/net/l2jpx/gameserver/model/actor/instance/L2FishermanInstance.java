package net.l2jpx.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.l2jpx.Config;
import net.l2jpx.gameserver.controllers.TradeController;
import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.datatables.sql.SkillTreeTable;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2SkillLearn;
import net.l2jpx.gameserver.model.L2TradeList;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.AquireSkillList;
import net.l2jpx.gameserver.network.serverpackets.BuyList;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.SellList;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.L2NpcTemplate;

import javolution.text.TextBuilder;

public class L2FishermanInstance extends L2FolkInstance
{
	/**
	 * @param objectId
	 * @param template
	 */
	public L2FishermanInstance(final int objectId, final L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(final int npcId, final int val)
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
		
		return "data/html/fisherman/" + pom + ".htm";
	}
	
	private void showBuyWindow(final L2PcInstance player, final int val)
	{
		double taxRate = 0;
		if (getIsInTown())
		{
			taxRate = getCastle().getTaxRate();
		}
		player.tempInvetoryDisable();
		if (Config.DEBUG)
		{
			LOGGER.debug("Showing buylist");
		}
		L2TradeList list = TradeController.getInstance().getBuyList(val);
		
		if (list != null && list.getNpcId().equals(String.valueOf(getNpcId())))
		{
			if (player.isGM())
			{
				player.sendMessage("FISHER BUYLIST SHOP ID: " + val);
			}
			
			BuyList bl = new BuyList(list, player.getAdena(), taxRate);
			player.sendPacket(bl);
			list = null;
			bl = null;
		}
		else
		{
			LOGGER.warn("possible client hacker: " + player.getName() + " attempting to buy from GM shop! (L2FishermanInstance)");
			LOGGER.warn("buylist id:" + val);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void showSellWindow(final L2PcInstance player)
	{
		if (Config.DEBUG)
		{
			LOGGER.debug("Showing selllist");
		}
		
		player.sendPacket(new SellList(player));
		
		if (Config.DEBUG)
		{
			LOGGER.debug("Showing sell window");
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void onBypassFeedback(final L2PcInstance player, final String command)
	{
		if (command.startsWith("FishSkillList"))
		{
			player.setSkillLearningClassId(player.getClassId());
			showSkillList(player);
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String command2 = st.nextToken();
		
		if (command2.equalsIgnoreCase("Buy"))
		{
			if (st.countTokens() < 1)
			{
				return;
			}
			
			final int val = Integer.parseInt(st.nextToken());
			showBuyWindow(player, val);
		}
		else if (command2.equalsIgnoreCase("Sell"))
		{
			showSellWindow(player);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
		st = null;
		command2 = null;
	}
	
	public void showSkillList(final L2PcInstance player)
	{
		L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableSkills(player);
		AquireSkillList asl = new AquireSkillList(AquireSkillList.skillType.Fishing);
		
		int counts = 0;
		
		for (final L2SkillLearn s : skills)
		{
			final L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
			
			if (sk == null)
			{
				continue;
			}
			
			counts++;
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getSpCost(), 1);
		}
		
		if (counts == 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			final int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player);
			
			if (minlevel > 0)
			{
				// No more skills to learn, come back when you level.
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_REACHED_LEVEL_S1);
				sm.addNumber(minlevel);
				player.sendPacket(sm);
				sm = null;
			}
			else
			{
				TextBuilder sb = new TextBuilder();
				sb.append("<html><head><body>");
				sb.append("You've learned all skills.<br>");
				sb.append("</body></html>");
				html.setHtml(sb.toString());
				player.sendPacket(html);
				sb = null;
				html = null;
			}
		}
		else
		{
			player.sendPacket(asl);
		}
		
		skills = null;
		asl = null;
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
