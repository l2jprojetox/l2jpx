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

import org.l2jpx.gameserver.ai.CreatureAI;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.ai.FriendlyNpcAI;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.model.actor.Attackable;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnAttackableAttack;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnAttackableKill;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnNpcFirstTalk;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;

/**
 * @author GKR, Sdw
 */
public class FriendlyNpc extends Attackable
{
	private boolean _isAutoAttackable = true;
	
	public FriendlyNpc(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.FriendlyNpc);
		setCanReturnToSpawnPoint(false);
	}
	
	@Override
	public boolean isAttackable()
	{
		return false;
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return _isAutoAttackable && !attacker.isPlayable() && !(attacker instanceof FriendlyNpc);
	}
	
	@Override
	public void setAutoAttackable(boolean value)
	{
		_isAutoAttackable = value;
	}
	
	@Override
	public void addDamage(Creature attacker, int damage, Skill skill)
	{
		if (!attacker.isPlayable() && !(attacker instanceof FriendlyNpc))
		{
			super.addDamage(attacker, damage, skill);
		}
		
		if (attacker.isAttackable() && EventDispatcher.getInstance().hasListener(EventType.ON_ATTACKABLE_ATTACK, this))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAttack(null, this, damage, skill, false), this);
		}
	}
	
	@Override
	public void addDamageHate(Creature attacker, long damage, long aggro)
	{
		if (!attacker.isPlayable() && !(attacker instanceof FriendlyNpc))
		{
			super.addDamageHate(attacker, damage, aggro);
		}
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		// Kill the Npc (the corpse disappeared after 7 seconds)
		if (!super.doDie(killer))
		{
			return false;
		}
		
		// Notify to scripts.
		if ((killer != null) && killer.isAttackable() && EventDispatcher.getInstance().hasListener(EventType.ON_ATTACKABLE_KILL, this))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnAttackableKill(null, this, false), this);
		}
		return true;
	}
	
	@Override
	public void onAction(Player player, boolean interact)
	{
		if (!canTarget(player))
		{
			return;
		}
		
		// Check if the Player already target the GuardInstance
		if (getObjectId() != player.getTargetId())
		{
			// Set the target of the Player player
			player.setTarget(this);
		}
		else if (interact)
		{
			// Calculate the distance between the Player and the Npc
			if (!canInteract(player))
			{
				// Set the Player Intention to AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				player.setLastFolkNPC(this);
				
				// Open a chat window on client with the text of the GuardInstance
				if (hasListener(EventType.ON_NPC_QUEST_START))
				{
					player.setLastQuestNpcObject(getObjectId());
				}
				
				if (hasListener(EventType.ON_NPC_FIRST_TALK))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnNpcFirstTalk(this, player), this);
				}
				else
				{
					showChatWindow(player, 0);
				}
			}
		}
		// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String getHtmlPath(int npcId, int value, Player player)
	{
		String pom = "";
		if (value == 0)
		{
			pom = Integer.toString(npcId);
		}
		else
		{
			pom = npcId + "-" + value;
		}
		return "data/html/default/" + pom + ".htm";
	}
	
	@Override
	protected CreatureAI initAI()
	{
		return new FriendlyNpcAI(this);
	}
}
