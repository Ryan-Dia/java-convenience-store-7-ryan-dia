package store.model.item;

import java.io.IOException;
import java.util.List;
import store.error.FileParsingException;
import store.error.PromotionConfirmationForFreeException;
import store.model.Answer;
import store.model.order.OrderItem;
import store.model.promotion.Promotion;
import store.model.promotion.PromotionCalculation;
import store.model.promotion.PromotionManager;

public class Inventory {
    private static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";

    private final ItemRepository itemRepository;
    private final PromotionManager promotionManager;

    public Inventory() {
        this(PRODUCTS_FILE_PATH);
    }

    public Inventory(String productsFilePath) {
        try {
            this.promotionManager = new PromotionManager();
            promotionManager.loadPromotions();
            List<Item> items = ItemLoader.getInstance().loadItems(productsFilePath);
            this.itemRepository = new ItemRepository(items, promotionManager);
        } catch (IOException e) {
            throw new FileParsingException(e);
        }
    }

    public void validate(String itemName) {
        itemRepository.validateItemName(itemName);
    }

    public void setPrice(OrderItem orderItem) {
        for (Item item : itemRepository.getItems()) {
            if (item.getName().equals(orderItem.getName())) {
                orderItem.setPrice(item.getPrice());
            }
        }
    }

    public void consumePromotionItemWithoutPromotion(OrderItem orderItem) {
        Item promotionItem = itemRepository.findPromotionItemByName(orderItem.getName());
        int quantity = promotionItem.getQuantity();

        itemRepository.decreaseQuantity(promotionItem, quantity);
        orderItem.increaseTotalOrderQuantity(quantity);
        orderItem.increaseNonPromotionQuantity(quantity);
    }

    public void consumePromotionItemWithoutPromotion(int orderQuantity, OrderItem orderItem) {
        Item promotionItem = itemRepository.findPromotionItemByName(orderItem.getName());

        itemRepository.decreaseQuantity(promotionItem, orderQuantity);
        orderItem.increaseTotalOrderQuantity(orderQuantity);
        orderItem.increaseNonPromotionQuantity(orderQuantity);
    }

    public void consumePromotionItem(int orderQuantity, OrderItem orderItem) {
        Item itemInInventory = itemRepository.findOnlyActivePromotionItem(orderItem.getName());
        Promotion promotion = promotionManager.getPromotion(itemInInventory.getPromotionName());
        PromotionCalculation promotionData = PromotionCalculation.of(promotion, orderQuantity);
        processPromotionItem(orderQuantity, orderItem, promotionData, itemInInventory);
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
        itemRepository.decreaseQuantity(item, orderQuantity);
        orderItem.increaseTotalOrderQuantity(orderQuantity);
        orderItem.increasePromotionAppliedQuantity(promotionQuantity);
    }

    private void processNonPromotion(Item item, OrderItem orderItem, int orderQuantity) {
        itemRepository.decreaseQuantity(item, orderQuantity);
        orderItem.increaseTotalOrderQuantity(orderQuantity);
        orderItem.increaseNonPromotionQuantity(orderQuantity);
    }

    public void consumeRegularItem(String itemName, int quantity, OrderItem orderItem) {
        Item regularItem = itemRepository.findItemWithoutPromotionByName(itemName);
        regularItem.decreaseQuantity(quantity);
        orderItem.increaseTotalOrderQuantity(quantity);
        orderItem.increaseNonPromotionQuantity(quantity);
    }

    public void parseUserChoiceWithoutPromotion(String userChoice, OrderItem orderItem, int remainingPromotionQuantity,
                                                int remainingQuantity) {
        if (Answer.YES.isEqual(userChoice)) {
            consumePromotionItemWithoutPromotion(remainingPromotionQuantity, orderItem);
            consumeRegularItem(orderItem.getName(), remainingQuantity - remainingPromotionQuantity, orderItem);
        }
    }

    public void parseUserChoiceForFree(String userChoice, Item itemInInventory, OrderItem orderItem, int shortfall) {
        if (Answer.YES.isEqual(userChoice)) {
            itemRepository.decreaseQuantity(itemInInventory, orderItem.getQuantity() + shortfall);
            orderItem.increaseTotalOrderQuantity(orderItem.getQuantity() + shortfall);
            orderItem.increasePromotionAppliedQuantity(shortfall);
            return;
        }
        itemRepository.decreaseQuantity(itemInInventory, orderItem.getQuantity());
        orderItem.increaseTotalOrderQuantity(orderItem.getQuantity());
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
        return itemRepository.findMinPromotionQuantity(itemName);
    }

    public int getTotalQuantityForItem(String itemName) {
        return itemRepository.findTotalQuantityForItem(itemName);
    }

    public int getPromotionQuantityForItem(String itemName) {
        return itemRepository.findPromotionQuantityForItem(itemName);
    }

    public int getPromotionItemQuantityByName(String itemName) {
        return itemRepository.findPromotionItemQuantityByName(itemName);
    }

    public boolean hasPromotion(String itemName) {
        return itemRepository.hasPromotion(itemName);
    }

    public boolean isPromotionInactive(String itemName) {
        return itemRepository.isPromotionInactive(itemName);
    }

    public List<Item> getItems() {
        return itemRepository.getItems();
    }
}
