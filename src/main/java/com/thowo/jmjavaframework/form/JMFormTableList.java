/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.form;

import com.thowo.jmjavaframework.JMButtonInterface;
import com.thowo.jmjavaframework.JMFieldInterface;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.JMPanelInterface;
import com.thowo.jmjavaframework.JMTableInterface;
import com.thowo.jmjavaframework.db.JMDBNewRecordInterface;
import com.thowo.jmjavaframework.db.JMResultSetStyle;
import com.thowo.jmjavaframework.table.JMCell;
import com.thowo.jmjavaframework.table.JMDBListInterface;
import com.thowo.jmjavaframework.table.JMRow;
import com.thowo.jmjavaframework.table.JMTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jimi
 */
public class JMFormTableList implements JMTableInterface {
    private static final int COMPONENT_TYPE_TEXT=0;
    private static final int COMPONENT_TYPE_LOOKUP=1;
    private static final int COMPONENT_TYPE_SWITCH=2;
    
    private final String title;
    private final String tableName;
    private final String queryView;
    private final JMTable dbObject;
    private final JMDBListInterface list;
    private final JMFormDBButtonGroup btnGroup;
    private final JMFormDBButtonGroup btnGroup2;
    private final JMLocaleInterface fieldProp;
    private final List<JMFieldInterface> fields;
    private final boolean isLookup;
    private final boolean isEditable;    
    private final JMComponentWrapperInterface componentWrapper;
    private final JMDBNewRecordInterface dbNewRecWrapper;
    
    private boolean allowAdd=true;
    private boolean allowDelete=true;
    private boolean allowEdit=true;
    private boolean allowView=true;
    private boolean allowRefresh=true;
    private boolean allowPrint=true;
    private boolean allowFirst=true;
    private boolean allowPrev=true;
    private boolean allowNext=true;
    private boolean allowLast=true;
    private boolean allowGoto=true;
    
    private JMRow selectedRow=null;
    private JMFormTableList detailTable;
    private JMFormTableList masterTable;
    private JMFormTableList masterLookup;
    private List<JMFormTableList> lookupTables;
    //private JMFormDBButtonGroup btnGroup3;
    private JMFormInterface form;
    private JMFormInterface form2;
    private boolean hasDetail=false;
    private String queryTemplate="";
    private Integer[] newIdDependencyMasterColIndices;
    private Integer[] delDependencyMasterColIndices;
    private Integer[] delDependencyDetailColIndices;
    private Runnable filterAction;
    private JMTable detailBackup;
    private String myUniqueId;
    private JMFormActionsWrapperInterface formActionsWrapper;
    private boolean editing=false;
    
