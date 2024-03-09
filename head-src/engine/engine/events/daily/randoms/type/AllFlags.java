package engine.engine.events.daily.randoms.type;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.network.clientpackets.Say2;

import engine.engine.AbstractMod;
import engine.holders.objects.PlayerHolder;
import engine.util.UtilMessage;

/**
 * @author fissban
 */
public class AllFlags extends AbstractMod
{
	public AllFlags()
	{
		registerMod(false);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				UtilMessage.toAllOnline(Say2.ANNOUNCEMENT, "Event: All Flag has ben Started!");
				L2World.getInstance().getAllPlayers().forEach(p ->
				{
					p.setPvpFlag(1);// PURPLE
					p.updatePvPStatus(p);
					p.broadcastUserInfo();
				});
				break;
			case END:
				UtilMessage.toAllOnline(Say2.ANNOUNCEMENT, "Event: All Flag has ben Finished!");
				L2World.getInstance().getAllPlayers().forEach(p -> p.updatePvPFlag(0));// NON_PVP
				break;
		}
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		ph.getInstance().setPvpFlag(1);// PURPLE
		ph.getInstance().updatePvPStatus(ph.getInstance());
		ph.getInstance().broadcastUserInfo();
	}
}
