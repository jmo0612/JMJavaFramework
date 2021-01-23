/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.form;

import com.thowo.jmjavaframework.JMButtonInterface;
import com.thowo.jmjavaframework.JMColor;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.JMPanelInterface;
import com.thowo.jmjavaframework.JMTableInterface;
import com.thowo.jmjavaframework.JMVec2;
import com.thowo.jmjavaframework.lang.JMConstMessage;
import com.thowo.jmjavaframework.table.JMRow;
import com.thowo.jmjavaframework.table.JMTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jimi
 */
public class JMFormDBButtonGroup implements JMTableInterface {
    private JMPanelInterface op;
    private JMPanelInterface rec;
    private JMTable table;
    private String formTitle;
    
    private JMButtonInterface btnAdd;
    private JMButtonInterface btnEdit;
    private JMButtonInterface btnDelete;
    private JMButtonInterface btnSave;
    private JMButtonInterface btnPrint;
    private JMButtonInterface btnNext;
    private JMButtonInterface btnPrev;
    private JMButtonInterface btnFirst;
    private JMButtonInterface btnLast;
    private JMButtonInterface btnView;
    private JMButtonInterface btnRefresh;
    private JMButtonInterface btnCancel;
    private int defWidth=50;
    private int defHeight=10;
    private List<Boolean> befFilter=new ArrayList();
    
    public static JMFormDBButtonGroup create(JMTable table, String formTitle,boolean editing,boolean adding,List<JMButtonInterface> dbButtons,List<JMPanelInterface> dbButtonPanels){
        return new JMFormDBButtonGroup(table, formTitle,editing,adding,dbButtons,dbButtonPanels);
    }
    public static JMFormDBButtonGroup create(){
        return new JMFormDBButtonGroup();
    }
    public JMFormDBButtonGroup(JMTable table, String formTitle,boolean editing,boolean adding,List<JMButtonInterface> dbButtons,List<JMPanelInterface> dbButtonPanels){
        this.init(table, formTitle, editing, adding, dbButtons,dbButtonPanels);
    }
    public JMFormDBButtonGroup(){
        
    }
    private void init(JMTable table, String formTitle,boolean editing,boolean adding,List<JMButtonInterface> dbButtons,List<JMPanelInterface> dbButtonPanels){
        this.table=table;
        
        this.formTitle=formTitle;
        this.table.addInterface(this);
        this.op=dbButtonPanels.get(0);
        this.rec=dbButtonPanels.get(1);
        this.setProp(editing,adding,dbButtons);
    }
    
    private void setProp(boolean editing,boolean adding,List<JMButtonInterface> dbButtons){
        this.btnAdd=dbButtons.get(0);
        this.btnEdit=dbButtons.get(1);
        this.btnDelete=dbButtons.get(2);
        this.btnSave=dbButtons.get(3);
        this.btnPrint=dbButtons.get(4);
        this.btnNext=dbButtons.get(5);
        this.btnPrev=dbButtons.get(6);
        this.btnFirst=dbButtons.get(7);
        this.btnLast=dbButtons.get(8);
        this.btnView=dbButtons.get(9);
        this.btnRefresh=dbButtons.get(10);
        this.btnCancel=dbButtons.get(11);
        this.view(editing,adding);
    }
    
    private void display(boolean editing,boolean adding){
        if(editing){
            if(adding)this.stateAdd();
            else this.stateEdit();
        }else{
            this.stateInit();
        }
    }
    
    private void view(boolean editing,boolean adding){
        
        op.setOpaque(false);
        op.addComponent(this.btnAdd,null);
        op.addComponent(this.btnDelete,null);
        op.addComponent(this.btnEdit,null);
        op.addComponent(this.btnSave,null);
        op.addComponent(this.btnCancel,null);
        op.addComponent(this.btnView,null);
        op.addComponent(this.btnRefresh,null);
        op.addComponent(this.btnPrint,null);
        
        rec.setOpaque(false);
        rec.addComponent(this.btnFirst,null);
        rec.addComponent(this.btnPrev,null);
        rec.addComponent(this.btnNext,null);
        rec.addComponent(this.btnLast,null);
        this.addListener();
        this.display(editing, adding);
    }
    
