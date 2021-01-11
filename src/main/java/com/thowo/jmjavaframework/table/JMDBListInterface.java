/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

/**
 *
 * @author jimi
 */
public interface JMDBListInterface {
    void init(JMTable table);
    void setOnSelected(Runnable selectedAction);
    void setOnMoved(Runnable movedAction);
    void setOnViewedOption(Runnable viewedOptionAction);
}
