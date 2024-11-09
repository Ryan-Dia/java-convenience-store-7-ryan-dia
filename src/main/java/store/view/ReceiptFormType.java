package store.view;

public enum ReceiptFormType {
    HEADER("%%-%ds\t %%-%ds %%%ds%n", 18, 5, 5),
    ITEM("%%-%ds\t %%-%dd %%-,%dd%n", 16, 9, 9),
    GIVEAWAY("%%-%ds\t %%-%dd%n", 16, 9),
    TOTAL_AMOUNT("%%-%ds\t %%-%dd %%-,%dd%n", 16, 9, 9),
    DISCOUNT("%%-%ds\t %%-%dd -%%-,%dd%n", 16, 9, 9),
    AMOUNT_DUE("%%-%ds\t %%-%dd %%-,%dd%n", 16, 9, 9),
    START_LINE("================W 편의점================\n"),
    GIVEAWAY_LINE("=============증        정===============\n"),
    SEPARATOR("=======================================\n");

    private final String format;
    private final Object[] args;

    ReceiptFormType(String format, Object... args) {
        this.format = format;
        this.args = args;
    }

    public String getFormatted() {
        return String.format(format, args);
    }

    public String getText() {
        return format;
    }
}
