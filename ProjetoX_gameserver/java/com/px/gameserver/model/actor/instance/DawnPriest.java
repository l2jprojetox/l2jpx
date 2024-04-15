package com.px.gameserver.model.actor.instance;

import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.enums.CabalType;
import com.px.gameserver.enums.SealType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;

public class DawnPriest extends SignsPriest
{
	public DawnPriest(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("Chat"))
			showChatWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		String filename = SevenSignsManager.SEVEN_SIGNS_HTML_PATH;
		
		final CabalType winningCabal = SevenSignsManager.getInstance().getCabalHighestScore();
		
		switch (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()))
		{
			case DAWN:
				if (SevenSignsManager.getInstance().isCompResultsPeriod())
					filename += "dawn_priest_5.htm";
				else if (SevenSignsManager.getInstance().isRecruitingPeriod())
					filename += "dawn_priest_6.htm";
				else if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DAWN)
					{
						if (winningCabal != SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS))
							filename += "dawn_priest_2c.htm";
						else
							filename += "dawn_priest_2a.htm";
					}
					else if (winningCabal == CabalType.NORMAL)
						filename += "dawn_priest_2d.htm";
					else
						filename += "dawn_priest_2b.htm";
				}
				else
					filename += "dawn_priest_1b.htm";
				break;
			
			case DUSK:
				if (SevenSignsManager.getInstance().isSealValidationPeriod())
					filename += "dawn_priest_3a.htm";
				else
					filename += "dawn_priest_3b.htm";
				break;
			
			default:
				if (SevenSignsManager.getInstance().isCompResultsPeriod())
					filename += "dawn_priest_5.htm";
				else if (SevenSignsManager.getInstance().isRecruitingPeriod())
					filename += "dawn_priest_6.htm";
				else if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DAWN)
						filename += "dawn_priest_4.htm";
					else if (winningCabal == CabalType.NORMAL)
						filename += "dawn_priest_2d.htm";
					else
						filename += "dawn_priest_2b.htm";
				}
				else
					filename += "dawn_priest_1a.htm";
				break;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
}