    private Integer rptXlsSheetNameFromColIndex=0;
    private List<Integer> rptXlsExcluded=new ArrayList();
    private List<Object> rptXlsCustomData=new ArrayList();
    private List<Integer> rptXlsColumnsNoRepetition=new ArrayList();
    
    
    private void refreshLookup(JMFormTableList lookup){
        String query=lookup.getQueryTemplate();
        if(query!=null){
            List<JMCell> cells=this.selectedRow.getCells();
            for(int i=0;i<cells.size();i++){
                String rpl=cells.get(i).getDBValue();
                if(rpl!=null)query=query.replace("["+i+"]", rpl);
            }
            //JMFunctions.trace(query);
            lookup.requery(query,true);
        }
    }
    public void refreshDetail(){
        JMFormTableList det=this.detailTable;
        if(det==null)return;
        String query=det.getQueryTemplate();
        //JMFunctions.trace("QUERY TMP:\n\n"+query);
        if(query==null)return;
        if(this.dbObject.getCurrentRow()==null)return;
        List<JMCell> cells=this.dbObject.getCurrentRow().getCells();
        for(int i=0;i<cells.size();i++){
            String rpl=cells.get(i).getDBValue();
            if(rpl!=null)query=query.replace("["+i+"]", rpl);
        }
        //JMFunctions.trace("REFRESH DETAIL QUERY:\n\n"+query);
        det.requery(query,true);
    }
    public Integer getRptXlsSheetNameFromColIndex(){
        return this.rptXlsSheetNameFromColIndex;
    }
    public List<Integer> getRptXlsExcluded(){
        return this.rptXlsExcluded;
    }
    public List<Object> getRptXlsCustomData(){
        return this.rptXlsCustomData;
    }
    public List<Integer> getRptXlsColumnsNoRepetition(){
        return this.rptXlsColumnsNoRepetition;
    }
    public JMFormTableList setRptXlsSheetNameFromColIndex(Integer rptXlsSheetNameFromColIndex){
        this.rptXlsSheetNameFromColIndex=rptXlsSheetNameFromColIndex;
        return this;
    }
    public JMFormTableList setRptXlsExcluded(Integer... rptXlsExcluded){
        if(rptXlsExcluded==null)return this;
        this.rptXlsExcluded=new ArrayList();
        for(Integer tmp:rptXlsExcluded){
            this.rptXlsExcluded.add(tmp);
        }
        return this;
    }
    public JMFormTableList setRptXlsCustomData(Object... rptXlsCustomData){
        if(rptXlsCustomData==null)return this;
        this.rptXlsCustomData=new ArrayList();
        for(Object tmp:rptXlsCustomData){
            this.rptXlsCustomData.add(tmp);
        }
        return this;
    }
    public JMFormTableList setRptXlsColumnsNoRepetition(Integer... rptXlsColumnsNoRepetition){
        if(rptXlsColumnsNoRepetition==null)return this;
        this.rptXlsColumnsNoRepetition=new ArrayList();
        for(Integer tmp:rptXlsColumnsNoRepetition){
            this.rptXlsColumnsNoRepetition.add(tmp);
        }
        return this;
    }
    public boolean isEditing(){
        return this.editing;
    }
    public void setFormActionsWrapper(JMFormActionsWrapperInterface formActionsWrapper){
        this.formActionsWrapper=formActionsWrapper;
    }
    public String getTableName(){
        return this.tableName;
    }
    public String getId(){
        return this.myUniqueId;
    }
    public void setId(String id){
        this.myUniqueId=id;
    }
    public List<JMFormTableList> getLookupTables(){
        return this.lookupTables;
    }
    public void addLookupTable(JMFormTableList tableList){
        if(this.lookupTables==null)this.lookupTables=new ArrayList();
        tableList.masterLookup=this;
        this.lookupTables.add(tableList);
    }
    public Runnable getFilterAction(){
        return this.filterAction;
    }
    public void setNewIdDependencyMasterColIndices(Integer... dependencyMasterColIndices){
        this.newIdDependencyMasterColIndices=dependencyMasterColIndices;
    }
    public void setDelDependencyMasterColIndices(Integer... dependencyMasterColIndices){
        this.delDependencyMasterColIndices=dependencyMasterColIndices;
    }
    public void setDelDependencyDetailColIndices(Integer... dependencyDetailColIndices){
        this.delDependencyDetailColIndices=dependencyDetailColIndices;
    }
    public Integer[] getNewIdDependencyMasterColIndices(){
        return this.newIdDependencyMasterColIndices;
    }
    public JMDBListInterface getDBList(){
        return this.list;
    }
    public String getTitle(){
        return this.title;
    }
    public JMTable getDbObject(){
        return this.dbObject;
    }
    public List<JMFieldInterface> getFields(){
        return this.fields;
    }
    public JMFormInterface getForm2(){
        return this.form2;
    }
    public JMLocaleInterface getFieldProp(){
        return this.fieldProp;
    }
    public boolean getEditable(){
        return this.isEditable;
    }
    public JMFormDBButtonGroup getBtnGroup(){
        return this.btnGroup;
    }
    public JMFormDBButtonGroup getBtnGroup2(){
        return this.btnGroup2;
    }
    /*public JMFormDBButtonGroup getBtnGroup3(){
        return this.btnGroup3;
    }*/
    public String getQuery(){
        return this.queryView;
    }
    public JMFormTableList getDetailTable(){
        return this.detailTable;
    }
    public void setDetailTable(JMFormTableList detailTable){
        this.detailTable=detailTable;
        /*this.btnGroup3=JMFormDBButtonGroup.create(this.detailTable.getDbObject(), this.detailTable.getTitle(), false, false, this.componentWrapper.newDBButtons(null),this.componentWrapper.newDBButtonPanels());
        this.btnGroup3.getBtnAdd().setText(fieldProp.getLabel("DB_ADD"));
        this.btnGroup3.getBtnDelete().setText(fieldProp.getLabel("DB_DELETE"));
        this.btnGroup3.getBtnEdit().setText(fieldProp.getLabel("DB_EDIT"));
        this.btnGroup3.getBtnSave().setText(fieldProp.getLabel("DB_SAVE"));
        this.btnGroup3.getBtnCancel().setText(fieldProp.getLabel("DB_CANCEL"));
        this.btnGroup3.getBtnRefresh().setText(fieldProp.getLabel("DB_REFRESH"));
        this.btnGroup3.getBtnPrint().setText(fieldProp.getLabel("DB_PRINT"));
        this.btnGroup3.getBtnFirst().setText(fieldProp.getLabel("DB_FIRST"));
        this.btnGroup3.getBtnLast().setText(fieldProp.getLabel("DB_LAST"));
        this.btnGroup3.getBtnNext().setText(fieldProp.getLabel("DB_NEXT"));
        this.btnGroup3.getBtnPrev().setText(fieldProp.getLabel("DB_PREV"));*/
        if(!this.hasDetail){
            this.form2=this.componentWrapper.newFormMasterDetail();
        }
        this.hasDetail=true;
    }
    public JMFormTableList getMasterTable(){
        return this.masterTable;
    }
    public void setMasterTable(JMFormTableList masterTable){
        this.masterTable=masterTable;
        this.form=masterTable.getForm2();
        //this.form.init(this.list, this.btnGroup);
    }
    public void setQueryTemplate(String queryTemplate){
        this.queryTemplate=queryTemplate;
    }
    public String getQueryTemplate(){
        return this.queryTemplate;
    }
    public JMRow getSelectedRow(){
        return this.selectedRow;
    }
    
    
    public static JMFormTableList create(String title, String query, JMLocaleInterface fieldProp, String tableName,JMComponentWrapperInterface componentWrapper,JMDBNewRecordInterface dbNewRecWrapper, boolean isLookup,boolean isEditable){
        return new JMFormTableList(title,query,fieldProp,tableName,componentWrapper,dbNewRecWrapper,isLookup,isEditable);
    }
    
    
    public JMFormTableList(String title, String query, JMLocaleInterface fieldProp, String tableName,JMComponentWrapperInterface componentWrapper,JMDBNewRecordInterface dbNewRecWrapper, boolean isLookup,boolean isEditable){
        this.title=title;
        this.tableName=tableName;
        this.queryView=query;
        this.fieldProp=fieldProp;
        this.isLookup=isLookup;
        this.isEditable=isEditable;
        this.componentWrapper=componentWrapper;
        this.dbNewRecWrapper=dbNewRecWrapper;
        
        //Object[] boolImg={JMFunctions.getResourcePath("img/true.png", this.getClass()).getPath(),JMFunctions.getResourcePath("img/false.png", this.getClass()).getPath()};
        
        //JMFunctions.trace("HAHAHAH \n"+this.queryView+"\n\n\n\n\n");
        this.dbObject=JMTable.create(this.queryView,JMTable.DBTYPE_MYSQL);
        
        List<String> f=this.dbObject.getStyle().getFieldNames();
        for(int i=0;i<f.size();i++){
            this.dbObject.getStyle().setLabel(i, fieldProp.getFieldLabel(f.get(i)));
        }
        
        //this.dbObject.refresh();
        this.dbObject.addInterface(this);
        this.dbObject.setName(tableName);
        this.setKeyColumns(0);
        
        this.list=this.componentWrapper.newDBTableList();
        this.list.init(this.dbObject);
        
        this.btnGroup=JMFormDBButtonGroup.create(this.dbObject, this.title, false, false, this.componentWrapper.newDBButtons(null),this.componentWrapper.newDBButtonPanels());
        this.btnGroup.getBtnAdd().setText(fieldProp.getLabel("DB_ADD"));
        this.btnGroup.getBtnDelete().setText(fieldProp.getLabel("DB_DELETE"));
        this.btnGroup.getBtnEdit().setText(fieldProp.getLabel("DB_EDIT"));
        this.btnGroup.getBtnSave().setText(fieldProp.getLabel("DB_SAVE"));
        this.btnGroup.getBtnCancel().setText(fieldProp.getLabel("DB_CANCEL"));
        this.btnGroup.getBtnRefresh().setText(fieldProp.getLabel("DB_REFRESH"));
        this.btnGroup.getBtnPrint().setText(fieldProp.getLabel("DB_PRINT"));
        this.btnGroup.getBtnFirst().setText(fieldProp.getLabel("DB_FIRST"));
        this.btnGroup.getBtnLast().setText(fieldProp.getLabel("DB_LAST"));
        this.btnGroup.getBtnNext().setText(fieldProp.getLabel("DB_NEXT"));
        this.btnGroup.getBtnPrev().setText(fieldProp.getLabel("DB_PREV"));
        
        this.btnGroup2=JMFormDBButtonGroup.create(this.dbObject, this.title, false, false, this.componentWrapper.newDBButtons(null),this.componentWrapper.newDBButtonPanels());
        this.btnGroup2.getBtnAdd().setText(fieldProp.getLabel("DB_ADD"));
        this.btnGroup2.getBtnDelete().setText(fieldProp.getLabel("DB_DELETE"));
        this.btnGroup2.getBtnEdit().setText(fieldProp.getLabel("DB_EDIT"));
        this.btnGroup2.getBtnSave().setText(fieldProp.getLabel("DB_SAVE"));
        this.btnGroup2.getBtnCancel().setText(fieldProp.getLabel("DB_CANCEL"));
        this.btnGroup2.getBtnRefresh().setText(fieldProp.getLabel("DB_REFRESH"));
        this.btnGroup2.getBtnPrint().setText(fieldProp.getLabel("DB_PRINT"));
        this.btnGroup2.getBtnFirst().setText(fieldProp.getLabel("DB_FIRST"));
        this.btnGroup2.getBtnLast().setText(fieldProp.getLabel("DB_LAST"));
        this.btnGroup2.getBtnNext().setText(fieldProp.getLabel("DB_NEXT"));
        this.btnGroup2.getBtnPrev().setText(fieldProp.getLabel("DB_PREV"));
       
        
        
        
        this.addListener();
        
        //GOTO FIRST RECORD
        if(!this.dbObject.isEmpty()){
            this.dbObject.firstRow(false);
            this.btnGroup.stateInit();
        }
        
        this.lockAccess();
        
        this.form=this.componentWrapper.newFormTable();
        this.filterAction=new Runnable() {
            @Override
            public void run() {
                JMFormTableList.this.dbObject.filter(JMFormTableList.this.form.getFilterText());
            }
        };
        this.form.init(this);
        
        this.form2=this.componentWrapper.newFormInput();
        
        this.fields=new ArrayList();
        for(int i=0;i<f.size();i++){
            this.fields.add(this.componentWrapper.newTextFields(this.fieldProp.getFieldLabel(f.get(i)), this.fieldProp.getFieldLabel(f.get(i))));
        }
    }
    
