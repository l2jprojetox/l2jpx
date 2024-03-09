package net.l2jpx.gameserver.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DualBox
{
	private static Map<String, Integer> dualboxes = new ConcurrentHashMap<>();
	
	public static void increaseDualBoxCount(String ip)
	{
		if(dualboxes.containsKey(ip))
		{
			int count = dualboxes.get(ip);
			count++;
			dualboxes.put(ip, count);
		}
		else
		{
			dualboxes.put(ip, 0);
		}
	}
	
	public static void decreaseDualBoxCount(String ip)
	{
		if(dualboxes.containsKey(ip))
		{
			int count = dualboxes.get(ip);
			count--;
			
			if(count < 0)
				count = 0;
			
			dualboxes.put(ip, count);
		}
	}
	
	public static int getDualBoxCount(String ip)
	{
		return dualboxes.getOrDefault(ip, 0);
	}
}
