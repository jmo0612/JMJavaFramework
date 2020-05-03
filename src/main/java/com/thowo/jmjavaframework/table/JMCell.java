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
    private String queryPrefix="'";
    private String querySuffix="'";
    private Boolean excludeFromUpdate=false;
    
    public JMCell(Integer column, JMDataContainer data){
        setProp(column, data, true);
    }
    public JMCell(Integer column, JMDataContainer data, Boolean hidden){
        setProp(column, data, hidden);
    }
    public JMCell(){}
    public JMCell copy(){
        JMCell ret=new JMCell();
        ret.colNum=this.colNum;
        ret.data=this.data.copy();
        ret.excludeFromUpdate=this.excludeFromUpdate;
        ret.hidden=this.hidden;
        ret.queryPrefix=this.queryPrefix;
        ret.querySuffix=this.querySuffix;
        ret.row=this.row;
        return ret;
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
    public JMRow getRow(){
        return this.row;
    }
    public JMCell getNext(){
        return this.next;
    }
    public JMCell getPrev(){
        return this.prev;
    }
    public Integer getColNum(){
        return this.colNum;
    }
    public void setQueryPrefix(String prefix){
        this.queryPrefix=prefix;
    }
    public String getQueryPrefix(){
        return this.queryPrefix;
    }
    public void setQuerySuffix(String suffix){
        this.querySuffix=suffix;
    }
    public String getQuerySuffix(){
        return this.querySuffix;
    }
    public void setExcludeFromUpdate(Boolean exclude){
        this.excludeFromUpdate=exclude;
    }
    public boolean excludedFromUpdate(){
        return this.excludeFromUpdate;
    }
}
