package com.l2jpx.gameserver.model.olympiad;

import java.util.List;

import com.l2jpx.Config;
import com.l2jpx.gameserver.enums.OlympiadType;
import com.l2jpx.gameserver.model.holder.IntIntHolder;

public class OlympiadGameNonClassed extends OlympiadGameNormal
{
	private OlympiadGameNonClassed(int id, Participant[] opponents)
	{
		super(id, opponents);
	}
	
	@Override
	public final OlympiadType getType()
	{
		return OlympiadType.NON_CLASSED;
	}
	
	@Override
	protected final int getDivider()
	{
		return Config.OLY_DIVIDER_NON_CLASSED;
	}
	
	@Override
	protected final IntIntHolder[] getReward()
	{
		return Config.OLY_NONCLASSED_REWARD;
	}
	
	protected static final OlympiadGameNonClassed createGame(int id, List<Integer> list)
	{
		final Participant[] opponents = OlympiadGameNormal.createListOfParticipants(list);
		if (opponents == null)
			return null;
		
		return new OlympiadGameNonClassed(id, opponents);
	}
}