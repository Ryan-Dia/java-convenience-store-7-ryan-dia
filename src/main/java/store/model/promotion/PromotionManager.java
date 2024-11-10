package store.model.promotion;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.utils.MarkdownReader;

public class PromotionManager {
    private static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";
    private final Map<String, Promotion> promotions = new HashMap<>();

    public void loadPromotions() throws IOException {
        List<String[]> promotionData = MarkdownReader.readFile(PROMOTION_FILE_PATH);
        generatePromotion(promotionData);
    }

    private void generatePromotion(List<String[]> promotionData) {
        for (String[] data : promotionData) {
            String name = data[0];
            int buy = Integer.parseInt(data[1]);
            int get = Integer.parseInt(data[2]);
            LocalDateTime startDate = LocalDate.parse(data[3]).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(data[4]).atTime(23, 59, 59);
            Promotion promotion = new Promotion(name, buy, get, startDate, endDate);
            promotions.put(name, promotion);
        }
    }

    public Promotion getPromotion(String name) {
        return promotions.getOrDefault(name, null);
    }

    public boolean isPromotionActive(String name) {
        Promotion promotion = promotions.get(name);
        return promotion != null && promotion.isPromotionActive();
    }
}
