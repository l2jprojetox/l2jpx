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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.data.xml.FakePlayerData;
import org.l2jpx.gameserver.network.serverpackets.ExIsCharNameCreatable;
import org.l2jpx.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class RequestCharacterNameCreatable extends ClientPacket
{
	private String _name;
	
	public static final int CHARACTER_CREATE_FAILED = 1;
	public static final int NAME_ALREADY_EXISTS = 2;
	public static final int INVALID_LENGTH = 3;
	public static final int INVALID_NAME = 4;
	public static final int CANNOT_CREATE_SERVER = 5;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final int charId = CharInfoTable.getInstance().getIdByName(_name);
		int result;
		if (!Util.isAlphaNumeric(_name) || !isValidName(_name))
		{
			result = INVALID_NAME;
		}
		else if (charId > 0)
		{
			result = NAME_ALREADY_EXISTS;
		}
		else if (FakePlayerData.getInstance().getProperName(_name) != null)
		{
			result = NAME_ALREADY_EXISTS;
		}
		else if (_name.length() > 16)
		{
			result = INVALID_LENGTH;
		}
		else
		{
			result = -1;
		}
		
		getClient().sendPacket(new ExIsCharNameCreatable(result));
	}
	
	private boolean isValidName(String text)
	{
		return Config.CHARNAME_TEMPLATE_PATTERN.matcher(text).matches();
	}
}