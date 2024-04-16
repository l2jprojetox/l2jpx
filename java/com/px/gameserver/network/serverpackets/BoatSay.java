package com.px.gameserver.network.serverpackets;

import com.px.gameserver.enums.SayType;
import com.px.gameserver.network.SystemMessageId;

public class BoatSay extends CreatureSay
{
	public BoatSay(SystemMessageId smId)
	{
		super(SayType.BOAT, 801, smId);
	}
}