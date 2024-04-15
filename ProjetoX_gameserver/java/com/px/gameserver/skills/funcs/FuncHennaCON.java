package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.actors.HennaType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.basefuncs.Func;

public class FuncHennaCON extends Func
{
	private static final HennaType STAT = HennaType.CON;
	private static final FuncHennaCON INSTANCE = new FuncHennaCON();
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
	
	private FuncHennaCON()
	{
		super(STAT.getStats(), 0x10, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		final Player player = env.getPlayer();
		if (player != null)
			env.addValue(player.getHennaList().getStat(STAT));
	}
}