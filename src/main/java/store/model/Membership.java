package store.model;

public final class Membership {
    private static final int DISCOUNT_PERCENTAGE = 30;
    private static final int MAX_DISCOUNT = 8_000;

    private Membership() {
    }

    public static long calculateDiscount(long amount) {
        return amount * DISCOUNT_PERCENTAGE / 100;
    }
}
