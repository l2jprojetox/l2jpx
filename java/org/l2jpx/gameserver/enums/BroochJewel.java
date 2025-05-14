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
 * @author Mobius
 */
public enum BroochJewel
{
	RUBY_LV1(38855, 17814, 1, 0.01, true, false),
	RUBY_LV2(38856, 17814, 1, 0.035, true, false),
	RUBY_LV3(38857, 17815, 1, 0.075, true, false),
	RUBY_LV4(38858, 17816, 1, 0.125, true, false),
	RUBY_LV5(38859, 17817, 1, 0.2, true, false),
	
	GREATER_RUBY(47688, 18715, 1, 0.2, true, false),
	
	SAPPHIRE_LV1(38927, 17818, 1, 0.01, false, true),
	SAPPHIRE_LV2(38928, 17818, 1, 0.035, false, true),
	SAPPHIRE_LV3(38929, 17819, 1, 0.075, false, true),
	SAPPHIRE_LV4(38930, 17820, 1, 0.125, false, true),
	SAPPHIRE_LV5(38931, 17821, 1, 0.2, false, true),
	
	GREATER_SAPPHIRE(47689, 18718, 1, 0.2, false, true);
	
	private final int _itemId;
	private final int _skillId;
	private final int _skillLevel;
	private final double _bonus;
	private final boolean _isRuby;
	private final boolean _isSapphire;
	
	private BroochJewel(int itemId, int skillId, int skillLevel, double bonus, boolean isRuby, boolean isSapphire)
	{
		_itemId = itemId;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_bonus = bonus;
		_isRuby = isRuby;
		_isSapphire = isSapphire;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getSkillLevel()
	{
		return _skillLevel;
	}
	
	public double getBonus()
	{
		return _bonus;
	}
	
	public boolean isRuby()
	{
		return _isRuby;
	}
	
	public boolean isSapphire()
	{
		return _isSapphire;
	}
}
