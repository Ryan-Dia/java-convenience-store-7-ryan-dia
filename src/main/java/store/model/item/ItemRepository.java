package store.model.item;

import java.util.List;
import store.model.promotion.Promotion;
import store.model.promotion.PromotionManager;

public class ItemRepository {
    private final List<Item> items;
    private final PromotionManager promotionManager;

    public ItemRepository(List<Item> items, PromotionManager promotionManager) {
        this.items = items;
        this.promotionManager = promotionManager;
    }

    public List<Item> getItems() {
        return items;
    }

    public Item findItemWithoutPromotionByName(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName))
                .filter(item -> item.getPromotionName() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 상품목록에 존재하지 않는 상품입니다."));
    }

    public Item findPromotionItemByName(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName))
                .filter(item -> item.getPromotionName() != null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 상품목록에 존재하지 않는 상품입니다."));
    }

    public int findPromotionItemQuantityByName(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null)
                .findFirst()
                .map(Item::getQuantity)
                .orElse(0);
    }

    public Item findOnlyActivePromotionItem(String itemName) {
        return items.stream()
                .filter(item -> {
                    Promotion promotion = promotionManager.getPromotion(item.getPromotionName());
                    return isEligibleForPromotion(itemName, item, promotion);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 상품목록에 존재하지 않는 상품입니다."));
    }

    private boolean isEligibleForPromotion(String itemName, Item item, Promotion promotion) {
        return item.getName().equals(itemName) && promotion != null && promotion.isPromotionActive();
    }

    public int findMinPromotionQuantity(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName))
                .filter(item -> item.getPromotionName() != null)
                .mapToInt(item -> promotionManager.getPromotion(item.getPromotionName()).getGet()
                        + promotionManager.getPromotion(item.getPromotionName()).getBuy())
                .sum();
    }

    public int findPromotionQuantityForItem(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName) && promotionManager.isPromotionActive(
                        item.getPromotionName()))
                .mapToInt(Item::getQuantity)
                .sum();
    }

    public int findTotalQuantityForItem(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName))
                .mapToInt(Item::getQuantity)
                .sum();
    }

    public boolean hasPromotion(String itemName) {
        return items.stream()
                .anyMatch(item -> item.getName().equals(itemName) && item.getPromotionName() != null);
    }

    public boolean isPromotionInactive(String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName) && item.getPromotionName() != null)
                .anyMatch(item -> !promotionManager.isPromotionActive(item.getPromotionName()));
    }


    public void decreaseQuantity(Item item, int quantity) {
        item.decreaseQuantity(quantity);
    }
}
