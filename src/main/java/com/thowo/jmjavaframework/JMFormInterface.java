/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework;

import com.thowo.jmjavaframework.table.JMRow;

/**
 *
 * @author jimi
 */
public interface JMFormInterface {
    void actionAdd(JMRow rowAdded);
    void actionDelete(JMRow rowDeleted);
    void actionSave(String updateQuery);
    void actionEdit(JMRow rowEdited);
    void actionPrint(JMRow rowPrinted);
    void actionRefresh(JMRow rowRefreshed);
    void actionView(JMRow rowViewed);
    void actionNext(JMRow nextRow);
    void actionPrev(JMRow prevRow);
    void actionFirst(JMRow firstRow);
    void actionLast(JMRow lastRow);
    void gotoRecord(JMRow currentRow);
    void actionCancel(JMRow rowCanceled, boolean canceled);
}
