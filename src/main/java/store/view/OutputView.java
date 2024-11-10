package store.view;

import java.util.List;
import store.model.item.Item;
import store.model.order.Order;
import store.model.order.OrderItem;
import store.model.order.PaymentSummary;

public final class OutputView {
    private static final String ITEM_LIST_MESSAGE = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n";
    private static final String ITEM_FORMAT = "- %s %,d원 ";
    private static final String NO_STOCK = "재고 없음";
    private static final String QUANTITY_FORMAT = "%d개";
    private static final String ITEM_NAME = "상품명";
    private static final String QUANTITY = "수량";
    private static final String PRICE = "금액";
    private static final String TOTAL_AMOUNT = "총구매액";
    private static final String MEMBERSHIP_DISCOUNT = "멤버십할인";
    private static final String PROMOTION = "행사할인";
    private static final String AMOUNT_DUE = "내실돈";
    private static final String EMPTY = "";

    private OutputView() {
    }

    public static void printItems(List<Item> items) {
        printMessage(ITEM_LIST_MESSAGE);
        for (Item item : items) {
            String message = applyFormat(item);
            printMessage(message);
        }
    }

    private static String applyFormat(Item item) {
        StringBuilder message = new StringBuilder();
        message.append(String.format(ITEM_FORMAT, item.getName(), item.getPrice()));

        int quantity = item.getQuantity();
        String promotionName = item.getPromotionName();
        appendItemDetails(quantity, promotionName, message);

        return message.toString();
    }

    private static void appendItemDetails(int quantity, String promotionName, StringBuilder message) {
        if (quantity == 0) {
            message.append(NO_STOCK);
        }
        if (quantity > 0) {
            message.append(String.format(QUANTITY_FORMAT, quantity));
        }
        if (promotionName != null) {
            message.append(" ").append(promotionName);
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
        printF(ReceiptFormType.START_LINE.getText());
        printF(ReceiptFormType.HEADER.getFormatted(), ITEM_NAME, QUANTITY, PRICE);
        for (OrderItem item : order.getOrderItems()) {
            printF(ReceiptFormType.ITEM.getFormatted(), item.getName(), item.getTotalOrderQuantity(),
                    item.getTotalOrderQuantity() * item.getPrice());
        }
    }

    private static void printGiveaway(Order order) {
        printF(ReceiptFormType.GIVEAWAY_LINE.getText());
        for (OrderItem item : order.getOrderItems()) {
            int promotionAppliedQuantity = item.getPromotionAppliedQuantity();
            if (promotionAppliedQuantity != 0) {
                printF(ReceiptFormType.GIVEAWAY.getFormatted(), item.getName(), promotionAppliedQuantity);
            }
        }
    }

    private static void printPaymentSummary(PaymentSummary paymentSummary) {
        printF(ReceiptFormType.SEPARATOR.getText());
        printF(ReceiptFormType.TOTAL_AMOUNT.getFormatted(), TOTAL_AMOUNT, paymentSummary.totalQuantity(),
                paymentSummary.totalAmount());
        printF(ReceiptFormType.DISCOUNT.getFormatted(), PROMOTION, EMPTY, paymentSummary.promotionDiscount());
        printF(ReceiptFormType.DISCOUNT.getFormatted(), MEMBERSHIP_DISCOUNT, EMPTY,
                paymentSummary.membershipDiscount());
        printF(ReceiptFormType.AMOUNT_DUE.getFormatted(), AMOUNT_DUE, EMPTY, paymentSummary.finalAmount());
    }

    private static void printF(String message, Object... args) {
        System.out.printf(message, args);
    }
}
