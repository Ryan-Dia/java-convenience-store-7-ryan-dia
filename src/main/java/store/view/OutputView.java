package store.view;

import store.model.Item;
import store.model.Items;

public final class OutputView {
    private OutputView() {
    }

    public static void printItems(Items items) {
        printMessage("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        for (Item item : items.getItems()) {
            var promotionName = item.getPromotionName();
            int quantity = item.getQuantity();

            if (promotionName == null && quantity != 0) {
                printMessage(String.format("- %s %,d원 %s개", item.getName(), item.getPrice(), quantity));
                continue;
            }
            if (promotionName == null) {
                printMessage(String.format("- %s %,d원 %s", item.getName(), item.getPrice(), "재고 없음"));
                continue;
            }

            if (quantity == 0) {
                printMessage(String.format("- %s %,d원 %s %s", item.getName(), item.getPrice(), "재고 없음",
                        promotionName));
                continue;
            }

            printMessage(String.format("- %s %,d원 %s개 %s", item.getName(), item.getPrice(), quantity,
                    promotionName));
        }
    }

    public static void printMessage(String message) {
        System.out.println(message);
    }
}
