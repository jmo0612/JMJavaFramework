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
public class JMLanguage {
    private String langId;
    private String lang;
    private Boolean isDef;
    
    public JMLanguage(String langId, String lang){
        this.langId=langId;
        this.lang=lang;
        this.isDef=false;
    }
    
    public JMLanguage(String langId, String lang, Boolean isDef){
        this.langId=langId;
        this.lang=lang;
        this.isDef=isDef;
    }
    
    public void setDefault(Boolean def){
        this.isDef=def;
    }
    
    public Boolean getDefault(){
        return this.isDef;
    }
    
    public String getLangId(){
        return this.langId;
    }
    
}
