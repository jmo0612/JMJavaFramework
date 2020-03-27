/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework;

/**
 *
 * @author jimi
 */
public interface JMAsyncListener {
    void onJMStart(String id);
    void onJMProcess(String id);
    void onJMComplete(Object result,String id);
    void onJMError(String errorMessage,String id);
}
