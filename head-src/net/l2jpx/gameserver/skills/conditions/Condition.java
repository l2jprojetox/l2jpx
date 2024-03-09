package net.l2jpx.gameserver.skills.conditions;

//import org.apache.log4j.Logger;

import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public abstract class Condition implements ConditionListener
{
	private ConditionListener listener;
	private String msg;
	private boolean result;
	
	public final void setMessage(final String msg)
	{
		this.msg = msg;
	}
	
	public final String getMessage()
	{
		return msg;
	}
	
	void setListener(final ConditionListener listener)
	{
		this.listener = listener;
		notifyChanged();
	}
	
	final ConditionListener getListener()
	{
		return listener;
	}
	
	public final boolean test(final Env env)
	{
		final boolean res = testImpl(env);
		if (listener != null && res != result)
		{
			result = res;
			notifyChanged();
		}
		return res;
	}
	
	abstract boolean testImpl(Env env);
	
	@Override
	public void notifyChanged()
	{
		if (listener != null)
		{
			listener.notifyChanged();
		}
	}
}
