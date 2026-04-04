package src.service.payment.type.model;

public enum DefaultPaymentType {
    CREDIT_CARD("Credit Card"),
    CASH("Pay at Office"),
    BANK_MONEY_TRANSFER("Bank Transfer");

    private final String label;

    DefaultPaymentType(String label) {
        this.label = label;
    }

    // Newly added method
    public static DefaultPaymentType[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }

}
