/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.report;

import com.thowo.jmjavaframework.JMFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;


/**
 *
 * @author jimi
 */
public class JMWord {
    
    public static XWPFDocument open(String docxPath){
        File docx=new File(docxPath);
        return open(docx);
    }
    public static XWPFDocument open(File docx){
        XWPFDocument ret=null;
        if(!JMFunctions.fileExist(docx))return null;
        try {
            FileInputStream inp=new FileInputStream(docx);
            ret=new XWPFDocument(inp);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMWord.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.trace(ex.getMessage());
            return null;
        } catch (IOException ex) {
            Logger.getLogger(JMWord.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.trace(ex.getMessage());
            return null;
        }
        return ret;
    }
    public static List<XWPFAbstractNum> getAbstractNums(XWPFDocument doc){
        List<XWPFAbstractNum> ret=null;
        XWPFNumbering numbering=doc.getNumbering();
        if(numbering==null){
            JMFunctions.trace("no numbering");
            return null;
        }
        
        BigInteger curId=BigInteger.ONE;
        XWPFAbstractNum abstractNum=numbering.getAbstractNum(curId);
        while(abstractNum!=null){
            //JMFunctions.trace("\n\n\nID:"+abstractNum.getCTAbstractNum().getAbstractNumId()+"\n"+abstractNum.getCTAbstractNum().xmlText());
            if(ret==null)ret=new ArrayList();
            ret.add(abstractNum);
            curId=curId.add(BigInteger.ONE);
            abstractNum=numbering.getAbstractNum(curId);
        }
        //ret.remove(ret.size()-1);
        
        //JMFunctions.trace(curId.subtract(BigInteger.ONE).toString());

        return ret;
    }
    public static void test(File word){
        //testTunggal();
        //testGabung();
    }
    public static void testTunggal(){
        XWPFDocument doc=open("/home/jimi/Desktop/tespoi/wordtest1.docx");
        //List<XWPFParagraph> pars=doc.getParagraphs();
        JMFunctions.trace(String.valueOf(JMWord.getAbstractNums(doc).size()));
    }
    private static void testGabung(XWPFDocument master, XWPFDocument toAdd){
        XWPFDocument res=addDoc(master,toAdd,true,true);
        if(save(res,JMFunctions.getCacheDir()+"/res.docx"))JMFunctions.traceAndShow("ok");
        
    }
    private static List<Object> restartNumbering(XWPFDocument master,XWPFDocument toAdd){
        List<XWPFAbstractNum> abstractsMaster=JMWord.getAbstractNums(master);
        if(abstractsMaster==null){
            List<Object> ret=new ArrayList();
            ret.add(master);
            ret.add(toAdd);
            ret.add(0);
            ret.add(0);
            return ret;
        }
        Integer startId=abstractsMaster.size()+1;
        Integer bu=startId;
        
        List<XWPFAbstractNum> abstractsToAdd=JMWord.getAbstractNums(toAdd);
        Integer absNum=abstractsToAdd.size();
        XWPFNumbering masterNumbering=master.getNumbering();
        for(XWPFAbstractNum abs:abstractsToAdd){
            abs.getCTAbstractNum().setAbstractNumId(BigInteger.valueOf(startId++));
            masterNumbering.addAbstractNum(abs);
            XWPFNum tmp=new XWPFNum();
            tmp.setCTNum(CTNum.Factory.newInstance());
            tmp.getCTNum().setNumId(abs.getCTAbstractNum().getAbstractNumId());
            CTDecimalNumber ctdn=CTDecimalNumber.Factory.newInstance();
            ctdn.setVal(abs.getCTAbstractNum().getAbstractNumId());
            tmp.getCTNum().setAbstractNumId(ctdn);
            masterNumbering.addNum(tmp);
        }
        
        List<Object> ret=new ArrayList();
        ret.add(master);
        ret.add(toAdd);
        ret.add(bu);
        ret.add(absNum);
        return ret;
    }
    public static XWPFDocument addDoc(XWPFDocument master, XWPFDocument toAdd){
        return addDoc(master, toAdd, true, true);
    }
    public static XWPFDocument addDoc(XWPFDocument master, XWPFDocument toAdd, boolean newPage, boolean willRestartNumbering){
        
        if(newPage){
            XWPFParagraph paragraph = master.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.addBreak(BreakType.PAGE);
        }
        try {
            XWPFDocument m=master;
            XWPFDocument a=toAdd;
            Integer id=0;
            Integer abstractNum=0;
            
            if(willRestartNumbering){
                List<Object> res=JMWord.restartNumbering(master,toAdd);
                m=(XWPFDocument) res.get(0);
                a=(XWPFDocument) res.get(1);
                id=(Integer) res.get(2);
                abstractNum=(Integer) res.get(3);
            }
            //JMFunctions.trace("new master size:"+String.valueOf(JMWord.getAbstractNums(m).size()));
            CTBody masterBody = m.getDocument().getBody();
            CTBody toAddBody = a.getDocument().getBody();
            
            appendBody(masterBody, toAddBody,id,abstractNum);
            
            return master;
        } catch (Exception ex) {
            Logger.getLogger(JMWord.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.trace(ex.getMessage());
            return null;
        }
    }
    public static boolean save(XWPFDocument doc, String path){
        boolean ret=false;
        OutputStream dest=null;
        try {
            dest = new FileOutputStream(path);
            doc.write(dest);
            ret= true;
            dest.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMWord.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.trace(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(JMWord.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.trace(ex.getMessage());
        }
        return ret;
    }
    public static XWPFDocument replaceInBody(XWPFDocument doc, String str, String newStr) throws Exception{
        CTBody body=doc.getDocument().getBody();
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = body.xmlText(optionsOuter);
        String srcString = body.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String mainPart = appendString.substring(appendString.indexOf(">") + 1,
                        appendString.lastIndexOf("<"));
        mainPart=mainPart.replace(str, newStr);
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + sufix );
        body.set(makeBody);
        return doc;
    }
    private static void appendBody(CTBody src, CTBody append, Integer startId, Integer abstractNum) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1,
                        srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1,
                        appendString.lastIndexOf("<"));
        //String addPart=tes();
        if(startId>0){
            Integer c=1;
            for(Integer i=startId;i<startId+abstractNum;i++){
                String id=String.valueOf(i);
                addPart=addPart.replace("<w:numId w:val=\""+ c++ +"\"/>", "<w:numId w:val=\""+id+"\"/>");
            }
        }
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix );
        //CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + mainPart + addPart + sufix );
        src.set(makeBody);
        //JMFunctions.trace(prefix + mainPart + addPart
        //                + sufix);
        
        //JMFunctions.trace("\n\n\n"+addPart);
    }
    
}
