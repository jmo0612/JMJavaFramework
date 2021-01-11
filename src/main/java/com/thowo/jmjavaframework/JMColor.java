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
public class JMColor {
    public static final int black=0;
    public static final int blue=1;
    public static final int cyan=2;
    public static final int darkGray=3;
    public static final int gray=4;
    public static final int green=5;
    public static final int lightGray=6;
    public static final int magenta=7;
    public static final int orange=8;
    public static final int pink=9;
    public static final int red=10;
    public static final int white=11;
    public static final int yellow=12;
    
    public static final int BLACK=1000;
    public static final int BLUE=1001;
    public static final int CYAN=1002;
    public static final int DARK_GRAY=1003;
    public static final int GRAY=1004;
    public static final int GREEN=1005;
    public static final int LIGHT_GRAY=1006;
    public static final int MAGENTA=1007;
    public static final int ORANGE=1008;
    public static final int PINK=1009;
    public static final int RED=1010;
    public static final int WHITE=1011;
    public static final int YELLOW=1012;
    
    
    private int cA=255;
    private int cR=0;
    private int cG=0;
    private int cB=0;
    private String cStr="#000000";
    private int index=-1;
    
    public static JMColor setIndex(int index){
        JMColor ret=new JMColor();
        ret.index=index;
        return ret;
    }
    public JMColor setARGB(int a, int r, int g, int b){
        this.index=-1;
        this.cA=a;
        this.cR=r;
        this.cG=g;
        this.cB=b;
        return this;
    }
    public static JMColor decode(String color){
        JMColor ret=new JMColor();
        ret.index=-2;
        ret.cStr=color;
        return ret;
    }
    public int getIndex(){
        return this.index;
    }
    public int getAlpha(){
        return this.cA;
    }
    public int getRed(){
        return this.cR;
    }
    public int getBlue(){
        return this.cB;
    }
    public int getGreen(){
        return this.cG;
    }
    public String getString(){
        return this.cStr;
    }
}
