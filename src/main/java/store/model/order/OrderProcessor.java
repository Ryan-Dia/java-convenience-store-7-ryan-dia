package store.model.order;

import store.error.PurchaseConfirmationWithoutPromotionException;
import store.model.item.Inventory;

public class OrderProcessor {
    private final Inventory inventory;

    public OrderProcessor(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setPrice(OrderItem orderItem) {
        inventory.setPrice(orderItem);
    }

    public void processOrder(OrderItem orderItem) {
        checkOrderQuantity(orderItem);
        processOrderForItem(orderItem);
    }

    private void processOrderForItem(OrderItem orderItem) {
        if (processWithoutPromotion(orderItem)) {
            return;
        }
        if (processInactivePromotion(orderItem)) {
            return;
        }
        if (processPromotion(orderItem)) {
            return;
        }
        processPartialPromotion(orderItem);
    }

    private void checkOrderQuantity(OrderItem orderItem) {
        String itemName = orderItem.getName();
        int orderQuantity = orderItem.getQuantity();
        int totalAvailableQuantity = inventory.getTotalQuantityForItem(itemName);

        if (totalAvailableQuantity < orderQuantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private boolean processWithoutPromotion(OrderItem orderItem) {
        String itemName = orderItem.getName();
        int orderQuantity = orderItem.getQuantity();
        int promoAvailableQuantity = inventory.getPromotionQuantityForItem(itemName);
        if (!inventory.hasPromotion(itemName) || promoAvailableQuantity == 0) {
            inventory.consumeRegularItem(itemName, orderQuantity, orderItem);
            return true;
        }
        return false;
    }

    private boolean processInactivePromotion(OrderItem orderItem) {
        if (inventory.isPromotionInactive(orderItem.getName())) {
            int promotionQuantity = inventory.getPromotionItemQuantityByName(orderItem.getName());
            if (promotionQuantity >= orderItem.getQuantity()) {
                inventory.consumePromotionItemWithoutPromotion(orderItem);
                return true;
            }
            return consumeNonPromotion(orderItem, promotionQuantity);
        }
        return false;
    }

    private boolean consumeNonPromotion(OrderItem orderItem, int inactivePromotionQuantity) {
        String itemName = orderItem.getName();
        int orderQuantity = orderItem.getQuantity();
        int remainingQuantity = orderQuantity - inactivePromotionQuantity;
        inventory.consumePromotionItemWithoutPromotion(inactivePromotionQuantity, orderItem);
        inventory.consumeRegularItem(itemName, remainingQuantity, orderItem);
        return true;
    }

    private boolean processPromotion(OrderItem orderItem) {
        int orderQuantity = orderItem.getQuantity();
        int promoAvailableQuantity = inventory.getPromotionQuantityForItem(orderItem.getName());
        int minPromotionQuantity = inventory.getMinPromotionQuantity(orderItem.getName());
        if (promoAvailableQuantity >= orderQuantity && minPromotionQuantity <= promoAvailableQuantity) {
            inventory.consumePromotionItem(orderQuantity, orderItem);
            return true;
        }
        return false;
    }

    private void processPartialPromotion(OrderItem orderItem) {
        String itemName = orderItem.getName();
        int promoAvailableQuantity = inventory.getPromotionQuantityForItem(itemName);
        int applicablePromotionQuantity = inventory.getApplicablePromotionQuantity(itemName);
        int remainingQuantity = orderItem.getQuantity() - applicablePromotionQuantity;
        inventory.consumePromotionItem(applicablePromotionQuantity, orderItem);
        throw new PurchaseConfirmationWithoutPromotionException(itemName, remainingQuantity,
                promoAvailableQuantity - applicablePromotionQuantity, orderItem);
    }
}
