package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.geo.GeoData;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.Location;
import net.l2jpx.gameserver.model.actor.instance.L2CommanderInstance;
import net.l2jpx.gameserver.model.actor.instance.L2FolkInstance;
import net.l2jpx.gameserver.model.actor.instance.L2FortSiegeGuardInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2SiegeFlagInstance;
import net.l2jpx.gameserver.model.actor.instance.L2SiegeGuardInstance;
import net.l2jpx.gameserver.model.actor.instance.L2SiegeSummonInstance;
import net.l2jpx.gameserver.model.actor.position.L2CharPosition;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author littlecrow Implementation of the Fear Effect
 */
final class EffectFear extends L2Effect
{
	public static final int FEAR_RANGE = 500;
	
	public EffectFear(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.FEAR;
	}
	
	/** Notify started */
	@Override
	public void onStart()
	{
		if (getEffected().isSleeping())
		{
			getEffected().stopSleeping(null);
		}
		
		if (!getEffected().isAfraid())
		{
			getEffected().startFear();
			onActionTime();
		}
	}
	
	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopFear(this);
	}
	
	@Override
	public boolean onActionTime()
	{
		// Fear skills cannot be used l2pcinstance to l2pcinstance. Heroic Dread, Curse: Fear, Fear and Horror are the exceptions.
		if (getEffected() instanceof L2PcInstance && getEffector() instanceof L2PcInstance && getSkill().getId() != 1376 && getSkill().getId() != 1169 && getSkill().getId() != 65 && getSkill().getId() != 1092)
		{
			return false;
		}
		
		if (getEffected() instanceof L2FolkInstance)
		{
			return false;
		}
		
		if (getEffected() instanceof L2SiegeGuardInstance)
		{
			return false;
		}
		
		// Fear skills cannot be used on Headquarters Flag.
		if (getEffected() instanceof L2SiegeFlagInstance)
		{
			return false;
		}
		
		if (getEffected() instanceof L2SiegeSummonInstance)
		{
			return false;
		}
		
		if (getEffected() instanceof L2FortSiegeGuardInstance || getEffected() instanceof L2CommanderInstance)
		{
			return false;
		}
		
		int posX = getEffected().getX();
		int posY = getEffected().getY();
		final int posZ = getEffected().getZ();
		
		int signx = -1;
		int signy = -1;
		if (getEffected().getX() > getEffector().getX())
		{
			signx = 1;
		}
		if (getEffected().getY() > getEffector().getY())
		{
			signy = 1;
		}
		posX += signx * FEAR_RANGE;
		posY += signy * FEAR_RANGE;
		
		Location destiny = GeoData.getInstance().moveCheck(getEffected().getX(), getEffected().getY(), getEffected().getZ(), posX, posY, posZ);
		getEffected().setRunning();
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(destiny.getX(), destiny.getY(), destiny.getZ(), 0));
		
		destiny = null;
		return true;
	}
}
