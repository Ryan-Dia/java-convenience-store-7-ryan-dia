package store.model;

public enum Answer {
    YES("Y", true),
    NO("N", false);

    private final String answer;
    private final boolean status;

    Answer(String answer, boolean status) {
        this.answer = answer;
        this.status = status;
    }

    public boolean isEqual(String input) {
        return answer.equals(input);
    }

    public String getAnswer() {
        return answer;
    }

    public boolean getStatus() {
        return status;
    }

    public static void validate(String input) {
        if (!input.equals(YES.answer) && !input.equals(NO.answer)) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
    }
}
