package com.px.gameserver.model.actor.instance;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;

/**
 * An instance similar to {@link Folk}, except they can't be interacted by {@link Player}s.
 */
public final class MutedFolk extends Folk
{
	public MutedFolk(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onInteract(Player player)
	{
	}
}