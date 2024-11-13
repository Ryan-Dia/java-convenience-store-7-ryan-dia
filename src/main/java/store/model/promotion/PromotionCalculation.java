package store.model.promotion;

public class PromotionCalculation {
    private final int buyQuantity;
    private final int freeQuantity;
    private final int promotionUnitQuantity;
    private final int remainingQuantity;
    private final int shortfall;
    private final int promotionAppliedQuantity;

    private PromotionCalculation(Promotion promotion, int orderQuantity) {
        this.buyQuantity = promotion.getBuy();
        this.freeQuantity = promotion.getGet();
        this.promotionUnitQuantity = buyQuantity + freeQuantity;
        this.remainingQuantity = orderQuantity % promotionUnitQuantity;
        this.shortfall = getShortfall(remainingQuantity, promotionUnitQuantity);
        this.promotionAppliedQuantity = orderQuantity / promotionUnitQuantity;
    }

    public static PromotionCalculation of(Promotion promotion, int orderQuantity) {
        return new PromotionCalculation(promotion, orderQuantity);
    }

    public boolean hasExactPromotionQuantity() {
        return remainingQuantity == 0;
    }

    public boolean hasPartialPromotion() {
        return buyQuantity != freeQuantity && buyQuantity == shortfall;
    }

    public int getPromotionAppliedQuantity() {
        return promotionAppliedQuantity;
    }

    public int getShortfall() {
        return shortfall;
    }

    private int getShortfall(int remainingQuantity, int promotionUnitQuantity) {
        if (remainingQuantity != 0) {
            return promotionUnitQuantity - remainingQuantity;
        }
        return 0;
    }
}
