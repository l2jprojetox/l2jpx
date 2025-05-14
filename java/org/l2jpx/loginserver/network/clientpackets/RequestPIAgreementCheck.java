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
package org.l2jpx.loginserver.network.clientpackets;

import org.l2jpx.Config;
import org.l2jpx.loginserver.network.serverpackets.PIAgreementCheck;

/**
 * @author UnAfraid
 */
public class RequestPIAgreementCheck extends LoginClientPacket
{
	private int _accountId;
	
	@Override
	protected boolean readImpl()
	{
		_accountId = readInt();
		final byte[] padding0 = new byte[3];
		final byte[] checksum = new byte[4];
		final byte[] padding1 = new byte[12];
		readBytes(padding0);
		readBytes(checksum);
		readBytes(padding1);
		return true;
	}
	
	@Override
	public void run()
	{
		getClient().sendPacket(new PIAgreementCheck(_accountId, Config.SHOW_PI_AGREEMENT ? 0x01 : 0x00));
	}
}
