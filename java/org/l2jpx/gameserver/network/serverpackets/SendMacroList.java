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

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.MacroUpdateType;
import org.l2jpx.gameserver.model.Macro;
import org.l2jpx.gameserver.model.MacroCmd;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class SendMacroList extends ServerPacket
{
	private final int _count;
	private final Macro _macro;
	private final MacroUpdateType _updateType;
	
	public SendMacroList(int count, Macro macro, MacroUpdateType updateType)
	{
		_count = count;
		_macro = macro;
		_updateType = updateType;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MACRO_LIST.writeId(this, buffer);
		buffer.writeByte(_updateType.getId());
		buffer.writeInt(_updateType != MacroUpdateType.LIST ? _macro.getId() : 0); // modified, created or deleted macro's id
		buffer.writeByte(_count); // count of Macros
		buffer.writeByte(_macro != null); // unknown
		if ((_macro != null) && (_updateType != MacroUpdateType.DELETE))
		{
			buffer.writeInt(_macro.getId()); // Macro ID
			buffer.writeString(_macro.getName()); // Macro Name
			buffer.writeString(_macro.getDescr()); // Desc
			buffer.writeString(_macro.getAcronym()); // acronym
			buffer.writeInt(_macro.getIcon()); // icon
			buffer.writeByte(_macro.getCommands().size()); // count
			int i = 1;
			for (MacroCmd cmd : _macro.getCommands())
			{
				buffer.writeByte(i++); // command count
				buffer.writeByte(cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
				buffer.writeInt(cmd.getD1()); // skill id
				buffer.writeByte(cmd.getD2()); // shortcut id
				buffer.writeString(cmd.getCmd()); // command name
			}
		}
	}
}
