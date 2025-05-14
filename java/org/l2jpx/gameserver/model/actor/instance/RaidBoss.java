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
package org.l2jpx.gameserver.model.actor.instance;

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.network.serverpackets.PlaySound;

/**
 * This class manages all RaidBoss.<br>
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public class RaidBoss extends Monster
{
	private boolean _useRaidCurse = true;
	
	/**
	 * Constructor of RaidBoss (use Creature and Npc constructor).<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Call the Creature constructor to set the _template of the RaidBoss (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the RaidBoss</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * </ul>
	 * @param template to apply to the NPC
	 */
	public RaidBoss(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.RaidBoss);
		setIsRaid(true);
		setLethalable(false);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		setRandomWalking(false);
		broadcastPacket(new PlaySound(1, getParameters().getString("RaidSpawnMusic", "Rm01_A"), 0, 0, 0, 0, 0));
	}
	
	@Override
	public int getVitalityPoints(int level, double exp, boolean isBoss)
	{
		return -super.getVitalityPoints(level, exp, isBoss);
	}
	
	@Override
	public boolean useVitalityRate()
	{
		return Config.RAIDBOSS_USE_VITALITY;
	}
	
	public void setUseRaidCurse(boolean value)
	{
		_useRaidCurse = value;
	}
	
	@Override
	public boolean giveRaidCurse()
	{
		return _useRaidCurse;
	}
}
