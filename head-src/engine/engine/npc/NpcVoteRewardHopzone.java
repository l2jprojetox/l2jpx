package engine.engine.npc;

import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.network.clientpackets.Say2;

import engine.data.properties.ConfigData;
import engine.engine.AbstractMod;
import engine.engine.mods.VoteReward;
import engine.holders.objects.CharacterHolder;
import engine.holders.objects.NpcHolder;
import engine.holders.objects.PlayerHolder;
import engine.util.Util;
import engine.util.UtilInventory;
import engine.util.UtilMessage;
import engine.util.builders.html.Html;
import engine.util.builders.html.HtmlBuilder;
import engine.util.builders.html.HtmlBuilder.HtmlType;
import engine.util.builders.html.L2UI;
import engine.util.builders.html.L2UI_CH3;

/**
 * @author fissban
 */
public class NpcVoteRewardHopzone extends AbstractMod
{
	private static final int NPC = 60013;
	// player q esta votando
	private static PlayerHolder player = null;
	private static int votes = 0;
	
	public NpcVoteRewardHopzone()
	{
		registerMod(ConfigData.ENABLE_VoteRewardIndivualHopzone);
		loadValuesFromDb();
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public boolean onInteract(PlayerHolder ph, CharacterHolder character)
	{
		if (!Util.areObjectType(L2NpcInstance.class, character))
		{
			return false;
		}
		
		if (((L2NpcInstance) character.getInstance()).getNpcId() != NPC)
		{
			return false;
		}
		
		HtmlBuilder hb = new HtmlBuilder(HtmlType.HTML);
		hb.append(Html.START_BACKGROUND);
		hb.append(Html.head("VOTE REWARD HOPZONE"));
		hb.append("<br>");
		hb.append("<center>");
		hb.append("Welcome ", Html.fontColor("LEVEL", ph.getName()), "<br>");
		
		if (!checkLastVote(ph))
		{
			hb.append("Sorry, it's not past ", Html.fontColor("LEVEL", "12 hours"), "t<br>");
			hb.append("since your last vote, try later.<br>");
		}
		else
		{
			hb.append("Here you can vote for our server<br>");
			hb.append("and get a good reward for it.<br>");
			if (player == null)
			{
				hb.append("No one is currently voting.<br>");
				hb.append("Don't wait any longer, ", Html.fontColor("LEVEL", "vote for us."), "<br>");
				
				hb.append("<table width=280>");
				hb.append("<tr>");
				hb.append("<td align=center>", Html.image(L2UI.bbs_folder, 32, 32), "</td>");
				hb.append("<td><button value=\"Vote\" action=\"bypass -h Engine ", NpcVoteRewardHopzone.class.getSimpleName(), " vote\" width=216 height=32 back=", L2UI_CH3.refinegrade3_21, " fore=", L2UI_CH3.refinegrade3_21, "></td>");
				hb.append("<td align=center>", Html.image(L2UI.bbs_folder, 32, 32), "</td>");
				hb.append("</tr>");
				hb.append("</table>");
			}
			else
			{
				hb.append("He is currently ", Html.fontColor("LEVEL", player.getName()), " voting.<br>");
				hb.append("Just wait a moment and it will be your turn.<br>");
			}
		}
		
		hb.append("</center>");
		hb.append(Html.END);
		
		sendHtml((NpcHolder) character, ph, hb);
		
		return true;
	}
	
	private boolean checkLastVote(PlayerHolder ph)
	{
		// prevenimos que un player q ya voto nos bypasee
		String vote = getValueDB(ph, "lastVote").getString();
		if (vote != null)
		{
			long lastVote = Long.parseLong(vote);
			
			// chequeamos si ya transcurrio el tiempo para volver a votar
			if ((lastVote + (ConfigData.INDIVIDUAL_VOTE_TIME_CAN_VOTE * 3600000)) > System.currentTimeMillis())
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void onEvent(PlayerHolder ph, CharacterHolder character, String command)
	{
		if (!Util.areObjectType(L2NpcInstance.class, character))
		{
			return;
		}
		
		if (((L2NpcInstance) character.getInstance()).getNpcId() != NPC)
		{
			return;
		}
		
		// prevent bypass
		if (player != null)
		{
			return;
		}
		
		// prevent bypass
		if (!checkLastVote(ph))
		{
			return;
		}
		
		// init votes.
		votes = -1;
		
		if (command.equals("vote"))
		{
			try
			{
				votes = VoteReward.getVotesHopzone();
				HtmlBuilder hb = new HtmlBuilder();
				hb.append(Html.START_BACKGROUND);
				hb.append(Html.head("VOTE REWARD HOPZONE"));
				hb.append("<br>");
				hb.append("<br>");
				hb.append("<center>");
				
				if (votes == -1)
				{
					hb.append("The votes could not be obtained,<br> but try later");
				}
				else
				{
					player = ph;
					
					hb.append("Have ", Html.fontColor("LEVEL", ConfigData.INDIVIDUAL_VOTE_TIME_VOTE), " seconds to vote");
					
					startTimer("waitVote", ConfigData.INDIVIDUAL_VOTE_TIME_VOTE * 1000, null, player, false);
				}
				
				hb.append("</center>");
				hb.append(Html.END);
				sendHtml((NpcHolder) character, ph, hb);
			}
			catch (Exception e)
			{
				votes = 0;
				player = null;
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		switch (timerName)
		{
			case "waitVote":
				if (VoteReward.getVotesHopzone() > votes)
				{
					UtilMessage.sendCreatureMsg(player, Say2.TELL, "", "Your vote could not be verified, try later!");
					UtilInventory.giveItems(player, ConfigData.INDIVIDUAL_VOTE_REWARD.getRewardId(), ConfigData.INDIVIDUAL_VOTE_REWARD.getRewardCount(), 0);
					setValueDB(player, "lastVote", System.currentTimeMillis() + "");
				}
				else
				{
					UtilMessage.sendCreatureMsg(player, Say2.TELL, "", "Your vote could not be verified, try later");
				}
				
				player = null;
				break;
		}
	}
}
