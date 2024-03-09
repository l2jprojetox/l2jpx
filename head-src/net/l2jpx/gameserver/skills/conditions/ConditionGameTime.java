package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.controllers.GameTimeController;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public class ConditionGameTime extends Condition
{
	
	public enum CheckGameTime
	{
		NIGHT
	}
	
	private final CheckGameTime check;
	private final boolean required;
	
	public ConditionGameTime(final CheckGameTime check, final boolean required)
	{
		this.check = check;
		this.required = required;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		switch (check)
		{
			case NIGHT:
				return GameTimeController.getInstance().isNowNight() == required;
		}
		return !required;
	}
}
