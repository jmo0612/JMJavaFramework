/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.form;

import com.thowo.jmjavaframework.JMButtonInterface;
import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.JMFieldInterface;
import com.thowo.jmjavaframework.JMTableInterface;
import com.thowo.jmjavaframework.JMFormatCollection;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.JMPanelInterface;
import com.thowo.jmjavaframework.table.JMCell;
import com.thowo.jmjavaframework.table.JMDBListInterface;
import com.thowo.jmjavaframework.table.JMRow;
import com.thowo.jmjavaframework.table.JMTable;
import java.util.List;
/**
 *
 * @author jimi
 */
public class JMFormTableInput implements JMTableInterface {
    private final JMFormTableList tableList;
    private final String title;
    private final JMTable table;
    private final JMFormInterface form;
    private final List<JMFieldInterface> fields;
    private final JMFormDBButtonGroup btnGroup;
    private final JMLocaleInterface fieldProp;
    private final boolean isEditable;
    
    private JMRow row;
    private boolean editMode=false;
    private boolean formClosing=false;
    
    
    public static JMFormTableInput create(JMFormTableList tableList,boolean editing,boolean adding){
        return new JMFormTableInput(tableList,editing,adding);
    }
    //public JMFormTableInput(String title, JMTable table, JMLocaleInterface fieldProp, String tableName,List<JMFieldInterface> fields,JMFormDBButtonGroup btnGroup, JMFormInterface form, boolean isEditable,boolean editing,boolean adding,JMFormTableList detailTable){
    public JMFormTableInput(JMFormTableList tableList,boolean editing,boolean adding){
        this.tableList=tableList;
        this.title=this.tableList.getTitle();
        this.table=this.tableList.getDbObject();
        this.table.addInterface(this);
        this.fields=this.tableList.getFields();
        this.form=this.tableList.getForm2();
        this.fieldProp=this.tableList.getFieldProp();
        this.isEditable=this.tableList.getEditable();
        
        
        this.btnGroup=this.tableList.getBtnGroup2();
        
        
        this.row=this.table.getCurrentRow();
        
        this.view(editing,adding);
    }
    
    public void view(boolean editing,boolean adding){
        for(int i=0;i<this.fields.size();i++){
            JMFieldInterface f=this.fields.get(i);
            this.table.setFormInterface(f, i,true);
        }
        this.row.displayInterface(true);
        

        this.addListener();
        
        
        this.setEditMode(editing);
        //this.table.getCurrentRow().displayInterface(false);
        
        this.lockAccess();
        
        this.form.init(this.tableList);
        this.refreshDetail();
        this.form.load();
        this.table.removeInterface(this);
    }
    
    
    private void lockAccess(){
        boolean access=true;
        if(this.tableList.getMasterTable()!=null)access=this.tableList.getMasterTable().isEditing();
        this.btnGroup.getBtnAdd().setVisible(this.isEditable && access);
        this.btnGroup.getBtnDelete().setVisible(this.isEditable && access);
        this.btnGroup.getBtnEdit().setVisible(this.isEditable && access);
        this.btnGroup.getBtnSave().setVisible(this.isEditable && access);
        this.btnGroup.getBtnCancel().setVisible(this.isEditable && access);
        this.btnGroup.getBtnPrint().setVisible(this.isEditable && access);
    }
    
    private void setEditMode(boolean editMode){
        this.editMode=editMode;
        for(int i=0;i<this.fields.size();i++){
            JMFieldInterface f=this.fields.get(i);
            f.setEditMode(editMode,this.row,i);
        }
        
    }
    
