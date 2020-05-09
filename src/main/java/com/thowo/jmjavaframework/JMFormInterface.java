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
    void actionAfterAdded(JMRow rowAdded);
    void actionAfterDeleted(JMRow row, boolean deleted);
    void actionAfterSaved(String updateQuery,boolean saved);
    void actionAfterEdited(JMRow rowEdited);
    void actionAfterPrinted(JMRow rowPrinted);
    void actionAfterRefreshed(JMRow rowRefreshed);
    void actionAfterViewed(JMRow rowViewed);
    void actionAfterMovedNext(JMRow nextRow);
    void actionAfterMovedPrev(JMRow prevRow);
    void actionAfterMovedFirst(JMRow firstRow);
    void actionAfterMovedLast(JMRow lastRow);
    void actionAfterMovedtoRecord(JMRow currentRow);
    void actionAfterCanceled(JMRow rowCanceled, boolean canceled);
}
