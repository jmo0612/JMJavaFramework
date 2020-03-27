package com.thowo.jmjavaframework;

//import com.thowo.jmjavaframework.component.JMActivity;
//import com.thowo.jmjavaframework.db.JMServerConnectionSetting;
//import com.thowo.jmjavaframework.db.JMServerConnectionSetting;
import com.thowo.jmjavaframework.db.JMConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import static android.content.ContentValues.TAG;

/**
 * Created by jimi on 8/2/2017.
 */

public class JMFunctions {
    //private static Context currentContext;
    //private static JMActivity currentActivity;
    //private static JMServerConnectionSetting serverConnectionSetting;
    private static JMAsyncListener asyncL;
    private static JMUIListener uiL;
    private static JMConnection mDBConnection;
    private static String tes="jimix";

    public static void init(File languageExcelFile){
        JMStringMessages.init();
    }

    public static String getStringMessage(String msgType){
        String ret=JMStringMessages.getMessage(msgType);
        if(ret.equals("")){
            ret=JMStringMessages.getDefaultMessage(msgType);
        }
        
        return ret;
    }
    
    public static Object now(int calenderField){
        return Calendar.getInstance().get(calenderField);
    }


    public static JMConnection setConnection(JMConnection connection){
        mDBConnection=connection;
        return mDBConnection;
    }

    public static JMConnection getCurrentConnection(){
        return mDBConnection;
    }

    public static void reset(){
        mDBConnection=null;
        asyncL=null;
        uiL=null;
    }

    public static void update(Object platformDisplay){
        if(platformDisplay!=null){
            setUIListener((JMUIListener) platformDisplay);
            setAsyncListener((JMAsyncListener) platformDisplay);
        }
    }
    
    public static void setUIListener(JMUIListener platformUIListener){
        uiL=platformUIListener;
    }
    
    public static void setAsyncListener(JMAsyncListener platformDisplay){
        asyncL=platformDisplay;
    }


    public static Object getCurrentPlatformDisplay(){
        return asyncL;
    }
    
    public static Object getCurrentUIListener(){
        return uiL;
    }
    

    public static void trace(String msg){
        uiL.trace(msg);
    }
    
    public static void traceAndShow(String msg){
        uiL.trace(msg);
        uiL.messageBox(msg);
    }

    public static boolean fileExist(File file){
        if(file==null)return false;
        if(file.exists())return true;
        return false;
    }

    public static boolean deleteFile(File file){
        if(!fileExist(file))return false;
        return file.delete();
    }

    public static boolean createFile(File file){
        if(fileExist(file))return true;
        String dirs=file.getParent();
        File dir=new File(dirs);
        dir.mkdirs();
        if(!fileExist(dir)) return false;
        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<File> listFiles(File dir){
        List<File> ret= new ArrayList<>();
        if(!fileExist(dir)) return ret;
        File[] files=dir.listFiles();
        for(File f:files){
            ret.add(f);
        }
        return ret;
    }

    public static String readTxtFile(File file){
        return readTxtFile(file, "", "");
    }

    public static String readTxtFile(File file, String var){
        return readTxtFile(file,var,"=");
    }

    public static String readTxtFile(File file, String var, String operator){
        if(file==null)return "";
        if(!fileExist(file))return "";
        //Context ctx=getCurrentActivity().getApplicationContext();
        try {
            //FileInputStream fileInputStream=ctx.openFileInput(file.getAbsolutePath());
            FileInputStream fileInputStream=new FileInputStream(file);
            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            try {
                String ret="";
                String lineData=bufferedReader.readLine();
                while (lineData!=null){
                    trace("LINE: "+lineData);
                    if(var.equals("")){
                        if(!ret.equals(""))ret+="\n";
                        ret+=lineData;
                    }else{
                        trace("SUBSTR: "+lineData.substring(0,var.length()+operator.length()));
                        if(lineData.substring(0,var.length()+operator.length()).equals(var+operator)){
                            return lineData.substring(var.length()+operator.length());
                        }
                    }
                    lineData=bufferedReader.readLine();
                }
                fileInputStream.close();
                return ret;

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean writeTxtFile(String path, String var, String data){
        return writeTxtFile(path,var,"=",data);
    }

    public static boolean writeTxtFile(String path, String data){
        return writeTxtFile(path,"","",data);
    }

    public static boolean writeTxtFile(String path, String var, String operator, String data){
        File file=new File(path);
        if(!fileExist(file)){
            //create
            if(!createFile(file)) return false;
        }
        try {
            String tmp="";
            if(!var.equals("")){
                FileInputStream fileInputStream=new FileInputStream(file);
                InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                try {
                    String lineData=bufferedReader.readLine();
                    boolean edited=false;
                    while (lineData!=null){
                        if(!tmp.equals(""))tmp+="\n";
                        if(lineData.substring(0,var.length()+operator.length()).equals(var+operator)){
                            tmp+=var+operator+data;
                            edited=true;
                        } else {
                            tmp+=lineData;
                        }
                        lineData=bufferedReader.readLine();
                    }
                    if(!edited){
                        if(!tmp.equals(""))tmp+="\n";
                        tmp+=var+operator+data;
                    }
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }else{
                tmp=data;
            }


            FileOutputStream fileOutputStream=new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
            try {
                bufferedWriter.write(tmp);
                trace("TMP: "+tmp);
                bufferedWriter.close();
                fileOutputStream.close();
                trace("saved");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    

}
