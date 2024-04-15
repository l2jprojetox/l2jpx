package com.px.gameserver.model.actor.instance;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.MoveToPawn;

public final class HolyThing extends Folk
{
	public HolyThing(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onAction(Player player)
	{
		// Set the target of the player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			// Calculate the distance between the Player and the Npc.
			if (!canInteract(player))
			{
				// Notify the Player AI with INTERACT
				player.getAI().setIntention(IntentionType.INTERACT, this);
			}
			else
			{
				// Stop moving if we're already in interact range.
				if (player.isMoving() || player.isInCombat())
					player.getAI().setIntention(IntentionType.IDLE);
				
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, Npc.INTERACTION_DISTANCE));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
	
	@Override
	public boolean isAttackable()
	{
		return false;
	}
	
	@Override
	public void onForcedAttack(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill)
	{
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
	}
}
