package store.model.promotion;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;

public abstract class Promotion {
    private final String name;
    private final int buy;
    private final int get;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    protected Promotion(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isPromotionValid() {
        LocalDateTime today = DateTimes.now();
        return (today.isAfter(startDate) || today.isEqual(startDate)) && today.isBefore(endDate);
    }

    public String getName() {
        return name;
    }
}
