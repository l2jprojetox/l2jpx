package net.l2jpx.netcore;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;


/**
 * @param  <T>
 * @author KenM
 */
public abstract class ReceivablePacket<T extends MMOClient<?>> extends AbstractPacket<T> implements Runnable
{
	NioNetStringBuffer sbuf;
	protected static final Logger LOGGER = Logger.getLogger(ReceivablePacket.class);
	
	protected ReceivablePacket()
	{
		
	}
	
	protected abstract boolean read();
	
	@Override
	public abstract void run();
	
	protected final void readB(final byte[] dst)
	{
		try
		{
			buf.get(dst);
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
	}
	
	protected final void readB(final byte[] dst, final int offset, final int len)
	{
		try
		{
			buf.get(dst, offset, len);
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
	}
	
	protected final int readC()
	{
		try
		{
			return buf.get() & 0xFF;
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		return -1;
	}
	
	protected final int readH()
	{
		
		try
		{
			return buf.getShort() & 0xFFFF;
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
		return -1;
	}
	
	protected final int readD()
	{
		
		try
		{
			return buf.getInt();
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
		return -1;
	}
	
	protected final long readQ()
	{
		
		try
		{
			return buf.getLong();
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
		return -1;
	}
	
	protected final double readF()
	{
		try
		{
			return buf.getDouble();
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
		return -1;
	}
	
	protected final String readS()
	{
		sbuf.clear();
		
		try
		{
			char ch;
			while ((ch = buf.getChar()) != 0)
			{
				sbuf.append(ch);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("", e);
		}
		
		return sbuf.toString();
	}
	
	/**
	 * packet forge purpose
	 * @param data
	 * @param client
	 * @param sBuffer
	 */
	public void setBuffers(final ByteBuffer data, final T client, final NioNetStringBuffer sBuffer)
	{
		buf = data;
		this.client = client;
		sbuf = sBuffer;
	}
}
