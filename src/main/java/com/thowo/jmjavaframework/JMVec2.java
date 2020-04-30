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
public class JMVec2 {
    private double x=0;
    private double y=0;
    
    public static JMVec2 create(double x, double y){
        return new JMVec2(x, y);
    }
    public static JMVec2 create(int x, int y){
        return new JMVec2(x, y);
    }
    
    public double getDoubleX(){
        return this.x;
    }
    public long getLongX(){
        return Math.round(this.x);
    }
    public int getIntX(){
        return (int) Math.round(this.x);
    }
    public double getDoubleY(){
        return this.y;
    }
    public long getLongY(){
        return Math.round(this.y);
    }
    public int getIntY(){
        return (int) Math.round(this.y);
    }
    
    public JMVec2(double x, double y){
        this.x=x;
        this.y=y;
    }
    public JMVec2(int x, int y){
        this.x=x;
        this.y=y;
    }
    
    public void trace(){
        JMFunctions.trace(this.x+","+this.y);
    }
    
}
