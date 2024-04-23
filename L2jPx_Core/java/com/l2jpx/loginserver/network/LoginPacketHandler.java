package com.l2jpx.loginserver.network;

import java.nio.ByteBuffer;

import com.l2jpx.commons.mmocore.IPacketHandler;
import com.l2jpx.commons.mmocore.ReceivablePacket;

import com.l2jpx.loginserver.enums.LoginClientState;
import com.l2jpx.loginserver.network.clientpackets.AuthGameGuard;
import com.l2jpx.loginserver.network.clientpackets.RequestAuthLogin;
import com.l2jpx.loginserver.network.clientpackets.RequestServerList;
import com.l2jpx.loginserver.network.clientpackets.RequestServerLogin;

/**
 * Handler for packets received by Login Server
 */
public final class LoginPacketHandler implements IPacketHandler<LoginClient>
{
	@Override
	public ReceivablePacket<LoginClient> handlePacket(ByteBuffer buf, LoginClient client)
	{
		int opcode = buf.get() & 0xFF;
		
		ReceivablePacket<LoginClient> packet = null;
		LoginClientState state = client.getState();
		
		switch (state)
		{
			case CONNECTED:
				if (opcode == 0x07)
					packet = new AuthGameGuard();
				else
					debugOpcode(opcode, state);
				break;
			
			case AUTHED_GG:
				if (opcode == 0x00)
					packet = new RequestAuthLogin();
				else
					debugOpcode(opcode, state);
				break;
			
			case AUTHED_LOGIN:
				if (opcode == 0x05)
					packet = new RequestServerList();
				else if (opcode == 0x02)
					packet = new RequestServerLogin();
				else
					debugOpcode(opcode, state);
				break;
		}
		return packet;
	}
	
	private static void debugOpcode(int opcode, LoginClientState state)
	{
		System.out.println("Unknown Opcode: " + opcode + " for state: " + state.name());
	}
}