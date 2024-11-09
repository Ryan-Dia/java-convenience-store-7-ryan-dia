package store.model.order;

import java.util.List;
import store.model.SalesAmountType;

public final class OrderCalculator {
    private OrderCalculator() {
    }

    public static PaymentSummary calculateAmounts(List<OrderItem> orderItems, boolean isMembership) {
        long totalQuantity = getTotalQuantity(orderItems);
        long totalAmount = getTotalAmount(orderItems);
        long promotionDiscount = getPromotionDiscount(orderItems);
        long membershipDiscount = getMembershipDiscount(getTotalNonPromotionAmount(orderItems), isMembership);
        long finalAmount = totalAmount - promotionDiscount - membershipDiscount;

        return new PaymentSummary(totalQuantity, totalAmount, promotionDiscount, membershipDiscount, finalAmount);
    }

    private static long getTotalQuantity(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToLong(item -> item.getTotalOrderQuantity())
                .sum();
    }

    private static long getTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToLong(item -> item.getPrice() * item.getTotalOrderQuantity())
                .sum();
    }

    private static long getPromotionDiscount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToLong(item -> item.getPrice() * item.getPromotionAppliedQuantity())
                .sum();
    }

    private static long getTotalNonPromotionAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToLong(item -> item.getPrice() * item.getNonPromotionQuantity())
                .sum();
    }

    private static long getMembershipDiscount(long totalNonPromotionAmount, boolean isMembership) {
        if (isMembership) {
            return SalesAmountType.MEMBERSHIP.calculate(totalNonPromotionAmount);
        }
        return 0;
    }
}
