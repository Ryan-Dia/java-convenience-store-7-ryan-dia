package store.model.order;

import java.util.Arrays;
import java.util.List;

public class Order {

    public static final String DELIMITER = ",";

    private final List<OrderItem> orderItems;

    public Order(String userOrder) {
        List<OrderItem> parser = parse(userOrder);
        this.orderItems = parser;

    }

    private List<OrderItem> parse(String userOrder) {
        return Arrays.stream(userOrder.split(DELIMITER))
                .map(x -> x.replaceAll("[\\[\\]]", ""))
                .map(OrderItem::new)
                .toList();
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
