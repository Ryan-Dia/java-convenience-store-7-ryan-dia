package store.model.order;

public record PaymentSummary(long totalAmount, long promotionDiscount, long membershipDiscount, long finalAmount) {
}
