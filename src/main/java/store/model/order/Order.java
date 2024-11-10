package store.model.order;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Order {
    private static final Pattern ORDER_PATTERN = Pattern.compile("^\\[[가-힣]+-\\d+](,\\[[가-힣]+-\\d+])*$");
    public static final String DELIMITER = ",";

    private final List<OrderItem> orderItems;

    public Order(String userOrder) {
        validate(userOrder);
        List<OrderItem> parser = parse(userOrder);
        this.orderItems = parser;

    }

    private void validate(String userOrder) {
        validateEachFormat(userOrder);
    }

    private void validateEachFormat(String userOrder) {
        Matcher matcher = ORDER_PATTERN.matcher(userOrder);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private List<OrderItem> parse(String userOrder) {
        return Arrays.stream(userOrder.split(DELIMITER))
                .map(OrderItem::new)
                .toList();
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
