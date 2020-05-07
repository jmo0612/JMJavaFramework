/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.JMFormInterface;
import com.thowo.jmjavaframework.JMInputInterface;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.db.JMResultSet;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import com.thowo.jmjavaframework.lang.JMConstMessage;
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
    private JMResultSetStyle style;
    private List<JMFormInterface> interfaces;
    private List<String> labelTitles;
    private JMRow edited=null;
    
    public JMResultSetStyle getStyle(){
        return this.style;
    }
    public static JMTable create(JMResultSet rs){
        return new JMTable(rs);
    }
    public static JMTable create(JMResultSet rs, JMResultSetStyle style){
        return new JMTable(rs,style);
    }
    
    public JMTable(JMResultSet rs){
        JMResultSetStyle style=new JMResultSetStyle(rs);
        this.setProp(rs, style);
    }
    public JMTable(JMResultSet rs, JMResultSetStyle style){
        this.setProp(rs, style);
    }
    private void setProp(JMResultSet rs, JMResultSetStyle style){
        this.style=style;
        List<Boolean> colsVisibility=style.getVisibles();
        List<String> colsFormat=style.getFormats();
        List<Object[]> colsFormatParams=style.getListParams();
        List<String> fieldNames=style.getFieldNames();
        List<String> labelTitles=style.getLabelTitles();
        if(rs!=null){
            if(colsFormat.size()!=colsVisibility.size())JMFunctions.trace("different size between colsDataType and colsVisibility");
            if(colsFormat.size()!=colsFormatParams.size())JMFunctions.trace("different size between colsDataType and colsFormatParams");
            if(colsFormat.size()!=rs.getColCount())JMFunctions.trace("different size between colsDataType and resultSet column count");
            rs.first();
            Integer i=0;
            boolean first=true;
            do{
                this.currentRow=this.addRow();
                if(first){
                    this.firstRow=this.currentRow;
                    first=false;
                }
                for(int j=0;j<colsFormat.size();j++){
                    String dt="";
                    Boolean hidden=false;
                    Object[] prms=null;
                    JMDataContainer dc=null;
                    if(j<colsFormat.size())dt=colsFormat.get(j);
                    if(j<colsVisibility.size())hidden=!colsVisibility.get(j);
                    if(j<colsFormatParams.size())prms=colsFormatParams.get(j);
                    
                    if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_BOOLEAN))dc=JMDataContainer.create((Object)rs.getBool(j),j,fieldNames.get(j));
                    else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_DATE))dc=JMDataContainer.create((Object)rs.getDate(j, false),j,fieldNames.get(j));
                    else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_DOUBLE))dc=JMDataContainer.create((Object)rs.getDouble(j),j,fieldNames.get(j));
                    else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_INTEGER))dc=JMDataContainer.create((Object)rs.getInt(j),j,fieldNames.get(j));
                    else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_STRING))dc=JMDataContainer.create((Object)rs.getString(j),j,fieldNames.get(j));
                    else dc=JMDataContainer.create((Object)rs.getBlob(j),j,fieldNames.get(j));
                    
                    JMCell cell=this.currentRow.addCell(dc,hidden);
                    dc.setCell(cell);
                    dc.refreshInterfaces(style,null,true,false);
                }
                //JMFunctions.trace(this.currentRow.getCells().get(3).getText());
                //JMFunctions.trace(rs.getString(1));
            }while(rs.next());
            this.lastRow=this.currentRow;
            this.firstRow(true);
            this.labelTitles=labelTitles;
        }
    }
    public boolean isEmpty(){
        return this.currentRow==null;
    }
    public Integer getRowCount(){
        if(this.lastRow==null)return 0;
        return this.lastRow.getRowNum()+1;
    }
    public JMRow getCurrentRow(){
        if(this.currentRow==null)return null;
        return this.currentRow;
    }
    public JMRow firstRow(boolean updateContainer){
        if(this.currentRow==null)return null;
        this.currentRow=this.firstRow;
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.actionFirst(this.currentRow);
                }
            }
        }
        return this.currentRow;
    }
    public JMRow nextRow(boolean updateContainer){
        JMRow ret=this.currentRow.getNext();
        if(this.currentRow==null) return null;
        if(ret!=null)this.currentRow=ret;
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.actionNext(this.currentRow);
                }
            }
        }
        return ret;
    }
    public JMRow prevRow(boolean updateContainer){
        JMRow ret=this.currentRow.getPrev();
        if(this.currentRow==null) return null;
        if(ret!=null)this.currentRow=ret;
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.actionPrev(this.currentRow);
                }
            }
        }
        return ret;
    }
    public JMRow lastRow(boolean updateContainer){
        if(this.currentRow==null)return null;
        this.currentRow=this.lastRow;
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.actionLast(this.currentRow);
                }
            }
        }
        return this.currentRow;
    }
    public JMRow gotoRow(Integer rowNum,boolean updateContainer){
        JMRow r=this.firstRow;
        while(r!=null){
            if(r.getRowNum()==rowNum){
                this.currentRow=r;
                break;
            }
            r=r.getNext();
        }
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.gotoRecord(this.currentRow);
                }
            }
        }
        return this.currentRow;
    }
    public void gotoRow(JMRow row,boolean updateContainer){
        this.currentRow=row;
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.gotoRecord(this.currentRow);
                }
            }
        }
    }
    public void editRow(){
        if(this.currentRow==null)return;
        for(JMCell cell:this.currentRow.getCells()){
            cell.getDataContainer().backup();
        }
        this.edited=this.currentRow;
        if(this.interfaces!=null){
            for(JMFormInterface fi:this.interfaces){
                fi.actionEdit(this.currentRow);
            }
        }
    }
    public void cancelEdit(String message,int YesConfirm){
        boolean canceled=false;
        boolean adding=false;
        if(this.currentRow==null)return;
        if(JMFunctions.confirmBoxYN(JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_CONFIRM), message, JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_YES), JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_NO), true)==YesConfirm){
            for(JMCell cell:this.currentRow.getCells()){
                if(this.edited==null)adding=true;
                else cell.getDataContainer().restore();
            }
            this.edited=null;
            canceled=true;
        }else {
            canceled=false;
        }
        if(!adding){
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.actionCancel(this.currentRow,canceled);
                }
            }
        }else this.deleteAddedRow();
    }
    public String print(){
        return "";
    }
    public void save(){
        if(this.edited!=null){
            boolean edit=false;
            for(JMCell cell:this.currentRow.getCells()){
                if(cell.getDataContainer().isEdited()){
                    edit=true;
                    break;
                }
            }
            if(edit)JMFunctions.trace(this.currentRow.getUpdateSQL());
        }
        this.edited=null;
    }
    public List<JMDataContainer> getCurrentRowDatas(){
        List<JMDataContainer> data=new ArrayList();
        List<JMCell> cells=this.currentRow.getCells();
        for(JMCell cell:cells){
            data.add(cell.getDataContainer());
        }
        return data;
    }
    public void setFormInterface(JMInputInterface component, int column, boolean defaultValue){
        JMRow b=this.currentRow;
        this.firstRow(false);
        do{
            List<JMCell> cells=this.currentRow.getCells();
            cells.get(column).getDataContainer().addInterface(component,defaultValue);
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
    private JMRow addRow(){
        return this.addRow(false);
    }
    private JMRow addRow(Boolean hidden){
        this.lastRow(true);
        Integer r=-1;
        if(this.lastRow!=null)r=this.lastRow.getRowNum();
        if(r==Integer.MAX_VALUE)return null;
        r++;
        JMRow ret=new JMRow(r);
        if(this.currentRow!=null){
            ret.setPrev(this.currentRow);
            this.currentRow.setNext(ret);
        }
        ret.setTable(this);
        this.currentRow=ret;
        this.lastRow=this.currentRow;
        return ret;
    }
    public JMRow addNewRow(List<JMInputInterface> newInterfaces){
        JMRow ret=this.addRow(false);
        if(ret==null)return null;
        ret.setTable(this);
        
        List<Boolean> colsVisibility=style.getVisibles();
        List<String> colsFormat=style.getFormats();
        List<Object[]> colsFormatParams=style.getListParams();
        List<String> fieldNames=style.getFieldNames();
        JMFunctions.trace("Interfaces: "+newInterfaces.size()+", Formats: "+style.getFormats().size());
        for(int j=0;j<this.style.getFormats().size();j++){
            Boolean hidden=false;
            JMDataContainer dc=null;
            if(j<colsVisibility.size())hidden=!colsVisibility.get(j);
            dc=JMDataContainer.create("",j,fieldNames.get(j));
            JMCell cell=ret.addCell(dc,hidden);
            dc.setCell(cell);
            dc.refreshInterfaces(style,"",false,true);
            if(newInterfaces!=null){
                dc.addInterface(newInterfaces.get(j),true);
            }
        }
        
        this.currentRow=ret;
        if(this.interfaces!=null){
            for(JMFormInterface fi:this.interfaces){
                fi.actionAdd(this.currentRow);
            }
        }
        this.gotoRow(ret, true);
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
    private void deleteAddedRow(){
        JMRow delete=this.currentRow;
        JMRow p=this.currentRow.getPrev();
        JMRow n=this.currentRow.getNext();
        if(p!=null)p.setNext(n);
        if(n!=null)n.setPrev(p);
        Integer start=this.currentRow.getRowNum();
        if(p!=null){
            this.currentRow=p;
            start=p.getRowNum();
        }
        else this.currentRow=n;
        this.updateRowNums(this.currentRow, start);
        
        if(this.currentRow.getPrev()==null)this.firstRow=this.currentRow;
        if(this.currentRow.getNext()==null)this.lastRow=this.currentRow;
        this.currentRow.displayInterface(true);
        if(this.interfaces!=null){
            for(JMFormInterface fi:this.interfaces){
                fi.actionDelete(delete);
            }
        }
        this.gotoRow(this.currentRow, true);
        
        JMFunctions.trace(delete.getDeleteSQL());
    }
    public JMRow deleteRow(JMRow row, boolean updateContainer){
        
        JMRow p=row.getPrev();
        JMRow n=row.getNext();
        if(p!=null)p.setNext(n);
        if(n!=null)n.setPrev(p);
        Integer start=row.getRowNum();
        if(p!=null){
            this.currentRow=p;
            start=p.getRowNum();
        }
        else this.currentRow=n;
        this.updateRowNums(this.currentRow, start);
        
        if(this.currentRow.getPrev()==null)this.firstRow=this.currentRow;
        if(this.currentRow.getNext()==null)this.lastRow=this.currentRow;
        if(updateContainer){
            this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMFormInterface fi:this.interfaces){
                    fi.actionDelete(row);
                }
            }
        }
        this.gotoRow(this.currentRow, updateContainer);
        
        JMFunctions.trace(row.getDeleteSQL());
        return row;
    }
    private void updateRowNums(JMRow from,Integer start){
        JMRow w=from;
        Integer r=start;
        while(w!=null && r!=Integer.MAX_VALUE){
            JMFunctions.trace("UPDATED ROW NUM: "+r);
            w.setRowNum(r++);
            w=w.getNext();
        }
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
    public void addInterface(JMFormInterface component){
        if(this.interfaces==null)this.interfaces=new ArrayList();
        this.interfaces.add(component);
        
    }
    public List<String> getLabelTitles(){
        return this.labelTitles;
    }
}
