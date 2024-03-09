package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.TradeList;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class TradeDone extends L2GameClientPacket
{
	private int response;
	
	@Override
	protected void readImpl()
	{
		response = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("trade"))
		{
			player.sendMessage("You trading too fast.");
			return;
		}
		
		final TradeList trade = player.getActiveTradeList();
		if (trade == null)
		{
			// LOGGER.warn("player.getTradeList == null in " + getType() + " for player " + player.getName());
			return;
		}
		
		if (trade.getOwner().getActiveEnchantItem() != null || trade.getPartner().getActiveEnchantItem() != null)
		{
			return;
		}
		
		if (trade.isLocked())
		{
			return;
		}
		
		// abort cast anyway
		player.abortCast(true);
		
		if (player.isCastingNow() || player.isCastingPotionNow())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (response == 1)
		{
			if (trade.getPartner() == null || L2World.getInstance().findObject(trade.getPartner().getObjectId()) == null)
			{
				// Trade partner not found, cancel trade
				player.cancelActiveTrade();
				SystemMessage msg = new SystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
				player.sendPacket(msg);
				msg = null;
				return;
			}
			
			if (!player.getAccessLevel().allowTransaction())
			{
				player.sendMessage("Unsufficient privileges.");
				player.cancelActiveTrade();
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			trade.confirm();
		}
		else
		{
			player.cancelActiveTrade();
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 17 TradeDone";
	}
}
