/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.report;

import com.thowo.jmjavaframework.JMFormatCollection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author jimi
 */
public class JMExcel {
    Workbook wb;
    Sheet sheet;
    Iterator<Row> rowIterator;
    Row row;
    Iterator<Cell> cellIterator;
    Cell cell;
    
    public static JMExcel create(String xls){
        return new JMExcel(xls);
    }
    public JMExcel(String xls){
        try {
            FileInputStream file= new FileInputStream(new File(xls));
            this.wb=WorkbookFactory.create(file);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMExcel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JMExcel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(JMExcel.class.getName()).log(Level.SEVERE, null, ex);
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
