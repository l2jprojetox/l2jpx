package com.px.gameserver.model.actor.instance;

import com.px.gameserver.enums.SiegeSide;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.residence.castle.Siege;
import com.px.gameserver.model.spawn.Spawn;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ServerObjectInfo;

public class ControlTower extends Npc
{
	public ControlTower(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		if (!super.isAttackableBy(attacker))
			return false;
		
		if (!(attacker instanceof Playable))
			return false;
		
		if (getCastle() != null && getCastle().getSiege().isInProgress())
			return getCastle().getSiege().checkSides(attacker.getActingPlayer().getClan(), SiegeSide.ATTACKER);
		
		return false;
	}
	
	@Override
	public boolean isAttackableWithoutForceBy(Playable attacker)
	{
		return isAttackableBy(attacker);
	}
	
	@Override
	public void onInteract(Player player)
	{
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (getCastle() != null)
		{
			final Siege siege = getCastle().getSiege();
			if (siege.isInProgress())
			{
				setScriptValue(1);
				
				// If Life Control Tower amount reach 0, broadcast a message to defenders.
				if (siege.getControlTowerCount() == 0)
					siege.announce(SystemMessageId.TOWER_DESTROYED_NO_RESURRECTION, SiegeSide.DEFENDER);
				
				// Spawn a little version of it. This version is a simple NPC, cleaned on siege end.
				try
				{
					final Spawn spawn = new Spawn(13003);
					spawn.setLoc(getPosition());
					
					final Npc tower = spawn.doSpawn(false);
					tower.setCastle(getCastle());
					
					siege.getDestroyedTowers().add(tower);
				}
				catch (Exception e)
				{
					LOGGER.error("Couldn't spawn the control tower.", e);
				}
			}
		}
		return super.doDie(killer);
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket(new ServerObjectInfo(this, player));
	}
}