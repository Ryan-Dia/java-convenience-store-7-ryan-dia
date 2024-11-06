package store.model.promotion;

import java.time.LocalDateTime;

public class BuyTwoGetOneFree extends Promotion {

    public BuyTwoGetOneFree(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {
        super(name, buy, get, startDate, endDate);
    }
}
