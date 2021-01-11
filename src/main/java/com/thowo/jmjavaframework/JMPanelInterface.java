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
public interface JMPanelInterface {
    void setOpaque(boolean opaque);
    void addComponent(Object component, Object... params);
    void clear();
}
