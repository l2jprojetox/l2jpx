package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2JFrozen
 */
public class TitleUpdate extends L2GameServerPacket
{
	private final String title;
	private final int objectId;
	
	public TitleUpdate(final L2PcInstance cha)
	{
		objectId = cha.getObjectId();
		title = cha.getTitle();
	}
	
	/**
	 * @see net.l2jpx.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xcc);
		writeD(objectId);
		writeS(title);
	}
	
	/**
	 * @see net.l2jpx.gameserver.network.serverpackets.L2GameServerPacket#getType()
	 */
	@Override
	public String getType()
	{
		return "[S] cc TitleUpdate";
	}
	
}
