package com.px.gameserver.model.actor.attack;

import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;

/**
 * This class groups all attack data related to a {@link Creature}.
 * @param <T> : The {@link Playable} used as actor.
 */
public class PlayableAttack<T extends Playable> extends CreatureAttack<T>
{
	public PlayableAttack(T actor)
	{
		super(actor);
	}
	
	@Override
	public boolean canAttack(Creature target)
	{
		if (!super.canAttack(target))
			return false;
		
		if (target instanceof Playable)
		{
			if (_actor.isInsideZone(ZoneId.PEACE))
			{
				_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_ATK_PEACEZONE));
				return false;
			}
			
			if (target.isInsideZone(ZoneId.PEACE))
			{
				_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.TARGET_IN_PEACEZONE));
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void stop()
	{
		super.stop();
		
		_actor.getAI().tryToIdle();
	}
}