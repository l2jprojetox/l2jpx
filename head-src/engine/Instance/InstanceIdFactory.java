package engine.Instance;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrador
 */
public final class InstanceIdFactory
{
	private static AtomicInteger nextAvailable = new AtomicInteger(1);
	
	public synchronized static int getNextAvailable()
	{
		return nextAvailable.getAndIncrement();
	}
}
