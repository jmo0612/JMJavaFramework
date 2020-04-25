/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMFunctions;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jimi
 */
public class JMRow {
    private JMTable table;
    private List<JMTable> details;
    private JMRow next;
    private JMRow prev;
    
    private List<JMCell> cells;
    private Boolean hidden;
    private Boolean excluded;
    
    public JMRow(){
        this.setProp(false);
    }
    public JMRow(Boolean hidden){
        this.setProp(hidden);
    }
    private void setProp(Boolean hidden){
        cells=new ArrayList();
        this.hidden=hidden;
        this.excluded=false;
    }
    public JMCell addCell(JMDataContainer data){
        return addCell(data, false);
    }
    public JMCell addCell(JMDataContainer data, Boolean hidden){
        Integer column=this.cells.size();
        JMCell ret=new JMCell(column, data, hidden);
        if(column>0){
            ret.setPrev(this.cells.get(column-1));
            this.cells.get(column-1).setNext(ret);
        }
        ret.setRow(this);
        this.cells.add(ret);
        return ret;
    }
    public void setPrev(JMRow prev){
        if(prev==null)return;
        this.prev=prev;
        prev.next=this;
    }
    public void setNext(JMRow next){
        if(next==null)return;
        this.next=next;
        next.prev=this;
    }
    public Boolean getVisible(){
        return !this.hidden;
    }
    public void setTable(JMTable table){
        this.table=table;
    }
    public JMRow getNext(){
        return this.next;
    }
    public JMRow getPrev(){
        return this.prev;
    }
    public Boolean getExcluded(){
        return this.excluded;
    }
    public List<JMCell> getCells(){
        return this.cells;
    }
}
