package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.itemcontainer.Inventory;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.basefuncs.Func;

public class FuncPDefMod extends Func
{
	static final FuncPDefMod _fpa_instance = new FuncPDefMod();
	
	public static Func getInstance()
	{
		return _fpa_instance;
	}
	
	private FuncPDefMod()
	{
		super(Stats.POWER_DEFENCE, 0x20, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getCharacter() instanceof Player)
		{
			final Player player = env.getPlayer();
			final boolean isMage = player.isMageClass();
			
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) != null)
				env.subValue(12);
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
				env.subValue((isMage) ? 15 : 31);
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null)
				env.subValue((isMage) ? 8 : 18);
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null)
				env.subValue(8);
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null)
				env.subValue(7);
		}
		
		env.mulValue(env.getCharacter().getLevelMod());
	}
}