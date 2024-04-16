package com.px.gameserver.model.actor.instance;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.serverpackets.ServerObjectInfo;
import com.px.gameserver.skills.L2Skill;

public final class HolyThing extends Folk
{
	public HolyThing(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		return false;
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill)
	{
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
	}
	
	@Override
	public void onInteract(Player player)
	{
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket(new ServerObjectInfo(this, player));
	}
}