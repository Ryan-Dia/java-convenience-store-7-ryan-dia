package store.model.order;

public class OrderItem {
    private static final String DELIMITER = "-";

    private final String name;
    private final int quantity;

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
}
