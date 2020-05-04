/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.JMInputInterface;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.db.JMResultSet;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private JMRow firstRow;
    private JMRow lastRow;
    private String name;
    
    public static JMTable create(JMResultSet rs, List<Boolean> colsVisibility, List<String> colsDataType, List<Object[]> colsFormatParams){
        return new JMTable(rs, colsVisibility, colsDataType, colsFormatParams);
    }
    public static JMTable create(JMResultSet rs, List<String> colsDataType, List<Object[]> colsFormatParams){
        return new JMTable(rs, colsDataType, colsFormatParams);
    }
    public static JMTable create(JMResultSet rs, JMResultSetStyle style){
        return new JMTable(rs, style);
    }
    public static JMTable create(JMResultSet rs,List<JMDataContainer> dataContainers, JMResultSetStyle style){
        return new JMTable(rs,dataContainers, style);
    }
    public static JMTable create(JMResultSet rs,List<JMDataContainer> dataContainers){
        return new JMTable(rs,dataContainers);
    }
    public static JMTable create(JMResultSet rs){
        return new JMTable(rs);
    }
    
    public JMTable(JMResultSet rs, List<Boolean> colsVisibility, List<String> colsDataType, List<Object[]> colsFormatParams){
        this.setProp(rs, null,colsVisibility, colsDataType, colsFormatParams);
    }
    public JMTable(JMResultSet rs, List<String> colsDataType, List<Object[]> colsFormatParams){
        List<Boolean> colsVisibility=new ArrayList();
        for(String s:colsDataType){
            colsVisibility.add(true);
        }
        this.setProp(rs, null,colsVisibility, colsDataType, colsFormatParams);
    }
    public JMTable(JMResultSet rs,List<JMDataContainer> dataContainers, JMResultSetStyle style){
        this.setProp(rs,dataContainers, style);
    }
    public JMTable(JMResultSet rs,List<JMDataContainer> dataContainers){
        JMResultSetStyle style=new JMResultSetStyle(rs.getSQLResultSet());
        this.setProp(rs,dataContainers, style);
    }
    public JMTable(JMResultSet rs, JMResultSetStyle style){
        this.setProp(rs,null, style);
    }
    public JMTable(JMResultSet rs){
        JMResultSetStyle style=new JMResultSetStyle(rs.getSQLResultSet());
        this.setProp(rs,null, style);
    }
    public JMTable(){
        
    }
    
    private void setProp(JMResultSet rs,List<JMDataContainer> dataContainers , JMResultSetStyle style){
        List<Boolean> colsVisibility=style.getVisibles();
        List<String> colsDataType=style.getDataTypes();
        List<Object[]> colsFormatParams=style.getListParams();
        this.setProp(rs,dataContainers, colsVisibility, colsDataType, colsFormatParams);
    }
    
    private void setProp(JMResultSet rs,List<JMDataContainer> dataContainers, List<Boolean> colsVisibility, List<String> colsDataType, List<Object[]> colsFormatParams){
        if(rs!=null){
            if(colsDataType.size()!=colsVisibility.size())JMFunctions.trace("different size between colsDataType and colsVisibility");
            if(colsDataType.size()!=colsFormatParams.size())JMFunctions.trace("different size between colsDataType and colsFormatParams");
            if(colsDataType.size()!=rs.getColCount())JMFunctions.trace("different size between colsDataType and resultSet column count");
            rs.first();
            Integer i=0;
            boolean first=true;
            do{
                this.currentRow=this.addRow();
                if(first){
                    this.firstRow=this.currentRow;
                    first=false;
                }
                for(int j=0;j<colsDataType.size();j++){
                    String dt="";
                    Boolean hidden=false;
                    Object[] prms=null;
                    JMDataContainer dc=null;
                    if(j<colsDataType.size())dt=colsDataType.get(j);
                    if(j<colsVisibility.size())hidden=!colsVisibility.get(j);
                    if(j<colsFormatParams.size())prms=colsFormatParams.get(j);
                    if(dataContainers==null){
                        dc=new JMDataContainer(rs,j,dt,prms);
                    }else{
                        if(j<dataContainers.size())dc=dataContainers.get(j);
                    }
                    this.currentRow.addCell(dc,hidden);
                }
                //JMFunctions.trace(this.currentRow.getCells().get(3).getText());
                //JMFunctions.trace(rs.getString(1));
            }while(rs.next());
            this.firstRow(true);
            
        }
    }
    public boolean isEmpty(){
        return this.currentRow==null;
    }
    private JMRow addRow(){
        return this.addRow(false);
    }
    private JMRow addRow(Boolean hidden){
        this.lastRow(true);
        JMRow ret=new JMRow(hidden);
        if(this.currentRow!=null){
            ret.setPrev(this.currentRow);
            this.currentRow.setNext(ret);
        }
        ret.setTable(this);
        this.currentRow=ret;
        this.lastRow=this.currentRow;
        return ret;
    }
    public Integer getRowCount(){
        if(this.currentRow==null)return 0;
        JMRow b=this.currentRow;
        Integer ret=1;
        this.firstRow(false);
        while(this.nextRow(false)!=null){
            if(ret==Integer.MAX_VALUE){
                JMFunctions.trace("LIMIT ROW COUNT REACHED");
                return ret;
            }
            ret++;
        }
        this.currentRow=b;
        return ret;
    }
    public JMRow getCurrentRow(){
        if(this.currentRow==null)return null;
        return this.currentRow;
    }
    public JMRow firstRow(boolean updateContainer){
        if(this.currentRow==null)return null;
        this.currentRow=this.firstRow;
        if(updateContainer)this.currentRow.displayInterface();
        return this.currentRow;
    }
    public JMRow nextRow(boolean updateContainer){
        JMRow ret=this.currentRow.getNext();
        if(this.currentRow==null) return null;
        if(ret!=null)this.currentRow=ret;
        if(updateContainer)this.currentRow.displayInterface();
        return ret;
    }
    public JMRow prevRow(boolean updateContainer){
        JMRow ret=this.currentRow.getPrev();
        if(this.currentRow==null) return null;
        if(ret!=null)this.currentRow=ret;
        if(updateContainer)this.currentRow.displayInterface();
        return ret;
    }
    public JMRow lastRow(boolean updateContainer){
        if(this.currentRow==null)return null;
        this.currentRow=this.lastRow;
        if(updateContainer)this.currentRow.displayInterface();
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
    public void setFormInterface(JMInputInterface component, int column){
        JMRow b=this.currentRow;
        this.firstRow(false);
        do{
            List<JMCell> cells=this.currentRow.getCells();
            cells.get(column).getDataContainer().setInterface(component, !cells.get(column).getVisible());
        }while(this.nextRow(false)!=null);
        this.currentRow=b;
    }
    public JMRow findByKeys(List<String> keyValues){
        if(this.keyCols==null)return null;
        if(this.keyCols.size()!=keyValues.size())return null;
        JMRow c=this.currentRow;
        JMRow ret=null;
        this.firstRow(false);
        boolean match=true;
        do{
            match=true;
            for(int i=0;i<keyValues.size();i++){
                List<JMCell> cells=this.currentRow.getCells();
                if(!cells.get(this.keyCols.get(i)).getValueString().equals(keyValues.get(i))){
                    match=false;
                    break;
                }
            }
            if(match){
                ret=this.currentRow;
                break;
            }
        }while(this.nextRow(false)!=null);
        if(!match)this.currentRow=c;
        return ret;
    }
    public void gotoRow(JMRow row,boolean updateContainer){
        this.currentRow=row;
        if(updateContainer)row.displayInterface();
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    public void addKeyColumn(int column){
        if(this.keyCols==null){
            this.keyCols=new ArrayList();
        }
        boolean exist=false;
        for(int i=0;i<this.keyCols.size();i++){
            if(i==column){
                exist=true;
                break;
            }
        }
        if(!exist)this.keyCols.add(column);
    }
    public void setMasterCells(List<JMCell> masterCells){
        this.masterCells=masterCells;
    }
    public List<JMCell> getMasterCells(){
        return this.masterCells;
    }
    public void addMasterCell(JMCell cell){
        if(cell==null)return;
        boolean exist=false;
        for(JMCell c:this.masterCells){
            if(c==cell){
                exist=true;
                break;
            }
        }
        if(!exist)this.masterCells.add(cell);
    }
    public List<Integer> getKeyColumns(){
        return this.keyCols;
    }
    public List<String> getKeyValues(){
        List<String> ret=new ArrayList();
        for(JMCell cell:this.currentRow.getCells()){
            ret.add(cell.getValueString());
        }
        return ret;
    }
    public JMRow addNewRow(boolean updateContainer){
        JMRow ret=this.addRow(false);
        ret.setTable(this);
        ret.setNext(null);
        ret.setPrev(this.lastRow);
        if(this.lastRow!=null)this.lastRow.setNext(ret);
        this.lastRow=ret;
        ret.setHidden(this.currentRow.getHidden());
        ret.setExcluded(this.currentRow.getExcluded());
        for(JMCell cell:this.currentRow.getCells()){
            JMDataContainer dc=new JMDataContainer(cell.getDataContainer().getResultSet(),cell.getDataContainer().getRsCol(),cell.getDataContainer().getDataType(),cell.getDataContainer().getParams());
            ret.addCell(dc,cell.getDataContainer().getFiHidden());
        }
        this.currentRow=ret;
        return ret;
    }
    public JMRow addNewRowBU(boolean updateContainer){
        JMRow cur=this.currentRow;
        JMRow ret=this.addRow();
        this.currentRow=cur;
        List<JMCell> cells=new ArrayList();
        for(int i=0;i<this.currentRow.getCells().size();i++){
            cells.add(this.currentRow.getCells().get(i).copy());
        }
        ret.setCells(cells);
        for(int i=0;i<cells.size();i++){
            JMCell cell=cells.get(i);
            cell.setRow(ret);
            if(i-1>=0){
                cell.setPrev(cells.get(i-1));
                cells.get(i-1).setNext(cell);
            }
            if(i+1<cells.size()){
                cell.setNext(cells.get(i+1));
                cells.get(i+1).setPrev(cell);
            }
        }
        for(JMCell cell:cells){
            try {
                cell.getDataContainer().setValue("");
            } catch (ParseException ex) {
                JMDate d=new JMDate();
                try {
                    cell.getDataContainer().setValue(d.dateTimeDB());
                } catch (ParseException ex1) {
                    try {
                        cell.getDataContainer().setValue(d.dateDB());
                    } catch (ParseException ex2) {
                        Logger.getLogger(JMTable.class.getName()).log(Level.SEVERE, null, ex2);
                    } catch (NumberFormatException ex2) {
                        Logger.getLogger(JMTable.class.getName()).log(Level.SEVERE, null, ex2);
                    }
                } catch (NumberFormatException ex1) {
                    Logger.getLogger(JMTable.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (NumberFormatException ex) {
                try {
                    cell.getDataContainer().setValue("0");
                } catch (ParseException ex1) {
                    Logger.getLogger(JMTable.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (NumberFormatException ex1) {
                    try {
                        cell.getDataContainer().setValue("0.0");
                    } catch (ParseException ex2) {
                        Logger.getLogger(JMTable.class.getName()).log(Level.SEVERE, null, ex2);
                    } catch (NumberFormatException ex2) {
                        Logger.getLogger(JMTable.class.getName()).log(Level.SEVERE, null, ex2);
                    }
                }
            }
        }
        this.currentRow=ret;
        this.lastRow=this.currentRow;
        if(updateContainer)this.currentRow.displayInterface();
        return ret;
    }
    public void excludeColumnsFromUpdate(List<Integer> column){
        JMRow tmp=this.currentRow;
        this.firstRow(false);
        do{
            this.currentRow.excludeColumnsFromUpdate(column);
        }while(this.nextRow(false)!=null);
        this.currentRow=tmp;
    }
    public JMRow deleteRow(JMRow row, boolean updateContainer){
        JMRow p=row.getPrev();
        JMRow n=row.getNext();
        if(p!=null)p.setNext(n);
        if(n!=null)n.setPrev(p);
        if(p!=null)this.currentRow=p;
        else this.currentRow=n;
        if(this.currentRow.getPrev()==null)this.firstRow=this.currentRow;
        if(this.currentRow.getNext()==null)this.lastRow=this.currentRow;
        if(updateContainer)this.currentRow.displayInterface();
        return row;
    }
    public boolean isFirstRecord(){
        return this.currentRow.getPrev()==null;
    }
    public boolean isLastRecord(){
        return this.currentRow.getNext()==null;
    }
    public void setKeyColumns(List<Integer> keyColumns){
        this.keyCols=keyColumns;
    }
}