    public void show(){
        if(this.masterLookup!=null)this.masterLookup.refreshLookup(this);
        this.form.load();
    }
    
    private void setEditing(boolean editing){
        this.editing=editing;
        this.lockAccess();
        if(this.hasDetail)this.detailTable.lockAccess();
        
    }
    
    
    private void lockAccess(){
        //SEMENTARA
        if(this.btnGroup==null)return;
        if(!this.btnGroup.getBtnAdd().isLocked())this.btnGroup.getBtnAdd().setLocked(!(this.isEditable && this.allowAdd));
        if(!this.btnGroup.getBtnDelete().isLocked())this.btnGroup.getBtnDelete().setLocked(!(this.isEditable && this.allowDelete));
        if(!this.btnGroup.getBtnEdit().isLocked())this.btnGroup.getBtnEdit().setLocked(!(this.isEditable && this.allowEdit));
        //this.btnGroup.getBtnSave().setLocked(!(this.isEditable));
        //this.btnGroup.getBtnCancel().setLocked(!(this.isEditable));
        if(!this.btnGroup.getBtnPrint().isLocked())this.btnGroup.getBtnPrint().setLocked(!(this.allowPrint));
        if(!this.btnGroup.getBtnRefresh().isLocked())this.btnGroup.getBtnRefresh().setLocked(!(this.allowRefresh));
        if(!this.btnGroup.getBtnFirst().isLocked())this.btnGroup.getBtnFirst().setLocked(!(this.allowFirst));
        if(!this.btnGroup.getBtnPrev().isLocked())this.btnGroup.getBtnPrev().setLocked(!(this.allowPrev));
        if(!this.btnGroup.getBtnNext().isLocked())this.btnGroup.getBtnNext().setLocked(!(this.allowNext));
        if(!this.btnGroup.getBtnLast().isLocked())this.btnGroup.getBtnLast().setLocked(!(this.allowLast));
        //TODO LOCK GOTO 
        
        
        
        if(this.getMasterTable()==null)return;
        if(!this.btnGroup.getBtnAdd().isLocked())this.btnGroup.getBtnAdd().setLocked(!(this.isEditable && this.allowAdd && this.getMasterTable().editing));
        if(!this.btnGroup.getBtnDelete().isLocked())this.btnGroup.getBtnDelete().setLocked(!(this.isEditable && this.allowDelete && this.getMasterTable().editing));
        if(!this.btnGroup.getBtnEdit().isLocked())this.btnGroup.getBtnEdit().setLocked(!(this.isEditable && this.allowEdit && this.getMasterTable().editing));
        //this.btnGroup.getBtnSave().setLocked(!(this.isEditable));
        //this.btnGroup.getBtnCancel().setLocked(!(this.isEditable));
        if(!this.btnGroup.getBtnPrint().isLocked())this.btnGroup.getBtnPrint().setLocked(!(this.allowPrint));
        if(!this.btnGroup.getBtnRefresh().isLocked())this.btnGroup.getBtnRefresh().setLocked(!(this.allowRefresh));
        if(!this.btnGroup.getBtnFirst().isLocked())this.btnGroup.getBtnFirst().setLocked(!(this.allowFirst));
        if(!this.btnGroup.getBtnPrev().isLocked())this.btnGroup.getBtnPrev().setLocked(!(this.allowPrev));
        if(!this.btnGroup.getBtnNext().isLocked())this.btnGroup.getBtnNext().setLocked(!(this.allowNext));
        if(!this.btnGroup.getBtnLast().isLocked())this.btnGroup.getBtnLast().setLocked(!(this.allowLast));
        //TODO LOCK GOTO 


        /*
        //JMFunctions.trace(this.isEditable+""+this.getMasterTable().editing);
        //this.btnGroup.getBtnAdd().setLocked(false);
        this.btnGroup.getBtnAdd().setLocked(!(this.isEditable && this.getMasterTable().editing && this.allowAdd));
        this.btnGroup.getBtnDelete().setLocked(!(this.isEditable && this.getMasterTable().editing && this.allowDelete));
        this.btnGroup.getBtnEdit().setLocked(!(this.isEditable && this.getMasterTable().editing && this.allowE));
        //this.btnGroup.getBtnSave().setLocked((this.isEditable && this.getMasterTable().editing));
        //this.btnGroup.getBtnCancel().setLocked((this.isEditable && this.getMasterTable().editing));
        this.btnGroup.getBtnPrint().setLocked(!(this.isEditable && masterEditing));


        */
    }
    public void pack(){
        this.dbObject.refresh(false);
        this.list.init(this.dbObject);
    }
    
