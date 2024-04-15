package com.px.loginserver;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.px.commons.mmocore.IAcceptFilter;
import com.px.commons.mmocore.IClientFactory;
import com.px.commons.mmocore.IMMOExecutor;
import com.px.commons.mmocore.MMOConnection;
import com.px.commons.mmocore.ReceivablePacket;

import com.px.loginserver.network.LoginClient;
import com.px.loginserver.network.serverpackets.Init;
import com.px.util.IPv4Filter;

public class SelectorHelper implements IMMOExecutor<LoginClient>, IClientFactory<LoginClient>, IAcceptFilter
{
	private final ThreadPoolExecutor _generalPacketsThreadPool;
	
	private final IPv4Filter _ipv4filter;
	
	public SelectorHelper()
	{
		_generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		_ipv4filter = new IPv4Filter();
	}
	
	@Override
	public void execute(ReceivablePacket<LoginClient> packet)
	{
		_generalPacketsThreadPool.execute(packet);
	}
	
	@Override
	public LoginClient create(MMOConnection<LoginClient> con)
	{
		LoginClient client = new LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}
	
	@Override
	public boolean accept(SocketChannel sc)
	{
		return _ipv4filter.accept(sc) && !LoginController.getInstance().isBannedAddress(sc.socket().getInetAddress());
	}
}