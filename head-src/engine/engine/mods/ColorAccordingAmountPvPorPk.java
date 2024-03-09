package engine.engine.mods;

import java.util.Map.Entry;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

import engine.data.properties.ConfigData;
import engine.engine.AbstractMod;
import engine.holders.objects.CharacterHolder;
import engine.holders.objects.PlayerHolder;
import engine.util.Util;

/**
 * Here define a color the name according to:<br>
 * <li>Amount pvp</li>
 * @author fissban
 */
public class ColorAccordingAmountPvPorPk extends AbstractMod
{
	/**
	 * Constructor
	 */
	public ColorAccordingAmountPvPorPk()
	{
		registerMod(ConfigData.ENABLE_ColorAccordingAmountPvP);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!Util.areObjectType(L2PcInstance.class, killer, victim))
		{
			return;
		}
		
		checkColorPlayer(killer.getInstance().getActingPlayer());
	}
	
	@Override
	public void onEnterWorld(PlayerHolder player)
	{
		checkColorPlayer(player.getInstance());
	}
	
	private static void checkColorPlayer(L2PcInstance activeChar)
	{
		String color = null;
		for (Entry<Integer, String> entry : ConfigData.PVP_COLOR_NAME.entrySet())
		{
			int pvp = entry.getKey();
			
			if (activeChar.getPvpKills() > pvp)
			{
				color = entry.getValue();
			}
		}
		
		if (color != null)
		{
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + color));
			// update info
			activeChar.broadcastUserInfo();
		}
	}
}
