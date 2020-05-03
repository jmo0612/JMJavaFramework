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
        //if(prev==null)return;
        this.prev=prev;
        if(prev!=null)prev.next=this;
    }
    public void setNext(JMRow next){
        //if(next==null)return;
        this.next=next;
        if(next!=null)next.prev=this;
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
    public void displayInterface(){
        for(JMCell cell:this.cells){
            cell.getDataContainer().refresh();
        }
    }
    public JMTable getTable(){
        return this.table;
    }
    public void setDetails(List<JMTable> tables){
        this.details=tables;
    }
    public List<JMTable> getDetails(){
        return this.details;
    }
    public void addDetail(JMTable detail){
        if(detail==null)return;
        boolean exist=false;
        for(JMTable d:this.details){
            if(d==detail){
                exist=true;
                break;
            }
        }
        if(!exist)this.details.add(table);
    }
    public String getUpdateSQL(){
        String ret="REPLACE INTO ";
        String t=this.table.getName();
        String f="";
        String v="";
        for(JMCell cell:this.cells){
            if(!cell.excludedFromUpdate()){
                f+=cell.getFieldName()+",";
                v+=cell.getQueryPrefix()+cell.getValueString()+cell.getQuerySuffix()+",";
            }
        }
        if(f.lastIndexOf(",")<0)return "";
        if(v.lastIndexOf(",")<0)return "";
        f=f.substring(0, f.lastIndexOf(","));
        v=v.substring(0, v.lastIndexOf(","));
        ret+=t+"("+f+") VALUES("+v+")";
        return ret;
    }
    public String getDeleteSQL(){
        String ret="DELETE FROM ";
        String t=this.table.getName();
        List<Integer> keysInd=this.table.getKeyColumns();
        if(keysInd==null)return "";
        String w=" WHERE ";
        for(Integer i:keysInd){
            w+=this.cells.get(i).getFieldName()+"="+this.cells.get(i).getQueryPrefix()+this.cells.get(i).getValueString()+this.cells.get(i).getQuerySuffix()+" AND";
        }
        w=w.substring(0, w.lastIndexOf(" AND"));
        ret+=t+w;
        return ret;
    }
    public void excludeColumnsFromUpdate(List<Integer> column){
        for(Integer i:column){
            this.cells.get(i).setExcludeFromUpdate(true);
        }
    }
    public void setCells(List<JMCell> cells){
        this.cells=cells;
    }
}
