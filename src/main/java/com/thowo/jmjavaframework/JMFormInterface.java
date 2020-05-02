package com.thowo.jmjavaframework;

public interface JMFormInterface {
    void displayText(String text);
    void displayError(String errMsg);
    void displayHint(String hint);
    void setDataContainer(JMDataContainer dataContainer);
    void setHidden(boolean hidden);
}
