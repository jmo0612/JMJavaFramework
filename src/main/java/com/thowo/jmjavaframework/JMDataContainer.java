package com.thowo.jmjavaframework;

import com.thowo.jmjavaframework.db.JMResultSet;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import com.thowo.jmjavaframework.lang.JMConstMessage;
import com.thowo.jmjavaframework.table.JMCell;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JMDataContainer {
    public static final int DATA_STRING=0;
    public static final int DATA_IMAGE=1;
    
    public static final int ALIGN_LEFT=0;
    public static final int ALIGN_CENTER=1;
    public static final int ALIGN_RIGHT=2;
    
    private List<JMInputInterface> interfaces;
    private JMCell cell;
    private String txt;
    private Object val;
    private String valString;
    private String errMsg;
    private String hint;
    private String dbFieldName;
    private int colIndex;
    private int dataDisplay=DATA_STRING;
    private int align=ALIGN_LEFT;
    
    private String txtBU;
    private Object valBU;
    private String valStringBU;

    public static JMDataContainer create(Object valueObject,int colIndex, String dbFieldName){
        return new JMDataContainer(null, String.valueOf(valueObject), valueObject, "", "", dbFieldName, colIndex, null);
    }
    public JMDataContainer(List<JMInputInterface> interfaces,String valueString,Object valueObject,String errMsg,String hint,String dbFieldName, int colIndex,JMCell cell){
        this.setProp(interfaces,valueString,valueObject,errMsg,hint,dbFieldName,colIndex,cell);
    }
    private void setProp(List<JMInputInterface> interfaces,String valueString,Object valueObject,String errMsg,String hint,String dbFieldName, int colIndex,JMCell cell){
        this.txt=valueString;
        this.val=valueObject;
        this.errMsg=errMsg;
        this.hint=hint;
        this.dbFieldName=dbFieldName;
        this.colIndex=colIndex;
        this.cell=cell;
        this.setInterfaces(interfaces,true);
    }
    public void setInterfaces(List<JMInputInterface> components,boolean defaultValue){
        this.interfaces=components;
        if(components!=null){
            for(JMInputInterface ii:this.interfaces){
                ii.setDataContainer(this);
                ii.displayText(this.txt, this.align);
            }
        }
        if(this.cell!=null)this.refreshInterfaces(this.cell.getRow().getTable().getStyle(),null,true,defaultValue);
    }
    public void refreshInterfaces(JMResultSetStyle style, String newValue, boolean display, boolean defaultValue){
        String formatType=style.getFormat(this.colIndex)+"|";
        Object[] params=style.getParams(this.colIndex);
        
        if(formatType.contains(JMResultSetStyle.FORMAT_IMAGE+"|"))this.dataDisplay=DATA_IMAGE;
        if(formatType.contains(JMResultSetStyle.DATA_TYPE_STRING+"|")){
            if(newValue!=null)this.val=String.valueOf(newValue);
            if(this.val!=null)this.valString=String.valueOf(this.val);
            this.align=ALIGN_LEFT;
            this.setValueAsString((String) this.val,display);
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_INTEGER+"|")){
            if(newValue!=null){
                try{
                    this.val=Integer.valueOf(newValue);
                }catch(NumberFormatException e){
                    if(defaultValue){
                        JMFunctions.trace("MASUK SINI");
                        this.val=0;
                        this.valString="0";
                        this.setValToInterfaces();
                    }else{
                        this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_NUMBER_INVALID));
                        this.val=null;
                        this.valString=newValue;
                        this.setValToInterfaces();
                        return;
                    }
                }
            }
            if(this.val!=null)this.valString=String.valueOf(this.val);
            this.align=ALIGN_RIGHT;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(formatType.contains(JMResultSetStyle.FORMAT_INTEGER_LEADINGZEROS+"|")){
                    if(params!=null)if(params.length>0)this.setValueAsInteger((Integer) this.val,(Integer) params[0],display);
                }else{
                    if(params!=null)if(params.length>0)this.setValueAsInteger((Integer) this.val,(String) params[0],display);
                }
            }else {
                this.setValueAsInteger((Integer) this.val,display);
            }
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_DOUBLE+"|")){
            if(newValue!=null){
                try{
                    this.val=Double.valueOf(newValue);
                }catch(NumberFormatException e){
                    if(defaultValue){
                        this.val=0.0;
                        this.valString="0.0";
                        this.setValToInterfaces();
                    }else{
                        this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_NUMBER_INVALID));
                        this.val=null;
                        this.valString=newValue;
                        this.setValToInterfaces();
                        return;
                    }
                }
            }
            if(this.val!=null)this.valString=String.valueOf(this.val);
            this.align=ALIGN_RIGHT;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(formatType.contains(JMResultSetStyle.FORMAT_DOUBLE_MONEY+"|")){
                    this.setValueAsDoubleDecimal((Double) this.val,display);
                }else if(formatType.contains(JMResultSetStyle.FORMAT_DOUBLE_CURRENCY+"|")){
                    this.setValueAsDoubleCurrency((Double) this.val,display);
                }else{
                    if(params!=null)if(params.length>0)this.setValueAsDouble((Double) this.val,(String) params[0],display);
                }
            }else{
                this.setValueAsDouble((Double) this.val,display);
            }
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_BOOLEAN+"|")){
            if(newValue!=null){
                this.val=Boolean.valueOf(newValue);
            }
            if(this.val!=null)this.valString=String.valueOf(this.val);
            this.align=ALIGN_LEFT;
            if(this.dataDisplay==DATA_IMAGE)this.align=ALIGN_CENTER;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(params!=null)if(params.length>1)this.setValueAsBoolean((Boolean) this.val,(String) params[0],(String) params[1],display);
            }else{
                this.setValueAsBoolean((Boolean) this.val,display);
            }
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_DATE+"|")){
            this.align=ALIGN_RIGHT;
            JMDate dt=null;
            if(newValue!=null)try {
                dt=JMDate.create(newValue);
            } catch (ParseException ex) {
                if(defaultValue){
                    dt=new JMDate();
                }else{
                    this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_DATE_INVALID));
                    this.val=null;
                    this.valString=newValue;
                    this.setValToInterfaces();
                    return;
                }
            }
            if(formatType.contains(JMResultSetStyle.FORMAT_DATE_TIME+"|")){
                //DATETIME
                if(dt!=null){
                    this.valString=dt.dateTimeDB();
                    this.val=dt;
                }
                this.setValueAsJMDateTime((JMDate) this.val,display);
                if(formatType.contains(JMResultSetStyle.FORMAT_DATE_12+"|")){
                    //12
                    this.setValueAsJMDateTime12((JMDate) this.val,formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_S+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_M+"|"),display);
                }else{
                    //24
                    this.setValueAsJMDateTime24((JMDate) this.val,formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_S+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_M+"|"),display);
                }
            }else{
                //DATE
                if(dt!=null){
                    this.valString=dt.dateDB();
                    this.val=dt;
                }
                if(formatType.contains(JMResultSetStyle.FORMAT_DATE_DB+"|")){
                    this.setValueAsJMDate((JMDate) this.val,display);
                }else{
                    this.setValueAsJMDate((JMDate) this.val,!formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),display);
                }
            }
        }else{
            this.val=newValue;
            this.valString=String.valueOf(this.val);
            this.align=ALIGN_LEFT;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsObject((Object) this.val,(String) params[0],display);
            }else{
                this.setValueAsObject(this.val,display);
            }
        }
        this.setValToInterfaces();
    }
    private void displayToInterfaces(){
        if(this.interfaces==null)return;
        for(JMInputInterface i:this.interfaces){
            if(i!=null){
                i.displayText(this.txt, this.align);
            }
        }
    }
    private void setValToInterfaces(){
        if(this.interfaces==null)return;
        for(JMInputInterface i:this.interfaces){
            if(i!=null){
                i.setValueObject(this.val);
                i.setValueString(this.valString);
            }
        }
    }
    private void showError(String errMsg){
        if(this.interfaces==null)return;
        for(JMInputInterface i:this.interfaces){
            if(i!=null){
                i.displayError(errMsg);
            }
        }
    }
    public void hideInterfaces(Boolean hidden){
        if(this.interfaces==null)return;
        for(JMInputInterface i:this.interfaces){
            if(i!=null){
                i.setHidden(hidden);
            }
        }
    }
    
    private void setValueAsString(String value, boolean display){
        this.txt=value;
        this.val=value;
        this.valString=value;
        if(display)this.displayToInterfaces();
    }
    private void setValueAsInteger(Integer value, boolean display){
        this.txt=String.valueOf(value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsInteger(Integer value, int numOfLeadingZeros, boolean display){
        this.txt=JMFormatCollection.leadingZero(value,numOfLeadingZeros);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsInteger(Integer value, String format, boolean display){
        this.txt=String.format(format,value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsDouble(Double value, boolean display){
        this.txt=String.valueOf(value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsDoubleDecimal(Double value, boolean display){
        this.txt=JMFormatCollection.decimal(value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsDoubleCurrency(Double value, boolean display){
        this.txt=JMFormatCollection.currency(value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsDouble(Double value, String format, boolean display){
        this.txt=String.format(format,value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsBoolean(Boolean value, boolean display){
        this.txt=String.valueOf(value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsBoolean(Boolean value, String trueStr, String falseStr, boolean display){
        if(value){
            this.txt=trueStr;
        }else this.txt=falseStr;
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsObject(Object value, boolean display){
        this.txt=String.valueOf(value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsObject(Object value, String format, boolean display){
        this.txt=String.format(format,value);
        this.val=value;
        this.valString=String.valueOf(value);
        if(display)this.displayToInterfaces();
    }
    private void setValueAsJMDate(JMDate value, boolean display){
        this.txt=value.dateDB();
        this.val=value;
        this.valString=value.dateDB();
        if(display)this.displayToInterfaces();
    }
    private void setValueAsJMDate(JMDate value,boolean complete, boolean display){
        this.txt=value.dateShort();
        if(complete)this.txt=value.dateFull();
        this.val=value;
        this.valString=value.dateDB();
        if(display)this.displayToInterfaces();
    }
    private void setValueAsJMDateTime(JMDate value, boolean display){
        this.txt=value.dateTimeDB();
        this.val=value;
        this.valString=value.dateTimeDB();
        if(display)this.displayToInterfaces();
    }
    private void setValueAsJMDateTime24(JMDate value, boolean shortDate, boolean showSecond, boolean showMinute, boolean display){
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
        this.valString=value.dateTimeDB();
        if(display)this.displayToInterfaces();
    }
    private void setValueAsJMDateTime24(JMDate value, boolean shortDate, boolean display){
        this.txt=value.dateTimeFull24();
        if(shortDate)this.txt=value.dateTimeShortHM24();
        this.val=value;
        this.valString=value.dateTimeDB();
        if(display)this.displayToInterfaces();
    }
    private void setValueAsJMDateTime12(JMDate value, boolean shortDate, boolean showSecond, boolean showMinute, boolean display){
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
        this.valString=value.dateTimeDB();
        if(display)this.displayToInterfaces();
    }
    
    
    
    public int getColIndex(){
        return this.colIndex;
    }
    public JMCell getCell(){
        return this.cell;
    }
    public List<JMInputInterface> getInterfaces(){
        return this.interfaces;
    }
    public String getFieldName(){
        return this.dbFieldName;
    }
    public String getText(){
        return this.txt;
    }
    public int getDataDisplay(){
        return this.dataDisplay;
    }
    public String getErrorMessage(){
        return this.errMsg;
    }
    public String getHint(){
        return this.hint;
    }
    public Object getValue(){
        return this.val;
    }
    
    
    public void addInterface(JMInputInterface component,boolean defaultValue){
        component.setDataContainer(this);
        component.displayText(this.txt, this.align);
        if(this.interfaces==null)this.interfaces=new ArrayList();
        this.interfaces.add(component);
        this.refreshInterfaces(this.cell.getRow().getTable().getStyle(),null,true,defaultValue);
    }
    public void setcolIndex(int colIndex){
        this.colIndex=colIndex;
    }
    public void setErrorMessage(String errorMessage){
        this.errMsg=errorMessage;
        
    }
    public void setHint(String hint){
        this.hint=hint;
    }
    public void setValueString(String value, boolean display,boolean defaultValue){
        this.refreshInterfaces(this.cell.getRow().getTable().getStyle(),value,display,defaultValue);
    }
    public void setCell(JMCell cell){
        this.cell=cell;
    }
    public void backup(){
        this.valBU=this.val;
        this.txtBU=this.txt;
        this.valStringBU=this.valString;
    }
    public void restore(){
        this.val=this.valBU;
        this.txt=this.txtBU;
        this.valString=this.valStringBU;
        this.refreshInterfaces(this.cell.getRow().getTable().getStyle(), null,true,false);
    }
    public boolean isEdited(){
        return this.val!=this.valBU && !this.txt.equals(this.txtBU) && !this.valString.equals(this.valStringBU);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*public void refreshInterfaces(JMResultSetStyle style, String newValue, boolean display){
        String formatType=style.getFormat(this.colIndex)+"|";
        Object[] params=style.getParams(this.colIndex);
        
        if(formatType.contains(JMResultSetStyle.FORMAT_IMAGE+"|"))this.dataDisplay=DATA_IMAGE;
        if(formatType.contains(JMResultSetStyle.DATA_TYPE_STRING+"|")){
            if(newValue!=null)this.val=String.valueOf(newValue);
            if(this.val==null){
                this.valString="";
            }else{
                this.valString=String.valueOf(this.val);
            }
            this.align=ALIGN_LEFT;
            this.setValueAsString((String) this.val,display);
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_INTEGER+"|")){
            if(newValue!=null){
                try{
                    this.val=Integer.valueOf(newValue);
                }catch(NumberFormatException e){
                    this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_NUMBER_INVALID));
                    this.val=Integer.valueOf(0);
                }
            }
            if(this.val==null){
                this.valString="0";
            }else{
                this.valString=String.valueOf(this.val);
            }
            this.align=ALIGN_RIGHT;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(formatType.contains(JMResultSetStyle.FORMAT_INTEGER_LEADINGZEROS+"|")){
                    if(params!=null)if(params.length>0)this.setValueAsInteger((Integer) this.val,(Integer) params[0],display);
                }else{
                    if(params!=null)if(params.length>0)this.setValueAsInteger((Integer) this.val,(String) params[0],display);
                }
            }else {
                this.setValueAsInteger((Integer) this.val,display);
            }
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_DOUBLE+"|")){
            if(newValue!=null){
                try{
                    this.val=Double.valueOf(newValue);
                }catch(NumberFormatException e){
                    this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_NUMBER_INVALID));
                    this.val=Double.valueOf(0.0);
                }
            }
            if(this.val==null){
                this.valString="0";
            }else{
                this.valString=String.valueOf(this.val);
            }
            this.align=ALIGN_RIGHT;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(formatType.contains(JMResultSetStyle.FORMAT_DOUBLE_MONEY+"|")){
                    this.setValueAsDoubleDecimal((Double) this.val,display);
                }else if(formatType.contains(JMResultSetStyle.FORMAT_DOUBLE_CURRENCY+"|")){
                    this.setValueAsDoubleCurrency((Double) this.val,display);
                }else{
                    if(params!=null)if(params.length>0)this.setValueAsDouble((Double) this.val,(String) params[0],display);
                }
            }else{
                this.setValueAsDouble((Double) this.val,display);
            }
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_BOOLEAN+"|")){
            if(newValue!=null){
                this.val=Boolean.valueOf(newValue);
            }
            if(this.val==null){
                this.valString="true";
            }else{
                this.valString=String.valueOf(this.val);
            }
            this.align=ALIGN_LEFT;
            if(this.dataDisplay==DATA_IMAGE)this.align=ALIGN_CENTER;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(params!=null)if(params.length>1)this.setValueAsBoolean((Boolean) this.val,(String) params[0],(String) params[1],display);
            }else{
                this.setValueAsBoolean((Boolean) this.val,display);
            }
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_DATE+"|")){
            this.align=ALIGN_RIGHT;
            JMDate now=new JMDate();
            JMDate value=now;
            if(this.val!=null){
                value=(JMDate) this.val;
            }
            if(formatType.contains(JMResultSetStyle.FORMAT_DATE_TIME+"|")){
                //DATETIME
                if(newValue!=null)try {
                    value=JMDate.create(newValue);
                } catch (ParseException ex) {
                    this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_DATE_INVALID));
                    value=now;
                }
                this.val=value;
                this.valString=value.dateTimeDB();
                this.setValueAsJMDateTime((JMDate) this.val,display);
                if(formatType.contains(JMResultSetStyle.FORMAT_DATE_12+"|")){
                    //12
                    this.setValueAsJMDateTime12((JMDate) this.val,formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_S+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_M+"|"),display);
                }else{
                    //24
                    this.setValueAsJMDateTime24((JMDate) this.val,formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_S+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_M+"|"),display);
                }
            }else{
                //DATE
                if(newValue!=null)try {
                    value=JMDate.create(newValue);
                } catch (ParseException ex) {
                    this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_DATE_INVALID));
                    value=now;
                }
                this.val=value;
                this.valString=value.dateDB();
                if(formatType.contains(JMResultSetStyle.FORMAT_DATE_DB+"|")){
                    this.setValueAsJMDate((JMDate) this.val,display);
                }else{
                    this.setValueAsJMDate((JMDate) this.val,!formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"));
                }
            }
        }else{
            this.val=newValue;
            this.valString=String.valueOf(this.val);
            this.align=ALIGN_LEFT;
            if(formatType.contains(JMResultSetStyle.FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsObject((Object) this.val,(String) params[0],display);
            }else{
                this.setValueAsObject(this.val,display);
            }
        }
    }*/
    
    
    
    
    
    
    
    
    
    //=================================================================================================================================
    /*public static final String DATA_TYPE_STRING="|STRING";
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

    private JMInputInterface fi;
    private String txt;
    private Object val;
    private String errMsg;
    private String hint;
    private String dataType;
    private String fieldName;
    private Object[] params;
    private JMResultSet rs;
    private int rsCol;
    private boolean fiHidden;

    public JMDataContainer copy(){
        JMDataContainer ret=new JMDataContainer();
        ret.dataType=this.dataType;
        ret.errMsg=this.errMsg;
        ret.fi=this.fi;
        ret.fieldName=this.fieldName;
        ret.hint=this.hint;
        ret.params=this.params;
        ret.txt=this.txt;
        //ret.val=null;
        //ret.setValue(this.val);
        return ret;
    }
    public JMDataContainer(){
        this.setProp(null, null, 0, "|STRING|", null);
    }
    
    public JMDataContainer(JMInputInterface component){
        this.setProp(component, null, 0, "|STRING|", null);
    }
    
    public JMDataContainer(String dataType, Object[] params){
        this.setProp(null, null, 0, dataType, params);
    }
    
    public JMDataContainer(String dataType){
        this.setProp(null, null, 0, dataType, null);
    }
    
    public JMDataContainer(JMInputInterface component, String dataType, Object[] params){
        this.setProp(component, null, 0, dataType, params);
    }
    
    public JMDataContainer(JMInputInterface component, String dataType){
        this.setProp(component, null, 0, dataType, null);
    }

    public JMDataContainer(JMResultSet resultSet,int colIndex, String dataType, Object[] params){
        this.setProp(null, resultSet, colIndex, dataType, params);
    }
    
    public JMDataContainer(JMResultSet resultSet,int colIndex, String dataType){
        this.setProp(null, resultSet, colIndex, dataType, null);
    }
    
    public JMDataContainer(JMInputInterface component, JMResultSet resultSet,int colIndex, String dataType, Object[] params){
        this.setProp(component, resultSet, colIndex, dataType, params);
    }
    
    public JMDataContainer(JMInputInterface component, JMResultSet resultSet,int colIndex, String dataType){
        this.setProp(component, resultSet, colIndex, dataType, null);
    }
    
    
    public void setInterface(JMInputInterface component, boolean hidden){
        this.fi=component;
        this.setHidden(hidden);
        this.fi.setDataContainer(this);
        this.setValue(this.val);
        this.fi.displayText(this.txt);
        
        //JMFunctions.trace(this.txt);
    }
    
    public JMResultSet getResultSet(){
        return this.rs;
    }
    public int getRsCol(){
        return this.rsCol;
    }
    public String getDataType(){
        return this.dataType;
    }
    public Object[] getParams(){
        return this.params;
    }
    private void setProp(JMInputInterface component, JMResultSet resultSet,int colIndex, String dataType, Object[] params){
        this.fi=component;
        if(component!=null)this.fi.setDataContainer(this);
        this.dataType=dataType+"|";
        this.params=params;
        this.rs=resultSet;
        this.rsCol=colIndex;
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
    private void setValue(Object value){
        if(this.dataType.contains(JMDataContainer.DATA_TYPE_STRING+"|")){
            this.setValueAsString((String) value);
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_INTEGER+"|")){
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsInteger((Integer)(value),(String) params[0]);
            }else if(this.dataType.contains(JMDataContainer.DATA_INTEGER_LEADINGZEROS+"|")){
                if(params!=null)if(params.length>0)this.setValueAsInteger((Integer)(value),(Integer) params[0]);
            }else {
                this.setValueAsInteger((Integer)(value));
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_DOUBLE+"|")){
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsDouble((Double)(value),(String) params[0]);
            }else if(this.dataType.contains(JMDataContainer.DATA_DOUBLE_CURRENCY+"|")){
                if(params!=null)if(params.length>0)this.setValueAsDouble((Double)(value),(Boolean) params[0]);
            }else{
                this.setValueAsDouble((Double)(value));
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_BOOLEAN+"|")){
            if(this.dataType.contains(JMDataContainer.DATA_BOOLEAN_CUSTOM+"|")){
                if(params!=null)if(params.length>1)this.setValueAsBoolean((Boolean)(value),(String) params[0],(String) params[0]);
            }else{
                this.setValueAsBoolean((Boolean)(value));
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_DATE+"|")){
            if(this.dataType.contains(JMDataContainer.DATE_TIME+"|")){
                //DATETIME
                this.setValueAsJMDateTime((JMDate)value);
                if(this.dataType.contains(JMDataContainer.DATE_12+"|")){
                    //12
                    this.setValueAsJMDateTime12((JMDate)value,this.dataType.contains(JMDataContainer.DATE_SHORT+"|"),this.dataType.contains(JMDataContainer.DATE_S+"|"),this.dataType.contains(JMDataContainer.DATE_M+"|"));
                }else{
                    //24
                    this.setValueAsJMDateTime24((JMDate)value,this.dataType.contains(JMDataContainer.DATE_SHORT+"|"),this.dataType.contains(JMDataContainer.DATE_S+"|"),this.dataType.contains(JMDataContainer.DATE_M+"|"));
                }
            }else{
                //DATE
                this.setValueAsJMDate((JMDate)value,!this.dataType.contains(JMDataContainer.DATE_SHORT+"|"));
            }
        }else{
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsObject((Boolean)(value),(String) params[0]);
            }else{
                this.setValueAsObject(value);
            }
        }
    }
    public void setValue(String value) throws ParseException,NumberFormatException{
        
        if(this.dataType.contains(JMDataContainer.DATA_TYPE_STRING+"|")){
            //JMFunctions.trace("STRING"+value);
            this.setValueAsString((String) value);
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_INTEGER+"|")){
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsInteger(Integer.valueOf(value),(String) params[0]);
            }else if(this.dataType.contains(JMDataContainer.DATA_INTEGER_LEADINGZEROS+"|")){
                if(params!=null)if(params.length>0)this.setValueAsInteger(Integer.valueOf(value),(Integer) params[0]);
            }else {
                this.setValueAsInteger(Integer.valueOf(value));
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_DOUBLE+"|")){
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsDouble(Double.valueOf(value),(String) params[0]);
            }else if(this.dataType.contains(JMDataContainer.DATA_DOUBLE_CURRENCY+"|")){
                if(params!=null)if(params.length>0)this.setValueAsDouble(Double.valueOf(value),(Boolean) params[0]);
            }else{
                this.setValueAsDouble(Double.valueOf(value));
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_BOOLEAN+"|")){
            if(this.dataType.contains(JMDataContainer.DATA_BOOLEAN_CUSTOM+"|")){
                if(params!=null)if(params.length>1)this.setValueAsBoolean(Boolean.valueOf(value),(String) params[0],(String) params[0]);
            }else{
                this.setValueAsBoolean(Boolean.valueOf(value));
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_DATE+"|")){
            if(this.dataType.contains(JMDataContainer.DATE_TIME+"|")){
                //DATETIME
                this.setValueAsJMDateTime(new JMDate(value));
                if(this.dataType.contains(JMDataContainer.DATE_12+"|")){
                    //12
                    this.setValueAsJMDateTime12(new JMDate(value),this.dataType.contains(JMDataContainer.DATE_SHORT+"|"),this.dataType.contains(JMDataContainer.DATE_S+"|"),this.dataType.contains(JMDataContainer.DATE_M+"|"));
                }else{
                    //24
                    this.setValueAsJMDateTime24(new JMDate(value),this.dataType.contains(JMDataContainer.DATE_SHORT+"|"),this.dataType.contains(JMDataContainer.DATE_S+"|"),this.dataType.contains(JMDataContainer.DATE_M+"|"));
                }
            }else{
                //DATE
                this.setValueAsJMDate(new JMDate(value),!this.dataType.contains(JMDataContainer.DATE_SHORT+"|"));
            }
        }else{
            if(this.dataType.contains(JMDataContainer.DATA_FORMAT+"|")){
                if(params!=null)if(params.length>0)this.setValueAsObject(Boolean.valueOf(value),(String) params[0]);
            }else{
                this.setValueAsObject(value);
            }
        }
    }
    
    public void setHidden(boolean hidden){
        this.fiHidden=hidden;
        if(this.fi!=null)this.fi.setHidden(hidden);
    }
    public boolean getFiHidden(){
        return this.fiHidden;
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
        //JMFunctions.trace("ADAKAH? "+this.val);
        if(this.dataType.contains(JMDataContainer.DATA_TYPE_DATE+"|")){
            JMDate d=(JMDate) this.val;
            if(this.dataType.contains(JMDataContainer.DATE_TIME+"|")){
                //DATETIME
                return d.dateTimeDB();
            }else{
                //DATE
                return d.dateDB();
            }
        }else if(this.dataType.contains(JMDataContainer.DATA_TYPE_BOOLEAN+"|")){
            boolean val=(boolean) this.val;
            if(val)return "1";
            else return "0";
        }
        return String.valueOf(this.val);
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
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsInteger(Integer value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsInteger(Integer value, int numOfLeadingZeros){
        this.txt=JMFormatCollection.leadingZero(value,numOfLeadingZeros);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsInteger(Integer value, String format){
        this.txt=String.format(format,value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsDouble(Double value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsDouble(Double value, Boolean currency){
        this.txt=String.valueOf(value);
        if(currency){
            this.txt=JMFormatCollection.currency(value);
        }else{
            this.txt=JMFormatCollection.decimal(value);
        }
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsDouble(Double value, String format){
        this.txt=String.format(format,value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsBoolean(Boolean value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsBoolean(Boolean value, String trueStr, String falseStr){
        if(value){
            this.txt=trueStr;
        }else this.txt=falseStr;
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsObject(Object value){
        this.txt=String.valueOf(value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsObject(Object value, String format){
        this.txt=String.format(format,value);
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            this.fi.setValueString(String.valueOf(this.val));
        }
    }
    public void setValueAsJMDate(JMDate value){
        this.txt=value.dateDB();
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            //JMFunctions.trace("DATE: "+value.dateDB());
            this.fi.setValueString(value.dateDB());
        }
    }
    public void setValueAsJMDate(JMDate value,boolean complete){
        this.txt=value.dateShort();
        if(complete)this.txt=value.dateFull();
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            //JMFunctions.trace("DATE: "+value.dateDB());
            this.fi.setValueString(value.dateDB());
        }
    }
    public void setValueAsJMDateTime(JMDate value){
        this.txt=value.dateTimeDB();
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            //JMFunctions.trace("DATETIME: "+value.dateTimeDB());
            this.fi.setValueString(value.dateTimeDB());
        }
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
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            //JMFunctions.trace("DATETIME: "+value.dateTimeDB());
            this.fi.setValueString(value.dateTimeDB());
        }
    }
    public void setValueAsJMDateTime24(JMDate value, boolean shortDate){
        this.txt=value.dateTimeFull24();
        if(shortDate)this.txt=value.dateTimeShortHM24();
        this.val=value;
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            //JMFunctions.trace("DATETIME: "+value.dateTimeDB());
            this.fi.setValueString(value.dateTimeDB());
        }
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
        if(this.fi!=null){
            this.fi.displayText(this.txt);
            this.fi.setValueObject(this.val);
            //JMFunctions.trace("DATETIME: "+value.dateTimeDB());
            this.fi.setValueString(value.dateTimeDB());
        }
    }
    public void refresh(){
        this.setValue(this.val);
    }*/
}
