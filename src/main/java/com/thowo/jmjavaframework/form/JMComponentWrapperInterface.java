/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.form;

import com.thowo.jmjavaframework.JMButtonInterface;
import com.thowo.jmjavaframework.JMFieldInterface;
import com.thowo.jmjavaframework.JMPanelInterface;
import com.thowo.jmjavaframework.JMVec2;
import com.thowo.jmjavaframework.table.JMDBListInterface;
import com.thowo.jmjavaframework.table.JMTable;
import java.util.List;

/**
 *
 * @author jimi
 */
public interface JMComponentWrapperInterface {
    JMDBListInterface newDBTableList();
    List<JMPanelInterface> newDBButtonPanels();
    List<JMButtonInterface> newDBButtons(JMVec2 size);
    JMFieldInterface newTextFields(String label,String prompt);
    JMFieldInterface newLookupFields(String label,String prompt);
    JMFieldInterface newSwitchFields(String trueLabel,String falseLabel);
    JMFormInterface newFormTable();
    JMFormInterface newFormInput();
    JMFormInterface newFormMasterDetail();
}
