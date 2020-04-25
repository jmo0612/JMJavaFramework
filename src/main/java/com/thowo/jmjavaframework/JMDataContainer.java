package com.thowo.jmjavaframework;

import com.thowo.jmjavaframework.db.JMResultSet;
import com.thowo.jmjavaframework.db.JMResultSetStyle;

import java.util.Date;

public class JMDataContainer {
    public static final String DATA_TYPE_STRING="|STRING";
    public static final String DATA_TYPE_INTEGER="|INTEGER";
    public static final String DATA_TYPE_DOUBLE="|DOUBLE";
    public static final String DATA_TYPE_BOOLEAN="|BOOLEAN";
    public static final String DATA_TYPE_OBJECT="|OBJECT";
    public static final String DATA_TYPE_DATE="|DATE";

    public static final String DATA_FORMAT="|FORMAT";
    public static final String DATA_INTEGER_LEADINGZEROS="|LEADINGZEROS";
    public static final String DATA_DOUBLE_CURRENCY="|CURRENCY";
    public static final String DATA_BOOLEAN_CUSTOM="|CUSTOM";
    public static final String DATE_TIME="|TIME";
    public static final String DATE_DB="|DB";
    public static final String DATE_SHORT="|SHORT";
    public static final String DATE_12="|12";
    public static final String DATE_H="|H";
    public static final String DATE_M="|M";
    public static final String DATE_S="|S";

    private JMFormInterface fi;
    private String txt;
    private Object val;
    private String errMsg;
    private String hint;
    private String dataType;
    private String fieldName;
    private Object[] params;

    public JMDataContainer(){
        this.setProp(null, null, 0, "|STRING|", null);
    }
    
    public JMDataContainer(JMFormInterface component){
        this.setProp(component, null, 0, "|STRING|", null);
    }
    
    public JMDataContainer(String dataType, Object[] params){
        this.setProp(null, null, 0, dataType, params);
    }
    
    public JMDataContainer(String dataType){
        this.setProp(null, null, 0, dataType, null);
    }
    
    public JMDataContainer(JMFormInterface component, String dataType, Object[] params){
        this.setProp(component, null, 0, dataType, params);
    }
    
    public JMDataContainer(JMFormInterface component, String dataType){
        this.setProp(component, null, 0, dataType, null);
    }

    public JMDataContainer(JMResultSet resultSet,int colIndex, String dataType, Object[] params){
        this.setProp(null, resultSet, colIndex, dataType, params);
    }
    
    public JMDataContainer(JMResultSet resultSet,int colIndex, String dataType){
        this.setProp(null, resultSet, colIndex, dataType, null);
    }
    
    public JMDataContainer(JMFormInterface component, JMResultSet resultSet,int colIndex, String dataType, Object[] params){
        this.setProp(component, resultSet, colIndex, dataType, params);
    }
    
    public JMDataContainer(JMFormInterface component, JMResultSet resultSet,int colIndex, String dataType){
        this.setProp(component, resultSet, colIndex, dataType, null);
    }
    
    
    
    
    
    private void setProp(JMFormInterface component, JMResultSet resultSet,int colIndex, String dataType, Object[] params){
        this.fi=component;
        if(component!=null)this.fi.setDataContainer(this);
        this.dataType=dataType+"|";
        this.params=params;
        if(resultSet!=null){
            this.fieldName=resultSet.getColumnName(colIndex);
            this.setValue(resultSet, colIndex);
        }
    }

