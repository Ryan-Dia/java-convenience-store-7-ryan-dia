package store.model.promotion;

import java.time.LocalDateTime;

public final class PromotionFactory {
    private static final String CARBONATION = "탄산2+1";
    private static final String MD = "MD추천상품";
    private static final String FLASH_SALE = "반짝할인";

    private PromotionFactory() {
    }

    public static Promotion createPromotion(String name, int buy, int get, LocalDateTime startDate,
                                            LocalDateTime endDate) {
        if (name.equals(CARBONATION)) {
            return new BuyTwoGetOneFree(name, buy, get, startDate, endDate);
        }
        if (name.equals(MD)) {
            return new MDRecommendedPromotion(name, buy, get, startDate, endDate);
        }
        if (name.equals(FLASH_SALE)) {
            return new FlashSalePromotion(name, buy, get, startDate, endDate);
        }
        throw new IllegalArgumentException("[ERROR] 존재하지 않는 프로모션입니다.");
    }
}
