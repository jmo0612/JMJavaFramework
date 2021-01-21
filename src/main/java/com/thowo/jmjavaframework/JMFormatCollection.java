package com.thowo.jmjavaframework;


import com.thowo.jmjavaframework.lang.JMConstMessage;
import com.thowo.jmjavaframework.lang.JMMessage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//import static android.content.ContentValues.TAG;

/**
 * Created by jimi on 10/30/2017.
 */

public class JMFormatCollection {

    /*
    NUMBER="#,##0.00;-#,##0.00"
    MONEY_RUPIAH="'Rp. ' #,##0.00;'Rp. ' -#,##0.00"
    DATE_STANDARD="yyyy/MM/dd"
    DATE_SHORT="dd-MM-yyyy"
    DATE_MEDIUM="dd-MMM-yyyy"
    DATE_MEDIUM_COMPLETE="EEE, dd-MMM-yyyy"
    DATE_LONG="dd MMMMM yyyy"
    DATE_LONG_COMPLETE="EEEEE, dd MMMMM yyyy"
    DATETIME_STANDARD="yyyy/MM/dd HH:mm:ss"
    DATETIME_SHORT="dd-MM-yyyy HH:mm:ss"
    DATETIME_MEDIUM="dd-MMM-yyyy HH:mm:ss"
    DATETIME_MEDIUM_COMPLETE="EEE, dd-MMM-yyyy HH:mm:ss"
    DATETIME_LONG="dd MMMMM yyyy HH:mm:ss"
    DATETIME_LONG_COMPLETE="EEEEE, dd MMMMM yyyy HH:mm:ss"
    */
    
    public static final int JMO_NUMBER=0;
    public static final int JMO_MONEY_RUPIAH=1;
    public static final int JMO_DATE_STANDARD=2;
    public static final int JMO_DATE_SHORT=3;
    public static final int JMO_DATE_MEDIUM=4;
    public static final int JMO_DATE_MEDIUM_COMPLETE=5;
    public static final int JMO_DATE_LONG=6;
    public static final int JMO_DATE_LONG_COMPLETE=7;
    public static final int JMO_DATETIME_STANDARD=8;
    public static final int JMO_DATETIME_SHORT=9;
    public static final int JMO_DATETIME_MEDIUM=10;
    public static final int JMO_DATETIME_MEDIUM_COMPLETE=11;
    public static final int JMO_DATETIME_LONG=12;
    public static final int JMO_DATETIME_LONG_COMPLETE=13;


    public static final int JMO_NAME_FIRST=2000;
    public static final int JMO_NAME_MIDS=200;
    public static final int JMO_NAME_LAST=20;
    public static final int JMO_NAME_SHORT_FIRST=1000;
    public static final int JMO_NAME_SHORT_MIDS=100;
    public static final int JMO_NAME_SHORT_LAST=10;
    public static final int JMO_NAME_COMPLETE=2220;
    public static final int JMO_NAME_INITIAL=1110;
    public static final int JMO_NAME_SHORT_ALL_BUT_LAST=1120;
    public static final int JMO_NAME_LAST_FIRST=2021; //reverse (last digit)
    public static final int JMO_NAME_FIRST_AND_LAST=2020;

    public static final int JMO_STRING_NO_CAPS=0;
    public static final int JMO_STRING_ALL_CAPS=1;
    public static final int JMO_STRING_FIRST_CAPS=2;
    public static final int JMO_STRING_FIRST_CAPS_ALWAYS=3;
    


