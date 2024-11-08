package store.view;

import camp.nextstep.edu.missionutils.Console;

public final class InputView {

    private InputView() {
    }

    public static String readItem() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        return readInput();
    }

    public static String readPromotionConfirmationForFree(String itemName, int quantity) {
        System.out.printf("현재 %s은(는) %s개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", itemName, quantity);
        return readInput();
    }

    public static String readPurchaseConfirmationWithoutPromotion(String itemName, int quantity) {
        System.out.printf("현재 %s %s개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n", itemName, quantity);
        return readInput();
    }

    private static String readInput() {
        String input = Console.readLine().trim();

        if (input.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 빈 값은 입력할 수 없습니다.");
        }
        return input;
    }
}
