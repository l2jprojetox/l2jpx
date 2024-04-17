package com.px.l2jhost.CapsuleBox;


import com.px.l2jhost.XML.XMLDocument;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CapsuleBoxData extends XMLDocument {
    private Map<Integer, CapsuleBoxItem> capsuleBoxItems;

    public CapsuleBoxData() {
        capsuleBoxItems = new HashMap<>();
        load();
    }

   public void reload()
   {
       capsuleBoxItems.clear();
       load();
   }  
    
    
    public static CapsuleBoxData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final CapsuleBoxData INSTANCE = new CapsuleBoxData();
    }

    @Override
    protected void load() {
        loadDocument("./data/xml/CapsuleBox.xml");
        LOG.info("CapsuleBoxData: Loaded " + capsuleBoxItems.size() + " items.");
    }

    @Override
    protected void parseDocument(Document doc, File file) {
        try {
            final Node root = doc.getFirstChild();

            for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (!"capsuled_items".equalsIgnoreCase(node.getNodeName())) {
                    continue;
                }

                NamedNodeMap attrs = node.getAttributes();
                int id = Integer.parseInt(attrs.getNamedItem("ID").getNodeValue());
                int playerLevel = Integer.parseInt(attrs.getNamedItem("PlayerLevel").getNodeValue());

                CapsuleBoxItem capsuleBoxItem = new CapsuleBoxItem(id, playerLevel);

                for (Node itemNode = node.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling()) {
                    if (!"item".equalsIgnoreCase(itemNode.getNodeName())) {
                        continue;
                    }

                    NamedNodeMap itemAttrs = itemNode.getAttributes();
                    int itemId = Integer.parseInt(itemAttrs.getNamedItem("itemId").getNodeValue());
                    int amount = 1; // Default amount is 1
                    if (itemAttrs.getNamedItem("min") != null && itemAttrs.getNamedItem("max") != null) {
                        int min = Integer.parseInt(itemAttrs.getNamedItem("min").getNodeValue());
                        int max = Integer.parseInt(itemAttrs.getNamedItem("max").getNodeValue());
                        amount = getRandomAmount(min, max);
                    }
                    int enchantLevel = Integer.parseInt(itemAttrs.getNamedItem("enchantLevel").getNodeValue());
                    int chance = Integer.parseInt(itemAttrs.getNamedItem("chance").getNodeValue());

                    CapsuleBoxItem.Item item = new CapsuleBoxItem.Item(itemId, amount, enchantLevel, chance);
                    capsuleBoxItem.addItem(item);
                }

                capsuleBoxItems.put(id, capsuleBoxItem);
            }
        } catch (Exception e) {
            // LOG.warning("CapsuleBoxData: Error while loading items: " + e);
            e.printStackTrace();
        }
    }

    public Map<Integer, CapsuleBoxItem> getCapsuleBoxItems() {
        return capsuleBoxItems;
    }

    public CapsuleBoxItem getCapsuleBoxItemById(int id) {
        return capsuleBoxItems.get(id);
    }

    private static int getRandomAmount(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}