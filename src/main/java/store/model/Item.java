package store.model;

public class Item {
    private final String name;
    private final int price;
    private final String promotionName;
    private int quantity;

    public Item(String name, int price, int quantity, String promotionName) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotionName = promotionName;
    }

    void decreaseQuantity(int quantity) {
        if (quantity == 0) {
            throw new IllegalArgumentException("[ERROR] 현재 수량이 0이기 때문에 더 이상 감소시킬 수 없습니다.");
        }
        this.quantity -= quantity;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPromotionName() {
        return promotionName;
    }
}
