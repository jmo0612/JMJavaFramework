/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.form;

import com.thowo.jmjavaframework.JMFieldInterface;
import com.thowo.jmjavaframework.JMFilterListener;
import com.thowo.jmjavaframework.table.JMDBListInterface;
import com.thowo.jmjavaframework.table.JMTable;
import java.util.List;

/**
 *
 * @author jimi
 */
public interface JMFormInterface {
    String getFilterText();
    JMFilterListener getFilterComponent();
    //void init(String title,JMDBListInterface list,JMFormDBButtonGroup btnGroup,Runnable filterAction,JMLocaleInterface fieldProp, boolean isLookup);
    //void init(JMDBListInterface list,JMFormDBButtonGroup btnGroup);
    //void init(String title,List<JMFieldInterface> fields,JMFormDBButtonGroup btnGroup);
    //void init(String title,List<JMFieldInterface> fields,JMFormDBButtonGroup btnGroup,JMFormTableList detailTable);
    void init(JMFormTableList tableList);
    void setOkCancelRunnables(List<Runnable> okCancelRunnables);
    void load();
    void setDisposeOnClosed();
    void setDoNothingOnClosed();
    void closeMe(boolean ok);
    void setOnClosed(Runnable runnable);
}
