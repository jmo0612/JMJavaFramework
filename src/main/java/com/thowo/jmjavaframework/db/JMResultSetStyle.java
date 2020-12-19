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
    public static final String DATA_TYPE_STRING="|STRING";
    public static final String DATA_TYPE_INTEGER="|INTEGER";
    public static final String DATA_TYPE_DOUBLE="|DOUBLE";
    public static final String DATA_TYPE_BOOLEAN="|BOOLEAN";
    public static final String DATA_TYPE_OBJECT="|OBJECT";
    public static final String DATA_TYPE_DATE="|DATE";

    public static final String FORMAT="|FORMAT";
    public static final String FORMAT_INTEGER_LEADINGZEROS="|FORMAT|LEADINGZEROS";
    public static final String FORMAT_DOUBLE_MONEY="|FORMAT|MONEY";
    public static final String FORMAT_DOUBLE_CURRENCY="|FORMAT|CURRENCY";
    public static final String FORMAT_DATE_TIME="|FORMAT|TIME";
    public static final String FORMAT_DATE_DB="|FORMAT|DATEDB";
    public static final String FORMAT_DATE_SHORT="|FORMAT|SHORT";
    public static final String FORMAT_DATE_12="|FORMAT|12";
    public static final String FORMAT_DATE_H="|FORMAT|H";
    public static final String FORMAT_DATE_M="|FORMAT|M";
    public static final String FORMAT_DATE_S="|FORMAT|S";
    public static final String FORMAT_DATE_CUSTOM="|FORMAT|S";
    public static final String FORMAT_IMAGE="|FORMAT|IMAGE";

    public static final String PARAM_DATE_WEEKDAY_SHORT="|WEEKDAYSHORT|";
    public static final String PARAM_DATE_WEEKDAY_LONG="|WEEKDAYLONG|";
    public static final String PARAM_DATE_DAY_SHORT="|DAYSHORT|";
    public static final String PARAM_DATE_DAY_LONG="|DAYLONG|";
    public static final String PARAM_DATE_MONTH_NUMBER_SHORT="|MONTHNUMBERSHORT|";
    public static final String PARAM_DATE_MONTH_NUMBER_LONG="|MONTHNUMBERLONG|";
    public static final String PARAM_DATE_MONTH_SHORT="|MONTHSHORT|";
    public static final String PARAM_DATE_MONTH_LONG="|MONTHLONG|";
    public static final String PARAM_DATE_YEAR_SHORT="|YEARSHORT|";
    public static final String PARAM_DATE_YEAR_LONG="|YEARLONG|";
    public static final String PARAM_DATE_HOUR24_SHORT="|HOUR24SHORT|";
    public static final String PARAM_DATE_HOUR24_LONG="|HOUR24LONG|";
    public static final String PARAM_DATE_HOUR12_SHORT="|HOUR12SHORT|";
    public static final String PARAM_DATE_HOUR12_LONG="|HOUR12LONG|";
    public static final String PARAM_DATE_MINUTE_SHORT="|MINUTESHORT|";
    public static final String PARAM_DATE_MINUTE_LONG="|MINUTELONG|";
    public static final String PARAM_DATE_SECOND_SHORT="|SECONDSHORT|";
    public static final String PARAM_DATE_SECOND_LONG="|SECONDLONG|";
    
    
    private List<String> formatTypes=new ArrayList();
    private List<Object[]> listParams=new ArrayList();
    List<Boolean> hiddens=new ArrayList();
    List<String> labelTitles=new ArrayList();
    List<String> fieldNames=new ArrayList();

    public JMResultSetStyle setFormat(int column,String JMResultSetStyleConstant, Object[] params){
        String tmp=this.formatTypes.get(column);
        int l=tmp.indexOf(FORMAT);
        if(l>=0)tmp=tmp.substring(0,l);
        this.formatTypes.set(column, tmp+JMResultSetStyleConstant);
        this.listParams.set(column,params);
        return this;
    }
    public JMResultSetStyle addFormat(int column,String JMResultSetStyleConstant, Object[] params){
        this.formatTypes.set(column, this.formatTypes.get(column)+JMResultSetStyleConstant);
        this.listParams.set(column,params);
        return this;
    }
    public JMResultSetStyle addFormat(String columnName,String JMResultSetStyleConstant, Object[] params){
        return this.addFormat(this.getIndexOf(columnName), JMResultSetStyleConstant, params);
    }
    public JMResultSetStyle setLabel(int column,String title){
        this.labelTitles.set(column, title);
        return this;
    }
    public JMResultSetStyle setLabel(String columnName,String title){
        return this.setLabel(this.getIndexOf(columnName), title);
    }
    
    public static JMResultSetStyle create(JMResultSet rs){
        return new JMResultSetStyle(rs);
    }
    public JMResultSetStyle(JMResultSet rs){
        this.setResultSet(rs);
    }
    private void setResultSet(JMResultSet rs){
        if(rs!=null){
            List<String> formatTypes=new ArrayList();
            List<Object[]> listParams=new ArrayList();
            List<Boolean> hiddens=new ArrayList();
            List<String> labelTitles=new ArrayList();
            List<String> fieldNames=new ArrayList();
            try {
                for(int i=0;i<rs.getSQLResultSet().getMetaData().getColumnCount();i++){
                    hiddens.add(false);
                    listParams.add(null);
                    fieldNames.add(rs.getSQLResultSet().getMetaData().getColumnName(i+1));
                    labelTitles.add(rs.getSQLResultSet().getMetaData().getColumnName(i+1));
                    //JMFunctions.trace("**** "+rs.getMetaData().getColumnType(i+1)+"->"+java.sql.Types.LONGVARCHAR);
                    if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.BIT || rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.BOOLEAN){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_BOOLEAN);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_BOOLEAN);
                    }else if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.DATE){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_DATE+JMResultSetStyle.FORMAT_DATE_SHORT);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_DATE);
                    }else if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.TIMESTAMP){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_DATE + JMResultSetStyle.FORMAT_DATE_TIME + JMResultSetStyle.FORMAT_DATE_SHORT + JMResultSetStyle.FORMAT_DATE_12);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_DATE + JMDataContainer.DATE_TIME);
                    }else if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.FLOAT || rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.DOUBLE){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_DOUBLE + JMResultSetStyle.FORMAT_DOUBLE_MONEY);
                        //Object[] prm={false};
                        //listParams.set(i, prm);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_DOUBLE);
                    }else if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.INTEGER){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_INTEGER);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_INTEGER);
                    }else if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.VARCHAR){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_STRING);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_STRING);
                    }else if(rs.getSQLResultSet().getMetaData().getColumnType(i+1)==java.sql.Types.LONGVARCHAR){
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_STRING);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_STRING);
                    }else{
                        formatTypes.add(JMResultSetStyle.DATA_TYPE_OBJECT);
                        //JMFunctions.trace(JMDataContainer.DATA_TYPE_OBJECT);
                    }
                }
                this.fieldNames=fieldNames;
                this.formatTypes=formatTypes;
                this.hiddens=hiddens;
                this.labelTitles=labelTitles;
                this.listParams=listParams;
            } catch (SQLException ex) {
                Logger.getLogger(JMResultSetStyle.class.getName()).log(Level.SEVERE, null, ex);
                JMFunctions.trace(ex.getMessage());
            }
        }
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
    public int getColCount(){
        return this.fieldNames.size();
    }
    public String getFormat(int columnIndex){
        return this.formatTypes.get(columnIndex);
    }
    public String getFormat(String fieldName){
        return this.formatTypes.get(this.getIndexOf(fieldName));
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
    public List<String> getFormats(){
        return this.formatTypes;
    }
    public List<String> getLabelTitles(){
        return this.labelTitles;
    }
}