    public JMFormTableList makeFieldsHidden(Integer... fieldIndices){
        for(Integer x:fieldIndices){
            this.fields.get(x).setHidden(true);
        }
        return this;
    }
    public JMFormTableList makeFieldsDisabled(Integer... fieldIndices){
        for(Integer x:fieldIndices){
            this.fields.get(x).setDisabled(true);
        }
        return this;
    }
    public JMFormTableList setFieldAsSwitch(int fieldIndex, String trueString, String falseString){
        if(fieldIndex<this.fields.size()){
            this.fields.set(fieldIndex, this.componentWrapper.newSwitchFields(trueString, falseString));
        }
        return this;
    }
    public JMFormTableList setFieldAsLookup(int fieldIndex, JMFormTableList lookTable, List<Integer> masterIndex, List<Integer> lookupIndex){
        if(fieldIndex<this.fields.size()){
            List<String> f=this.dbObject.getStyle().getFieldNames();
            JMFieldInterface tmp=this.componentWrapper.newLookupFields(this.fieldProp.getFieldLabel(f.get(fieldIndex)), this.fieldProp.getFieldLabel(f.get(fieldIndex)));
            Runnable r=new Runnable() {
                @Override
                public void run() {
                    JMRow res=lookTable.select();
                    if(res!=null){
                        for(int i=0;i<masterIndex.size();i++){
                            JMFormTableList.this.dbObject.getCurrentRow().setValueFromString(masterIndex.get(i), res.getCells().get(lookupIndex.get(i)).getDBValue());
                        }
                    }
                }
            };
            tmp.setLookUpAction(r);
            this.fields.set(fieldIndex, tmp);
        }
        return this;
    }
    
    
    public JMFormTableList setAllowAdd(boolean allowAdd){
        this.allowAdd=allowAdd;
        return this;
    }
    public boolean canAdd(){
        return this.allowAdd;
    }
    public JMFormTableList setAllowDelete(boolean allowDelete){
        this.allowDelete=allowDelete;
        return this;
    }
    public boolean canDelete(){
        return this.allowDelete;
    }
    public JMFormTableList setAllowEdit(boolean allowEdit){
        this.allowEdit=allowEdit;
        return this;
    }
    public boolean canEdit(){
        return this.allowEdit;
    }
    public JMFormTableList setAllowView(boolean allowView){
        this.allowView=allowView;
        return this;
    }
    public boolean canView(){
        return this.allowView;
    }
    public JMFormTableList setAllowRefresh(boolean allowRefresh){
        this.allowRefresh=allowRefresh;
        return this;
    }
    public boolean canRefresh(){
        return this.allowRefresh;
    }
    public JMFormTableList setAllowPrint(boolean allowPrint){
        this.allowPrint=allowPrint;
        return this;
    }
    public boolean canPrint(){
        return this.allowPrint;
    }
    public JMFormTableList setAllowFirst(boolean allowFirst){
        this.allowFirst=allowFirst;
        return this;
    }
    public boolean canFirst(){
        return this.allowFirst;
    }
    public JMFormTableList setAllowPrev(boolean allowPrev){
        this.allowPrev=allowPrev;
        return this;
    }
    public boolean canPrev(){
        return this.allowPrev;
    }
    public JMFormTableList setAllowNext(boolean allowNext){
        this.allowNext=allowNext;
        return this;
    }
    public boolean canNext(){
        return this.allowNext;
    }
    public JMFormTableList setAllowLast(boolean allowLast){
        this.allowLast=allowLast;
        return this;
    }
    public boolean canLast(){
        return this.allowLast;
    }
    public JMFormTableList setAllowGoto(boolean allowGoto){
        this.allowGoto=allowGoto;
        return this;
    }
    public boolean canGoto(){
        return this.allowGoto;
    }
    
    
    public JMFormTableList setBoolImage(Object[] boolImage, int col){
        this.dbObject.getStyle().addFormat(col, JMResultSetStyle.FORMAT_IMAGE, boolImage);
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        return this;
    }
    public JMFormTableList overrideLabel(String label, int col){
        this.dbObject.getStyle().setLabel(col, label);
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        return this;
    }
    
