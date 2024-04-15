package com.px.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.px.commons.lang.StringUtil;

import com.px.gameserver.handler.IAdminCommandHandler;
import com.px.gameserver.model.World;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands: - heal = restores HP/MP/CP on target, name or radius
 */
public class AdminHeal implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_heal"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_heal"))
		{
			WorldObject object = player.getTarget();
			
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (st.hasMoreTokens())
			{
				final String nameOrRadius = st.nextToken();
				
				final Player target = World.getInstance().getPlayer(nameOrRadius);
				if (target != null)
					object = target;
				else if (StringUtil.isDigit(nameOrRadius))
				{
					final int radius = Integer.parseInt(nameOrRadius);
					for (Creature creature : player.getKnownTypeInRadius(Creature.class, radius))
					{
						creature.setCurrentHpMp(creature.getMaxHp(), creature.getMaxMp());
						if (creature instanceof Player)
							creature.setCurrentCp(creature.getMaxCp());
					}
					player.sendMessage("You instant healed all characters within " + radius + " unit radius.");
					return true;
				}
			}
			
			if (object == null)
				object = player;
			
			if (object instanceof Creature)
			{
				final Creature creature = (Creature) object;
				creature.setCurrentHpMp(creature.getMaxHp(), creature.getMaxMp());
				
				if (creature instanceof Player)
					creature.setCurrentCp(creature.getMaxCp());
				
				player.sendMessage("You instant healed " + creature.getName() + ".");
			}
			else
				player.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}