package store.model.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.error.FileParsingException;
import store.utils.MarkdownReader;

public class ItemLoader {
    private static final String NULL = "null";

    private ItemLoader() {
    }

    public static ItemLoader getInstance() {
        return new ItemLoader();
    }

    public List<Item> loadItems(String filePath) {
        try {
            List<String[]> itemData = MarkdownReader.readFile(filePath);
            return parseItems(itemData);
        } catch (IOException e) {
            throw new FileParsingException(e);
        }
    }

    private List<Item> parseItems(List<String[]> itemData) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < itemData.size(); i++) {
            Item item = parseItem(itemData.get(i));
            items.add(item);
            if (shouldAddNonPromotionItem(item, i, itemData)) {
                items.add(new Item(item.getName(), item.getPrice(), 0, null));
            }
        }
        return items;
    }

    private Item parseItem(String[] itemData) {
        String name = itemData[0];
        int price = Integer.parseInt(itemData[1]);
        int quantity = Integer.parseInt(itemData[2]);
        String promotionName = itemData[3];
        if (NULL.equals(promotionName)) {
            promotionName = null;
        }
        return new Item(name, price, quantity, promotionName);
    }

    private boolean shouldAddNonPromotionItem(Item currentItem, int currentIndex, List<String[]> itemData) {
        if (currentItem.getPromotionName() == null) {
            return false;
        }
        boolean isLastItem = currentIndex == itemData.size() - 1;
        boolean isDifferentNextItem = !isLastItem && !itemData.get(currentIndex + 1)[0].equals(currentItem.getName());
        return isLastItem || isDifferentNextItem;
    }
}
