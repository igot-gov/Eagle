package com.tarento.walletService.models;

/**
 * Enum to list the available wallet types
 */
public enum WalletType {
    DEPARTMENT("Department"),
    USER("User");

    private String value;

    WalletType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static WalletType fromValue(String text) {
        for (WalletType b : WalletType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}