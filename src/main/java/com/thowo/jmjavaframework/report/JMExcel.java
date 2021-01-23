/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.report;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.JMFormatCollection;
import static com.thowo.jmjavaframework.JMFormatCollection.terbilang;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.JMVec2;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import com.thowo.jmjavaframework.form.JMFormTableList;
import com.thowo.jmjavaframework.table.JMCell;
import com.thowo.jmjavaframework.table.JMRow;
import com.thowo.jmjavaframework.table.JMTable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author jimi
 */
public class JMExcel {
    public static final String XLS_FIELD_CODE_MASTER_CUSTOM="$_JM_XLS_Master_Custom_";
    public static final String XLS_FIELD_CODE_MASTER_DB="$_JM_XLS_Master_DB_";
    public static final String XLS_FIELD_CODE_DETAIL="$_JM_XLS_Detail_";
    
    public static final String XLS_XTRA_TERBILANG_DATE="[TERBILANG_DATE]";
    public static final String XLS_XTRA_TERBILANG_DAYWEEK="[TERBILANG_DAYWEEK]";
    public static final String XLS_XTRA_TERBILANG_DAYMONTH="[TERBILANG_DAYMONTH]";
    public static final String XLS_XTRA_TERBILANG_MONTH="[TERBILANG_MONTH]";
    public static final String XLS_XTRA_TERBILANG_YEAR="[TERBILANG_YEAR]";
    public static final String XLS_XTRA_TERBILANG="[TERBILANG]";
    public static final String XLS_XTRA_FORMAT_DATE="[FORMAT_DATE]";
    
    public static final int RPT_MODE_DETAIL=0;
    public static final int RPT_MODE_MASTER_DETAIL=1;
    public static final int RPT_MODE_MASTER=2;
    
    
    Workbook wb;
    Sheet sheet;
    Iterator<Row> rowIterator;
    Row row;
    Iterator<Cell> cellIterator;
    Cell cell;
    
