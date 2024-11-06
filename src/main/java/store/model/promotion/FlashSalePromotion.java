package store.model.promotion;

import java.time.LocalDateTime;

public class FlashSalePromotion extends Promotion {
    public FlashSalePromotion(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {
        super(name, buy, get, startDate, endDate);
    }
}
