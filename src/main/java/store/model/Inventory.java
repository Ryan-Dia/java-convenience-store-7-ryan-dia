package store.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.model.promotion.Promotion;
import store.model.promotion.PromotionFactory;
import store.utils.MarkdownReader;

public class Inventory {

    private Map<String, Promotion> promotions;

    public Inventory() {
        try {
            getPromotions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Items setItems() {
        try {
            List<String[]> itemData = MarkdownReader.readFile("src/main/resources/products.md");
            List<Item> items = new ArrayList<>();
            for (String[] item : itemData) {
                String name = item[0];
                int price = Integer.parseInt(item[1]);
                int quantity = Integer.parseInt(item[2]);
                Promotion promotion = this.promotions.getOrDefault(item[3], null);
                items.add(new Item(name, price, quantity, promotion));
            }
            return new Items(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getPromotions() throws IOException {
        List<String[]> promotionDataBase = MarkdownReader.readFile("src/main/resources/promotions.md");
        Map<String, Promotion> promotions = new HashMap<>();

        for (String[] promotionData : promotionDataBase) {
            String name = promotionData[0];
            int buy = Integer.parseInt(promotionData[1]);
            int get = Integer.parseInt(promotionData[2]);
            LocalDateTime startDate = LocalDate.parse(promotionData[3]).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(promotionData[4]).atTime(23, 59, 59);
            Promotion promotion = PromotionFactory.createPromotion(name, buy, get, startDate, endDate);
            promotions.put(name, promotion);
        }
        this.promotions = promotions;
    }
}
