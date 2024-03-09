package net.l2jpx.gameserver.thread.daemons;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author ProGramMoS
 */

public class PcPoint implements Runnable
{
	Logger LOGGER = Logger.getLogger(PcPoint.class);
	private static PcPoint instance;
	
	public static PcPoint getInstance()
	{
		if (instance == null)
		{
			instance = new PcPoint();
		}
		
		return instance;
	}
	
	private PcPoint()
	{
		LOGGER.info("PcBang point event started.");
	}
	
	@Override
	public void run()
	{
		int score = 0;
		for (L2PcInstance activeChar : L2World.getInstance().getAllPlayers())
		{
			if (activeChar.isOnline() && activeChar.getLevel() > Config.PCB_MIN_LEVEL)
			{
				score = ThreadLocalRandom.current().nextInt(Config.PCB_POINT_MIN, Config.PCB_POINT_MAX + 1);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_RECEVIED_$51_GLASSES_PC);
				
				if (ThreadLocalRandom.current().nextInt(100) <= Config.PCB_DOUBLE_POINT_CHANCE)
				{
					score *= 2;
					sm = new SystemMessage(SystemMessageId.DOUBLE_POINTS_YOU_GOT_$51_GLASSES_PC);
				}
				
				activeChar.addPcBangScore(score);
				sm.addNumber(score);
				activeChar.sendPacket(sm);
				activeChar.updatePcBangWnd(score, true, true);
			}
		}
	}
}
