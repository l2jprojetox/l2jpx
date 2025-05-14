/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.model.actor.instance;

import org.l2jpx.gameserver.data.xml.BuyListData;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.enums.TaxType;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.buylist.ProductList;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.BuyList;
import org.l2jpx.gameserver.network.serverpackets.ExBuySellList;

/**
 * @version $Revision: 1.10.4.9 $ $Date: 2005/04/11 10:06:08 $
 */
public class Merchant extends Folk
{
	public Merchant(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.Merchant);
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		if (attacker.isMonster())
		{
			return true;
		}
		return super.isAutoAttackable(attacker);
	}
	
	@Override
	public String getHtmlPath(int npcId, int value, Player player)
	{
		String pom;
		if (value == 0)
		{
			pom = Integer.toString(npcId);
		}
		else
		{
			pom = npcId + "-" + value;
		}
		return "data/html/merchant/" + pom + ".htm";
	}
	
	public void showBuyWindow(Player player, int value)
	{
		showBuyWindow(player, value, true);
	}
	
	public void showBuyWindow(Player player, int value, boolean applyCastleTax)
	{
		final ProductList buyList = BuyListData.getInstance().getBuyList(value);
		if (buyList == null)
		{
			LOGGER.warning("BuyList not found! BuyListId:" + value);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!buyList.isNpcAllowed(getId()))
		{
			LOGGER.warning("Npc not allowed in BuyList! BuyListId:" + value + " NpcId:" + getId());
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.setInventoryBlockingStatus(true);
		
		player.sendPacket(new BuyList(buyList, player, (applyCastleTax) ? getCastleTaxRate(TaxType.BUY) : 0));
		player.sendPacket(new ExBuySellList(player, false));
	}
}
