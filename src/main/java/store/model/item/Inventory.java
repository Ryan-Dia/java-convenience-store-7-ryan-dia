package store.model.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.error.PromotionConfirmationForFreeException;
import store.model.order.OrderItem;
import store.model.promotion.PromotionManager;
import store.utils.MarkdownReader;

public class Inventory {
    private Items items;
    private final PromotionManager promotionManager;

    public Inventory() {
        try {
            this.promotionManager = new PromotionManager();
            promotionManager.loadPromotions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setPrice(OrderItem orderItem) {
        for (Item item : items.getItems()) {
            if (item.getName().equals(orderItem.getName())) {
                orderItem.setPrice(item.getPrice());
            }
        }
    }

    public Items setItems() {
        try {
            List<String[]> itemData = MarkdownReader.readFile("src/main/resources/products.md");
            List<Item> items = new ArrayList<>();

            for (int i = 0; i < itemData.size(); i++) {
                String[] item = itemData.get(i);
                String name = item[0];
                int price = Integer.parseInt(item[1]);
                int quantity = Integer.parseInt(item[2]);
                String promotionName = item[3];
                if (promotionName.equals("null")) {
                    promotionName = null;
                }
                items.add(new Item(name, price, quantity, promotionName));

                boolean isLastItem = i == itemData.size() - 1;
                boolean isDifferentNextItem = !isLastItem && !itemData.get(i + 1)[0].equals(name);
                if (promotionName != null && (isLastItem || isDifferentNextItem)) {
                    items.add(new Item(name, price, 0, null));
                }
            }
            this.items = new Items(items);
            return this.items;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    // TODO: ex) 2+1이라면 1개는 그냥 구매가능 2개는 혜택에 대한 안내 메시지 3개는 그냥 구매 4개 그냥구매 5개 안내
    // 무조건 프로모션 개수로 처리가능해서 이것만 신경쓰면 됩니당
    public void consumePromotionItem(String itemName, int orderQuantity, OrderItem orderItem) {
        if (orderQuantity == 0) {
            return;
        }
        for (Item itemInInventory : items.getItems()) {
            if (!itemInInventory.getName().equals(itemName) || itemInInventory.getPromotionName() == null
                    || !promotionManager.getPromotion(itemInInventory.getPromotionName()).isPromotionActive()) {
                continue;
            }

            int buyQuantity = promotionManager.getPromotion(itemInInventory.getPromotionName()).getBuy();
            int freeQuantity = promotionManager.getPromotion(itemInInventory.getPromotionName()).getGet();
            int totalQuantity = getMinPromotionQuantity(itemName);
            int remainder = orderQuantity % totalQuantity;
            int shortfall = 0;
            if (remainder != 0) {
                shortfall = totalQuantity - remainder;
            }
            int promotionQuantity = orderQuantity / totalQuantity;

            // 프로모션 개수에 딱 맞게 샀을 대
            if (remainder == 0) {

                decreaseQuantity(itemInInventory, orderQuantity);
                orderItem.increasePromotionAppliedQuantity(promotionQuantity);
                orderItem.increaseTotalOrderQuantity(orderQuantity);
                return;
            }
            // 프로모션 개수에 충족되지 않았을 때 (ex 2+1 인데 1개만 구매했을 때)
            // 1+1은 제외
            if (buyQuantity != freeQuantity && buyQuantity == shortfall) {
                decreaseQuantity(itemInInventory, orderQuantity);
                orderItem.increaseTotalOrderQuantity(orderQuantity);
                orderItem.increaseNonPromotionQuantity(orderQuantity);
                return;
            }
            // 프로모션 개수에 충족되었는데 무료 증정 수량을 안 가져왔을 때
            throw new PromotionConfirmationForFreeException(itemInInventory, orderItem, shortfall);
        }
    }

    public void consumeRegularItem(String itemName, int quantity, OrderItem orderItem) {
        for (Item itemInInventory : items.getItems()) {
            if (!itemInInventory.getName().equals(itemName) || itemInInventory.getPromotionName() != null) {
                continue;
            }

            itemInInventory.decreaseQuantity(quantity);
            orderItem.increaseTotalOrderQuantity(quantity);
            orderItem.increaseNonPromotionQuantity(quantity);
            return;

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
}