package com.px.gameserver.handler.itemhandlers.custom;

import com.px.commons.random.Rnd;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.serverpackets.MagicSkillUse;
import com.px.l2jhost.CapsuleBox.CapsuleBoxData;
import com.px.l2jhost.CapsuleBox.CapsuleBoxItem;

import java.util.logging.Logger;

public class CapsuleBox_System implements IItemHandler {
    private static final Logger LOGGER = Logger.getLogger(CapsuleBox_System.class.getName());

    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player)) {
            LOGGER.warning("Playable is not an instance of Player");
            return;
        }

        final Player activeChar = (Player) playable;
        final int itemId = item.getItemId();

        CapsuleBoxItem capsuleBoxItem = CapsuleBoxData.getInstance().getCapsuleBoxItemById(itemId);
        if (capsuleBoxItem != null) {
            if (activeChar.getStatus().getLevel() < capsuleBoxItem.getPlayerLevel()) {
                activeChar.sendMessage("Para Usar Esta Capsule Box Necesitas El LvL." + capsuleBoxItem.getPlayerLevel());
                LOGGER.warning("Player level is less than required level for Capsule Box");
                return;
            }

            ItemInstance toGive = null;
            for (CapsuleBoxItem.Item boxItem : capsuleBoxItem.getItems()) {
                toGive = new ItemInstance(IdFactory.getInstance().getNextId(), boxItem.getItemId());
                int random = Rnd.get(100);
                if (random < boxItem.getChance()) {
                    if (!toGive.isStackable()) {
                        toGive.setEnchantLevel(boxItem.getEnchantLevel(), activeChar);
                        activeChar.addItem("CapsuleBox", toGive, activeChar, true);
                    } else {
                        activeChar.addItem("CapsuleBox", boxItem.getItemId(), boxItem.getAmount(), activeChar, true);
                    }
                } else {
                    LOGGER.info("Random number is greater than box item chance");
                }
                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
                activeChar.broadcastPacket(MSU);

            }

        } else {
            activeChar.sendMessage("This Capsule box expired or is invalid!");
            LOGGER.warning("Capsule box is expired or invalid");
        }

        playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
    }
}