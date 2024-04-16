package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class L2SkillAppearance extends L2Skill
{
	private final int _faceId;
	private final int _hairColorId;
	private final int _hairStyleId;
	
	public L2SkillAppearance(StatSet set)
	{
		super(set);
		
		_faceId = set.getInteger("faceId", -1);
		_hairColorId = set.getInteger("hairColorId", -1);
		_hairStyleId = set.getInteger("hairStyleId", -1);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		for (WorldObject target : targets)
		{
			if (target instanceof Player)
			{
				final Player targetPlayer = (Player) target;
				if (_faceId >= 0)
					targetPlayer.getAppearance().setFace(_faceId);
				if (_hairColorId >= 0)
					targetPlayer.getAppearance().setHairColor(_hairColorId);
				if (_hairStyleId >= 0)
					targetPlayer.getAppearance().setHairStyle(_hairStyleId);
				
				targetPlayer.broadcastUserInfo();
			}
		}
	}
}