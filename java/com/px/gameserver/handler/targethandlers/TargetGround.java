package com.px.gameserver.handler.targethandlers;

import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.handler.ITargetHandler;
import com.px.gameserver.model.WorldRegion;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.network.serverpackets.ValidateLocation;
import com.px.gameserver.skills.L2Skill;

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