package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Playable;

public class PetInventoryUpdate extends AbstractInventoryUpdate
{
	public PetInventoryUpdate(Playable playable)
	{
		super(playable);
	}
}