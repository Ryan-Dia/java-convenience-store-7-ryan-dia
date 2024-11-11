package store.model.item;

import store.error.FileContentException;

public class Item {
    private static final String KOREAN_REGEX = "^[가-힣]+$";
    private static final int MAX_PRICE = 100_000_000;
    public static final int MAX_QUANTITY = 999;

    private final String name;
    private final int price;
    private final String promotionName;
    private int quantity;

    public Item(String name, int price, int quantity, String promotionName) {
        validate(name, price, quantity);
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotionName = promotionName;
    }

    private void validate(String name, int price, int quantity) {
        validateName(name);
        validatePrice(price);
        validateQuantity(quantity);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new FileContentException("[ERROR] 상품 이름이 잘못되었습니다.");
        }
        if (!name.matches(KOREAN_REGEX)) {
            throw new FileContentException("[ERROR] 상품 이름은 한글만 입력할 수 있습니다.");
        }
    }

    private void validatePrice(int price) {
        if (price < 0) {
            throw new FileContentException("[ERROR] 가격은 0원 이상이어야 합니다.");
        }
        if (price > MAX_PRICE) {
            throw new FileContentException("[ERROR] 가격은 최대 1억원 이하까지로만 설정할 수 있습니다.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new FileContentException("[ERROR] 수량은 0개 이상으로만 설정할 수 있습니다.");
        }
        if (quantity > MAX_QUANTITY) {
            throw new FileContentException("[ERROR] 수량은 최대 999개까지 설정할 수 있습니다.");
        }
    }

    void decreaseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
        if (this.quantity == 0) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
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