    public static String strFormat(int format){
        List<String> frmt= new ArrayList<String>();
        frmt.add("#,##0.00;-#,##0.00");
        frmt.add("'Rp. ' #,##0.00;'Rp. ' -#,##0.00");
        frmt.add("yyyy/MM/dd");
        frmt.add("dd-MM-yyyy");
        frmt.add("dd-MMM-yyyy");
        frmt.add("EEE, dd-MMM-yyyy");
        frmt.add("dd MMMMM yyyy");
        frmt.add("EEEEE, dd MMMMM yyyy");
        frmt.add("yyyy/MM/dd HH:mm:ss");
        frmt.add("dd-MM-yyyy HH:mm:ss");
        frmt.add("dd-MMM-yyyy HH:mm:ss");
        frmt.add("EEE, dd-MMM-yyyy HH:mm:ss");
        frmt.add("dd MMMMM yyyy HH:mm:ss");
        frmt.add("EEEEE, dd MMMMM yyyy HH:mm:ss");
        frmt.add("yyyy-MM-dd");
        frmt.add("yyyy-MM-dd HH:mm:ss");
        String ret="";
        if(format>=0 && format<frmt.size()){
            ret=frmt.get(format);
        }
        return ret;
    }
    public static String stringWithFormat(String str,int JMO_STRING){
        String ret="";
        if(str.equals(""))return "";
        if(JMO_STRING>2 || JMO_STRING<0)return "";
        if(JMO_STRING==JMO_STRING_ALL_CAPS){
            ret=str.toUpperCase();
        }else if(JMO_STRING==JMO_STRING_FIRST_CAPS){
            String a=str.substring(0,1).toUpperCase();
            String b="";
            if(str.length()>1)b=str.substring(1,str.length());
            ret=a+b;
        }else if(JMO_STRING==JMO_STRING_NO_CAPS){
            ret=str.toLowerCase();
        }else if(JMO_STRING==JMO_STRING_FIRST_CAPS_ALWAYS){
            ret=stringFirstCapsAndFixButDot(str);
        }
        return ret;
    }
    public static Date dateDBFormat(String date) throws ParseException{
        Date ret=null;
        try {
            ret=dateFromString(date,"yyyy-MM-dd HH:mm:ss");
        } catch (ParseException ex) {
            ret=dateFromString(date,"yyyy-MM-dd");
        }
        return ret;
    }
    public static Date dateIndoFormat(String date) throws ParseException{
        Date ret=null;
        try {
            ret=dateFromString(date,"dd/MM/yyyy HH:mm:ss");
        } catch (ParseException ex) {
            ret=dateFromString(date,"dd/MM/yyyy");
        }
        return ret;
    }
    public static Date dateFromString(String date){
        Date ret=null;
        for(int i=2;i<=15;i++){
            try {
                ret=dateFromString(date,strFormat(i));
                JMFunctions.trace(strFormat(i));
                break;
            } catch (ParseException ex) {
                //Logger.getLogger(JMFormatCollection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    public static Date dateFromString(String date, String dateFormat) throws ParseException{
        Date ret=null;
        if(dateFormat.equals(""))dateFormat=strFormat(JMO_DATE_STANDARD);
        SimpleDateFormat sdf= new SimpleDateFormat(dateFormat);
        ret=sdf.parse(date);
        return ret;
    }
    public static Date dateTimeFromString(String dateTime){
        return dateTimeFromString(dateTime,"");
    }
    public static Date dateTimeFromString(String dateTime, String dateTimeFormat){
        Date ret=null;
        if(dateTimeFormat.equals(""))dateTimeFormat=strFormat(JMO_DATETIME_STANDARD);
        SimpleDateFormat sdf= new SimpleDateFormat(dateTimeFormat);
        try {
            ret=sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
        }
        return ret;
    }
    public static String stringFromDate(Date date){
        return stringFromDate(date,"");
    }
    public static String stringFromDate(Date date, String dateFormat){
        String ret=null;
        if(dateFormat.equals(""))dateFormat=strFormat(JMO_DATE_STANDARD);
        SimpleDateFormat sdf= new SimpleDateFormat(dateFormat);
        ret=sdf.format(date);
        return ret;
    }
    public static String stringFromdateTime(Date dateTime, String dateTimeFormat){
        String ret=null;
        if(dateTimeFormat.equals(""))dateTimeFormat=strFormat(JMO_DATETIME_STANDARD);
        try {
            SimpleDateFormat sdf= new SimpleDateFormat(dateTimeFormat);
            ret=sdf.format(dateTime);
        }catch (IllegalArgumentException e){
            JMFunctions.trace("Datetime error : "+e.getMessage());
        }
        return ret;
    }
    public static String stringFirstCapsAndFix(String str){
        String words[] = str.replaceAll("\\s+", " ").trim().split(" ");
        String newSentence = "";
        for (String word : words) {
            for (int i = 0; i < word.length(); i++)
                newSentence = newSentence + ((i == 0) ? word.substring(i, i + 1).toUpperCase():
                        (i != word.length() - 1) ? word.substring(i, i + 1).toLowerCase() : word.substring(i, i + 1).toLowerCase() + " ");
        }

        return newSentence.trim();
    }
    public static String stringFirstCapsAndFixButDot(String str){
        String words[] = str.replaceAll("\\s+", " ").trim().split(" ");
        String newSentence = "";
        for (String word : words) {
            String bef="";
            for (int i = 0; i < word.length(); i++) {
                String tmp=((i == 0) ? word.substring(i, i + 1).toUpperCase() :
                        (i != word.length() - 1) ? word.substring(i, i + 1).toLowerCase() : word.substring(i, i + 1).toLowerCase() + " ");
                if(bef.equals(".")){
                    if(tmp.length()>1){
                        tmp=tmp.substring(0,1).toUpperCase()+tmp.substring(1,tmp.length()).toLowerCase();
                    }else{
                        tmp=tmp.substring(0,1).toUpperCase();
                    }
                }
                newSentence = newSentence + tmp;
                bef=word.substring(i, i + 1);
            }
        }

        return newSentence.trim();
    }
    public static String stringFirstCaps(String str){
        String words[] = str.replaceAll("\\s+", " ").trim().split(" ");
        String newSentence = "";
        for (String word : words) {
            for (int i = 0; i < word.length(); i++)
                newSentence = newSentence + ((i == 0) ? word.substring(i, i + 1).toUpperCase():
                        (i == word.length() - 1) ? word.substring(i, i + 1) + " " : word.substring(i, i + 1));
        }

        return newSentence.trim();
    }
    public static String stringName(String theName){
        return stringName(null, "", theName, null, "", JMO_NAME_FIRST, null, "",JMO_STRING_FIRST_CAPS);
    }
    public static String stringName(String theName, int stringCapsMode){
        return stringName(null, "", theName, null, "", JMO_NAME_FIRST, null, "",stringCapsMode);
    }
    public static String stringName(List<String> firstTitles, String firstTitlesSeparator, String theName, List<String> lastTitles, String lastTitlesSeparator){
        return stringName(firstTitles, firstTitlesSeparator, theName, null, "", JMO_NAME_FIRST, lastTitles, lastTitlesSeparator,JMO_STRING_FIRST_CAPS);
    }
    public static String stringName(List<String> firstTitles, String firstTitlesSeparator, String theName, List<String> lastTitles, String lastTitlesSeparator, int stringCapsMode){
        return stringName(firstTitles, firstTitlesSeparator, theName, null, "", JMO_NAME_FIRST, lastTitles, lastTitlesSeparator,stringCapsMode);
    }
    public static String stringName(String firstName, List<String> midNames, String lastName){
        return stringName(null, "", firstName, midNames, lastName, JMO_NAME_COMPLETE, null, "",JMO_STRING_FIRST_CAPS);
    }
    public static String stringName(String firstName, List<String> midNames, String lastName, int nameFormat){
        return stringName(null, "", firstName, midNames, lastName, nameFormat, null, "",JMO_STRING_FIRST_CAPS);
    }
    public static String stringName(String firstName, List<String> midNames, String lastName, int nameFormat, int stringCapsMode){
        return stringName(null, "", firstName, midNames, lastName, nameFormat, null, "",stringCapsMode);
    }
    public static String stringName(List<String> firstTitles, String firstTitlesSeparator, String firstName, List<String> midNames, String lastName, List<String> lastTitles, String lastTitlesSeparator){
        return stringName(firstTitles, firstTitlesSeparator, firstName, midNames, lastName, JMO_NAME_COMPLETE, lastTitles, lastTitlesSeparator,JMO_STRING_FIRST_CAPS);
    }
    public static String stringName(List<String> firstTitles, String firstTitlesSeparator, String firstName, List<String> midNames, String lastName, int nameFormat, List<String> lastTitles, String lastTitlesSeparator){
        return stringName(firstTitles, firstTitlesSeparator, firstName, midNames, lastName, nameFormat, lastTitles, lastTitlesSeparator,JMO_STRING_FIRST_CAPS);
    }
    private static String formatTheNames(String firstName, List<String> midNames, String lastName, int nameFormat){
        String format=new DecimalFormat("0000").format(nameFormat);
        if(format.length()>4)format=format.substring(format.length()-4,format.length()-1);

        int fN=Integer.parseInt(format.substring(0,1));
        int mN=Integer.parseInt(format.substring(1,2));
        int lN=Integer.parseInt(format.substring(2,3));
        int rev=Integer.parseInt(format.substring(3,4));
        String mNames="";
        if(midNames!=null){
            for(int i=0;i<midNames.size();i++){
                String sep="";
                if(mN==2){
                    mNames+=midNames.get(i);
                    sep=" ";
                }else if(mN==1){
                    mNames+=midNames.get(i).substring(0,1);
                    sep=".";
                }
                if(i<midNames.size()-1){
                    mNames+=sep;
                }
            }
        }
        if(fN==0){
            firstName="";
        }else if(fN==1){
            firstName=firstName.substring(0,1).toUpperCase();
        }
        if(lN==0){
            lastName="";
        }else if(lN==1){
            lastName=lastName.substring(0,1).toUpperCase();
        }

        if(!firstName.equals("")){
            if(rev==0){
                if(fN==1){
                    if(!mNames.equals("") || !lastName.equals(""))firstName+=".";
                }
            }
        }
        if(!mNames.equals("")){
            if(rev==0){
                if(mN==1){
                    if(!lastName.equals("")){
                        mNames+=".";
                    }
                }
            }
        }
        if(!lastName.equals("")){
            if(rev==1){
                if(lN==2) {
                    if (!firstName.equals("") || !mNames.equals("")) {
                        lastName += ",";
                    }
                }
            }
        }

        List<String> lsName=new ArrayList<String>();
        List lsType=new ArrayList(Arrays.asList(fN,mN,lN));
        if(!firstName.equals(""))lsName.add(firstName);
        if(!mNames.equals(""))lsName.add(mNames);
        if(!lastName.equals(""))lsName.add(lastName);

        String ret="";
        int walk;
        int adder;
        if(rev==0) {
            walk = 0;
            adder=1;
        } else {
            walk=lsName.size()-1;
            adder=-1;
        }

        for(int i=0;i<lsName.size();i++){
            boolean shortNow=(int)lsType.get(walk)==1;
            boolean shortNext= ((i==lsName.size()-1) ? false :  (int)lsType.get(walk+1)==1);
            boolean removeSpace=shortNow && shortNext;
            if(removeSpace){
                ret+=lsName.get(walk);
            }else{
                ret+=((i==lsName.size()-1)?lsName.get(walk):lsName.get(walk)+" ");
            }
            walk+=adder;

        }
        return ret;
    }
    public static String stringName(List<String> firstTitles, String firstTitlesSeparator, String firstName, List<String> midNames, String lastName, int nameFormat, List<String> lastTitles, String lastTitlesSeparator, int stringCapsMode){
        String fT="";
        String lT="";
        String nm=stringFirstCapsAndFixButDot(formatTheNames(firstName,midNames,lastName,nameFormat));
        if(stringCapsMode==JMO_STRING_ALL_CAPS){
            nm=nm.toUpperCase();
        }else if(stringCapsMode==JMO_STRING_NO_CAPS){
            nm=nm.toLowerCase();
        }
        if(firstTitles!=null){
            for(int i=0;i<firstTitles.size();i++){
                if(i<firstTitles.size()-1){
                    fT+=firstTitles.get(i)+firstTitlesSeparator+" ";
                }else{
                    fT+=firstTitles.get(i)+firstTitlesSeparator;
                }
            }
        }
        if(lastTitles!=null){
            for(int i=0;i<lastTitles.size();i++){
                if(i<lastTitles.size()-1){
                    lT+=lastTitles.get(i)+lastTitlesSeparator+" ";
                }else{
                    lT+=lastTitles.get(i);
                }
            }
        }
        return fT+" " + nm + ((!lT.equals("")) ? ", " + lT:"");
    }
    public static String leadingZero(Integer number, int numOfZero){
        String ret="";
        if(number<0)ret="-";
        ret+=String.format("%0"+numOfZero+"d", number);
        return ret;
    }
    public static String currency(Double number){
        NumberFormat df = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol(JMFunctions.getMessege(JMConstMessage.MSG_CURRENCY+JMConstMessage.MSG_CURRENCY_SYMBOL));
        dfs.setGroupingSeparator(JMFunctions.getMessege(JMConstMessage.MSG_CURRENCY+JMConstMessage.MSG_CURRENCY_SEPARATOR).charAt(0));
        dfs.setMonetaryDecimalSeparator(JMFunctions.getMessege(JMConstMessage.MSG_CURRENCY+JMConstMessage.MSG_CURRENCY_COMMA).charAt(0));
        ((DecimalFormat) df).setDecimalFormatSymbols(dfs);
        return df.format(number);
    }
    public static String decimal(Double number){
        NumberFormat df = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setGroupingSeparator(JMFunctions.getMessege(JMConstMessage.MSG_CURRENCY+JMConstMessage.MSG_CURRENCY_SEPARATOR).charAt(0));
        dfs.setMonetaryDecimalSeparator(JMFunctions.getMessege(JMConstMessage.MSG_CURRENCY+JMConstMessage.MSG_CURRENCY_COMMA).charAt(0));
        ((DecimalFormat) df).setDecimalFormatSymbols(dfs);
        return df.format(number);
    }
    public static boolean isThisDateValid(String dateToValidate, String dateFromat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }
    public static Double strToDouble(String value, Double defValue){
        try {
            return Double.valueOf(value);
        }catch (NumberFormatException ex){
            return defValue;
        }
    }
    public static Double strToDouble(String value){
        return strToDouble(value,0.0);
    }
    public static Integer strToInteger(String value, Integer defValue){
        if(!value.equals("")){
            String[] arrTmp=strToArray(value,"[.]");
            if(arrTmp.length>1)value=arrTmp[0];
        }
        try {
            return Integer.valueOf(value);
        }catch (NumberFormatException ex){
            return defValue;
        }
    }
    public static Integer strToInteger(String value){
        return strToInteger(value,0);
    }
    public static Boolean strToBoolean(String value, String strTrue, String strFalse){
        if(strTrue.equals("")|| strFalse.equals("")){
            strTrue="True";
            strFalse="False";
        }

        if(value.length()==1){
            if(strTrue.substring(0,1).toUpperCase().equals(value.toUpperCase())){
                return true;
            }else if(strFalse.substring(0,1).toUpperCase().equals(value.toUpperCase())){
                return false;
            }else{
                if(value.toUpperCase().equals("T")){
                    return true;
                }else if(value.toUpperCase().equals("F")){
                    return false;
                }else if(value.toUpperCase().equals("Y")){
                    return true;
                }else if(value.toUpperCase().equals("N")){
                    return false;
                }else{
                    return false;
                }
            }
        }else{
            if(strTrue.toUpperCase().contains(value.toUpperCase())){
                return true;
            }else if(strFalse.toUpperCase().contains(value.toUpperCase())){
                return false;
            }else{
                if(value.toUpperCase().equals("TRUE")){
                    return true;
                }else if(value.toUpperCase().equals("FALSE")){
                    return false;
                }else if(value.toUpperCase().equals("YES")){
                    return true;
                }else if(value.toUpperCase().equals("NO")){
                    return false;
                }else{
                    return false;
                }
            }
        }


    }
    public static Boolean strToBoolean(String value){
        return strToBoolean(value,"","");
    }
    public static JMDate strSerialToJMDate(String dateSerial,boolean defaultNow){
        JMDate ret=JMDate.createFromSerial(dateSerial);
        if(ret.getDate()==null){
            if(defaultNow)ret=JMDate.now();
        }
        return ret;
    }
    public static JMDate strSerialToJMDate(String dateSerial){
        return strSerialToJMDate(dateSerial,false);
    }
    public static String booleanToString(Boolean value, String strTrue, String strFalse){
        if(strTrue.equals("")||strFalse.equals("")){
            strTrue="True";
            strFalse="False";
        }
        return (value?strTrue:strFalse);
    }
    public static String booleanToString(Boolean value){
        return booleanToString(value,"","");
    }
    public static String[] strToArray(String str, String splitter){
        return str.split(splitter);
    }
    private static Integer monthFromString(String str){
        for(int i=1;i<=12;i++){
            String m=JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_MONTH+JMConstMessage.MSG_DATE_TYPE_COMPLETE+JMFormatCollection.leadingZero(i, 3));
            if(m.toUpperCase().equals(str.toUpperCase())){
                return i;
            }
        }
        for(int i=1;i<=12;i++){
            try {
                Date d= new SimpleDateFormat("dd/MM/yyyy").parse("01/"+JMFormatCollection.leadingZero(i,2)+"/2000");
                String m= new SimpleDateFormat("MMMM").format(d);
                if(m.toUpperCase().equals(str.toUpperCase())){
                    return i;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public static Integer doubleToInt(Double value, int errorValue){
        if(value>Integer.MAX_VALUE)return errorValue;
        String d=String.valueOf(value);
        d=d.substring(0,d.indexOf("."));
        return Integer.valueOf(d);
    }
    public static Integer doubleToInt(Double value){
        return JMFormatCollection.doubleToInt(value, 0);
    }
    
    private static String kata(int nomor){
        if(nomor>9)return "";
        String[] k=new String[]{"", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan"};
        return k[nomor];
    }
    private static String kata(char nomor){
        return kata(Integer.valueOf(String.valueOf(nomor)));
    }
    public static String terbilang(Double angka){
        DecimalFormat df = new DecimalFormat("#.##");
        String nDbl=df.format(angka);
        return terbilang(nDbl);
    }
    public static String terbilang(Integer angka){
        //DecimalFormat df = new DecimalFormat("#.##");
        //String nDbl=df.format(angka);
        return terbilang(String.valueOf(angka));
    }
    private static String terbilang(String angka){
        //Long num=Math.round(Math.floor(Math.abs(angka)));
        //String nInt=String.valueOf(num);
        
        String[] seps=JMFormatCollection.strToArray(angka, "\\.");
        
        char des1='\0';
        char des2='\0';
        if(seps.length>1){
            try{
                des1=angka.charAt(seps[0].length()+1);
                des2=angka.charAt(seps[0].length()+2);
            }catch(java.lang.StringIndexOutOfBoundsException ex){
                
            }
        }
        String koma="";
        if(des2=='\0'){
            if(des1=='\0' || des1=='0'){
                koma="";
            }else{
                koma=" koma "+ kata(des1);
            }
        }else if(des2=='0'){
            if(des1 == '0'){
                koma="";
            }else if(des1=='1'){
                koma=" koma sepuluh";
            }else{
                koma = " koma " + kata(des1) + " puluh";
            }
        }else{
            if(des1=='0'){
                koma = " koma nol " + kata(des2);
            }else if(des1=='1'){
                if(des2=='1'){
                    koma = " koma sebelas";
                }else{
                    koma = " koma " + kata(des2) + " belas";
                }
            }else{
                koma = " koma " + kata(des1) + " puluh " + kata(des2);
            }
        }
        char no1 = '\0';
        char no2 = '\0';
        char no3 = '\0';
        char no4 = '\0';
        char no5 = '\0';
        char no6 = '\0';
        char no7 = '\0';
        char no8 = '\0';
        char no9 = '\0';
        char no10 = '\0';
        char no11 = '\0';
        char no12 = '\0';
        char no13 = '\0';
        char no14 = '\0';
        char no15 = '\0';
        
        try{
            no1 = seps[0].charAt(seps[0].length()-1);
            //JMFunctions.trace(no1+"");
            no2 = seps[0].charAt(seps[0].length()-2);
            //JMFunctions.trace(no2+"");
            no3 = seps[0].charAt(seps[0].length()-3);
            //JMFunctions.trace(no3+"");
            no4 = seps[0].charAt(seps[0].length()-4);
            no5 = seps[0].charAt(seps[0].length()-5);
            no6 = seps[0].charAt(seps[0].length()-6);
            no7 = seps[0].charAt(seps[0].length()-7);
            no8 = seps[0].charAt(seps[0].length()-8);
            no9 = seps[0].charAt(seps[0].length()-9);
            no10 = seps[0].charAt(seps[0].length()-10);
            no11 = seps[0].charAt(seps[0].length()-11);
            no12 = seps[0].charAt(seps[0].length()-12);
            no13 = seps[0].charAt(seps[0].length()-13);
            no14 = seps[0].charAt(seps[0].length()-14);
            no15 = seps[0].charAt(seps[0].length()-15);
        }catch(java.lang.StringIndexOutOfBoundsException ex){

        }
        
        
        String nomor1 = "";
        String nomor2 = "";
        String nomor3 = "";
        String nomor4 = "";
        String nomor5 = "";
        String nomor6 = "";
        String nomor7 = "";
        String nomor8 = "";
        String nomor9 = "";
        String nomor10 = "";
        String nomor11 = "";
        String nomor12 = "";
        String nomor13 = "";
        String nomor14 = "";
        String nomor15 = "";
        
        //Satuan
        if(seps[0].length() >= 1){
            if(seps[0].length() == 1 && no1 == '1'){
                nomor1 = "satu";
            }else if(seps[0].length() == 1 && no1 == '0'){
                nomor1 = "Nol";
            }else if(no2 == '1'){
                if(no1 == '1'){
                    nomor1 = "sebelas";
                }else if(no1 == '0'){
                    nomor1 = "sepuluh";
                }else{
                    nomor1 = kata(no1) + " belas";
                }
            }else{
                nomor1 = kata(no1);
            }
        }else{
            nomor1 = "";
        }

        //Puluhan
        if(seps[0].length() >= 2){
            if(no2 == '1' || no2 == '0'){
                nomor2 = "";
            }else{
                nomor2 = kata(no2) + " puluh ";
            }
        }else{
            nomor2 = "";
        }
        //Ratusan
        if(seps[0].length() >= 3){
            if(no3 == '1'){
                nomor3 = "seratus ";
            }else if(no3 == '0'){
                nomor3 = "";
            }else{
                nomor3 = kata(no3) + " ratus ";
            }
        }else{
            nomor3 = "";
        }
        //Ribuan
        if(seps[0].length() >= 4){
            if(no6 == '0' && no5 == '0' && no4 == '0'){
                nomor4 = "";
            }else if((no4 == '1' && seps[0].length() == 4) || (no6 == '0' && no5 == '0' && no4 == '1')){
                nomor4 = "seribu ";
            }else if(no5 == '1'){
                if(no4 == '1'){
                    nomor4 = "sebelas ribu ";
                }else if(no4 == '0'){
                    nomor4 = "sepuluh ribu ";
                }else{
                    nomor4 = kata(no4) + " belas ribu ";
                }
            }else{
                nomor4 = kata(no4) + " ribu ";
            }
        }else{
            nomor4 = "";
        }
        //Puluhan ribu
        if(seps[0].length() >= 5){
            if(no5 == '1' || no5 == '0'){
                nomor5 = "";
            }else{
                nomor5 = kata(no5) + " puluh ";
            }
        }else{
            nomor5 = "";
        }
        //Ratusan Ribu
        if(seps[0].length() >= 6){
            if(no6 == '1'){
                nomor6 = "seratus ";
            }else if(no6 == '0'){
                nomor6 = "";
            }else{
                nomor6 = kata(no6) + " ratus ";
            }
        }else{
            nomor6 = "";
        }
        //Jutaan
        if(seps[0].length() >= 7){
            if(no9 == '0' && no8 == '0' && no7 == '0'){
                nomor7 = "";
            }else if(no7 == '1' && seps[0].length() == 7){
                nomor7 = "satu juta ";
            }else if(no8 == '1'){
                if(no7 == '1'){
                    nomor7 = "sebelas juta ";
                }else if(no7 == '0'){
                    nomor7 = "sepuluh juta ";
                }else{
                    nomor7 = kata(no7) + " belas juta ";
                }
            }else{
                nomor7 = kata(no7) + " juta ";
            }
        }else{
            nomor7 = "";
        }
        //Puluhan juta
        if(seps[0].length() >= 8){
            if(no8 == '1' || no8 == '0'){
                nomor8 = "";
            }else{
                nomor8 = kata(no8) + " puluh ";
            }
        }else{
            nomor8 = "";
        }
        //Ratusan juta
        if(seps[0].length() >= 9){
            if(no9 == '1'){
                nomor9 = "seratus ";
            }else if(no9 == '0'){
                nomor9 = "";
            }else{
                nomor9 = kata(no9) + " ratus ";
            }
        }else{
            nomor9 = "";
        }
        //Milyar
        if(seps[0].length() >= 10){
            if(no12 == '0' && no11 == '0' && no10 == '0'){
                nomor10 = "";
            }else if(no10 == '1' && seps[0].length() == 10){
                nomor10 = "satu milyar ";
            }else if(no11 == '1'){
                if(no10 == '1'){
                    nomor10 = "sebelas milyar ";
                }else if(no10 == '0'){
                    nomor10 = "sepuluh milyar ";
                }else{
                    nomor10 = kata(no10) + " belas milyar ";
                }
            }else{
                nomor10 = kata(no10) + " milyar ";
            }
        }else{
            nomor10 = "";
        }
        //Puluhan Milyar
        if(seps[0].length() >= 11){
            if(no11 == '1' || no11 == '0'){
                nomor11 = "";
            }else{
                nomor11 = kata(no11) + " puluh ";
            }
        }else{
            nomor11 = "";
        }
        //Ratusan Milyar
        if(seps[0].length() >= 12){
            if(no12 == '1'){
                nomor12 = "seratus ";
            }else if(no12 == '0'){
                nomor12 = "";
            }else{
                nomor12 = kata(no12) + " ratus ";
            }
        }else{
            nomor12 = "";
        }
        //Triliun
        if(seps[0].length() >= 13){
            if(no15 == '0' && no14 == '0' && no13 == '0'){
                nomor13 = "";
            }else if(no13 == '1' && seps[0].length() == 13){
                nomor13 = "satu triliun ";
            }else if(no14 == '1'){
                if(no13 == '1'){
                    nomor13 = "sebelas triliun ";
                }else if(no13 == '0'){
                    nomor13 = "sepuluh triliun ";
                }else{
                    nomor13 = kata(no13) + " belas triliun ";
                }
            }else{
                nomor13 = kata(no13) + " triliun ";
            }
        }else{
            nomor13 = "";
        }
        //Puluhan triliun
        if(seps[0].length() >= 14){
            if(no14 == '1' || no14 == '0'){
                nomor14 = "";
            }else{
                nomor14 = kata(no14) + " puluh ";
            }
        }else{
            nomor14 = "";
        }
        //Ratusan triliun
        if(seps[0].length() >= 15){
            if(no15 == '1'){
                nomor15 = "seratus ";
            }else if(no15 == '0'){
                nomor15 = "";
            }else{
                nomor15 = kata(no15) + " ratus ";
            }
        }else{
            nomor15 = "";
        }

        String bilang="";
        if(seps[0].length() > 15){
            bilang = "Digit Angka Terlalu Banyak";
        }else{
            if(angka==null || angka.equals("")){
                bilang = "";
            }else if(Double.valueOf(angka) < 0){
                String tmp=nomor15 + nomor14 + nomor13 + nomor12 + nomor11 + nomor10 + nomor9 + nomor8 + nomor7 + nomor6 + nomor5 + nomor4 + nomor3 + nomor2 + nomor1 + koma;
                bilang = "minus " + tmp.trim();
            }else{
                String tmp=nomor15 + nomor14 + nomor13 + nomor12 + nomor11 + nomor10 + nomor9 + nomor8 + nomor7 + nomor6 + nomor5 + nomor4 + nomor3 + nomor2 + nomor1 + koma;
                bilang = tmp.trim();
            }
        }
        return bilang.replace("  ", " ");
    }
    
    public static String toUpperFirstLetters(String str){  
        if(str.equals(""))return str;
        String words[]=str.split("\\s");  
        String capitalizeWord="";  
        for(String w:words){  
            String first=w.substring(0,1);  
            String afterfirst=w.substring(1);  
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
        }  
        return capitalizeWord.trim();  
    }
}