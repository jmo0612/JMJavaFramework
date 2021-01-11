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
public interface JMButtonInterface {
    JMButtonInterface setFontColor(JMColor color);
    JMButtonInterface increaseFontSize(int inc);
    JMButtonInterface decreaseFontSize(int dec);
    JMButtonInterface setFont(String font);
    JMButtonInterface setText(String text);
    void setLocked(boolean locked);
    void setVisible(boolean visible);
    boolean isLocked();
    void setAction(Runnable action);
    void addAction(Runnable action);
    
}
