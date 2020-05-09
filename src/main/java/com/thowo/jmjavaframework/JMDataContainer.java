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
    private String valDB;
    private String errMsg;
    private String hint;
    private String dbFieldName;
    private int colIndex;
    private int dataDisplay=DATA_STRING;
    private int align=ALIGN_LEFT;
    private boolean error=false;
    
    private String txtBU;
    private Object valBU;
    private String valStringBU;
    private String valDBBU;
    

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
        this.error=false;
        String formatType=style.getFormat(this.colIndex)+"|";
        Object[] params=style.getParams(this.colIndex);
        
        if(formatType.contains(JMResultSetStyle.FORMAT_IMAGE+"|"))this.dataDisplay=DATA_IMAGE;
        if(formatType.contains(JMResultSetStyle.DATA_TYPE_STRING+"|")){
            if(newValue!=null)this.val=String.valueOf(newValue);
            if(this.val!=null){
                this.valString=String.valueOf(this.val);
                this.valDB=this.valString;
            }
            this.align=ALIGN_LEFT;
            this.setValueAsString((String) this.val,display);
        }else if(formatType.contains(JMResultSetStyle.DATA_TYPE_INTEGER+"|")){
            if(newValue!=null){
                try{
                    this.val=Integer.valueOf(newValue);
                }catch(NumberFormatException e){
                    if(defaultValue){
                        this.val=0;
                        this.valString="0";
                        this.setValToInterfaces();
                    }else{
                        this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_NUMBER_INVALID));
                        this.error=true;
                        this.val=null;
                        this.valString=newValue;
                        this.setValToInterfaces();
                        return;
                    }
                }
            }
            if(this.val!=null){
                this.valString=String.valueOf(this.val);
                this.valDB=this.valString;
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
                    if(defaultValue){
                        this.val=0.0;
                        this.valString="0.0";
                        this.setValToInterfaces();
                    }else{
                        this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_NUMBER_INVALID));
                        this.error=true;
                        this.val=null;
                        this.valString=newValue;
                        this.setValToInterfaces();
                        return;
                    }
                }
            }
            if(this.val!=null){
                this.valString=String.valueOf(this.val);
                this.valDB=this.valString;
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
            if(this.val!=null){
                this.valString=String.valueOf(this.val);
                if(this.valString.equals("true"))this.valDB="1";
                else this.valDB="0";
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
            JMDate dt=null;
            if(newValue!=null)try {
                dt=JMDate.create(newValue);
            } catch (ParseException ex) {
                if(defaultValue){
                    dt=new JMDate();
                }else{
                    this.showError(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_DATE_INVALID));
                    this.error=true;
                    this.val=null;
                    this.valString=newValue;
                    this.setValToInterfaces();
                    return;
                }
            }
            JMDate dVal=dt;
            if(dt==null)dVal=(JMDate) this.val;
            if(formatType.contains(JMResultSetStyle.FORMAT_DATE_TIME+"|")){
                //DATETIME
                
                this.valString=dVal.dateTimeDB();
                this.val=dVal;
                this.valDB=this.valString;
                this.setValueAsJMDateTime(dVal,display);
                if(formatType.contains(JMResultSetStyle.FORMAT_DATE_12+"|")){
                    //12
                    this.setValueAsJMDateTime12(dVal,formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_S+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_M+"|"),display);
                }else{
                    //24
                    this.setValueAsJMDateTime24(dVal,formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_S+"|"),formatType.contains(JMResultSetStyle.FORMAT_DATE_M+"|"),display);
                }
            }else{
                //DATE
                this.valString=dVal.dateDB();
                this.val=dVal;
                this.valDB=this.valString;
                if(formatType.contains(JMResultSetStyle.FORMAT_DATE_DB+"|")){
                    this.setValueAsJMDate(dVal,display);
                }else{
                    this.setValueAsJMDate(dVal,!formatType.contains(JMResultSetStyle.FORMAT_DATE_SHORT+"|"),display);
                }
            }
        }else{
            this.val=newValue;
            this.valString=String.valueOf(this.val);
            this.valDB=this.valString;
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
    public String getValueString(){
        return this.valString;
    }
    public String getValueDB(){
        return this.valDB;
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
        this.valDBBU=this.valDB;
        JMFunctions.trace("BACKED UP");
    }
    public void restore(){
        this.val=this.valBU;
        this.txt=this.txtBU;
        this.valString=this.valStringBU;
        this.valDB=this.valDBBU;
        this.refreshInterfaces(this.cell.getRow().getTable().getStyle(), null,true,false);
    }
    public boolean isEdited(){
        JMFunctions.trace(this.val+"="+this.valBU);
        JMFunctions.trace(this.txt+"="+this.txtBU);
        JMFunctions.trace(this.valString+"="+this.valStringBU);
        JMFunctions.trace(this.valDB+"="+this.valDBBU);
        return this.val!=this.valBU || !this.txt.equals(this.txtBU) || !this.valString.equals(this.valStringBU) || !this.valDB.equals(this.valDBBU);
    }
    public boolean isInterfaceRegistered(JMInputInterface ii){
        if(this.interfaces==null)return false;
        for(JMInputInterface i:this.interfaces){
            if(i==ii)return true;
        }
        return false;
    }
    public boolean isError(){
        return this.error;
    }
}