    public JMRow getCurrentRow(){
        return this.row;
    }
    
    
    private void addListener(){
        this.form.setOnClosed(new Runnable() {
            @Override
            public void run() {
                if(JMFormTableInput.this.editMode){
                    JMFormTableInput.this.formClosing=true;
                    JMFormTableInput.this.btnGroup.btnCancelClick();
                }else{
                    JMFormTableInput.this.form.setDisposeOnClosed();
                }
            }
        });
    }
    private void refreshLookups(){
        List<JMFormTableList> lookups=this.tableList.getLookupTables();
        if(lookups==null)return;
        for(JMFormTableList lookup:lookups){
            if(lookup==null)return;
            String query=lookup.getQueryTemplate();
            if(query!=null){
                List<JMCell> cells=this.row.getCells();
                for(int i=0;i<cells.size();i++){
                    String rpl=cells.get(i).getDBValue();
                    if(rpl!=null)query=query.replace("["+i+"]", rpl);
                }
                //JMFunctions.trace(query);
                lookup.requery(query);
            }
        }
    }
    private void refreshDetail(){
        JMFormTableList det=this.tableList.getDetailTable();
        if(det==null)return;
        String query=det.getQueryTemplate();
        if(query==null)return;
        if(this.row==null)return;
        List<JMCell> cells=this.row.getCells();
        for(int i=0;i<cells.size();i++){
            String rpl=cells.get(i).getDBValue();
            if(rpl!=null)query=query.replace("["+i+"]", rpl);
        }
        //JMFunctions.trace(query);
        det.requery(query);
        this.refreshLookups();
    }
    
    @Override
    public void actionAfterAdded(JMRow rowAdded) {
        this.row=rowAdded;
        this.setEditMode(true);
        this.refreshDetail();
    }

    @Override
    public void actionAfterDeleted(JMRow rowDeleted, boolean deleted, String extra) {
        this.setEditMode(false);
        this.row=this.table.getCurrentRow();
        this.refreshDetail();
    }

    @Override
    public void actionAfterSaved(String updateQuery,boolean saved) {
        this.setEditMode(!saved);
        this.btnGroup.stateNav();
        this.refreshDetail();
    }

    @Override
    public void actionAfterEdited(JMRow rowEdited) {
        this.row=rowEdited;
        this.setEditMode(true);
        this.refreshDetail();
    }

    @Override
    public void actionAfterPrinted(JMRow rowPrinted) {
        this.row=rowPrinted;
        this.setEditMode(false);
    }

    @Override
    public void actionAfterRefreshed(JMRow rowRefreshed) {
        this.row=rowRefreshed;
        this.setEditMode(false);
        this.refreshDetail();
    }

    @Override
    public void actionAfterViewed(JMRow rowViewed) {
        this.row=rowViewed;
        this.setEditMode(false);
    }

    @Override
    public void actionAfterMovedNext(JMRow nextRow) {
        this.row=nextRow;
        //this.setEditMode(false);
        this.refreshDetail();
    }

    @Override
    public void actionAfterMovedPrev(JMRow prevRow) {
        this.row=prevRow;
        //this.setEditMode(false);
        this.refreshDetail();
    }

    @Override
    public void actionAfterMovedFirst(JMRow firstRow) {
        this.row=firstRow;
        //this.setEditMode(false);
        this.refreshDetail();
    }

    @Override
    public void actionAfterMovedLast(JMRow lastRow) {
        this.row=lastRow;
        //this.setEditMode(false);
        this.refreshDetail();
    }

    @Override
    public void actionAfterMovedtoRecord(JMRow currentRow) {
        this.row=currentRow;
        //this.setEditMode(false);
        this.refreshDetail();
    }

    @Override
    public void actionAfterCanceled(JMRow newCurrentRow, boolean canceled, JMRow canceledRow) {
        if(this.formClosing){
            if(canceled){
                this.form.setDisposeOnClosed();
            }else{
                JMFormTableInput.this.form.setDoNothingOnClosed();
            }
        }else{
            this.setEditMode(!canceled);
            if(canceled)this.row=newCurrentRow;
        }
        this.refreshDetail();
    }

    @Override
    public void actionBeforeRefresh(JMRow rowRefreshed) {
        
    }

    @Override
    public void actionAfterFiltered(String filter) {
        //this.parent.setSearch(filter);
    }

    @Override
    public void actionBeforeFilter(String filter) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
