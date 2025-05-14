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
package org.l2jpx.gameserver.network.serverpackets.training;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.ExperienceData;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExTrainingZone_Admission extends ServerPacket
{
	private final long _timeElapsed;
	private final long _timeRemaining;
	private final double _maxExp;
	private final double _maxSp;
	
	public ExTrainingZone_Admission(int level, long timeElapsed, long timeRemaing)
	{
		_timeElapsed = timeElapsed;
		_timeRemaining = timeRemaing;
		_maxExp = Config.TRAINING_CAMP_EXP_MULTIPLIER * ((ExperienceData.getInstance().getExpForLevel(level) * ExperienceData.getInstance().getTrainingRate(level)) / Config.TRAINING_CAMP_MAX_DURATION);
		_maxSp = Config.TRAINING_CAMP_SP_MULTIPLIER * (_maxExp / 250d);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_TRAINING_ZONE_ADMISSION.writeId(this, buffer);
		buffer.writeInt((int) _timeElapsed); // Training time elapsed in minutes, max : 600 - 10hr RU / 300 - 5hr NA
		buffer.writeInt((int) _timeRemaining); // Time remaining in seconds, max : 36000 - 10hr RU / 18000 - 5hr NA
		buffer.writeDouble(_maxExp); // Training time elapsed in minutes * this value = acquired exp IN GAME DOESN'T SEEM LIKE THE FIELD IS LIMITED
		buffer.writeDouble(_maxSp); // Training time elapsed in minutes * this value = acquired sp IN GAME LIMITED TO INTEGER.MAX_VALUE SO THE MULTIPLY WITH REMAINING TIME CANNOT EXCEED IT (so field max value can't exceed 3579139.0 for 10hr) !! // Note real sp gain is exp gained / 250
	}
}
