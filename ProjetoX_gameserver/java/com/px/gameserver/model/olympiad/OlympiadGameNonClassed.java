package com.px.gameserver.model.olympiad;

import java.util.List;

import com.px.Config;
import com.px.gameserver.enums.OlympiadType;
import com.px.gameserver.model.holder.IntIntHolder;

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
		return Config.ALT_OLY_DIVIDER_NON_CLASSED;
	}
	
	@Override
	protected final IntIntHolder[] getReward()
	{
		return Config.ALT_OLY_NONCLASSED_REWARD;
	}
	
	protected static final OlympiadGameNonClassed createGame(int id, List<Integer> list)
	{
		final Participant[] opponents = OlympiadGameNormal.createListOfParticipants(list);
		if (opponents == null)
			return null;
		
		return new OlympiadGameNonClassed(id, opponents);
	}
}