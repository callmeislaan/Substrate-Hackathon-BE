package com.backend.apiserver.entity;

public enum PaymentMethod {

    ANEST_CARD(1, "Thẻ cào"),
    SMS(2, "SMS"),
    E_WALLET(3, "Ví Điện Tử"),
    QR_CODE(4, "QR Pay"),
    BANK_CARD(5, "Thẻ Ngân Hàng")
    ;


    private int id;
    private String methodName;

    PaymentMethod(int id, String methodName) {
        this.id = id;
        this.methodName = methodName;
    }

    public int getId() {
        return id;
    }

    public String getMethodName() {
        return methodName;
    }

    public static PaymentMethod findById(int id) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.getId() == id) return paymentMethod;
        }
        return null;
    }
}
