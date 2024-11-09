package store.model.order;

public record PaymentSummary(long totalQuantity, long totalAmount, long promotionDiscount, long membershipDiscount,
                             long finalAmount) {
}
