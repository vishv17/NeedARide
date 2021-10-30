package com.app.ride.authentication.utility;

public enum MessageEnum {
    SENDER("sender", 0),
    RECEIVER("receiver", 1);

    private String stringValue;
    private int intValue;
    private MessageEnum(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }


    @Override
    public String toString() {
        return stringValue;
    }

    public Integer value() {
        return intValue;
    }
}