    public static void adjustRowHeights(XSSFSheet sheet){
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            JMFunctions.trace(row.getHeight()+"");
            row.setHeight((short)-1);
        }
    }
    
    public static XSSFSheet cloneSheet(XSSFWorkbook workbook, int sheetNumber, String sheetName){
        XSSFSheet ret=null;
        XSSFSheet ori=workbook.getSheetAt(sheetNumber);
        if(ori==null)return null;
        if(workbook==null)return null;
        while(ret==null && !sheetName.equals("")){
            sheetName=JMExcel.newValidSheetName(workbook, sheetName);
            try{
                ret=workbook.cloneSheet(sheetNumber, sheetName);
            }catch(java.lang.IllegalArgumentException ex){
                sheetName=sheetName.substring(0, sheetName.length()-1);
            }
        }
        if(ret==null){
            sheetName=JMExcel.newValidSheetName(workbook, workbook.getSheetName(sheetNumber));
            ret=workbook.cloneSheet(sheetNumber, sheetName);
        }
        if(ret!=null){
            String pArea=workbook.getPrintArea(sheetNumber);
            if(pArea!=null){
                int m=pArea.indexOf('!');
                if(m>=0)pArea=pArea.substring(m+1);
                workbook.setPrintArea(workbook.getSheetIndex(ret), pArea);
            }
        }
        if(ret!=null){
            //ret.getPrintSetup().setLandscape(ori.getPrintSetup().getLandscape());
            //ret.getPrintSetup().setPaperSize(ori.getPrintSetup().getPaperSize());
            ret.getPrintSetup().setOrientation(ori.getPrintSetup().getOrientation());
            
            //ret.setFitToPage(ori.getFitToPage());
            //ret.getPrintSetup().setFitWidth(ori.getPrintSetup().getFitWidth());
            //ret.getPrintSetup().setFitHeight(ori.getPrintSetup().getFitHeight());
            
            
        }
        return ret;
    }
    public static String terbilangDate(JMDate date){
        return terbilangDayweek(date)
                +" tanggal "
                +terbilangDaymonth(date)
                +" bulan "
                +terbilangMonth(date)
                +" tahun "
                +terbilangYear(date);
    }
    public static String formatDateNormal(JMDate date){
        return JMFormatCollection.leadingZero(date.getDayOfMonth(), 2)
                +" "
                +JMFormatCollection.toUpperFirstLetters(terbilangMonth(date))
                +" "
                +date.getYearFull();
    }
    public static String terbilangDayweek(JMDate date){
        return date.getDayOfWeekString();
    }
    public static String terbilangDaymonth(JMDate date){
        return terbilang(date.getDayOfMonth());
    }
    public static String terbilangMonth(JMDate date){
        return date.getDayOfMonthString();
    }
    public static String terbilangYear(JMDate date){
        return terbilang(date.getYearFull());
    }
    public static String terbilangNumber(Double number){
        return terbilang(number);
    }
    public static String terbilangNumber(Integer number){
        return terbilang(number);
    }
    
    public static String newValidSheetName(XSSFWorkbook workbook,String sheetName){
        String ret=sheetName;
        if(workbook==null)JMFunctions.trace("WOOOOOOOOOOOOOOOOY                  "+sheetName);
        
        XSSFSheet tmp=workbook.getSheet(sheetName);
        int n=2;
        while(tmp!=null){
            ret=sheetName+"_"+n++;
            tmp=workbook.getSheet(ret);
        }
        return ret;
    }
    private static List<XSSFCell> findByValueString(XSSFSheet sheet, String value){
        List<XSSFCell> ret=new ArrayList();
        DataFormatter dataFormatter = new DataFormatter();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(cell.getCellType()!=CellType.FORMULA){
                    String cellValue = dataFormatter.formatCellValue(cell);
                    String tmpVal=removeExtraFormat(cellValue);
                    if(tmpVal.equals(value))ret.add((XSSFCell) cell);
                    //row.setHeight((short)50);
                }
            }
        }
        return ret;
    }
    private static String removeExtraFormat(String str){
        if(str==null)return null;
        if(str.equals(""))return str;
        String tmp=str.replace(XLS_XTRA_TERBILANG, "");
        tmp=tmp.replace(XLS_XTRA_TERBILANG_DATE, "");
        tmp=tmp.replace(XLS_XTRA_TERBILANG_DAYMONTH, "");
        tmp=tmp.replace(XLS_XTRA_TERBILANG_DAYWEEK, "");
        tmp=tmp.replace(XLS_XTRA_TERBILANG_MONTH, "");
        tmp=tmp.replace(XLS_XTRA_TERBILANG_YEAR, "");
        tmp=tmp.replace(XLS_XTRA_FORMAT_DATE, "");
        return tmp;
    }
    private static List<Object> findDetailStartRow(XSSFSheet sheet){
        List<Object> ret=new ArrayList();
        boolean valid=false;
        DataFormatter dataFormatter = new DataFormatter();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            List<Integer> fields=new ArrayList();
            List<Integer> cols=new ArrayList();
            List<String> codes=new ArrayList();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(cell.getCellType()!=CellType.FORMULA){
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if(cellValue.contains(XLS_FIELD_CODE_DETAIL)){
                        valid=true;
                        if(ret.isEmpty()){
                            ret.add((Integer)row.getRowNum());
                        }
                        String tmp=cellValue.replace(XLS_FIELD_CODE_DETAIL, "");
                        tmp=removeExtraFormat(tmp);
                        Integer i=Integer.valueOf(tmp);
                        fields.add(i);
                        cols.add(cell.getColumnIndex());
                        codes.add(cellValue);
                    }
                }
            }
            if(valid){
                ret.add(fields);
                ret.add(cols);
                ret.add(codes);
                break;
            }
        }
        return ret;
    }
    public static XSSFWorkbook writeListTo(XSSFWorkbook workbook, String sheetName, List<Object> list){
        if(workbook==null)return null;
        if(list==null)return null;
        
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if(sheet==null)return null;
        for(Integer i=0;i<list.size();i++){
            List<XSSFCell> cells=findByValueString(sheet, XLS_FIELD_CODE_MASTER_CUSTOM+i);
            if(!cells.isEmpty()){
                for(XSSFCell cell:cells){
                    if(list.get(i) instanceof Boolean){
                        cell.setCellValue((Boolean) list.get(i));
                    }else if(list.get(i) instanceof Integer){
                        cell.setCellValue((Integer) list.get(i));
                    }else if(list.get(i) instanceof Double){
                        cell.setCellValue((Double) list.get(i));
                    }else if(list.get(i) instanceof JMDate){
                        JMDate dt=(JMDate) list.get(i);
                        cell.setCellValue(dt.getDate());
                    }else{
                        cell.setCellValue((String) list.get(i));
                    }
                }
            }
        }
        return workbook;
    }
    public static XSSFWorkbook writeRowTo(XSSFWorkbook workbook, String sheetName, JMRow row, List<Integer> excluded){
        if(workbook==null)return null;
        if(row==null)return null;
        if(excluded==null)excluded=new ArrayList();
        
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if(sheet==null)return null;
        List<JMCell> dbCells=row.getCells();
        for(Integer i=0;i<dbCells.size();i++){
            if(!excluded.contains(i)){
                List<XSSFCell> cells=findByValueString(sheet, XLS_FIELD_CODE_MASTER_DB+i);
                for(XSSFCell cell:cells){
                    JMExcel.setCellValue(cell, dbCells.get(i),cell.getStringCellValue());
                    
                }
            }
        }
        
        return workbook;
    }
    private static List<Integer> getColIndicesOfFieldNo(Integer fieldNo, List<Integer> lFields, List<Integer> lCols){
        List<Integer> ret=new ArrayList();
        for(int i=0;i<lFields.size();i++){
            if(lFields.get(i)==fieldNo){
                ret.add(lCols.get(i));
            }
        }
        return ret;
    }
    private static List<String> getColCodesOfFieldNo(Integer fieldNo, List<Integer> lFields, List<String> lCodes){
        List<String> ret=new ArrayList();
        for(int i=0;i<lFields.size();i++){
            if(lFields.get(i)==fieldNo){
                ret.add(lCodes.get(i));
            }
        }
        return ret;
    }
    public static XSSFWorkbook writeTableTo(XSSFWorkbook workbook, String sheetName, JMFormTableList tableList){
        if(workbook==null)return workbook;
        if(tableList==null)return workbook;
        JMTable table=tableList.getDbObject();
        if(table.isEmpty())return workbook;
        List<Integer> excluded=tableList.getRptXlsExcluded();
        if(excluded==null)excluded=table.getExcludedCols();
        List<Integer> sortedXlsColumnsNoRepetition=tableList.getRptXlsColumnsNoRepetition();
        if(sortedXlsColumnsNoRepetition==null)sortedXlsColumnsNoRepetition=new ArrayList();
        
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if(sheet==null)return workbook;
        List<Object> detObj=findDetailStartRow(sheet);
        if(detObj.isEmpty())return workbook;
        int startRow=(Integer)detObj.get(0);
        List<Integer> xlsFields=(List<Integer>) detObj.get(1);
        List<Integer> xlsCols=(List<Integer>) detObj.get(2);
        List<String> xlsCodes=(List<String>) detObj.get(3);
        //JMFunctions.trace("ARRAY ISI: "+xlsColumns.toString());
        int wTmp=startRow;
        table.firstRow(false);
        do{
            JMExcel.shiftRows(sheet, wTmp, sheet.getLastRowNum(), 1);
            wTmp++;
            sheet.copyRows(wTmp, wTmp, wTmp-1, new CellCopyPolicy());
            List<JMCell> dbCells=table.getCurrentRow().getCells();
            for(Integer i=0;i<dbCells.size();i++){
                if(!excluded.contains(i)){
                    XSSFRow row = sheet.getRow(wTmp-1);
                    List<Integer> cols=getColIndicesOfFieldNo(i, xlsFields, xlsCols);
                    List<String> codes=getColCodesOfFieldNo(i, xlsFields, xlsCodes);
                    for(int c=0;c<cols.size();c++){
                        XSSFCell cell=row.getCell(cols.get(c));
                        String code=codes.get(c);
                        JMExcel.setCellValue(cell, dbCells.get(i),code);
                    }
                }
            }
        }while(table.nextRow(false)!=null);
        JMExcel.shiftRows(sheet, wTmp+1, sheet.getLastRowNum(), -1);
        return workbook;
    }
    
    public static void setCellValue(XSSFCell xlsCell, JMCell tblCell, String extraXlsFormat){
        if(tblCell.getFormatType().contains(JMResultSetStyle.DATA_TYPE_BOOLEAN + "|")){
            xlsCell.setCellValue(tblCell.getValueBoolean());
        }else if(tblCell.getFormatType().contains(JMResultSetStyle.DATA_TYPE_DATE + "|")){
            if(extraXlsFormat.contains(XLS_XTRA_TERBILANG_DATE)){
                xlsCell.setCellValue(terbilangDate(tblCell.getValueDate()));
            }else if(extraXlsFormat.contains(XLS_XTRA_TERBILANG_DAYMONTH)){
                xlsCell.setCellValue(terbilangDaymonth(tblCell.getValueDate()));
            }else if(extraXlsFormat.contains(XLS_XTRA_TERBILANG_DAYWEEK)){
                xlsCell.setCellValue(terbilangDayweek(tblCell.getValueDate()));
            }else if(extraXlsFormat.contains(XLS_XTRA_TERBILANG_MONTH)){
                xlsCell.setCellValue(terbilangMonth(tblCell.getValueDate()));
            }else if(extraXlsFormat.contains(XLS_XTRA_TERBILANG_YEAR)){
                xlsCell.setCellValue(terbilangYear(tblCell.getValueDate()));
            }else if(extraXlsFormat.contains(XLS_XTRA_FORMAT_DATE)){
                xlsCell.setCellValue(formatDateNormal(tblCell.getValueDate()));
            }else{
                xlsCell.setCellValue(tblCell.getValueDate().getDate());
            }
        }else if(tblCell.getFormatType().contains(JMResultSetStyle.DATA_TYPE_DOUBLE + "|")){
            if(extraXlsFormat.contains(XLS_XTRA_TERBILANG)){
                xlsCell.setCellValue(terbilangNumber(tblCell.getValueDouble()));
            }else{
                xlsCell.setCellValue(tblCell.getValueDouble());
            }
        }else if(tblCell.getFormatType().contains(JMResultSetStyle.DATA_TYPE_INTEGER + "|")){
            if(extraXlsFormat.contains(XLS_XTRA_TERBILANG)){
                xlsCell.setCellValue(terbilangNumber(tblCell.getValueInteger()));
            }else{
                xlsCell.setCellValue(tblCell.getValueInteger());
            }
        }else{
            xlsCell.setCellValue(tblCell.getDBValue());
        }
    }
    public static void shiftRows(XSSFSheet sheet, int startRow, int endRow, int n){
        shiftRows(sheet,startRow,endRow,n,false,true);
    }
    public static void shiftRows(XSSFSheet sheet, int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight){
        sheet.shiftRows(startRow, endRow, n, copyRowHeight, resetOriginalRowHeight);
        for (int r = sheet.getFirstRowNum(); r < sheet.getLastRowNum() + 1; r++) {
            XSSFRow row = sheet.getRow(r); 
            if (row != null) {
                long rRef = row.getCTRow().getR();
                for (Cell cell : row) {
                    String cRef = ((XSSFCell)cell).getCTCell().getR();
                    ((XSSFCell)cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
                }
            }
        }
    }
    
    public static JMExcel create(String xls){
        return new JMExcel(xls);
    }
    public JMExcel(String xls){
        try {
            FileInputStream file= new FileInputStream(new File(xls));
            this.wb=WorkbookFactory.create(file);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMExcel.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(JMExcel.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(JMExcel.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
        }
    }
    public JMExcel setSheet(int no){
        if(this.wb==null)return this;
        this.sheet=this.wb.getSheetAt(no);
        this.rowIterator=this.sheet.iterator();
        if(this.rowIterator.hasNext()){
            this.row=this.rowIterator.next();
        }else{
            this.row=null;
        }
        return this;
    }
    public JMExcel setSheet(String name){
        if(this.wb==null)return this;
        this.sheet=this.wb.getSheet(name);
        this.rowIterator=this.sheet.iterator();
        if(this.rowIterator.hasNext()){
            this.row=this.rowIterator.next();
        }else{
            this.row=null;
        }
        return this;
    }
    public JMExcel nextRow(){
        if(this.row==null)return this;
        if(this.rowIterator.hasNext()){
            this.row=this.rowIterator.next();
            this.cellIterator=this.row.cellIterator();
            if(this.cellIterator.hasNext()){
                this.cell=this.cellIterator.next();
            }else{
                this.cell=null;
            }
        }else{
            this.row=null;
        }
        return this;
    }
    public JMExcel nextCell(){
        if(this.cell==null)return this;
        if(this.cellIterator.hasNext()){
            this.cell=this.cellIterator.next();
        }else{
            this.cell=null;
        }
        return this;
    }
    public String getString(){
        String ret="";
        if(this.cell==null)return "";
        //CellType
        //CellType a;
        
        switch(this.cell.getCellType()){
            case BLANK:
                ret="";
                break;
            case BOOLEAN:
                ret=Boolean.toString(this.cell.getBooleanCellValue());
                break;
            case ERROR:
                ret="";
                break;
            case FORMULA:
                ret=this.cell.getCellFormula();
                break;
            case NUMERIC:
                ret=Double.toString(this.cell.getNumericCellValue());
                break;
            case STRING:
                ret=this.cell.getStringCellValue();
                break;
        }
        return ret;
    }
    public boolean hasNextRow(){
        if(this.rowIterator==null)return false;
        return this.rowIterator.hasNext();
    }
    public boolean hasNextCell(){
        if(this.cellIterator==null)return false;
        return this.cellIterator.hasNext();
    }
    public boolean rowNotNull(){
        return this.row!=null;
    }
    public boolean cellNotNull(){
        return this.cell!=null;
    }
    public int getCurrentRowNum(){
        if(this.row==null)return -1;
        return this.row.getRowNum();
    }
    public int getCurrentCellNum(){
        if(this.cell==null)return -1;
        return this.cell.getColumnIndex();
    }
    
    
}
