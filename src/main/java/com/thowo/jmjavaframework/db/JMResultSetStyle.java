/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.db;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMFunctions;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimi
 */
public class JMResultSetStyle {
    List<String> dataTypes=new ArrayList();
    List<Object[]> listParams=new ArrayList();
    List<Boolean> hiddens=new ArrayList();
    List<String> fieldNames=new ArrayList();
    
    public JMResultSetStyle(ResultSet rs){
        this.setResultSet(rs);
    }
    
    private int getIndexOf(String colName){
        if(!colName.equals("")){
            for(int i=0;i<this.fieldNames.size();i++){
                if(this.fieldNames.get(i).equals(colName)){
                    return i;
                }
            }
        }
        return -1;
    }
    
    public JMResultSetStyle setColHidden(int columnIndex){
        if(columnIndex>=0){
            this.hiddens.set(columnIndex, true);
        }
        return this;
    }
    
    public JMResultSetStyle setColHidden(String columnName){
        return this.setColHidden(this.getIndexOf(columnName));
    }
    
    public JMResultSetStyle setColType(int columnIndex,String dataType){
        if(columnIndex>=0){
            this.dataTypes.set(columnIndex, dataType);
        }
        return this;
    }
    
    public JMResultSetStyle setColType(String columnName, String dataType){
        return this.setColType(this.getIndexOf(columnName),dataType);
    }
    
    public JMResultSetStyle setColParams(int columnIndex,Object[] params){
        if(columnIndex>=0){
            this.listParams.set(columnIndex, params);
        }
        return this;
    }
    
    public JMResultSetStyle setColParams(String columnName, Object[] params){
        return this.setColParams(this.getIndexOf(columnName), params);
    }
    
    public int getColCount(){
        return this.fieldNames.size();
    }
    
    public String getDataType(int columnIndex){
        return this.dataTypes.get(columnIndex);
    }
    public String getDataType(String fieldName){
        return this.dataTypes.get(this.getIndexOf(fieldName));
    }
    
    public Boolean getVisible(int columnIndex){
        return !this.hiddens.get(columnIndex);
    }
    public Boolean getVisible(String fieldName){
        return !this.hiddens.get(this.getIndexOf(fieldName));
    }
    
    public String getFieldName(int columnIndex){
        return this.fieldNames.get(columnIndex);
    }
    
    public Object[] getParams(int columnIndex){
        return this.listParams.get(columnIndex);
    }
    public Object[] getParams(String fieldName){
        return this.listParams.get(this.getIndexOf(fieldName));
    }
    
    public Object getParam(int columnIndex, int paramIndex){
        return this.listParams.get(columnIndex)[paramIndex];
    }
    public Object getParam(String fieldName, int paramIndex){
        return this.listParams.get(this.getIndexOf(fieldName))[paramIndex];
    }
    
    public List<String> getDataTypes(){
        return this.dataTypes;
    }
    
    public List<Object[]> getListParams(){
        return this.listParams;
    }
    
    public List<Boolean> getHiddens(){
        return this.hiddens;
    }
    
    public List<Boolean> getVisibles(){
        List<Boolean> ret=new ArrayList();
        for(Boolean h:this.hiddens){
            ret.add(!h);
        }
        return ret;
    }
    
    public List<String> getFieldNames(){
        return this.fieldNames;
    }
    
    private void setResultSet(ResultSet rs){
        if(rs!=null){
            List<String> types=new ArrayList();
            List<Object[]> prms=new ArrayList();
            List<Boolean> hids=new ArrayList();
            List<String> names=new ArrayList();
            try {
                for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                    hids.add(false);
                    prms.add(null);
                    names.add(rs.getMetaData().getColumnName(i+1));
                    if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.BIT || rs.getMetaData().getColumnType(i+1)==java.sql.Types.BOOLEAN){
                        types.add(JMDataContainer.DATA_TYPE_BOOLEAN);
                        JMFunctions.trace(JMDataContainer.DATA_TYPE_BOOLEAN);
                    }else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.DATE || rs.getMetaData().getColumnType(i+1)==java.sql.Types.TIME){
                        types.add(JMDataContainer.DATA_TYPE_DATE);
                        JMFunctions.trace(JMDataContainer.DATA_TYPE_DATE);
                    }else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.FLOAT || rs.getMetaData().getColumnType(i+1)==java.sql.Types.DOUBLE){
                        types.add(JMDataContainer.DATA_TYPE_DOUBLE);
                        JMFunctions.trace(JMDataContainer.DATA_TYPE_DOUBLE);
                    }else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.INTEGER){
                        types.add(JMDataContainer.DATA_TYPE_INTEGER);
                        JMFunctions.trace(JMDataContainer.DATA_TYPE_INTEGER);
                    }else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.VARCHAR){
                        types.add(JMDataContainer.DATA_TYPE_STRING);
                        JMFunctions.trace(JMDataContainer.DATA_TYPE_STRING);
                    }else{
                        types.add(JMDataContainer.DATA_TYPE_OBJECT);
                        JMFunctions.trace(JMDataContainer.DATA_TYPE_OBJECT);
                    }
                }
                this.setProp(names, types, prms, hids);
            } catch (SQLException ex) {
                Logger.getLogger(JMResultSetStyle.class.getName()).log(Level.SEVERE, null, ex);
                JMFunctions.trace(ex.getMessage());
            }
        }
    }
    
    private void setProp(List<String> fieldNames, List<String> dataTypes, List<Object[]> listParams, List<Boolean> hiddens){
        this.dataTypes=dataTypes;
        this.hiddens=hiddens;
        this.listParams=listParams;
    }
}
