package com.px.gameserver.model.actor.instance;

import java.util.List;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.EventHandler;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.scripting.Quest;

/**
 * This class represents Friendly Mobs lying over the world.<br>
 * These friendly mobs should only attack players with karma > 0 and it is always aggro, since it just attacks players with karma.
 */
public class FriendlyMonster extends Attackable
{
	public FriendlyMonster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onInteract(Player player)
	{
		getAI().onRandomAnimation(Rnd.get(8));
		
		player.getQuestList().setLastQuestNpcObjectId(getObjectId());
		
		final List<Quest> scripts = getTemplate().getEventQuests(EventHandler.FIRST_TALK);
		if (scripts.size() == 1)
			scripts.get(0).notifyFirstTalk(this, player);
		else
			showChatWindow(player);
	}
	
	@Override
	public boolean isAggressive()
	{
		return true;
	}
}