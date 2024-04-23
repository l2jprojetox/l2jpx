package com.l2jpx.gameserver.enums;

public enum OlympiadType
{
	CLASSED("classed"),
	NON_CLASSED("non-classed");
	
	private final String _name;
	
	private OlympiadType(String name)
	{
		_name = name;
	}
	
	@Override
	public final String toString()
	{
		return _name;
	}
}