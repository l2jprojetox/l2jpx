package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.World;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.network.serverpackets.ActionFailed;

public final class RequestPetGetItem extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null || !player.hasPet())
			return;
		
		final WorldObject item = World.getInstance().getObject(_objectId);
		if (item == null)
			return;
		
		final Pet pet = (Pet) player.getSummon();
		if (pet.isDead() || pet.isOutOfControl())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		pet.getAI().tryToPickUp(_objectId, false);
	}
}