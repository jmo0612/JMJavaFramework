/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.table;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.JMTableInterface;
import com.thowo.jmjavaframework.JMFieldInterface;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.db.JMResultSet;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import com.thowo.jmjavaframework.lang.JMConstMessage;
import com.thowo.jmjavaframework.report.JMWordMM;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimi
 */
public class JMTable {
    public static final int DBTYPE_MYSQL=0;
    public static final int DBTYPE_SQLITE=1;
    
    public static final int YES_OPTION=0;
    public static final int NO_OPTION=1;
    public static final int CANCEL_OPTION=2;
    
    //INTEGER SIZE OF ROWS ONLY
    //AUTOMATIC FIRST ROW
    private List<Integer> keyCols=new ArrayList();
    private List<JMCell> masterCells=new ArrayList();
    private JMRow currentRow; 
    private JMRow firstRow;
    private JMRow lastRow;
    private String name;
    private JMResultSetStyle style;
    private List<JMTableInterface> interfaces;
    private List<String> labelTitles;
    private JMRow edited=null;
    private boolean adding=false;
    private String query="";
    private int dbType=DBTYPE_MYSQL;
    private String filter="";
    private List<Integer> excludedBU;
    
    public static JMTable create(String query, int dbType){
        return new JMTable(query,dbType);
    }
    public JMTable(String query, int dbType){
        this.query=query;
        this.dbType=dbType;
        
        JMResultSet rs=this.getResultSet(query, dbType);
        //if(!rs.first())return;
        JMResultSetStyle style=new JMResultSetStyle(rs);
        this.setProp(rs, style);
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
        this.labelTitles=labelTitles;
        if(rs!=null){
            if(colsFormat.size()!=colsVisibility.size())JMFunctions.trace("different size between colsDataType and colsVisibility");
            if(colsFormat.size()!=colsFormatParams.size())JMFunctions.trace("different size between colsDataType and colsFormatParams");
            if(colsFormat.size()!=rs.getColCount())JMFunctions.trace("different size between colsDataType and resultSet column count");
            boolean empty=!rs.first();
            //if(empty)return;
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
                    
                    
                    if(!empty){
                        if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_BOOLEAN))dc=JMDataContainer.create((Object)rs.getBool(j),j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_DATE))dc=JMDataContainer.create((Object)rs.getDate(j, false),j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_DOUBLE))dc=JMDataContainer.create((Object)rs.getDouble(j),j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_INTEGER))dc=JMDataContainer.create((Object)rs.getInt(j),j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_STRING))dc=JMDataContainer.create((Object)rs.getString(j),j,fieldNames.get(j));
                        else dc=JMDataContainer.create((Object)rs.getBlob(j),j,fieldNames.get(j));
                    }else{
                        if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_BOOLEAN))dc=JMDataContainer.create(false,j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_DATE))dc=JMDataContainer.create(new JMDate(),j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_DOUBLE))dc=JMDataContainer.create(0.0,j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_INTEGER))dc=JMDataContainer.create(0,j,fieldNames.get(j));
                        else if(colsFormat.get(j).contains(JMResultSetStyle.DATA_TYPE_STRING))dc=JMDataContainer.create("",j,fieldNames.get(j));
                        else dc=JMDataContainer.create("",j,fieldNames.get(j));
                    }
                    
                    
                    JMCell cell=this.currentRow.addCell(dc,hidden);
                    dc.setCell(cell);
                    dc.refreshInterfaces(style,null,true,false);
                    
                }
                this.gotoRow(this.currentRow, true);
            }while(rs.next());
            this.firstRow(true);
            if(empty)this.deleteAddedRow("init");
        }
    }
    private JMResultSet getResultSet(String query, int dbType){
        JMResultSet rs=null;
        if(dbType==JMTable.DBTYPE_MYSQL){
            rs=JMFunctions.getCurrentConnection().queryMySQL(query, true);
        }else if(dbType==JMTable.DBTYPE_SQLITE){
            rs=JMFunctions.getCurrentConnection().querySQLite(query, true);
        }
        return rs;
    }
    public JMResultSetStyle getStyle(){
        return this.style;
    }
    public void requery(String query){
        this.query=query;
        if(this.query.equals(""))return;
        List<String> keys=null;
        if(this.currentRow!=null)keys=this.getKeyValues();
        
        List<List<JMFieldInterface>> inputs=new ArrayList();
        if(this.currentRow!=null){
            for(JMDataContainer dc:this.currentRow.getDataContainers()){
                inputs.add(dc.getInterfaces());
            }
        }
        
        
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionBeforeRefresh(this.currentRow);
            }
        }
        List<JMRow> deleted=new ArrayList();
        JMRow r=this.firstRow;
        while(r!=null){
            this.currentRow=r;
            deleted.add(this.deleteAddedRow("refresh"));
            r=r.getNext();
        }
        this.currentRow=null;
        this.firstRow=null;
        this.lastRow=null;
        for(JMRow d:deleted){
            d=null;
        }
        
        JMResultSet rs=this.getResultSet(this.query, this.dbType);
        this.setProp(rs, this.style);
        
        
        if(keys!=null)this.currentRow=this.findByKeys(keys);
        if(this.currentRow==null)this.currentRow=this.firstRow;
        
        if(inputs.size()>0 && this.currentRow!=null){
            List<JMDataContainer> dcs=this.currentRow.getDataContainers();
            for(int i=0;i<dcs.size();i++){
                //JMDataContainer dc=dcs.get(i);
                //dc.setInterfaces(inputs.get(i), true);
                List<JMFieldInterface> iis=inputs.get(i);
                if(iis!=null){
                    for(JMFieldInterface ii:iis){
                        this.setFormInterface(ii, i, true);
                    }
                }
                
            }
        }
        
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterRefreshed(this.currentRow);
            }
        }
        this.gotoRow(this.currentRow, true);
        //JMFunctions.trace("REFRESHED");
    }
    
    public void refresh(){
        if(this.query.equals(""))return;
        if(this.currentRow==null)return;
        List<String> keys=this.getKeyValues();
        
        List<List<JMFieldInterface>> inputs=new ArrayList();
        if(this.currentRow!=null){
            for(JMDataContainer dc:this.currentRow.getDataContainers()){
                inputs.add(dc.getInterfaces());
            }
        }
        
        
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionBeforeRefresh(this.currentRow);
            }
        }
        List<JMRow> deleted=new ArrayList();
        JMRow r=this.firstRow;
        while(r!=null){
            this.currentRow=r;
            deleted.add(this.deleteAddedRow("refresh"));
            r=r.getNext();
        }
        this.currentRow=null;
        this.firstRow=null;
        this.lastRow=null;
        for(JMRow d:deleted){
            d=null;
        }
        
        JMResultSet rs=this.getResultSet(this.query, this.dbType);
        this.setProp(rs, this.style);
        
        
        this.currentRow=this.findByKeys(keys);
        if(this.currentRow==null)this.currentRow=this.firstRow;
        
        if(inputs.size()>0 && this.currentRow!=null){
            List<JMDataContainer> dcs=this.currentRow.getDataContainers();
            for(int i=0;i<dcs.size();i++){
                //JMDataContainer dc=dcs.get(i);
                //dc.setInterfaces(inputs.get(i), true);
                List<JMFieldInterface> iis=inputs.get(i);
                if(iis!=null){
                    for(JMFieldInterface ii:iis){
                        this.setFormInterface(ii, i, true);
                    }
                }
                
            }
        }
        
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterRefreshed(this.currentRow);
            }
        }
        this.gotoRow(this.currentRow, true);
    }
    public void filter(String filter){
        //NOTHING TO DO WITH THE DATAS;
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionBeforeFilter(filter);
            }
        }
        this.filter=filter;
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterFiltered(filter);
            }
        }
    }
    public String getFilter(){
        return this.filter;
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
        this.currentRow=this.firstRow;
        if(updateContainer){
            if(this.currentRow!=null){
                this.currentRow.displayInterface(true);
                if(this.interfaces!=null){
                    for(JMTableInterface fi:this.interfaces){
                        fi.actionAfterMovedFirst(this.currentRow);
                    }
                }
            }
        }
        return this.currentRow;
    }
    public JMRow nextRow(boolean updateContainer){
        JMRow ret=this.currentRow;
        if(ret!=null)ret=this.currentRow.getNext();
        if(ret!=null)this.currentRow=ret;
        if(updateContainer){
            if(this.currentRow!=null)this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMTableInterface fi:this.interfaces){
                    fi.actionAfterMovedNext(ret);
                }
            }
        }
        return ret;
    }
    public JMRow prevRow(boolean updateContainer){
        JMRow ret=this.currentRow;
        if(ret!=null)ret=this.currentRow.getPrev();
        if(ret!=null)this.currentRow=ret;
        if(updateContainer){
            if(this.currentRow!=null)this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMTableInterface fi:this.interfaces){
                    fi.actionAfterMovedNext(ret);
                }
            }
        }
        return ret;
    }
    public JMRow lastRow(boolean updateContainer){
        this.currentRow=this.lastRow;
        if(updateContainer){
            if(this.currentRow!=null){
                this.currentRow.displayInterface(true);
                if(this.interfaces!=null){
                    for(JMTableInterface fi:this.interfaces){
                        fi.actionAfterMovedFirst(this.currentRow);
                    }
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
            if(this.currentRow!=null)this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMTableInterface fi:this.interfaces){
                    fi.actionAfterMovedtoRecord(this.currentRow);
                }
            }
        }
        return this.currentRow;
    }
    public JMRow gotoRow(List<String> values,boolean updateContainer){
        JMRow r=this.firstRow;
        while(r!=null){
            boolean found=true;
            for(int i=0;i<this.keyCols.size();i++){
                if(!r.getDataContainers().get(this.keyCols.get(i)).getValueString().equals(values.get(i))){
                    found=false;
                    break;
                }
            }
            if(found){
                this.currentRow=r;
                break;
            }
            r=r.getNext();
        }
        if(updateContainer){
            if(this.currentRow!=null)this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMTableInterface fi:this.interfaces){
                    fi.actionAfterMovedtoRecord(this.currentRow);
                }
            }
        }
        return this.currentRow;
    }
    public void gotoRow(JMRow row,boolean updateContainer){
        this.currentRow=row;
        if(updateContainer){
            if(this.currentRow!=null)this.currentRow.displayInterface(true);
            if(this.interfaces!=null){
                for(JMTableInterface fi:this.interfaces){
                    fi.actionAfterMovedtoRecord(this.currentRow);
                }
            }
        }
    }
    public void editRow(){
        //if(this.currentRow==null)return;
        for(JMCell cell:this.currentRow.getCells()){
            cell.getDataContainer().backup();
        }
        this.edited=this.currentRow;
        this.adding=false;
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterEdited(this.currentRow);
            }
        }
    }
    public void cancelEdit(String message,int YesConfirm){
        JMRow canceledRow=this.currentRow;
        boolean proceed=false;
        if(this.adding){
            //ADDING
            if(JMFunctions.confirmBoxYN(JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_CONFIRM), message, JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_YES), JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_NO), true)==YesConfirm){
                //USER CONFIRM YES
                this.deleteAddedRow(null);
                this.edited=null;
                proceed=true;
            }
        }else{
            //JUST EDITING
            if(this.edited!=null){
                //ROW EDITED EXISTED
                if(this.edited.isEdited()){
                    //USER MADE CHANGES
                    if(JMFunctions.confirmBoxYN(JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_CONFIRM), message, JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_YES), JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_NO), true)==YesConfirm){
                        //USER CONFIRM YES
                        for(JMCell cell:this.currentRow.getCells()){
                            if(!this.adding)cell.getDataContainer().restore();
                        }
                        this.edited=null;
                        proceed=true;
                    }
                }else{
                    //NO CHANGES 
                    for(JMCell cell:this.currentRow.getCells()){
                        if(!this.adding)cell.getDataContainer().restore();
                    }
                    this.edited=null;
                    proceed=true;
                }
            }else{
                //ROW EDITED NOT EXISTED 
                for(JMCell cell:this.currentRow.getCells()){
                    if(!this.adding)cell.getDataContainer().restore();
                }
                proceed=true;
            }
        }
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterCanceled(this.currentRow,proceed,canceledRow);
            }
        }
        
        
    }
    public void viewRow(){
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterViewed(this.currentRow);
            }
        }
    }
    public void print(){
        JMFunctions.trace("PRINTING");
        if(this.isEmpty())return;
        if(this.currentRow==null)return;
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterPrinted(this.currentRow);
            }
        }
    }
    public void save(){
        //if(this.currentRow==null)return;
        boolean proceed=true;
        if(this.edited==null){
            if(!this.adding)proceed=false;
        }else{
            if(!this.currentRow.isEdited())proceed=false;
        }
        boolean saved=true;
        if(proceed){
            if(this.currentRow.isValuesValid()){
                if(this.excludedBU!=null)this.excludeColumnsFromUpdate(this.excludedBU);
                saved=JMFunctions.getCurrentConnection().queryUpdateMySQL(this.currentRow.getUpdateSQL(), true);
                //JMFunctions.trace(this.currentRow.getUpdateSQL());
                //saved=true;
            }else{
                JMFunctions.errorMessage(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_INPUT_INVALID));
                saved=false;
            }
        }else{
            if(!this.currentRow.isValuesValid()){
                JMFunctions.errorMessage(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_INPUT_INVALID));
                saved=false;
            }else{
                JMFunctions.trace("NOTHING TO SAVE");
                //saved=true;
            }
        }
        
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterSaved(this.currentRow.getUpdateSQL(),saved);
            }
        }
        if(saved){
            this.gotoRow(this.currentRow, true);
            this.edited=null;
            this.adding=false;
        }
    }
    public List<JMDataContainer> getCurrentRowDatas(){
        List<JMDataContainer> data=new ArrayList();
        List<JMCell> cells=this.currentRow.getCells();
        for(JMCell cell:cells){
            data.add(cell.getDataContainer());
        }
        return data;
    }
    public void setFormInterface(JMFieldInterface component, int column, boolean defaultValue){
        JMRow b=this.currentRow;
        this.firstRow(false);
        //if(this.currentRow==null)return;
        do{
            List<JMCell> cells=this.currentRow.getCells();
            cells.get(column).getDataContainer().addInterface(component,defaultValue);
        }while(this.nextRow(false)!=null);
        this.currentRow=b;
    }
    public void unsetFormInterface(JMFieldInterface component, int column){
        JMRow b=this.currentRow;
        this.firstRow(false);
        //if(this.currentRow==null)return;
        do{
            List<JMCell> cells=this.currentRow.getCells();
            cells.get(column).getDataContainer().removeInterface(component);
        }while(this.nextRow(false)!=null);
        this.currentRow=b;
    }
    public JMRow findByKeys(List<String> keyValues){
        if(this.currentRow==null)return null;
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
        for(int i =0;i<this.keyCols.size();i++){
            ret.add(this.currentRow.getCells().get(this.keyCols.get(i)).getDBValue());
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
        if(this.firstRow==null)this.firstRow=this.currentRow;
        return ret;
    }
    public JMRow addNewRow(){
        //JMFunctions.traceAndShow("ADD NEW ROW");
        JMRow ret=this.addRow(false);
        //if(ret==null)return null;
        ret.setTable(this);
        
        List<Boolean> colsVisibility=style.getVisibles();
        List<String> colsFormat=style.getFormats();
        List<Object[]> colsFormatParams=style.getListParams();
        List<String> fieldNames=style.getFieldNames();
        
        for(int j=0;j<this.style.getFormats().size();j++){
            Boolean hidden=false;
            JMDataContainer dc=null;
            if(j<colsVisibility.size())hidden=!colsVisibility.get(j);
            dc=JMDataContainer.create("",j,fieldNames.get(j));
            JMCell cell=ret.addCell(dc,hidden);
            dc.setCell(cell);
            dc.refreshInterfaces(this.style,"",false,true);
        }
        //JMFunctions.trace(ret.getCells().size()+"     size");
        this.currentRow=ret;
        //this.edited=ret;
        this.adding=true;
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterAdded(this.currentRow);
            }
        }
        //this.gotoRow(ret, true);
        return ret;
    }
    public void excludeColumnsFromUpdate(List<Integer> column){
        this.excludedBU=column;
        if(this.currentRow==null)return;
        JMRow tmp=this.currentRow;
        this.firstRow(false);
        do{
            this.currentRow.excludeColumnsFromUpdate(column);
        }while(this.nextRow(false)!=null);
        this.currentRow=tmp;
    }
    
    private JMRow deleteAddedRow(String extra){
        JMRow delete=this.currentRow;
        //if(delete==null)return null;
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
        if(this.currentRow!=null){
            this.updateRowNums(this.currentRow, start);
            if(this.currentRow.getPrev()==null)this.firstRow=this.currentRow;
            if(this.currentRow.getNext()==null)this.lastRow=this.currentRow;
            this.currentRow.displayInterface(true);
        }else{
            this.firstRow=null;
            this.lastRow=null;
        }
        
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterDeleted(delete,true,extra);
            }
        }
        this.gotoRow(this.currentRow, true);
        return delete;
    }
    public void deleteRow(JMRow row,String message,int YesConfirm, String extra){
        boolean deleted=false;
        //if(this.currentRow==null)return;
        if(JMFunctions.confirmBoxYN(JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_CONFIRM), message, JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_YES), JMFunctions.getMessege(JMConstMessage.MSG_UI+JMConstMessage.MSG_UI_NO), true)==YesConfirm){
            deleted=JMFunctions.getCurrentConnection().queryUpdateMySQL(this.currentRow.getDeleteSQL(), true);
            //deleted=true;
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
            if(this.currentRow!=null){
                this.updateRowNums(this.currentRow, start);
                if(this.currentRow.getPrev()==null)this.firstRow=this.currentRow;
                if(this.currentRow.getNext()==null)this.lastRow=this.currentRow;
            }
        }else {
            deleted=false;
        }
        if(this.currentRow==null){
            this.firstRow=null;
            this.lastRow=null;
        }
        
        if(this.currentRow!=null)this.currentRow.displayInterface(true);
        if(this.interfaces!=null){
            for(JMTableInterface fi:this.interfaces){
                fi.actionAfterDeleted(row,deleted,extra);
            }
        }
        this.gotoRow(this.currentRow, true);
        
        //JMFunctions.trace(row.getDeleteSQL());
    }
    private void updateRowNums(JMRow from,Integer start){
        JMRow w=from;
        Integer r=start;
        while(w!=null && r!=Integer.MAX_VALUE){
            w.setRowNum(r++);
            w=w.getNext();
        }
    }
    public boolean isFirstRecord(){
        if(this.currentRow==null)return false;
        return this.currentRow.getPrev()==null;
    }
    public boolean isLastRecord(){
        if(this.currentRow==null)return false;
        return this.currentRow.getNext()==null;
    }
    public void setKeyColumns(List<Integer> keyColumns){
        this.keyCols=keyColumns;
    }
    public void addInterface(JMTableInterface component){
        if(this.interfaces==null)this.interfaces=new ArrayList();
        //JMFunctions.trace("ADD: "+component.toString());
        this.interfaces.add(component);
        
    }
    public void removeInterface(JMTableInterface component){
        if(this.interfaces==null)return;
        List<JMTableInterface> ni=new ArrayList();
        for(JMTableInterface ti:this.interfaces){
            if(!ti.equals(component)){
                ni.add(ti);
            }
        }
        this.interfaces=ni;
        //this.interfaces.remove(component);
        
    }
    public List<String> getLabelTitles(){
        return this.labelTitles;
    }
    public boolean isAddingRow(){
        return this.adding;
    }
    public boolean isEditingRow(){
        return this.edited!=null;
    }
    public List<Integer> getExcludedCols(){
        return this.excludedBU;
    }
    public List<JMRow> getAllRows(){
        List<JMRow> ret=new ArrayList();
        JMRow r=this.firstRow;
        while(r!=null){
            ret.add(r);
            r=r.getNext();
        }
        return ret;
    }
    
}
