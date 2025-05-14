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
package org.l2jpx.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.olympiad.AbstractOlympiadGame;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameClassed;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameManager;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameNonClassed;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameTask;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author mrTJO
 */
public class ExOlympiadMatchList extends ServerPacket
{
	private final List<OlympiadGameTask> _games = new ArrayList<>();
	
	public ExOlympiadMatchList()
	{
		OlympiadGameTask task;
		for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); i++)
		{
			task = OlympiadGameManager.getInstance().getOlympiadTask(i);
			if (task != null)
			{
				if (!task.isGameStarted() || task.isBattleFinished())
				{
					continue; // initial or finished state not shown
				}
				_games.add(task);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RECEIVE_OLYMPIAD.writeId(this, buffer);
		buffer.writeInt(0); // Type 0 = Match List, 1 = Match Result
		buffer.writeInt(_games.size());
		buffer.writeInt(0);
		for (OlympiadGameTask curGame : _games)
		{
			final AbstractOlympiadGame game = curGame.getGame();
			if (game != null)
			{
				buffer.writeInt(game.getStadiumId()); // Stadium Id (Arena 1 = 0)
				if (game instanceof OlympiadGameNonClassed)
				{
					buffer.writeInt(1);
				}
				else if (game instanceof OlympiadGameClassed)
				{
					buffer.writeInt(2);
				}
				else
				{
					buffer.writeInt(0);
				}
				buffer.writeInt(curGame.isRunning() ? 2 : 1); // (1 = Standby, 2 = Playing)
				buffer.writeString(game.getPlayerNames()[0]); // Player 1 Name
				buffer.writeString(game.getPlayerNames()[1]); // Player 2 Name
			}
		}
	}
}
