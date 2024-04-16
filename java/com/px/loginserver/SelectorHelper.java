package com.px.loginserver;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.px.commons.mmocore.IAcceptFilter;
import com.px.commons.mmocore.IClientFactory;
import com.px.commons.mmocore.IMMOExecutor;
import com.px.commons.mmocore.MMOConnection;
import com.px.commons.mmocore.ReceivablePacket;

import com.px.loginserver.data.manager.IpBanManager;
import com.px.loginserver.network.LoginClient;
import com.px.loginserver.network.serverpackets.Init;
import com.px.util.IPv4Filter;

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