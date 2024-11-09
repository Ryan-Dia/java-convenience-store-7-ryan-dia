package store.view;

import store.model.Item;
import store.model.Items;
import store.model.order.Order;
import store.model.order.OrderItem;
import store.model.order.PaymentSummary;

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

    public static void printReceipt(Order order, PaymentSummary paymentSummary) {
        int maxNameLength = 2;
        int maxQuantityLength = "수량".length();
        int maxAmountLength = "금액".length();

        // 각 열의 최대 길이를 계산
        for (OrderItem item : order.getOrderItems()) {
            maxNameLength = Math.max(maxNameLength, item.getName().length());
            maxQuantityLength = Math.max(maxQuantityLength, String.valueOf(item.getTotalOrderQuantity()).length());
            maxAmountLength = Math.max(maxAmountLength, String.format("%,d", 0).length());
        }

        int nameLength = 20 - (maxNameLength * 2);

        // 포맷 설정
        String headerFormat = String.format("%%-%ds %%-%ds %%%ds%n", nameLength, 1,
                maxAmountLength);
        String itemFormat = String.format("%%-%ds %%%dd %%,%dd%n", nameLength, 1,
                maxAmountLength);

        System.out.println("==============W 편의점================");
        System.out.printf(headerFormat, "상품명", "수량", "금액");

        for (OrderItem item : order.getOrderItems()) {
            System.out.printf(itemFormat, item.getName(), item.getTotalOrderQuantity(), item.getPrice());
        }

        System.out.println("=============증\t정===============");

        for (OrderItem item : order.getOrderItems()) {
            if (item.getPromotionAppliedQuantity() != 0) {
                System.out.printf("%-" + (maxNameLength + 4) + "s %" + (maxQuantityLength + 4) + "d%n", item.getName(),
                        item.getPromotionAppliedQuantity());
            }
        }

        System.out.println("====================================");
        String totalFormat = String.format("%%-%ds %%%ds %%,%dd%n", maxNameLength + 4, maxQuantityLength + 4,
                maxAmountLength + 6);
        System.out.printf(totalFormat, "총구매액", "", paymentSummary.totalAmount());
        System.out.printf(totalFormat, "행사할인", "", -paymentSummary.promotionDiscount());
        System.out.printf(totalFormat, "멤버십할인", "", -paymentSummary.membershipDiscount());
        System.out.printf(totalFormat, "내실돈", "", paymentSummary.finalAmount());
    }
}
