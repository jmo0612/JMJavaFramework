/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework;

import java.io.File;

/**
 *
 * @author jimi
 */
public interface JMUIListener {
    void trace(String message);
    //void messageBox(String title, String message, File icon);
    void messageBox(String message);
    //void confirmBox(String message);
}
