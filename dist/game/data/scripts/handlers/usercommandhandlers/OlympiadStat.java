/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.usercommandhandlers;

import org.l2jpx.Config;
import org.l2jpx.gameserver.handler.IUserCommandHandler;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.olympiad.Olympiad;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * Olympiad Stat user command.
 * @author kamy, Zoey76
 */
public class OlympiadStat implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		109
	};
	
	@Override
	public boolean useUserCommand(int id, Player player)
	{
		if (!Config.OLYMPIAD_ENABLED)
		{
			player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		final int nobleObjId = player.getObjectId();
		final WorldObject target = player.getTarget();
		if ((target == null) || !target.isPlayer() || !target.getActingPlayer().isNoble())
		{
			player.sendPacket(SystemMessageId.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
			return false;
		}
		
		final SystemMessage sm = new SystemMessage(SystemMessageId.FOR_THE_CURRENT_OLYMPIAD_YOU_HAVE_PARTICIPATED_IN_S1_MATCH_ES_AND_HAD_S2_WIN_S_AND_S3_DEFEAT_S_YOU_CURRENTLY_HAVE_S4_OLYMPIAD_POINT_S);
		sm.addInt(Olympiad.getInstance().getCompetitionDone(nobleObjId));
		sm.addInt(Olympiad.getInstance().getCompetitionWon(nobleObjId));
		sm.addInt(Olympiad.getInstance().getCompetitionLost(nobleObjId));
		sm.addInt(Olympiad.getInstance().getNoblePoints((Player) target));
		player.sendPacket(sm);
		
		final SystemMessage sm2 = new SystemMessage(SystemMessageId.THE_MATCHES_THIS_WEEK_ARE_ALL_CLASS_BATTLES_THE_NUMBER_OF_MATCHES_THAT_ARE_ALLOWED_TO_PARTICIPATE_IS_S1);
		sm2.addInt(Olympiad.getInstance().getRemainingWeeklyMatches(nobleObjId));
		player.sendPacket(sm2);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
