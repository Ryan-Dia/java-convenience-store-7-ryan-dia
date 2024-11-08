package store.error;

public class PurchaseConfirmationWithoutPromotionException extends RuntimeException {
    private final String itemName;
    private final int remainingPromoQuantity;
    private final int withoutPromoQuantity;

    public PurchaseConfirmationWithoutPromotionException(String itemName, int withoutPromoQuantity,
                                                         int remainingPromoQuantity
    ) {
        super();
        this.itemName = itemName;
        this.remainingPromoQuantity = remainingPromoQuantity;
        this.withoutPromoQuantity = withoutPromoQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getRemainingPromoQuantity() {
        return remainingPromoQuantity;
    }

    public int getWithoutPromoQuantity() {
        return withoutPromoQuantity;
    }
}
