package store.model.item;

import java.io.IOException;
import store.error.FileParsingException;
import store.error.PromotionConfirmationForFreeException;
import store.model.order.OrderItem;
import store.model.promotion.Promotion;
import store.model.promotion.PromotionCalculation;
import store.model.promotion.PromotionManager;

public class Inventory {
    private static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";

    private final Items items;
    private final PromotionManager promotionManager;

    public Inventory() {
        this(PRODUCTS_FILE_PATH);
    }

    public Inventory(String productsFilePath) {
        try {
            this.items = new Items(ItemLoader.getInstance().loadItems(productsFilePath));
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

    public void consumePromotionItemWithoutPromotion(OrderItem orderItem) {
        for (Item itemInInventory : items.getItems()) {
            if (itemInInventory.getName().equals(orderItem.getName()) && itemInInventory.getPromotionName() != null) {
                decreaseQuantity(itemInInventory, orderItem.getQuantity());
                orderItem.increaseTotalOrderQuantity(orderItem.getQuantity());
                orderItem.increaseNonPromotionQuantity(orderItem.getQuantity());
                return;
            }
        }
    }

    public void consumePromotionItemWithoutPromotion(int orderQuantity, OrderItem orderItem) {
        for (Item itemInInventory : items.getItems()) {
            if (itemInInventory.getName().equals(orderItem.getName()) && itemInInventory.getPromotionName() != null) {
                decreaseQuantity(itemInInventory, orderQuantity);
                orderItem.increaseTotalOrderQuantity(orderQuantity);
                orderItem.increaseNonPromotionQuantity(orderQuantity);
                return;
            }
        }
    }

    public void consumePromotionItem(int orderQuantity, OrderItem orderItem) {
        Item itemInInventory = findItemForPromotion(orderItem.getName());
        Promotion promotion = promotionManager.getPromotion(itemInInventory.getPromotionName());
        PromotionCalculation promotionData = PromotionCalculation.of(promotion, orderQuantity);
        processPromotionItem(orderQuantity, orderItem, promotionData, itemInInventory);
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

    private void processPromotionItem(int orderQuantity, OrderItem orderItem, PromotionCalculation promotionData,
                                      Item itemInInventory) {
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

    public void parseUserChoiceWithoutPromotion(String userChoice, OrderItem orderItem, int remainingPromotionQuantity,
                                                int remainingQuantity) {
        if (userChoice.equals("Y")) {
            consumePromotionItemWithoutPromotion(remainingPromotionQuantity, orderItem);
            consumeRegularItem(orderItem.getName(), remainingQuantity - remainingPromotionQuantity, orderItem);
        }
    }

    public void parseUserChoiceForFree(String userChoice, Item itemInInventory, OrderItem orderItem, int shortfall) {
        if (userChoice.equals("Y")) {
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
            throw new IllegalStateException("[ERROR] 일어나면 안되는 에러가 발생했습니다. : Inventory");
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