    public JMFormTableList overrideLabel(String label, String col){
        this.dbObject.getStyle().setLabel(col, label);
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        return this;
    }
    
    public JMFormTableList makeAllowNulls(Integer... cols){
        for(Integer c:cols){
            this.dbObject.getStyle().setAllowNull(c, true);
        }
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        //this.form.init(this.title, this.list, this.btnGroup, filterAction, this.fieldProp, this.isLookup);
        return this;
    }
    public JMFormTableList makeAllowNulls(String... cols){
        for(String c:cols){
            this.dbObject.getStyle().setAllowNull(c, true);
        }
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        return this;
    }
    
    public JMFormTableList hideColumns(Integer... cols){
        for(Integer c:cols){
            this.dbObject.getStyle().setColHidden(c);
        }
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        //this.form.init(this.title, this.list, this.btnGroup, filterAction, this.fieldProp, this.isLookup);
        return this;
    }
    
    public JMFormTableList hideColumns(String... cols){
        for(String c:cols){
            this.dbObject.getStyle().setColHidden(c);
        }
        //this.dbObject.refresh();
        //this.list.init(this.dbObject);
        return this;
    }
    
    public JMFormTableList excludeColumnsFromUpdate(Integer... cols){
        List<Integer> ex=new ArrayList();
        for(Integer c:cols){
            ex.add(c);
        }
        this.dbObject.excludeColumnsFromUpdate(ex);
        return this;
    }

