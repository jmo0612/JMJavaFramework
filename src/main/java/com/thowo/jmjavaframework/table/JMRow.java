/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMFunctions;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Integer rowNum;
    
    
    public Integer getRowNum(){
        return this.rowNum;
    }
    public void setRowNum(Integer rowNum){
        this.rowNum=rowNum;
    }
    public JMRow(Integer rowNum){
        this.setProp(rowNum);
    }
    private void setProp(Integer rowNum){
        cells=new ArrayList();
        this.rowNum=rowNum;
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
    public void setTable(JMTable table){
        this.table=table;
    }
    public JMRow getNext(){
        return this.next;
    }
    public JMRow getPrev(){
        return this.prev;
    }
    public List<JMCell> getCells(){
        return this.cells;
    }
    public void displayInterface(boolean defaultValue){
        for(JMCell cell:this.cells){
            cell.getDataContainer().refreshInterfaces(this.table.getStyle(),null,true,defaultValue);
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
                v+=cell.getQueryPrefix()+cell.getDBValue()+cell.getQuerySuffix()+",";
            }
        }
        if(f.lastIndexOf(",")<0)return "";
        if(v.lastIndexOf(",")<0)return "";
        f=f.substring(0, f.lastIndexOf(","));
        v=v.substring(0, v.lastIndexOf(","));
        ret+=t+"("+f+") VALUES("+v+")";
        JMFunctions.trace(ret);
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
    public List<JMDataContainer> getDataContainers(){
        List<JMDataContainer> data=new ArrayList();
        List<JMCell> cells=this.getCells();
        for(JMCell cell:cells){
            data.add(cell.getDataContainer());
        }
        return data;
    }
    public void setValueFromString(int column, String value){
        this.cells.get(column).getDataContainer().refreshInterfaces(this.getTable().getStyle(), value, true, true);
    }
    public boolean isEdited(){
        for(JMCell cell:this.cells){
            if(cell.getDataContainer().isEdited())return true;
        }
        return false;
    }
    public boolean isValuesValid(){
        for(JMDataContainer dc:this.getDataContainers()){
            if(dc.isError())return false;
            
        }
        return true;
    }
}
