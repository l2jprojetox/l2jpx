package com.px.gameserver.model.actor.instance;

import com.px.commons.random.Rnd;

import com.px.gameserver.data.manager.HeroManager;
import com.px.gameserver.data.manager.RaidPointManager;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.PlaySound;
import com.px.gameserver.network.serverpackets.SystemMessage;

/**
 * This class manages all {@link GrandBoss}es.<br>
 * <br>
 * Those npcs inherit from {@link Monster}. Since a script is generally associated to it, {@link GrandBoss#returnHome} returns false to avoid misbehavior. No random walking is allowed.
 */
public final class GrandBoss extends Monster
{
	public GrandBoss(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setRaidRelated();
	}
	
	@Override
	public void onSpawn()
	{
		setNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		final Player player = killer.getActingPlayer();
		if (player != null)
		{
			broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
			broadcastPacket(new PlaySound("systemmsg_e.1209"));
			
			final Party party = player.getParty();
			if (party != null)
			{
				for (Player member : party.getMembers())
				{
					RaidPointManager.getInstance().addPoints(member, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5));
					if (member.isNoble())
						HeroManager.getInstance().setRBkilled(member.getObjectId(), getNpcId());
				}
			}
			else
			{
				RaidPointManager.getInstance().addPoints(player, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5));
				if (player.isNoble())
					HeroManager.getInstance().setRBkilled(player.getObjectId(), getNpcId());
			}
		}
		
		return true;
	}
	
	@Override
	public boolean returnHome()
	{
		return false;
	}
}