    public JMFormTableList setKeyColumns(Integer... cols){
        List<Integer> keys=new ArrayList();
        for(Integer c:cols){
            keys.add(c);
        }
        this.dbObject.setKeyColumns(keys);
        return this;
    }
    
    public JMRow select(){
        List<Runnable> okCancelRunnables=new ArrayList();
        okCancelRunnables.add(new Runnable() {
            @Override
            public void run() {
                JMFormTableList.this.selectedRow=JMFormTableList.this.dbObject.getCurrentRow();
            }
        });
        okCancelRunnables.add(new Runnable() {
            @Override
            public void run() {
                JMFormTableList.this.selectedRow=null;
            }
        });
        this.form.setOkCancelRunnables(okCancelRunnables);
        this.show();
        return this.selectedRow;
    }
    
    private void openInput(boolean editing, boolean adding){
        //InputOPD.create(TablePengadaan.this.dbObject,parent,editing,adding);
        //if(this.parentTable!=null)InputBidang.create(TableBidang.this.dbObject,this.parentTable,editing,adding);
        
        JMFormTableInput.create(this,editing,adding);
    }
    
    private void backupDetail(){
        JMFormTableList det=this.detailTable;
        if(det==null)return;
        String query=det.getQueryTemplate();
        if(query==null)return;
        if(this.dbObject.getCurrentRow()==null)return;
        List<JMCell> cells=this.dbObject.getCurrentRow().getCells();
        for(int i=0;i<cells.size();i++){
            String rpl=cells.get(i).getDBValue();
            if(rpl!=null)query=query.replace("["+i+"]", rpl);
        }
        
        
        JMFunctions.trace("BACKED UP : \n"+query);
        this.detailBackup=JMTable.create(query, JMTable.DBTYPE_MYSQL);
        this.detailBackup.setName(this.detailTable.tableName);
        List<Integer> excluded=this.detailTable.dbObject.getExcludedCols();
        this.detailBackup.excludeColumnsFromUpdate(excluded);
    }
    
