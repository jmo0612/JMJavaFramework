/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework;

import com.thowo.jmjavaframework.lang.JMConstMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jimi
 */
public class JMDate {
    private Date dt;
    
    public JMDate(Date dt){
        this.dt=dt;
    }
    
    public JMDate(Date dt, Boolean showNullError){
        this.dt=dt;
        if(showNullError && this.dt==null)JMFunctions.traceAndShow(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_DATE_EMPTY));
    }
    
    public Date getDate(){
        if(this.dt==null)return null;
        return this.dt;
    }
    
    public int getDayOfMonth(){
        if(this.dt==null)return 0;
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return c.get(Calendar.DATE);
    }
    public int getDayOfWeek(){
        if(this.dt==null)return 0;
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return c.get(Calendar.DAY_OF_WEEK);
    }
    public int getYearFull(){
        if(this.dt==null)return -1;
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return c.get(Calendar.YEAR);
    }
    public int getMonth(){
        if(this.dt==null)return 0;
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return c.get(Calendar.MONTH)+1;
    }
    public String getYearShort(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return JMFormatCollection.leadingZero(c.get(Calendar.YEAR)%100, 2);
    }
    public String getHour24(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return JMFormatCollection.leadingZero(c.get(Calendar.HOUR_OF_DAY), 2);
    }
    public String getHour12(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return JMFormatCollection.leadingZero(c.get(Calendar.HOUR), 2);
    }
    public String getMinute(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return JMFormatCollection.leadingZero(c.get(Calendar.MINUTE), 2);
    }
    public String getSecond(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return JMFormatCollection.leadingZero(c.get(Calendar.SECOND), 2);
    }
    public String getTime24(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        return this.getHour24()+":"+this.getMinute()+":"+this.getSecond();
    }
    public String getHMS12(){
        if(this.dt==null)return "";
        return this.getHour12()+":"+this.getMinute()+":"+this.getSecond();
    }
    public String getHMS24(){
        if(this.dt==null)return "";
        return this.getHour24()+":"+this.getMinute()+":"+this.getSecond();
    }
    public String getHM12(){
        if(this.dt==null)return "";
        return this.getHour12()+":"+this.getMinute();
    }
    public String getHM24(){
        if(this.dt==null)return "";
        return this.getHour24()+":"+this.getMinute();
    }
    public String getAmPmComplete(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        int h=c.get(Calendar.HOUR_OF_DAY);
        String ampm="001";
        if(h>11){
            ampm="002";
        }
        
        return JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_HOUR+JMConstMessage.MSG_DATE_TYPE_COMPLETE+ampm);
    }
    public String getAmPmShort(){
        if(this.dt==null)return "";
        Calendar c=Calendar.getInstance();
        c.setTime(this.dt);
        int h=c.get(Calendar.HOUR_OF_DAY);
        String ampm="001";
        if(h>11){
            ampm="002";
        }
        
        return JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_HOUR+JMConstMessage.MSG_DATE_TYPE_SHORT+ampm);
    }
    public String dateDB(){
        String ret="";
        if(this.dt!=null){
            ret=this.getYearFull()+"-"+this.getMonth()+"-"+this.getDayOfMonth();
        }
        return ret;
    }
    public String dateTimeDB(){
        String ret="";
        if(this.dt!=null){
            ret=this.getYearFull()+"-"+this.getMonth()+"-"+this.getDayOfMonth()+" "+this.getHour24()+this.getMinute()+this.getSecond();
        }
        return ret;
    }
    public String dateFull(){
        String ret="";
        if(this.dt!=null){
            ret=JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_DAY+JMConstMessage.MSG_DATE_TYPE_COMPLETE+JMFormatCollection.leadingZero(this.getDayOfWeek(), 3));
            ret+=", "+JMFormatCollection.leadingZero(this.getDayOfMonth(), 2)+" "+JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_MONTH+JMConstMessage.MSG_DATE_TYPE_COMPLETE+JMFormatCollection.leadingZero(this.getMonth(), 3));
            ret+=" "+this.getYearFull();
        }
        return ret;
    }
    
    public String dateShort(){
        String ret="";
        if(this.dt!=null){
            ret=JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_DAY+JMConstMessage.MSG_DATE_TYPE_SHORT+JMFormatCollection.leadingZero(this.getDayOfWeek(), 3));
            ret+=", "+JMFormatCollection.leadingZero(this.getDayOfMonth(), 2)+"-"+JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_MONTH+JMConstMessage.MSG_DATE_TYPE_SHORT+JMFormatCollection.leadingZero(this.getMonth(), 3));
            ret+="-"+this.getYearShort();
        }
        return ret;
    }
    public String dateTimeFull24(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateFull();
            ret+=" "+this.getHMS24();
        }
        return ret;
    }
    public String dateTimeFull12(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateFull();
            ret+=" "+this.getHMS12()+" "+this.getAmPmComplete();
        }
        return ret;
    }

    public String dateTimeShort24(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateShort();
            ret+=" "+this.getHMS24();
        }
        return ret;
    }
    public String dateTimeShort12(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateShort();
            ret+=" "+this.getHMS12();
        }
        return ret;
    }
    public String dateTimeShortHM24(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateShort();
            ret+=" "+this.getHM24();
        }
        return ret;
    }
    public String dateTimeShortHM12(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateShort();
            ret+=" "+this.getHM12()+" "+this.getAmPmShort();
        }
        return ret;
    }
    public String dateTimeShortH24(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateShort();
            ret+=" "+this.getHour24();
        }
        return ret;
    }
    public String dateTimeShortH12(){
        String ret="";
        if(this.dt!=null){
            ret=this.dateShort();
            ret+=" "+this.getHour12()+" "+this.getAmPmShort();
        }
        return ret;
    }

    public static String toStringYMDhms(String dt){
        final String dateInputFormat=JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_INPUTFORMAT);
        final String timeInputFormat=JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_TIME_INPUTFORMAT);
        final char dateSep='/';
        final char timeSep=':';
        final char dtSep=' ';
        final char DDSymbol='D';
        final char MMSymbol='M';
        final char YYSymbol='Y';
        final char hhSymbol='h';
        final char mmSymbol='m';
        final char ssSymbol='s';

        int DD=0;
        int MM=0;
        int YY=0;
        int hh=0;
        int mm=0;
        int ss=0;

        String[] myDt=dt.split(String.valueOf(dtSep));
        if(myDt.length==0)return dt;
        String[] d=myDt[0].split(String.valueOf(dateSep));
        if(d.length==0)return dt;

        List<Character> dOrder=new ArrayList();
        List<Character> tOrder=new ArrayList();
        for(int i=0;i<dateInputFormat.length();i++){
            dOrder.add(dateInputFormat.charAt(i));
        }
        for(int i=0;i<timeInputFormat.length();i++){
            tOrder.add(timeInputFormat.charAt(i));
        }

        int tmpInd=dOrder.indexOf(DDSymbol);
        if(d.length>tmpInd)DD=Integer.valueOf(d[tmpInd]);
        tmpInd=dOrder.indexOf(MMSymbol);
        if(d.length>tmpInd)MM=Integer.valueOf(d[tmpInd]);
        tmpInd=dOrder.indexOf(YYSymbol);
        if(d.length>tmpInd)YY=Integer.valueOf(d[tmpInd]);

        String defDateFormat=""+YYSymbol+MMSymbol+DDSymbol;
        String defTimeFormat=""+hhSymbol+mmSymbol+ssSymbol;
        String tmpDate=defDateFormat.replaceAll(String.valueOf(DDSymbol),dateSep+JMFormatCollection.leadingZero(DD,2));
        tmpDate=tmpDate.replaceAll(String.valueOf(MMSymbol),dateSep+JMFormatCollection.leadingZero(MM,2));
        tmpDate=tmpDate.replaceAll(String.valueOf(YYSymbol), String.valueOf(dateSep)+YY);
        tmpDate=tmpDate.substring(1);


        if(myDt.length>1){
            String[] t=myDt[1].split(String.valueOf(timeSep));
            if(t.length==0)return tmpDate;

            tmpInd=dOrder.indexOf(hhSymbol);
            if(t.length>tmpInd)hh=Integer.valueOf(t[tmpInd]);
            tmpInd=dOrder.indexOf(mmSymbol);
            if(t.length>tmpInd)mm=Integer.valueOf(t[tmpInd]);
            tmpInd=dOrder.indexOf(ssSymbol);
            if(t.length>tmpInd)ss=Integer.valueOf(t[tmpInd]);

            String tmpTime=defTimeFormat.replaceAll(String.valueOf(hhSymbol),timeSep+JMFormatCollection.leadingZero(hh,2));
            tmpTime=tmpTime.replaceAll(String.valueOf(mmSymbol),timeSep+JMFormatCollection.leadingZero(mm,2));
            tmpTime=tmpTime.replaceAll(String.valueOf(ssSymbol),timeSep+JMFormatCollection.leadingZero(ss,2));
            tmpTime=tmpTime.substring(1);
            tmpDate+=dtSep+tmpTime;
        }



        return tmpDate;
    }
}
