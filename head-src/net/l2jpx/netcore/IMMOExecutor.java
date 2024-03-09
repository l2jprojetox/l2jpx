package net.l2jpx.netcore;

/**
 * @param  <T>
 * @author KenM
 */
public interface IMMOExecutor<T extends MMOClient<?>>
{
	public void execute(ReceivablePacket<T> packet);
}