    private void deleteDetails(JMRow row){
        if(!this.hasDetail)return;
        JMFunctions.trace("HAS DETAIL");
        if(this.delDependencyMasterColIndices!=null && this.delDependencyDetailColIndices!=null){
            String qDel="";
            List<String> fields=this.detailTable.dbObject.getStyle().getFieldNames();
            for(int i=0;i<this.delDependencyMasterColIndices.length;i++){
                String tmp=fields.get(this.delDependencyDetailColIndices[i])+"='"+row.getCells().get(this.delDependencyMasterColIndices[i]).getDBValue()+"'";
                if(qDel.equals("")){
                    qDel=tmp;
                }else{
                    qDel+=" AND "+tmp;
                }
            }
            JMFunctions.trace("\n\ndelete from "+this.detailTable.tableName+" where ("+qDel+")\n\n");
            JMFunctions.getCurrentConnection().queryUpdateMySQL("delete from "+this.detailTable.tableName+" where ("+qDel+")", false);
            JMFunctions.trace("DETAIL DELETED");
        }
        JMFunctions.trace("EXIT DELETE DETAIL");
    }
    
    private void restoreDetail(JMRow canceledRow){
        if(!this.hasDetail)return;
        //JMFunctions.trace("delete from p_tb_mutasi_det_real where id_mutasi='"+canceledRow.getCells().get(0).getDBValue()+"'");
        this.deleteDetails(canceledRow);
        
        if(this.detailBackup==null)return;
        if(!this.detailBackup.isEmpty()){
            this.detailBackup.firstRow(false);
            do{
                JMFunctions.trace("PPPPP    \n"+this.detailBackup.getCurrentRow().getUpdateSQL());
                JMFunctions.getCurrentConnection().queryUpdateMySQL(this.detailBackup.getCurrentRow().getUpdateSQL(), false);
            }while(this.detailBackup.nextRow(false)!=null);
        }
        this.detailBackup=null;
    }
    
