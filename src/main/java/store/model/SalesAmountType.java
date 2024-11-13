package store.model;

import java.util.function.Function;

public enum SalesAmountType {
    MEMBERSHIP("멤버십할인", Membership::calculateDiscount);

    private final String viewName;
    private final Function<Long, Long> expression;

    SalesAmountType(String viewName, Function<Long, Long> expression) {
        this.viewName = viewName;
        this.expression = expression;
    }

    public long calculate(long amount) {
        return expression.apply(amount);
    }

    public String getViewName() {
        return viewName;
    }
}
