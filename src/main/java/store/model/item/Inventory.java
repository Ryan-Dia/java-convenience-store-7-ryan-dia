package store.model.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.error.FileParsingException;
import store.error.PromotionConfirmationForFreeException;
import store.model.order.OrderItem;
import store.model.promotion.Promotion;
import store.model.promotion.PromotionCalculation;
import store.model.promotion.PromotionManager;
import store.utils.MarkdownReader;

public class Inventory {
    private static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";
    private static final String NULL = "null";

    private final Items items;
    private final PromotionManager promotionManager;

    public Inventory() {
        this(PRODUCTS_FILE_PATH);
    }

    public Inventory(String productsFilePath) {
        try {
            this.items = loadItemsFromFile(productsFilePath);
            this.promotionManager = new PromotionManager();
            promotionManager.loadPromotions();
        } catch (IOException e) {
            throw new FileParsingException(e);
        }
    }

    public void setPrice(OrderItem orderItem) {
        for (Item item : items.getItems()) {
            if (item.getName().equals(orderItem.getName())) {
                orderItem.setPrice(item.getPrice());
            }
        }
    }

    private Items loadItemsFromFile(String productsFilePath) {
        try {
            List<String[]> itemData = MarkdownReader.readFile(productsFilePath);
            List<Item> items = parseItems(itemData);
            return new Items(items);
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

    public void consumePromotionItemWithoutPromotion(String itemName, int orderQuantity, OrderItem orderItem) {
        for (Item itemInInventory : items.getItems()) {
            if (itemInInventory.getName().equals(itemName) && itemInInventory.getPromotionName() != null) {
                decreaseQuantity(itemInInventory, orderQuantity);
                orderItem.increaseTotalOrderQuantity(orderQuantity);
                orderItem.increaseNonPromotionQuantity(orderQuantity);
                return;
            }
        }
    }

    // TODO: orderQuantity가 0이 들어왔을 때 고려해야함
    public void consumePromotionItem(String itemName, int orderQuantity, OrderItem orderItem) {
        Item itemInInventory = findItemForPromotion(itemName);
        Promotion promotion = promotionManager.getPromotion(itemInInventory.getPromotionName());
        PromotionCalculation promotionData = PromotionCalculation.of(promotion, orderQuantity);
        if (promotionData.hasExactPromotionQuantity()) {
            processPromotion(itemInInventory, orderItem, orderQuantity, promotionData.getPromotionAppliedQuantity());
            return;
        }
        if (promotionData.hasPartialPromotion()) {
            processNonPromotion(itemInInventory, orderItem, orderQuantity);
            return;
        }
        throw new PromotionConfirmationForFreeException(itemInInventory, orderItem, promotionData.getShortfall());
    }

    private Item findItemForPromotion(String itemName) {
        return items.getItems().stream()
                .filter(item -> {
                    Promotion promotion = promotionManager.getPromotion(item.getPromotionName());
                    return isEligibleForPromotion(item, itemName, promotion);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 상품목록에 존재하지 않는 상품입니다."));
    }

    private boolean isEligibleForPromotion(Item item, String itemName, Promotion promotion) {
        return item.getName().equals(itemName)
                && item.getPromotionName() != null
                && promotion != null
                && promotion.isPromotionActive();
    }

    private void processPromotion(Item item, OrderItem orderItem, int orderQuantity, int promotionQuantity) {
        decreaseQuantity(item, orderQuantity);
        orderItem.increaseTotalOrderQuantity(orderQuantity);
        orderItem.increasePromotionAppliedQuantity(promotionQuantity);
    }

    private void processNonPromotion(Item item, OrderItem orderItem, int orderQuantity) {
        decreaseQuantity(item, orderQuantity);
        orderItem.increaseTotalOrderQuantity(orderQuantity);
        orderItem.increaseNonPromotionQuantity(orderQuantity);
    }

    public void consumeRegularItem(String itemName, int quantity, OrderItem orderItem) {
        for (Item itemInInventory : items.getItems()) {
            if (!itemInInventory.getName().equals(itemName) || itemInInventory.getPromotionName() != null) {
                continue;
            }
            itemInInventory.decreaseQuantity(quantity);
            orderItem.increaseTotalOrderQuantity(quantity);
            orderItem.increaseNonPromotionQuantity(quantity);
        }
    }

    public void parseUserChoice(String choice, Item itemInInventory, OrderItem orderItem, int shortfall) {
        if (choice.equals("Y")) {
            decreaseQuantity(itemInInventory, orderItem.getQuantity() + shortfall);
            orderItem.increaseTotalOrderQuantity(orderItem.getQuantity() + shortfall);
            orderItem.increasePromotionAppliedQuantity(shortfall);
            return;
        }
        decreaseQuantity(itemInInventory, orderItem.getQuantity());
        orderItem.increaseTotalOrderQuantity(orderItem.getQuantity());
    }

    private void decreaseQuantity(Item itemInInventory, int quantity) {
        itemInInventory.decreaseQuantity(quantity);
    }

    public int getApplicablePromotionQuantity(String itemName) {
        int promotionQuantityForItem = getPromotionQuantityForItem(itemName);
        int minPromotionQuantity = getMinPromotionQuantity(itemName);
        if (minPromotionQuantity == 0) {
            throw new IllegalStateException("일어나면 안되는 에러가 발생했습니다. : Inventory");
        }
        int remainder = promotionQuantityForItem % minPromotionQuantity;
        return promotionQuantityForItem - remainder;
    }

    public int getMinPromotionQuantity(String itemName) {
        return items.getItems().stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null)
                .mapToInt(item -> promotionManager.getPromotion(item.getPromotionName()).getGet()
                        + promotionManager.getPromotion(item.getPromotionName()).getBuy())
                .sum();
    }

    public int getTotalQuantityForItem(String itemName) {
        return items.getItems().stream()
                .filter(item -> item.getName().equals(itemName))
                .mapToInt(Item::getQuantity)
                .sum();
    }

    public int getPromotionQuantityForItem(String itemName) {
        return items.getItems().stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null
                        && promotionManager.isPromotionActive(item.getPromotionName()))
                .mapToInt(Item::getQuantity)
                .sum();
    }

    public int getInactivePromotionQuantity(String itemName) {
        return items.getItems().stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null)
                .findFirst()
                .map(Item::getQuantity)
                .orElse(0);
    }


    public int getMinPromotionApplicableQuantity(String itemName) {
        return items.getItems().stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null)
                .map(item -> promotionManager.getPromotion(item.getPromotionName()).getBuy())
                .findFirst()
                .orElse(0);
    }

    public boolean hasPromotion(String itemName) {
        return items.getItems().stream()
                .anyMatch(item -> item.getName().equals(itemName) && item.getPromotionName() != null);
    }

    public boolean isPromotionInactive(String itemName) {
        return items.getItems().stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null)
                .anyMatch(item -> !promotionManager.isPromotionActive(item.getPromotionName()));
    }

    public Items getItems() {
        return items;
    }
}
