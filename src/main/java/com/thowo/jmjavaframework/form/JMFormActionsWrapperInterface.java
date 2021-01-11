/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.form;

/**
 *
 * @author jimi
 */
public interface JMFormActionsWrapperInterface {
    void formOnSaved(JMFormTableList me,String id);
    void formOnEdited(JMFormTableList me,String id);
}
