package store.model.order;

import store.error.PurchaseConfirmationWithoutPromotionException;
import store.model.Inventory;

public class OrderProcessor {
    private final Inventory inventory;

    public OrderProcessor(Inventory inventory) {
        this.inventory = inventory;
    }

    public void processOrderForItem(String itemName, int orderQuantity) {
        int totalAvailableQuantity = inventory.getTotalQuantityForItem(itemName);
        int promoAvailableQuantity = inventory.getPromotionQuantityForItem(itemName);
        // 전체 프로모션+일반 개수 총합보다 더 많이 주문했다면 예외 0 재입력
        if (totalAvailableQuantity < orderQuantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }

        //TODO: 프로모션 아예 없어서 일반구매
        if (!inventory.hasPromotion(itemName)) {
            inventory.consumeRegularItem(itemName, orderQuantity);
            return;
        }

        // TODO: 프로모션 기간이 아님
        if (inventory.isPromotionInactive(itemName)) {
            int inactivePromotionQuantity = inventory.getInactivePromotionQuantity(itemName);
            if (inactivePromotionQuantity >= orderQuantity) {
                inventory.consumePromotionItemWithoutPromotion(itemName, orderQuantity);
                return;
            }
            int remainingQuantity = orderQuantity - inactivePromotionQuantity;
            inventory.consumePromotionItemWithoutPromotion(itemName, inactivePromotionQuantity);
            inventory.consumeRegularItem(itemName, remainingQuantity);
            return;
        }

        //TODO: 프로모션으로 지급가능한 개수 파악 예를 들어 2+1 이고 재고가 3개이면
        // 프로모션으로 구매 가능한 개수가 주문한 개수를 커버할 수 있다면 프로모션으로 처리
        // 1+1이면 최소 2개 2+1이면 최소 3개를 가지고 있어야 프로모션구매가 가능 그외는 통과처리
        int minPromotionQuantity = inventory.getMinPromotionQuantity(itemName);

        if (promoAvailableQuantity >= orderQuantity && minPromotionQuantity <= promoAvailableQuantity) {
            inventory.consumePromotionItem(itemName, orderQuantity);
            return;
        }

        // TODO: 프로모션 최저 조건 수량이 안 돼서 안내없이 일반 구매 처리
        // ex) 2+1 이면 프로모션 재고 1개 / 1+1 프로모션 재고 0개
        int minPromotionApplicableQuantity = inventory.getMinPromotionApplicableQuantity(itemName);

        if (minPromotionApplicableQuantity > promoAvailableQuantity) {
            inventory.consumePromotionItem(itemName, promoAvailableQuantity);
            inventory.consumeRegularItem(itemName, orderQuantity - promoAvailableQuantity);
            return;
        }

        // TODO: 일부만 프로모션 할인으로 구매 가능할 때 (프로모션 + 일반 구매)
        int applicablePromotionQuantity = inventory.getApplicablePromotionQuantity(itemName);
        int remainingPromoQuantity = promoAvailableQuantity - applicablePromotionQuantity;
        inventory.consumePromotionItem(itemName, applicablePromotionQuantity);
        throw new PurchaseConfirmationWithoutPromotionException(itemName,
                orderQuantity - applicablePromotionQuantity,
                remainingPromoQuantity);
        // 프로모션이 1+1이고  재고가 1개 남았는데 3개를 주문하면 안내후 구매해야함
        // 프로모션이 1+1이고  재고가 2개 / 일반 1개 남았는데 3개를 주문하면 안내후 모두 구매
        // -> 즉 프로모션을 받을 수 있응 개수가 충족했는데 프로모션 재고가 있으면 안내 만약 프로모션 받을 수 있는 최저 개수가 충족되지 않으면 그냥 바로
        // 안내없이 일반구매
        // ex) 프로모션 콜라 10개 / 일반 콜라 10개   14개 구매시 4개는 프로모션 할인 적용 x

    }

    public void parseUserChoice(String userChoice, String itemName, int withoutPromoQuantity,
                                int remainingPromoQuantity) {
        int regular = withoutPromoQuantity - remainingPromoQuantity;
        if (userChoice.equals("Y")) {
            inventory.consumePromotionItemWithoutPromotion(itemName, remainingPromoQuantity);
            inventory.consumeRegularItem(itemName, regular);
        }
    }
}
