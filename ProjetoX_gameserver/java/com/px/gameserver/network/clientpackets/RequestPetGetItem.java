package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.IntentionType;
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
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null || !activeChar.hasPet())
			return;
		
		final WorldObject item = World.getInstance().getObject(_objectId);
		if (item == null)
			return;
		
		final Pet pet = (Pet) activeChar.getSummon();
		if (pet.isDead() || pet.isOutOfControl())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		pet.getAI().setIntention(IntentionType.PICK_UP, item);
	}
}