package com.thowo.jmjavaframework;


import com.thowo.jmjavaframework.db.JMConnection;
import com.thowo.jmjavaframework.form.JMFormTableList;
import com.thowo.jmjavaframework.lang.JMLanguage;
import com.thowo.jmjavaframework.lang.JMMessage;
import com.thowo.jmjavaframework.report.JMExcel;
import com.thowo.jmjavaframework.table.JMCell;
import com.thowo.jmjavaframework.table.JMRow;
import com.thowo.jmjavaframework.table.JMTable;

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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



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

    public static void init(File languageExcelFile, String cacheDirPath, String docDirPath,String localeId){
        //System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        //JMStringMessages.init();
        JMFunctions.languages=new ArrayList();
        JMFunctions.messages=new ArrayList();
        JMFunctions.cacheDir=cacheDirPath;
        JMFunctions.docDir=docDirPath;
        if(languageExcelFile==null){
            languageExcelFile = JMFunctions.resourceToCache("raw/jmlanguagepack.xls", JMFunctions.class);
        }

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
    
    
    public static boolean writeTableToExistingExcel(int CONST_JMExcel_RPT_MODE, String sourceResId, String destFilePath, JMFormTableList table, boolean all){
        InputStream cpResource = ClassLoader.getSystemClassLoader().getResourceAsStream(sourceResId);
        File src=null;
        try {
            src = File.createTempFile("file", "temp");
            FileUtils.copyInputStreamToFile(cpResource, src); 
        } catch (IOException ex) {
            JMFunctions.errorMessage("NDA ADA FILE : ");
            return false;
        }
        try {
            FileInputStream fis = new FileInputStream(src);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet;
            if(CONST_JMExcel_RPT_MODE==JMExcel.RPT_MODE_MASTER){
                JMTable tblMaster=table.getDbObject();
                if(tblMaster.isEmpty())return false;

                JMRow buRow=tblMaster.getCurrentRow();
                do{
                    List<JMCell> tblCells=tblMaster.getCurrentRow().getCells();
                    sheet=JMExcel.cloneSheet(workbook, 0, tblCells.get(table.getRptXlsSheetNameFromColIndex()).getDBValue());
                    String sheetName=sheet.getSheetName();
                    List<Object> cData=table.getRptXlsCustomData();
                    if(!cData.isEmpty()){
                        workbook=JMExcel.writeListTo(workbook, sheet.getSheetName(), cData);
                    }
                    
                    workbook=JMExcel.writeRowTo(workbook, sheetName, tblMaster.getCurrentRow(), table.getRptXlsExcluded());
                    sheet.getFooter().setRight("&A-&P");
                }while(tblMaster.nextRow(false)!=null && all);
                workbook.removeSheetAt(0);
                tblMaster.gotoRow(buRow, false);
            }else if(CONST_JMExcel_RPT_MODE==JMExcel.RPT_MODE_MASTER_DETAIL){
                JMTable tblMaster=table.getDbObject();
                if(tblMaster.isEmpty())return false;

                JMRow buRow=tblMaster.getCurrentRow();
                do{
                    List<JMCell> tblCells=tblMaster.getCurrentRow().getCells();
                    sheet=JMExcel.cloneSheet(workbook, 0, tblCells.get(table.getRptXlsSheetNameFromColIndex()).getDBValue());
                    String sheetName=sheet.getSheetName();
                    List<Object> cData=table.getRptXlsCustomData();
                    if(!cData.isEmpty()){
                        workbook=JMExcel.writeListTo(workbook, sheet.getSheetName(), cData);
                    }
                    
                    workbook=JMExcel.writeRowTo(workbook, sheetName, tblMaster.getCurrentRow(), table.getRptXlsExcluded());
                    table.refreshDetail();
                    JMFormTableList det=table.getDetailTable();
                    workbook=JMExcel.writeTableTo(workbook, sheet.getSheetName(), det);
                    sheet.getFooter().setRight("&A-&P");
                    //JMFunctions.trace("LASO");
                    //JMExcel.adjustRowHeights(sheet);
                }while(tblMaster.nextRow(false)!=null && all);
                workbook.removeSheetAt(0);
                tblMaster.gotoRow(buRow, false);
            }else{
                sheet=workbook.getSheetAt(0);
                List<Object> cData=table.getRptXlsCustomData();
                if(!cData.isEmpty()){
                    workbook=JMExcel.writeListTo(workbook, sheet.getSheetName(), cData);
                }
                workbook=JMExcel.writeTableTo(workbook, sheet.getSheetName(), table);
                sheet.getFooter().setRight("&A-&P");
            }
            /*
            try{
                XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            }catch(java.lang.NoClassDefFoundError ex){
            
            }
            */
            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            
            FileOutputStream out = new FileOutputStream(destFilePath);
            workbook.write(out);
            out.close();
            src.delete();
            return true;
        } catch (FileNotFoundException ex) {
            JMFunctions.trace(ex.getMessage());
            src.delete();
            return false;
        } catch (IOException ex) {
            JMFunctions.trace(ex.getMessage());
            src.delete();
            return false;
        }
    }
    
    public static void writeTableToExcel(JMTable table, String dest, List<Integer> excluded){
        if(table==null)return;
        if(table.isEmpty())return;
        if(excluded==null)excluded=table.getExcludedCols();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(table.getName());
        
        
        CellStyle style = workbook.createCellStyle();  
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        
        int r=0;
        int xlsR=0;
        JMRow row=table.getCurrentRow();
        table.firstRow(false);
        
        Row xlsRow=sheet.createRow(xlsR);
        int c=0;
        int xlsC=0;
        for(JMCell col:table.getCurrentRow().getCells()){
            boolean exclude=false;
            if(excluded!=null){
                for(Integer tmp:excluded){
                    if(tmp==c){
                        exclude=true;
                        break;
                    }
                }
            }
            if(!exclude){
                Cell xlsCell=xlsRow.createCell(xlsC);
                xlsCell.setCellStyle(style);
                xlsCell.setCellValue(table.getLabelTitles().get(c));
                xlsC++;
            }
            c++;
        }
        
        style.setAlignment(HorizontalAlignment.LEFT);
        xlsR=1;
        do{
            xlsRow=sheet.createRow(xlsR);
            c=0;
            xlsC=0;
            for(JMCell col:table.getCurrentRow().getCells()){
                JMFunctions.trace(""+c);
                boolean exclude=false;
                if(excluded!=null){
                    for(Integer tmp:excluded){
                        if(tmp==c){
                            exclude=true;
                            break;
                        }
                    }
                }
                if(!exclude){
                    Cell xlsCell=xlsRow.createCell(xlsC);
                    xlsCell.setCellStyle(style);
                    xlsCell.setCellValue(col.getDBValue());
                    xlsC++;
                }
                c++;
            }
            r++;
            xlsR++;
        }while(table.nextRow(false)!=null);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(dest);
            workbook.write(outputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        table.gotoRow(r, false);
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
    public static String removeMultipleSpaces(String text){
        return text.trim().replaceAll("\\s{2,}", " ");
    }
    public static String removeSpaces(String text){
        return text.trim().replaceAll("\\s", "");
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
        return FileUtils.deleteQuietly(file);
    }
    public static boolean deleteFolder(File folder){
        if(!fileExist(folder))return false;
        try {
            FileUtils.deleteDirectory(folder);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(JMFunctions.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
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
    public static boolean moveFileReplace(File fileS, File fileD){
        if(!JMFunctions.fileExist(fileS))return false;
        if(JMFunctions.fileExist(fileD)){
            JMFunctions.deleteFile(fileD);
        }
        try {
            FileUtils.moveFile(fileS, fileD);
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
        //JMFunctions.trace("JIMI "+resId);
        return CLASS.getClassLoader().getResource(resId);
    }
    public static URL getResourcePath(String resId){
        return ClassLoader.getSystemClassLoader().getResource(resId);
    }

    public static File resourceToCache(String resId, Class<?> CLASS){
        File ret=null;
        URL tes=JMFunctions.getResourcePath(resId, CLASS);
        //JMFunctions.trace("JIMI "+tes.toString());
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
    public static String removeExtension(String fileName) {
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
 
    }
    public static String getExtension(String fileName) {
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
 
    }
    public static String validDBValue(String value,String nullValue){
        String ret=nullValue;
        if(!value.equals("")){
            ret="'"+value+"'";
        }else{
            if(nullValue.equals("")){
                ret="''";
            }else{
                ret=nullValue;
            }
        }
        return ret;
    }
    public static String validDBValue(String value){
        return validDBValue(value,"");
    }
    public static List<Integer> getListIntegerFrom(Integer... intValues){
        List<Integer> ret=new ArrayList();
        for(Integer i:intValues){
            ret.add(i);
        }
        return ret;
    }
    public static List<Integer> listIntegerOf(Integer... obj){
        List<Integer> ret= new ArrayList();
        for(Integer o:obj){
            ret.add(o);
        }
        return ret;
    }

}
