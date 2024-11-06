package store.model.promotion;

import java.time.LocalDateTime;

public class MDRecommendedPromotion extends Promotion {
    public MDRecommendedPromotion(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {
        super(name, buy, get, startDate, endDate);
    }
}
