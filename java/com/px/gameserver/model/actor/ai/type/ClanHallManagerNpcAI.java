package com.px.gameserver.model.actor.ai.type;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.ClanHallManagerNpc;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;
import com.px.gameserver.skills.L2Skill;

public class ClanHallManagerNpcAI extends NpcAI<ClanHallManagerNpc>
{
	public ClanHallManagerNpcAI(ClanHallManagerNpc clanHallManager)
	{
		super(clanHallManager);
	}
	
	@Override
	protected void thinkCast()
	{
		final L2Skill skill = _currentIntention.getSkill();
		
		if (_actor.isSkillDisabled(skill))
			return;
		
		final Player player = (Player) _currentIntention.getFinalTarget();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(_actor.getObjectId());
		if (_actor.getStatus().getMp() < skill.getMpConsume() + skill.getMpInitialConsume())
			html.setFile("data/html/clanHallManager/support-no_mana.htm");
		else
		{
			super.thinkCast();
			
			html.setFile("data/html/clanHallManager/support-done.htm");
		}
		
		html.replace("%mp%", (int) _actor.getStatus().getMp());
		html.replace("%objectId%", _actor.getObjectId());
		player.sendPacket(html);
	}
}