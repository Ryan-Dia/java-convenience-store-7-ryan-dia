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
}
