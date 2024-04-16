package com.px.gameserver.model.item;

public class LifeStone
{
	private static final int[] LEVELS =
	{
		46,
		49,
		52,
		55,
		58,
		61,
		64,
		67,
		70,
		76
	};
	
	private final int _grade;
	private final int _level;
	
	public LifeStone(int grade, int level)
	{
		_grade = grade;
		_level = level;
	}
	
	public final int getLevel()
	{
		return _level;
	}
	
	public final int getGrade()
	{
		return _grade;
	}
	
	public final int getPlayerLevel()
	{
		return LEVELS[_level];
	}
}