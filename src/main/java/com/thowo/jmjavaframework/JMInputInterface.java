package com.thowo.jmjavaframework;

public interface JMInputInterface {
    void displayText(String text);
    void displayError(String errMsg);
    void displayHint(String hint);
    void setDataContainer(JMDataContainer dataContainer);
    void setHidden(boolean hidden);
    void setValueString(String value);
    void setValueObject(Object value);
}
