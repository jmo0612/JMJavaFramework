/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.report;

import com.thowo.jmjavaframework.JMDataContainer;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.table.JMCell;
import com.thowo.jmjavaframework.table.JMRow;
import com.thowo.jmjavaframework.table.JMTable;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

/**
 *
 * @author jimi
 */
public class JMWordMM {
    private XWPFDocument doc;
    private String templatePath;
    private String outputPath;
    private JMTable table;
    
    public JMWordMM(JMTable table, String templatePath, String outputPath){
        this.setProp(table, templatePath, outputPath);
    }
    private void setProp(JMTable table, String templatePath, String outputPath){
        this.templatePath=templatePath;
        this.outputPath=outputPath;
        this.table=table;
        this.doMailMerge();
        this.saveResult();
    }
    private XWPFDocument process(XWPFDocument template,JMRow row){
        List<JMCell> cells=row.getCells();
        for(JMCell cell:cells){
            String field=cell.getFieldName();
            JMDataContainer dc=cell.getDataContainer();
            if(dc==null)break;
            try {
                template=JMWord.replaceInBody(template, "$_JM_Master_"+field, dc.getText());// WATCH OUT FOR IMAGE
            } catch (Exception ex) {
                Logger.getLogger(JMWordMM.class.getName()).log(Level.SEVERE, null, ex);
                JMFunctions.trace(ex.getMessage());
            }
        }
        return template;
    }
    private void doMailMerge(){
        this.doc=JMWord.open(this.templatePath);
        if(this.doc==null)return;
        if(this.table.isEmpty())return;
        boolean first=true;
        this.table.firstRow(false);
        do{
            JMRow r=this.table.getCurrentRow();
            //if(r!=null)JMFunctions.trace(r.getCells().get(0).getValueString());
            XWPFDocument template=JMWord.open(this.templatePath);
            //Fill data here
            template=this.process(template, r);
            if(first){
                this.doc=template;
                first=false;
            }else{
                this.doc=JMWord.addDoc(this.doc, template);
            }
        }while(this.table.nextRow(false)!=null);
    }
    private boolean saveResult(){
        return JMWord.save(this.doc, this.outputPath);
    }
}
