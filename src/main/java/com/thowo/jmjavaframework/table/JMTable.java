/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.db.JMResultSet;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jimi
 */
public class JMTable {
    //INTEGER SIZE OF ROWS ONLY
    //AUTOMATIC FIRST ROW
    private List<Integer> keyCols=new ArrayList();
    private List<JMCell> masterCells=new ArrayList();
    private JMRow currentRow; 
    
    public static JMTable create(JMResultSet rs, List<Boolean> colsVisibility, List<String> colsDataType, List<Object[]> colsFormatParams){
        return new JMTable(rs, colsVisibility, colsDataType, colsFormatParams);
    }
    public static JMTable create(JMResultSet rs, List<String> colsDataType, List<Object[]> colsFormatParams){
        return new JMTable(rs, colsDataType, colsFormatParams);
    }
    public static JMTable create(JMResultSet rs, JMResultSetStyle style){
        return new JMTable(rs, style);
    }
    public static JMTable create(JMResultSet rs){
        return new JMTable(rs);
    }
    
    public JMTable(JMResultSet rs, List<Boolean> colsVisibility, List<String> colsDataType, List<Object[]> colsFormatParams){
        this.setProp(rs, colsVisibility, colsDataType, colsFormatParams);
    }
    public JMTable(JMResultSet rs, List<String> colsDataType, List<Object[]> colsFormatParams){
        List<Boolean> colsVisibility=new ArrayList();
        for(String s:colsDataType){
            colsVisibility.add(true);
        }
        this.setProp(rs, colsVisibility, colsDataType, colsFormatParams);
    }
    public JMTable(JMResultSet rs, JMResultSetStyle style){
        this.setProp(rs, style);
    }
    public JMTable(JMResultSet rs){
        JMResultSetStyle style=new JMResultSetStyle(rs.getSQLResultSet());
        this.setProp(rs, style);
    }
    public JMTable(){
        
    }
    
    private void setProp(JMResultSet rs, JMResultSetStyle style){
        List<Boolean> colsVisibility=style.getVisibles();
        List<String> colsDataType=style.getDataTypes();
        List<Object[]> colsFormatParams=style.getListParams();
        this.setProp(rs, colsVisibility, colsDataType, colsFormatParams);
    }
    
    private void setProp(JMResultSet rs, List<Boolean> colsVisibility, List<String> colsDataType, List<Object[]> colsFormatParams){
        if(rs!=null){
            if(colsDataType.size()!=colsVisibility.size())JMFunctions.trace("different size between colsDataType and colsVisibility");
            if(colsDataType.size()!=colsFormatParams.size())JMFunctions.trace("different size between colsDataType and colsFormatParams");
            if(colsDataType.size()!=rs.getColCount())JMFunctions.trace("different size between colsDataType and resultSet column count");
            rs.first();
            Integer i=0;
            do{
                this.currentRow=this.addRow();
                for(int j=0;j<colsDataType.size();j++){
                    String dt="";
                    Boolean hidden=false;
                    Object[] prms=null;
                    if(j<colsDataType.size())dt=colsDataType.get(j);
                    if(j<colsVisibility.size())hidden=!colsVisibility.get(j);
                    if(j<colsFormatParams.size())prms=colsFormatParams.get(j);
                    JMDataContainer dc=new JMDataContainer(rs,j,dt,prms);
                    this.currentRow.addCell(dc,hidden);
                }
                JMFunctions.trace(this.currentRow.getCells().get(3).getText());
                //JMFunctions.trace(rs.getString(1));
            }while(rs.next());
            this.firstRow();
            
        }
    }
    public boolean isEmpty(){
        return this.currentRow==null;
    }
    public JMRow addRow(){
        return this.addRow(false);
    }
    public JMRow addRow(Boolean hidden){
        JMRow ret=new JMRow(hidden);
        if(this.currentRow!=null){
            ret.setPrev(this.currentRow);
            this.currentRow.setNext(ret);
        }
        ret.setTable(this);
        this.currentRow=ret;
        return ret;
    }
    public Integer getRowCount(){
        if(this.currentRow==null)return 0;
        Integer ret=1;
        this.firstRow();
        while(this.nextRow()!=null){
            if(ret==Integer.MAX_VALUE){
                JMFunctions.trace("LIMIT ROW COUNT REACHED");
                return ret;
            }
            ret++;
        }
        return ret;
    }
    public JMRow getCurrentRow(){
        if(this.currentRow==null)return null;
        return this.currentRow;
    }
    public JMRow firstRow(){
        if(this.currentRow==null)return null;
        while(this.currentRow.getPrev()!=null){
            this.currentRow=this.currentRow.getPrev();
        }
        return this.currentRow;
    }
    public JMRow nextRow(){
        JMRow ret=this.currentRow.getNext();
        if(this.currentRow==null) return null;
        if(ret!=null)this.currentRow=ret;
        return ret;
    }
    public JMRow prevRow(){
        JMRow ret=this.currentRow.getPrev();
        if(this.currentRow==null) return null;
        if(ret!=null)this.currentRow=ret;
        return ret;
    }
    public JMRow lastRow(){
        if(this.currentRow==null)return null;
        while(this.currentRow.getNext()!=null){
            this.currentRow=this.currentRow.getNext();
        }
        return this.currentRow;
    }
    public Object[] getTableData(){
        Object[] ret={};
        List<Object> data=new ArrayList();
        List<JMCell> cells=this.currentRow.getCells();
        for(JMCell cell:cells){
            data.add(cell.getText());
        }
        ret=data.toArray();
        return ret;
    }
}
