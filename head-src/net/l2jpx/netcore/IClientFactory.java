package net.l2jpx.netcore;

/**
 * @param  <T>
 * @author KenM
 */
public interface IClientFactory<T extends MMOClient<?>>
{
	public T create(final MMOConnection<T> con);
}