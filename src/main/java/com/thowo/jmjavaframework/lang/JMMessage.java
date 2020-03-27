/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.lang;

/**
 *
 * @author jimi
 */
public class JMMessage {
    private String langId;
    private String msgId;
    private String msg;
    
    public JMMessage(String langId, String msgId, String msg){
        this.langId=langId;
        this.msgId=msgId;
        this.msg=msg;
    }
    
    public String getLangId(){
        return this.langId;
    }
    public String getMsgId(){
        return this.msgId;
    }
    public String getMsg(){
        return this.msg;
    }
}
