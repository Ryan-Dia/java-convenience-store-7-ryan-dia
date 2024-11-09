package store.model.order;

public class OrderItem {
    private static final String DELIMITER = "-";

    private final String name;
    private final int quantity;
    private int promotionAppliedQuantity = 0;
    private int totalOrderQuantity = 0;
    private int nonPromotionQuantity = 0;
    private int price = 0;

    public OrderItem(String item) {
        String[] itemData = parse(item);
        this.name = itemData[0];
        this.quantity = Integer.parseInt(itemData[1]);
    }

    private String[] parse(String item) {
        return item.split(DELIMITER);
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
