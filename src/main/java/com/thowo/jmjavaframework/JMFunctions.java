package com.thowo.jmjavaframework;


import com.thowo.jmjavaframework.db.JMConnection;
import com.thowo.jmjavaframework.lang.JMLanguage;
import com.thowo.jmjavaframework.lang.JMMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


/**
 * Created by jimi on 8/2/2017.
 */

public class JMFunctions {
    private static List<JMLanguage> languages;
    private static List<JMMessage> messages;
    private static JMAsyncListener asyncL;
    private static JMUIListener uiL;
    private static JMConnection mDBConnection;
    private static String tes="jimix";

    public static void init(File languageExcelFile){
        //JMStringMessages.init();
        JMFunctions.languages=new ArrayList();
        JMFunctions.messages=new ArrayList();
        if(JMFunctions.fileExist(languageExcelFile))JMFunctions.readExcelLang(languageExcelFile);
    }
    
    private static void readExcelLang(File excel){
        Workbook wb=null;
        try {
            wb = Workbook.getWorkbook(excel);
            if(wb==null)return;
            Sheet sheet=wb.getSheet("lang");
            if(sheet==null)return;
            for(int r=0;r<sheet.getRows();r++){
                Cell[] cells=sheet.getRow(r);
                if(cells==null)return;
                String idLang="";
                String lang="";
                String def="";
                boolean isDef=false;
                if(cells.length>0)idLang=cells[0].getContents();
                if(cells.length>1)lang=cells[1].getContents();
                if(cells.length>2)def=cells[2].getContents();
                if(def.equals("1"))isDef=true;
                JMFunctions.languages.add(new JMLanguage(idLang,lang,isDef));
            }
            for(JMLanguage lang:JMFunctions.languages){
                sheet=wb.getSheet(lang.getLangId());
                if(sheet!=null){
                    for(int r=0;r<sheet.getRows();r++){
                        Cell[] cells=sheet.getRow(r);
                        if(cells==null)break;
                        String idMsg="";
                        String msg="";
                        if(cells.length>0)idMsg=cells[0].getContents();
                        if(cells.length>1)msg=cells[1].getContents();
                        JMFunctions.messages.add(new JMMessage(lang.getLangId(),idMsg,msg));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JMFunctions.traceAndShow(e.getMessage());
        } catch (BiffException e) {
            e.printStackTrace();
            JMFunctions.traceAndShow(e.getMessage());
        } finally {
            if(wb!=null){
                wb.close();
            }
        }
    }
    
    public static String getMessege(String idMsg){
        String ret="";
        JMLanguage defLang=JMFunctions.getDefaultLanguage();
        if(defLang==null)return "";
        for(JMMessage tmp:JMFunctions.messages){
            if(tmp.getLangId().equals(defLang.getLangId()) && tmp.getMsgId().equals(idMsg)){
                ret=tmp.getMsg();
                break;
            }
        }
        return ret;
    }
    
    public static void setDefaultLanguage(int listIndex){
        for(int i=0;i<languages.size();i++){
            JMLanguage tmp=languages.get(i);
            if(i==listIndex){
                tmp.setDefault(true);
            }else{
                tmp.setDefault(false);
            }
        }
    }
    
    public static JMLanguage getDefaultLanguage(){
        for(JMLanguage tmp:languages){
            if(tmp.getDefault())return tmp;
        }
        return null;
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


    public static JMAsyncListener getCurrentAsyncListener(){
        return asyncL;
    }
    
    public static JMUIListener getCurrentUIListener(){
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
    public static final int SCALE_FIT=0;
    public static final int SCALE_STRETCH=1;
    public static final int SCALE_CENTER=2;
    public static List<JMVec2> scaledSize(JMVec2 size, JMVec2 parentSize, int JMFunctionsConstScale){
        List<JMVec2> ret=null;
        if(size==null)return null;
        if(parentSize==null)return null;
        ret=new ArrayList();
        double x,y;
        double scale=size.getDoubleX()/size.getDoubleY();
        if(JMFunctionsConstScale==SCALE_STRETCH){
            ret.add(parentSize);
            ret.add(new JMVec2(0,0));
            return ret;
        }else if(JMFunctionsConstScale==SCALE_CENTER){
            size=new JMVec2(parentSize.getDoubleX(),getYScaledValue(scale,parentSize.getDoubleX()));
            if(size.getDoubleY()>parentSize.getDoubleY())size=new JMVec2(getXScaledValue(scale,parentSize.getDoubleY()),parentSize.getDoubleY());
        }else{
            size=new JMVec2(parentSize.getDoubleX(),getYScaledValue(scale,parentSize.getDoubleX()));
            if(size.getDoubleY()<parentSize.getDoubleY())size=new JMVec2(getXScaledValue(scale,parentSize.getDoubleY()),parentSize.getDoubleY());
        }
        x=(parentSize.getDoubleX()-size.getDoubleX())/2;
        y=(parentSize.getDoubleY()-size.getDoubleY())/2;
        ret.add(size);
        ret.add(new JMVec2(x,y));
        return ret;
    }
    
    private static double getYScaledValue(double scale, double topValue){
        return topValue/scale;
    }
    private static double getXScaledValue(double scale, double bottomValue){
        return bottomValue*scale;
    }
    

}
