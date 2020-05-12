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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



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
    private static String cacheDir;
    private static String docDir;
    
    public static JMFunctions INSTANCE(){
        return new JMFunctions();
    }
    
    public JMFunctions(){
        
    }

    public static void init(File languageExcelFilex, String cacheDirPath, String docDirPath,String localeId){
        //JMStringMessages.init();
        JMFunctions.languages=new ArrayList();
        JMFunctions.messages=new ArrayList();
        JMFunctions.cacheDir=cacheDirPath;
        JMFunctions.docDir=docDirPath;
        
        //URL excl=JMFunctions.getResourcePath("raw/jmlanguagepack.xls", this.getClass());
        URL excl=JMFunctions.getResourcePath("raw/jmlanguagepack.xls", JMFunctions.INSTANCE().getClass());
        //JMFunctions.trace("LASO = "+excl.getPath());
        File languageExcelFile = JMFunctions.resourceToCache("raw/jmlanguagepack.xls", JMFunctions.class);
        if(JMFunctions.fileExist(languageExcelFile)){
            JMFunctions.readExcelLang(languageExcelFile,localeId);
        }
    }
    
    public static String getCacheDir(){
        return cacheDir;
    }
    public static String getDocDir(){
        return docDir;
    }
    
    private static void readExcelLang(File excel,String localeId){
        try {
            InputStream inp=new FileInputStream(excel);
            Workbook wb=WorkbookFactory.create(inp);
            Sheet sheet=wb.getSheet("lang");
            if(sheet==null)return;
            int r=1;
            Row row=sheet.getRow(r);
            while(row!=null){
                String idLang="";
                String lang="";
                int def=0;
                Boolean isDef=false;
                Cell cell=row.getCell(0);
                if(cell!=null)idLang=cell.getStringCellValue();
                cell=row.getCell(1);
                if(cell!=null)lang=cell.getStringCellValue();
                //cell=row.getCell(2);
                //if(cell!=null)def=(int) cell.getNumericCellValue();//USELESS
                //isDef=(r==1);
                if(!localeId.equals("")){
                    if(idLang.equals(localeId))isDef=true;
                    else isDef=false;
                }
                JMFunctions.languages.add(new JMLanguage(idLang,lang,isDef));
                row=sheet.getRow(++r);
            }
            //if(JMFunctions.getDefaultLanguage()==null)JMFunctions.setDefaultLanguage(0);
            //JMFunctions.trace(JMFunctions.getDefaultLanguage().getLangId());
            for(JMLanguage lang:JMFunctions.languages){
                r=1;
                sheet=wb.getSheet(lang.getLangId());
                if(sheet!=null){
                    row=sheet.getRow(r);
                    while(row!=null){
                        String idMsg="";
                        String msg="";
                        Cell cell=row.getCell(0);
                        if(cell!=null)idMsg=cell.getStringCellValue();
                        cell=row.getCell(1);
                        if(cell!=null)msg=cell.getStringCellValue();
                        JMFunctions.messages.add(new JMMessage(lang.getLangId(),idMsg,msg));
                        row=sheet.getRow(++r);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
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
    public static void setDefaultLanguage(String localeCode){
        for(JMLanguage l:languages){
            if(l.getLangId().equals(localeCode)){
                l.setDefault(true);
            }else{
                l.setDefault(false);
            }
        }
    }
    
    public static JMLanguage getDefaultLanguage(){
        for(JMLanguage tmp:languages){
            if(tmp.getDefault())return tmp;
        }
        return languages.get(0);
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
    public static void errorMessage(String msg){
        uiL.errorBox(msg);
    }
    public static int confirmBoxYN(String title, String message, String yes, String no, boolean defaultNo){
        return uiL.confirmBoxYN(title, message, yes, no, defaultNo);
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
    
    public static boolean moveFile(File fileS, File fileD){
        if(!JMFunctions.fileExist(fileS))return false;
        try {
            /*if(!fileExist(fileS)){
            JMFunctions.trace("KOSONG");
            return false;
            }
            deleteFile(fileD);*/
            //createFile(fileD);
            FileUtils.moveFile(fileS, fileD);
            //return fileS.renameTo(fileD);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    public static boolean copyFile(String fileS, String fileD){
        try {
            FileUtils.copyFile(new File(fileS), new File(fileD));
            return true;
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
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

    public static URL getResourcePath(String resId, Class<?> CLASS) {
        return CLASS.getClassLoader().getResource(resId);
    }
    public static URL getResourcePath(String resId){
        return ClassLoader.getSystemClassLoader().getResource(resId);
    }
    public static File resourceToCache(String resId, Class<?> CLASS){
        File ret=null;
        URL tes=JMFunctions.getResourcePath(resId, CLASS);
        ret=new File(JMFunctions.getCacheDir()+"/"+resId);
        try {
            FileUtils.copyURLToFile(tes, ret);
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /*InputStream inp=CLASS.getResourceAsStream(resId);
        try {
            byte[] buffer = new byte[inp.available()];
            inp.read(buffer);
            ret=new File(JMFunctions.getCacheDir()+resId);
            OutputStream out=new FileOutputStream(ret);
            out.write(buffer);
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return ret;
    }

}
