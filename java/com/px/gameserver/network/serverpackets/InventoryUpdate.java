package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Playable;

public class InventoryUpdate extends AbstractInventoryUpdate
{
	public InventoryUpdate(Playable playable)
	{
		super(playable);
	}
}