package store.model;

import java.util.function.Function;

public enum SalesAmountType {
    TOTAL_AMOUNT("총구매액", amount -> amount),
    PROMOTION("행사할인", amount -> amount),
    MEMBERSHIP("멤버십", Membership::calculateDiscount);

    private String viewName;
    private Function<Long, Long> expression;

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
