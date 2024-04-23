package com.l2jpx.gameserver.model.actor.ai.type;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.ClanHallManagerNpc;
import com.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jpx.gameserver.skills.L2Skill;

public class ClanHallManagerNpcAI extends CreatureAI
{
	public ClanHallManagerNpcAI(Creature creature)
	{
		super(creature);
	}
	
	@Override
	public ClanHallManagerNpc getActor()
	{
		return (ClanHallManagerNpc) _actor;
	}
	
	@Override
	protected void thinkCast()
	{
		final L2Skill skill = _currentIntention.getSkill();
		
		if (getActor().isSkillDisabled(skill))
			return;
		
		final Player player = (Player) _currentIntention.getFinalTarget();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getActor().getObjectId());
		if (getActor().getStatus().getMp() < skill.getMpConsume() + skill.getMpInitialConsume())
			html.setFile("data/html/clanHallManager/support-no_mana.htm");
		else
		{
			super.thinkCast();
			
			html.setFile("data/html/clanHallManager/support-done.htm");
		}
		
		html.replace("%mp%", (int) getActor().getStatus().getMp());
		html.replace("%objectId%", getActor().getObjectId());
		player.sendPacket(html);
	}
}