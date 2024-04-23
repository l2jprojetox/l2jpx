package com.l2jpx.loginserver;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.l2jpx.commons.mmocore.IAcceptFilter;
import com.l2jpx.commons.mmocore.IClientFactory;
import com.l2jpx.commons.mmocore.IMMOExecutor;
import com.l2jpx.commons.mmocore.MMOConnection;
import com.l2jpx.commons.mmocore.ReceivablePacket;

import com.l2jpx.loginserver.data.manager.IpBanManager;
import com.l2jpx.loginserver.network.LoginClient;
import com.l2jpx.loginserver.network.serverpackets.Init;
import com.l2jpx.util.IPv4Filter;

public class SelectorHelper implements IMMOExecutor<LoginClient>, IClientFactory<LoginClient>, IAcceptFilter
{
	private final ThreadPoolExecutor _generalPacketsThreadPool;
	
	private final IPv4Filter _ipv4filter;
	
	public SelectorHelper()
	{
		_generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		_ipv4filter = new IPv4Filter();
	}
	
	@Override
	public boolean accept(Socket socket)
	{
		return _ipv4filter.accept(socket) && !IpBanManager.getInstance().isBannedAddress(socket.getInetAddress());
	}
	
	@Override
	public LoginClient create(MMOConnection<LoginClient> con)
	{
		LoginClient client = new LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}
	
	@Override
	public void execute(ReceivablePacket<LoginClient> packet)
	{
		_generalPacketsThreadPool.execute(packet);
	}
}