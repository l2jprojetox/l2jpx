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

import java.util.concurrent.ScheduledFuture;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.enums.Team;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.holders.SkillHolder;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;

public class EffectPoint extends Npc
{
	private final Player _owner;
	private ScheduledFuture<?> _skillTask;
	
	public EffectPoint(NpcTemplate template, Creature owner)
	{
		super(template);
		setInstanceType(InstanceType.EffectPoint);
		setInvul(false);
		
		_owner = owner == null ? null : owner.getActingPlayer();
		if (owner != null)
		{
			setInstance(owner.getInstanceWorld());
		}
		
		final SkillHolder skill = template.getParameters().getSkillHolder("union_skill");
		if (skill != null)
		{
			final long castTime = (long) (template.getParameters().getFloat("cast_time", 0.1f) * 1000);
			final long skillDelay = (long) (template.getParameters().getFloat("skill_delay", 2) * 1000);
			_skillTask = ThreadPool.scheduleAtFixedRate(() ->
			{
				if ((isDead() || !isSpawned()) && (_skillTask != null))
				{
					_skillTask.cancel(false);
					_skillTask = null;
					return;
				}
				
				doCast(skill.getSkill());
			}, castTime, skillDelay);
		}
	}
	
	@Override
	public boolean deleteMe()
	{
		if (_skillTask != null)
		{
			_skillTask.cancel(false);
			_skillTask = null;
		}
		return super.deleteMe();
	}
	
	@Override
	public Player getActingPlayer()
	{
		return _owner;
	}
	
	/**
	 * this is called when a player interacts with this NPC
	 * @param player
	 */
	@Override
	public void onAction(Player player, boolean interact)
	{
		// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void onActionShift(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Return the Party object of its Player owner or null.
	 */
	@Override
	public Party getParty()
	{
		if (_owner == null)
		{
			return null;
		}
		return _owner.getParty();
	}
	
	/**
	 * Return True if the Creature has a Party in progress.
	 */
	@Override
	public boolean isInParty()
	{
		return (_owner != null) && _owner.isInParty();
	}
	
	@Override
	public int getClanId()
	{
		return (_owner != null) ? _owner.getClanId() : 0;
	}
	
	@Override
	public int getAllyId()
	{
		return (_owner != null) ? _owner.getAllyId() : 0;
	}
	
	@Override
	public byte getPvpFlag()
	{
		return _owner != null ? _owner.getPvpFlag() : 0;
	}
	
	@Override
	public Team getTeam()
	{
		return _owner != null ? _owner.getTeam() : Team.NONE;
	}
}