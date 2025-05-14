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
package org.l2jpx.gameserver.enums;

/**
 * Basic property type of skills.<br>
 * Before Goddess of Destruction, BaseStats was used. CON for physical, MEN for magical, and others for special cases.<br>
 * After, only 3 types are used: physical, magic and none.<br>
 * <br>
 * Quote from Juji:<br>
 * ----------------------------------------------------------------------<br>
 * Physical: Stun, Paralyze, Knockback, Knock Down, Hold, Disarm, Petrify<br>
 * Mental: Sleep, Mutate, Fear, Aerial Yoke, Silence<br>
 * ----------------------------------------------------------------------<br>
 * All other are considered with no basic property aka NONE.<br>
 * <br>
 * @author Nik
 */
public enum BasicProperty
{
	NONE,
	PHYSICAL,
	MAGIC;
}
