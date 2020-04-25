package com.thowo.jmjavaframework;


import com.thowo.jmjavaframework.lang.JMConstMessage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public static Date dateFromString(String date){
        return dateFromString(date,"");
    }

    public static Date dateFromString(String date, String dateFormat){
        Date ret=null;
        if(dateFormat.equals(""))dateFormat=strFormat(JMO_DATE_STANDARD);
        SimpleDateFormat sdf= new SimpleDateFormat(dateFormat);
        try {
            ret=sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
        }
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

}