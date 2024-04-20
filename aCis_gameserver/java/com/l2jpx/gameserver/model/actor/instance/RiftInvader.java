package com.l2jpx.gameserver.model.actor.instance;

import com.l2jpx.gameserver.model.actor.template.NpcTemplate;

public class RiftInvader extends Monster
{
	// Not longer needed since rift monster targeting control now is handled by the room zones for any mob
	public RiftInvader(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
}