    private void setValue(JMResultSet resultSet,int colIndex){
        Object value=null;
        if(this.dataType.contains(JMDataContainer.DATA_TYPE_STRING+"|")){
            value=resultSet.getString(colIndex);
            this.setValueAsString((String) value);
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_INTEGER+"|")){
            value=resultSet.getInt(colIndex);
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsInteger((Integer) value,(String) params[0]);
            }else if(this.dataType.contains(JMDataContainer.DATA_INTEGER_LEADINGZEROS+"|")){
                if(params!=null)if(params.length>0)this.setValueAsInteger((Integer) value,(Integer) params[0]);
            }else {
                this.setValueAsInteger((Integer) value);
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_DOUBLE+"|")){
            value=resultSet.getDouble(colIndex);
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsDouble((Double) value,(String) params[0]);
            }else if(this.dataType.contains(JMDataContainer.DATA_DOUBLE_CURRENCY+"|")){
                if(params!=null)if(params.length>0)this.setValueAsDouble((Double) value,(Boolean) params[0]);
            }else{
                this.setValueAsDouble((Double) value);
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_BOOLEAN+"|")){
            value=resultSet.getBool(colIndex);
            if(this.dataType.contains(JMDataContainer.DATA_BOOLEAN_CUSTOM+"|")){
                if(params!=null)if(params.length>1)this.setValueAsBoolean((Boolean) value,(String) params[0],(String) params[0]);
            }else{
                this.setValueAsBoolean((Boolean) value);
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_DATE+"|")){
            value=resultSet.getDate(colIndex,false);
            if(this.dataType.contains(JMDataContainer.DATE_TIME+"|")){
                //DATETIME
                this.setValueAsJMDateTime((JMDate) value);
                if(this.dataType.contains(JMDataContainer.DATE_12+"|")){
                    //12
                    this.setValueAsJMDateTime12((JMDate) value,this.dataType.contains(JMDataContainer.DATE_SHORT+"|"),this.dataType.contains(JMDataContainer.DATE_S+"|"),this.dataType.contains(JMDataContainer.DATE_M+"|"));
                }else{
                    //24
                    this.setValueAsJMDateTime24((JMDate) value,this.dataType.contains(JMDataContainer.DATE_SHORT+"|"),this.dataType.contains(JMDataContainer.DATE_S+"|"),this.dataType.contains(JMDataContainer.DATE_M+"|"));
                }
            }else{
                //DATE
                this.setValueAsJMDate((JMDate) value,!this.dataType.contains(JMDataContainer.DATE_SHORT+"|"));
            }
        }else{
            value=resultSet.getBlob(colIndex);
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsObject((Boolean) value,(String) params[0]);
            }else{
                this.setValueAsObject(value);
            }
        }
    }

    public String getFieldName(){
        return this.fieldName;
    }
    
    public String getText(){
        return this.txt;
    }
    public String getErrorMessage(){
        return this.errMsg;
    }
    public String getHint(){
        return this.hint;
    }
    public Object getValueAsObject(){
        return this.val;
    }
    public String getValueAsString(){
        return (String) this.val;
    }
    public Integer getValueAsInteger(){
        return (Integer) this.val;
    }
    public Double getValueAsDouble(){
        return (Double) this.val;
    }
    public Boolean getValueAsBoolean(){
        return (Boolean) this.val;
    }
    public JMDate getValueAsDate(){
        return (JMDate) this.val;
    }


    public void setErrorMessage(String errorMessage){
        this.errMsg=errorMessage;
        if(this.fi!=null)this.fi.displayError(this.errMsg);
    }
    public void setHint(String hint){
        this.hint=hint;
        if(this.fi!=null)this.fi.displayHint(this.hint);
    }
    public void setValueAsString(String value){
        this.txt=value;
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsInteger(Integer value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsInteger(Integer value, int numOfLeadingZeros){
        this.txt=JMFormatCollection.leadingZero(value,numOfLeadingZeros);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsInteger(Integer value, String format){
        this.txt=String.format(format,value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsDouble(Double value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsDouble(Double value, Boolean currency){
        this.txt=String.valueOf(value);
        if(currency)this.txt=JMFormatCollection.currency(value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsDouble(Double value, String format){
        this.txt=String.format(format,value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsBoolean(Boolean value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsBoolean(Boolean value, String trueStr, String falseStr){
        if(value){
            this.txt=trueStr;
        }else this.txt=falseStr;
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsObject(Object value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsObject(Object value, String format){
        this.txt=String.format(format,value);
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsJMDate(JMDate value){
        this.txt=value.dateDB();
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsJMDate(JMDate value,boolean complete){
        this.txt=value.dateShort();
        if(complete)this.txt=value.dateFull();
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsJMDateTime(JMDate value){
        this.txt=value.dateTimeDB();
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsJMDateTime24(JMDate value, boolean shortDate, boolean showSecond, boolean showMinute){
        this.txt=value.dateTimeFull24();
        if(shortDate){
            this.txt=value.dateTimeShort24();
            if(!showSecond){
                this.txt=value.dateTimeShortHM24();
            }
            if(!showMinute){
                this.txt=value.dateTimeShortH24();
            }
        }
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsJMDateTime24(JMDate value, boolean shortDate){
        this.txt=value.dateTimeFull24();
        if(shortDate)this.txt=value.dateTimeShortHM24();
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
    public void setValueAsJMDateTime12(JMDate value, boolean shortDate, boolean showSecond, boolean showMinute){
        this.txt=value.dateTimeFull12();
        if(shortDate){
            this.txt=value.dateTimeShort12();
            if(!showSecond){
                this.txt=value.dateTimeShortHM12();
            }
            if(!showMinute){
                this.txt=value.dateTimeShortH12();
            }
        }
        this.val=value;
        if(this.fi!=null)this.fi.displayText(this.txt);
    }
}
