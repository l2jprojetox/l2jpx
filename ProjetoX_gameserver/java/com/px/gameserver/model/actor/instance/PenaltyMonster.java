package com.px.gameserver.model.actor.instance;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;

public class PenaltyMonster extends Monster
{
	private Player _ptk;
	
	public PenaltyMonster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public Creature getMostHated()
	{
		if (_ptk != null)
			return _ptk; // always attack only one person
			
		return super.getMostHated();
	}
	
	public void setPlayerToKill(Player ptk)
	{
		if (Rnd.get(100) <= 80)
			broadcastNpcSay("Your bait was too delicious! Now, I will kill you!");
		
		_ptk = ptk;
		
		getAI().notifyEvent(AiEventType.AGGRESSION, _ptk, Rnd.get(1, 100));
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (Rnd.get(100) <= 75)
			broadcastNpcSay("I will tell fish not to take your bait!");
		
		return true;
	}
}