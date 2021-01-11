package com.thowo.jmjavaframework;

import com.thowo.jmjavaframework.form.JMFormTableList;
import com.thowo.jmjavaframework.table.JMRow;

public interface JMFieldInterface {
    void displayText(String text, int JMDataContainerConstantAlign);
    void displayError(String errMsg);
    void displayHint(String hint);
    void setDataContainer(JMDataContainer dataContainer);
    void setHidden(boolean hidden);
    void setDisabled(boolean disabled);
    void setValueString(String value);
    void setValueObject(Object value);
    void setEditMode(boolean editMode, JMRow currentRow, int column);
    void setLookUpAction(Runnable action);
}