    public JMPanelInterface getNavigationPanel(){
        return this.rec;
    }
    public JMPanelInterface getEditorPanel(){
        return this.op;
    }
    
    private void addListener(){
        this.btnAdd.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.btnAddClick();
            }
        });
        this.btnEdit.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.editRow();
            }
        });
        this.btnDelete.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.btnDeleteClick();
            }
        });
        this.btnSave.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.save();
            }
        });
        this.btnPrint.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.print();
            }
        });
        this.btnNext.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.nextRow(true);
            }
        });
        this.btnPrev.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.prevRow(true);
            }
        });
        this.btnFirst.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.firstRow(true);
            }
        });
        this.btnLast.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.table.lastRow(true);
            }
        });
        this.btnView.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.btnViewClick();
            }
        });
        this.btnRefresh.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.btnRefreshClick();
            }
        });
        this.btnCancel.setAction(new Runnable(){
            @Override
            public void run() {
                JMFormDBButtonGroup.this.btnCancelClick();
            }
        });
    }
    
    public void btnCancelClick(){
        this.table.cancelEdit(JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_CANCEL_EDITING)+" "+formTitle+"?", JMTable.YES_OPTION);
    }
    public void btnDeleteClick(){
        this.table.deleteRow(this.table.getCurrentRow(), JMFunctions.getMessege(JMConstMessage.MSG_ELSE+JMConstMessage.MSG_ELSE_DELETE)+" "+formTitle+"?", JMTable.YES_OPTION,null);
    }
    public void btnAddClick(){
        //List<String> fieldNames=this.table.getStyle().getFieldNames();
        //List<JMFieldInterface> objs=new ArrayList();
        //for(String fieldName:fieldNames)objs.add(JMPCCellObject.create());
        
        this.table.addNewRow();
        
    }
    public void btnRefreshClick(){
        JMFormDBButtonGroup.this.table.refresh(true);
    }
    public void btnViewClick(){
        JMFormDBButtonGroup.this.table.viewRow();
    }
    
    private void backUpState(){
        if(!this.befFilter.isEmpty())return;
        this.befFilter.clear();
        this.befFilter.add(this.btnAdd.isLocked());
        this.befFilter.add(this.btnSave.isLocked());
        this.befFilter.add(this.btnRefresh.isLocked());
        this.befFilter.add(this.btnCancel.isLocked());
        this.befFilter.add(this.btnPrev.isLocked());
        this.befFilter.add(this.btnNext.isLocked());
        this.befFilter.add(this.btnLast.isLocked());
        this.befFilter.add(this.btnFirst.isLocked());
        this.befFilter.add(this.btnView.isLocked());
        this.befFilter.add(this.btnPrint.isLocked());
        this.befFilter.add(this.btnEdit.isLocked());
        this.befFilter.add(this.btnDelete.isLocked());
    }
    
    private void stateFilter(){
        if(this.befFilter.isEmpty())return;
        //if(this.befFilter.isEmpty())this.backUpState();
        String filter=this.table.getFilter();
        //JMFunctions.trace("Button locked:"+this.befFilter.get(0)+",   Search empty:"+filter.equals(""));
        this.btnAdd.setLocked(this.befFilter.get(0)||!filter.equals(""));
        //this.btnSave.setLocked(this.befFilter.get(1));
        //this.btnRefresh.setLocked(this.befFilter.get(2));
        //this.btnCancel.setLocked(this.befFilter.get(3));
        this.btnPrev.setLocked(this.befFilter.get(4)||!filter.equals(""));
        this.btnNext.setLocked(this.befFilter.get(5)||!filter.equals(""));
        this.btnLast.setLocked(this.befFilter.get(6)||!filter.equals(""));
        this.btnFirst.setLocked(this.befFilter.get(7)||!filter.equals(""));
        //this.btnView.setLocked(this.befFilter.get(8));
        //this.btnPrint.setLocked(this.befFilter.get(9));
        //this.btnEdit.setLocked(this.befFilter.get(10));
        //this.btnDelete.setLocked(this.befFilter.get(11));
        if(filter.equals(""))this.befFilter.clear();
    }
    
    public void stateInit(){
        boolean on=false;//NEGATE
        //if(this.table.isAddingRow())on=true;
        //else if(this.table.isEditingRow())on=true;
        this.btnAdd.setLocked(on);
        this.btnDelete.setLocked(on);
        this.btnEdit.setLocked(on);
        this.btnPrint.setLocked(on);
        this.btnSave.setLocked(!on);
        this.btnView.setLocked(on);
        this.btnRefresh.setLocked(on);
        this.btnCancel.setLocked(!on);
        this.stateNav();
    }
    public void stateAdd(){
        boolean on=false;//NEGATE
        this.btnAdd.setLocked(!on);
        this.btnDelete.setLocked(!on);
        this.btnEdit.setLocked(!on);
        this.btnPrint.setLocked(!on);
        this.btnSave.setLocked(on);
        this.btnView.setLocked(!on);
        this.btnRefresh.setLocked(!on);
        this.btnCancel.setLocked(on);
        this.btnPrev.setLocked(true);
        this.btnNext.setLocked(true);
        this.btnLast.setLocked(true);
        this.btnFirst.setLocked(true);
        this.stateFilter();
    }
    public void stateDelete(){
        this.stateInit();
    }
    public void stateEdit(){
        boolean on=false;//NEGATE
        this.btnAdd.setLocked(!on);
        this.btnDelete.setLocked(!on);
        this.btnEdit.setLocked(!on);
        this.btnPrint.setLocked(!on);
        this.btnSave.setLocked(on);
        this.btnView.setLocked(!on);
        this.btnRefresh.setLocked(!on);
        this.btnCancel.setLocked(on);
        this.btnPrev.setLocked(true);
        this.btnNext.setLocked(true);
        this.btnLast.setLocked(true);
        this.btnFirst.setLocked(true);
        this.stateFilter();
    }
    private void statePrint(){
        /*boolean on=false;//NEGATE
        this.btnAdd.setLocked(on);
        this.btnDelete.setLocked(on);
        this.btnEdit.setLocked(!on);
        this.btnPrint.setLocked(!on);
        this.btnSave.setLocked(on);
        this.btnPrev.setLocked(!on);
        this.btnNext.setLocked(!on);
        this.btnLast.setLocked(!on);
        this.btnFirst.setLocked(!on);
        this.btnView.setLocked(!on);
        this.btnRefresh.setLocked(!on);*/
    }
    private void stateSave(){
        this.stateInit();
    }
    public void stateNav(){
        if(this.table==null){
            this.btnPrev.setLocked(true);
            this.btnNext.setLocked(true);
            this.btnLast.setLocked(true);
            this.btnFirst.setLocked(true);
            this.btnView.setLocked(true);
            this.btnPrint.setLocked(true);
            this.btnEdit.setLocked(true);
            this.btnDelete.setLocked(true);
        }else{
            if(this.table.getCurrentRow()==null){
                this.btnPrev.setLocked(true);
                this.btnNext.setLocked(true);
                this.btnLast.setLocked(true);
                this.btnFirst.setLocked(true);
                this.btnView.setLocked(true);
                this.btnPrint.setLocked(true);
                this.btnEdit.setLocked(true);
                this.btnDelete.setLocked(true);
            }else{
                boolean f=!this.table.isFirstRecord();//NEGATE
                boolean l=!this.table.isLastRecord();//NEGATE
                this.btnPrev.setLocked(!f);
                this.btnNext.setLocked(!l);
                this.btnLast.setLocked(!l);
                this.btnFirst.setLocked(!f);
            }
        }
        this.stateFilter();
    }
    public void stateView(){
        this.stateInit();
    }
    private void stateRefresh(){
        this.stateInit();
    }
    
    public JMButtonInterface getBtnCancel() {
        return btnCancel;
    }
    public void setBtnCancel(JMButtonInterface btnCancel) {
        this.btnCancel = btnCancel;
    }
    public JMButtonInterface getBtnRefresh() {
        return btnRefresh;
    }
    public void setBtnRefresh(JMButtonInterface btnRefresh) {
        this.btnRefresh = btnRefresh;
    }
    public JMButtonInterface getBtnView() {
        return btnView;
    }
    public void setBtnView(JMButtonInterface btnView) {
        this.btnView = btnView;
    }
    public JMButtonInterface getBtnAdd() {
        return btnAdd;
    }
    public void setBtnAdd(JMButtonInterface btnAdd) {
        this.btnAdd = btnAdd;
    }
    public JMButtonInterface getBtnEdit() {
        return btnEdit;
    }
    public void setBtnEdit(JMButtonInterface btnEdit) {
        this.btnEdit = btnEdit;
    }
    public JMButtonInterface getBtnDelete() {
        return btnDelete;
    }
    public void setBtnDelete(JMButtonInterface btnDelete) {
        this.btnDelete = btnDelete;
    }
    public JMButtonInterface getBtnSave() {
        return btnSave;
    }
    public void setBtnSave(JMButtonInterface btnSave) {
        this.btnSave = btnSave;
    }
    public JMButtonInterface getBtnPrint() {
        return btnPrint;
    }
    public void setBtnPrint(JMButtonInterface btnPrint) {
        this.btnPrint = btnPrint;
    }
    public JMButtonInterface getBtnNext() {
        return btnNext;
    }
    public void setBtnNext(JMButtonInterface btnNext) {
        this.btnNext = btnNext;
    }
    public JMButtonInterface getBtnPrev() {
        return btnPrev;
    }
    public void setBtnPrev(JMButtonInterface btnPrev) {
        this.btnPrev = btnPrev;
    }
    public JMButtonInterface getBtnFirst() {
        return btnFirst;
    }
    public void setBtnFirst(JMButtonInterface btnFirst) {
        this.btnFirst = btnFirst;
    }
    public JMButtonInterface getBtnLast() {
        return btnLast;
    }
    public void setBtnLast(JMButtonInterface btnLast) {
        this.btnLast = btnLast;
    }

    @Override
    public void actionAfterAdded(JMRow rowAdded) {
        this.table.gotoRow(rowAdded, true);
        this.stateAdd();
    }

    @Override
    public void actionAfterDeleted(JMRow row, boolean deleted, String extra) {
        this.stateDelete();
    }

    @Override
    public void actionAfterSaved(String updateQuery, boolean saved) {
        if(saved)this.stateSave();
    }

    @Override
    public void actionAfterEdited(JMRow rowEdited) {
        this.stateEdit();
    }

    @Override
    public void actionAfterPrinted(JMRow rowPrinted) {
        this.statePrint();
    }

    @Override
    public void actionAfterRefreshed(JMRow rowRefreshed) {
        this.stateRefresh();
    }

    @Override
    public void actionBeforeRefresh(JMRow rowRefreshed) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionAfterViewed(JMRow rowViewed) {
        this.stateView();
    }

    @Override
    public void actionAfterMovedNext(JMRow nextRow) {
        this.stateNav();
    }

    @Override
    public void actionAfterMovedPrev(JMRow prevRow) {
        this.stateNav();
    }

    @Override
    public void actionAfterMovedFirst(JMRow firstRow) {
        this.stateNav();
    }

    @Override
    public void actionAfterMovedLast(JMRow lastRow) {
        this.stateNav();
    }

    @Override
    public void actionAfterMovedtoRecord(JMRow currentRow) {
        this.stateNav();
    }

    @Override
    public void actionAfterCanceled(JMRow newCurrentRow, boolean canceled, JMRow canceledRow) {
        if(canceled)this.stateInit();
    }

    @Override
    public void actionBeforeFilter(String filter) {
        this.backUpState();
    }

    @Override
    public void actionAfterFiltered(String filter) {
        this.stateFilter();
    }
}
