package store.view;

import store.model.item.Item;
import store.model.item.Items;
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
        printItems(order);
        printGiveaway(order);
        printPaymentSummary(paymentSummary);
    }

    private static void printItems(Order order) {
        System.out.printf(ReceiptFormType.START_LINE.getText());
        System.out.printf(ReceiptFormType.HEADER.getFormatted(), "상품명", "수량", "금액");
        for (OrderItem item : order.getOrderItems()) {
            System.out.printf(ReceiptFormType.ITEM.getFormatted(), item.getName(), item.getTotalOrderQuantity(),
                    item.getTotalOrderQuantity() * item.getPrice());
        }
    }

    private static void printGiveaway(Order order) {
        System.out.printf(ReceiptFormType.GIVEAWAY_LINE.getText());
        for (OrderItem item : order.getOrderItems()) {
            if (item.getPromotionAppliedQuantity() != 0) {
                System.out.printf(ReceiptFormType.GIVEAWAY.getFormatted(), item.getName(),
                        item.getPromotionAppliedQuantity());
            }
        }
    }

    private static void printPaymentSummary(PaymentSummary paymentSummary) {
        System.out.printf(ReceiptFormType.SEPARATOR.getText());
        System.out.printf(ReceiptFormType.TOTAL_AMOUNT.getFormatted(), "총구매액", paymentSummary.totalQuantity(),
                paymentSummary.totalAmount());
        System.out.printf(ReceiptFormType.DISCOUNT.getFormatted(), "행사할인", "", paymentSummary.promotionDiscount());
        System.out.printf(ReceiptFormType.DISCOUNT.getFormatted(), "멤버십할인", "", paymentSummary.membershipDiscount());
        System.out.printf(ReceiptFormType.AMOUNT_DUE.getFormatted(), "내실돈", "", paymentSummary.finalAmount());
    }
}
