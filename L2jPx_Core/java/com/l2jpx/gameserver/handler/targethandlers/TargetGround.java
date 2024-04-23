package com.l2jpx.gameserver.handler.targethandlers;

import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.geoengine.GeoEngine;
import com.l2jpx.gameserver.handler.ITargetHandler;
import com.l2jpx.gameserver.model.WorldRegion;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.location.Location;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.network.serverpackets.ValidateLocation;
import com.l2jpx.gameserver.skills.L2Skill;

public class TargetGround implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.GROUND;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		return new Creature[]
		{
			caster
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return caster;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		final WorldRegion region = caster.getRegion();
		if (region == null || !(caster instanceof Player))
			return false;
		
		final Player player = (Player) caster;
		
		final Location signetLocation = player.getCast().getSignetLocation();
		if (!GeoEngine.getInstance().canSeeLocation(player, signetLocation))
		{
			player.sendPacket(SystemMessageId.CANT_SEE_TARGET);
			return false;
		}
		
		if (!region.checkEffectRangeInsidePeaceZone(skill, signetLocation))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
			return false;
		}
		
		player.getPosition().setHeadingTo(signetLocation);
		player.broadcastPacket(new ValidateLocation(player));
		return true;
	}
}