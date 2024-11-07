package store.view;

import camp.nextstep.edu.missionutils.Console;

public final class InputView {

    private InputView() {
    }

    public static String readItem() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
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