    private void addListener(){
        this.list.setOnSelected(new Runnable() {
            @Override
            public void run() {
                if(JMFormTableList.this.isLookup){
                    JMFormTableList.this.form.closeMe(true);
                }else JMFormTableList.this.openInput(false,false);
            }
        });
        this.btnGroup.getBtnAdd().addAction(new Runnable() {
            @Override
            public void run() {
                JMFormTableList.this.openInput(true,true);
            }
        });
        this.btnGroup.getBtnEdit().addAction(new Runnable() {
            @Override
            public void run() {
                JMFormTableList.this.openInput(true,false);
            }
        });
        this.btnGroup.getBtnView().addAction(new Runnable() {
            @Override
            public void run() {
                JMFormTableList.this.openInput(false,false);
            }
        });
    }
    public void requery(String query,boolean refresh){
        this.dbObject.requery(query,refresh);
    }
    
    private List<String> getDependencyMasterColValues(){
        List<String> ret=new ArrayList();
        if(this.newIdDependencyMasterColIndices!=null && this.getMasterTable().getSelectedRow()!=null){
            List<JMCell> cells=this.getMasterTable().getSelectedRow().getCells();
            for(Integer n:this.newIdDependencyMasterColIndices){
                ret.add(cells.get(n).getDBValue());
            }
        }
        return ret;
    }

    @Override
    public void actionAfterAdded(JMRow rowAdded) {
        //JMFunctions.traceAndShow("ADDED");
        this.selectedRow=rowAdded;
        this.dbNewRecWrapper.newDefaultRow(this.tableName,rowAdded,this.getDependencyMasterColValues(),this.myUniqueId);
        this.backupDetail();
        this.setEditing(true);
    }

    @Override
    public void actionAfterDeleted(JMRow row, boolean deleted, String extra) {
        if(deleted && extra==null){
            this.deleteDetails(row);
            this.detailBackup=null;
        }
    }

    @Override
    public void actionAfterSaved(String updateQuery, boolean saved) {
        if(saved){
            if(this.formActionsWrapper!=null)this.formActionsWrapper.formOnSaved(this, this.myUniqueId);
            this.detailBackup=null;
            this.setEditing(false);
        }
    }

    @Override
    public void actionAfterEdited(JMRow rowEdited) {
        this.backupDetail();
        this.setEditing(true);
        if(this.formActionsWrapper!=null)this.formActionsWrapper.formOnEdited(this, this.myUniqueId);
    }

    @Override
    public void actionAfterPrinted(JMRow rowPrinted) {
        
    }

    @Override
    public void actionAfterRefreshed(JMRow rowRefreshed) {
        this.detailBackup=null;
        this.setEditing(false);
    }

    @Override
    public void actionBeforeRefresh(JMRow rowRefreshed) {
        this.selectedRow=rowRefreshed;
        this.setEditing(false);
    }

    @Override
    public void actionAfterViewed(JMRow rowViewed) {
        
    }

    @Override
    public void actionAfterMovedNext(JMRow nextRow) {
        this.selectedRow=nextRow;
        this.detailBackup=null;
        this.setEditing(this.editing);
    }

    @Override
    public void actionAfterMovedPrev(JMRow prevRow) {
        this.selectedRow=prevRow;
        this.detailBackup=null;
        this.setEditing(this.editing);
    }

    @Override
    public void actionAfterMovedFirst(JMRow firstRow) {
        this.selectedRow=firstRow;
        this.detailBackup=null;
        this.setEditing(this.editing);
    }

    @Override
    public void actionAfterMovedLast(JMRow lastRow) {
        this.selectedRow=lastRow;
        this.detailBackup=null;
        this.setEditing(this.editing);
    }

    @Override
    public void actionAfterMovedtoRecord(JMRow currentRow) {
        this.selectedRow=currentRow;
        this.detailBackup=null;
        this.setEditing(this.editing);
    }

    @Override
    public void actionAfterCanceled(JMRow newCurrentRow, boolean canceled, JMRow canceledRow) {
        if(canceled){
            this.restoreDetail(canceledRow);
            this.setEditing(false);
        }
    }

    @Override
    public void actionBeforeFilter(String filter) {
        
    }

    @Override
    public void actionAfterFiltered(String filter) {
        this.setEditing(false);
    }
}
