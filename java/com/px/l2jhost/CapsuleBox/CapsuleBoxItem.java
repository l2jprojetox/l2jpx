package com.px.l2jhost.CapsuleBox;

import java.util.ArrayList;
import java.util.List;

public class CapsuleBoxItem {
    private int id;
    private int playerLevel;
    private List<Item> items;

    public CapsuleBoxItem(int id, int playerLevel) {
        this.id = id;
        this.playerLevel = playerLevel;
        items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public static class Item {
        private int itemId;
        private int amount;
        private int enchantLevel;
        private int chance;

        public Item(int itemId, int amount, int enchantLevel, int chance) {
            this.itemId = itemId;
            this.amount = amount;
            this.enchantLevel = enchantLevel;
            this.chance = chance;
        }

        public int getItemId() {
            return itemId;
        }

        public int getAmount() {
            return amount;
        }

        public int getEnchantLevel() {
            return enchantLevel;
        }

        public int getChance() {
            return chance;
        }

 
    }
}