/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;

/**
 *
 * @author jimi
 */
public class JMCell {
    private Integer colNum;
    private JMRow row;
    private JMCell next;
    private JMCell prev;
    
    private JMDataContainer data;
    private Boolean hidden;
    
    public JMCell(Integer column, JMDataContainer data){
        setProp(column, data, true);
    }
    public JMCell(Integer column, JMDataContainer data, Boolean hidden){
        setProp(column, data, hidden);
    }
    private void setProp(Integer column, JMDataContainer data, Boolean hidden){
        this.colNum=column;
        this.data=data;
        this.hidden=hidden;
        this.data.setHidden(hidden);
    }
    public void setRow(JMRow row){
        if(row==null)return;
        this.row=row;
    }
    public void setPrev(JMCell prev){
        if(prev==null)return;
        this.prev=prev;
        prev.next=this;
        
    }
    public void setNext(JMCell next){
        if(next==null)return;
        this.next=next;
        next.prev=this;
    }
    public Boolean getVisible(){
        return !this.hidden;
    }
    public Object getValueObject(){
        return this.data;
    }
    public String getText(){
        return this.data.getText();
    }
    public String getValueString(){
        return this.data.getValueAsString();
    }
    public Integer getValueInteger(){
        return this.data.getValueAsInteger();
    }
    public Double getValueDouble(){
        return this.data.getValueAsDouble();
    }
    public Boolean getValueBoolean(){
        return this.data.getValueAsBoolean();
    }
    public String getFieldName(){
        return this.data.getFieldName();
    }
    public JMDataContainer getDataContainer(){
        return this.data;
    }
    
}
