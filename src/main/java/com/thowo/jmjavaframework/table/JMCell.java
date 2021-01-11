/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.db.JMResultSetStyle;

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
    
    public JMCell(Integer column, Boolean hidden){
        setProp(column, null, true);
    }
    public void addDataContainer(JMDataContainer dc){
        this.data=dc;
    }
    public JMCell(Integer column, JMDataContainer data){
        setProp(column, data, true);
    }
    public JMCell(Integer column, JMDataContainer data, Boolean hidden){
        setProp(column, data, hidden);
    }
    public JMCell(){}
    private void setProp(Integer column, JMDataContainer data, Boolean hidden){
        this.colNum=column;
        this.data=data;
        this.hidden=hidden;
        this.data.hideInterfaces(hidden);
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
    public Object getValue(){
        return this.data.getValue();
    }
    public String getText(){
        return this.data.getText();
    }
    public JMDate getValueDate(){
        return (JMDate)this.data.getValue();
    }
    public String getValueString(){
        return String.valueOf(this.data.getValue());
    }
    public String getDBValue(){
        return this.getDataContainer().getValueDB();
    }
    public Integer getValueInteger(){
        return Integer.valueOf(String.valueOf(this.data.getValue()));
    }
    public Double getValueDouble(){
        return Double.valueOf(String.valueOf(this.data.getValue()));
    }
    public Boolean getValueBoolean(){
        return Boolean.valueOf(String.valueOf(this.data.getValue()));
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
    public String getFormatType(){
        int ind=this.data.getColIndex();
        JMResultSetStyle style=this.row.getTable().getStyle();
        return style.getFormat(ind);
    }
}
