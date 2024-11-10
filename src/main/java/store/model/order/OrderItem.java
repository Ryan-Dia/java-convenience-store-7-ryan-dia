package store.model.order;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderItem {
    private static final String DELIMITER = "-";
    private static final String REGEX = "[\\[\\]]";
    private static final Pattern EACH_PATTERN = Pattern.compile("\\[([가-힣]+)-(\\d+)]");
    private static final String EMPTY = "";

    private final String name;
    private final int quantity;
    private int promotionAppliedQuantity = 0;
    private int totalOrderQuantity = 0;
    private int nonPromotionQuantity = 0;
    private int price = 0;

    public OrderItem(String item) {
        validate(item);
        String[] itemData = parse(item);
        this.name = itemData[0];
        this.quantity = Integer.parseInt(itemData[1]);
    }

    private void validate(String item) {
        Matcher matcher = EACH_PATTERN.matcher(item);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private String[] parse(String item) {
        return item.replaceAll(REGEX, EMPTY).split(DELIMITER);
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increasePromotionAppliedQuantity(int quantity) {
        promotionAppliedQuantity += quantity;
    }

    public void increaseNonPromotionQuantity(int quantity) {
        nonPromotionQuantity += quantity;
    }

    public int getNonPromotionQuantity() {
        return nonPromotionQuantity;
    }

    public int getPromotionAppliedQuantity() {
        return promotionAppliedQuantity;
    }

    public void increaseTotalOrderQuantity(int quantity) {
        totalOrderQuantity += quantity;
    }

    public int getTotalOrderQuantity() {
        return totalOrderQuantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }
}
