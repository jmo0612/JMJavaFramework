package com.thowo.jmjavaframework.db;

/*
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
*/

import com.thowo.jmjavaframework.JMAsyncListener;
import com.thowo.jmjavaframework.JMAsyncTask;
import com.thowo.jmjavaframework.JMFunctions;
//import com.thowo.jmframework.R;
//import com.thowo.jmjavaframework.component.JMLoadingSprite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jimi on 6/3/2017.
 */

public class JMConnection {
    public static final String JM_ASYNC_CONNECT="DBCONNECT";
    public static final String JM_ASYNC_FETCH="DBFETCH";
    public static final String JM_ASYNC_UPDATE="DBUPDATE";
    public static final String JM_ASYNC_DELETE="DBDELETE";
    
    //private Context context;
    private Connection conSQLite=null;
    private Connection conMySQL=null;
    
    private Boolean connectedSQLite=false;
    private Boolean connectedMySQL=false;
    
    private String errMsg="";
    
    private JMDBMySQL dbMySQL=null;
    private File dbSQLite=null;


    public JMConnection(File dbSQLite, JMDBMySQL dbMySQL){
        if(dbSQLite!=null)connectSQLite(dbSQLite);
        if(dbMySQL!=null)connectMySQL(dbMySQL);
        
    }
    
    private void connectSQLite(File dbSQLite){
        this.dbSQLite=dbSQLite;
        
        if(JMFunctions.fileExist(this.dbSQLite)){
            String url="jdbc:sqlite:"+this.dbSQLite.getAbsolutePath();
            
            Callable<Boolean> c=()->{
                try {
                    this.conSQLite=DriverManager.getConnection(url);
                    this.connectedSQLite=true;
                    return true;
                } catch (SQLException ex) {
                    Logger.getLogger(JMConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.errMsg=ex.getMessage();
                    this.connectedSQLite=false;
                    return false;
                }
            };
            
            new JMAsyncTask(JMFunctions.getCurrentAsyncListener(),c,JMConnection.JM_ASYNC_CONNECT);
            
        }
        if(!this.errMsg.equals(""))JMFunctions.traceAndShow(this.errMsg);
    }

    private void connectMySQL(JMDBMySQL dbMySQL){
        this.dbMySQL=dbMySQL;
        if(this.dbMySQL!=null){
            
            Callable<Boolean> c=()->{
                try {
                    this.conMySQL=DriverManager.getConnection(this.dbMySQL.getConnectionString(),this.dbMySQL.getUser(),this.dbMySQL.getPass());
                    this.connectedMySQL=true;
                    return true;
                } catch (SQLException ex) {
                    Logger.getLogger(JMConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.errMsg=ex.getMessage();
                    this.connectedMySQL=false;
                    return false;
                }
            };
            
            new JMAsyncTask(JMFunctions.getCurrentAsyncListener(),c,JMConnection.JM_ASYNC_CONNECT);
            
        }
        if(!this.errMsg.equals(""))JMFunctions.traceAndShow(this.errMsg);
    }

    public JMConnection(File dbSQLite){
        if(dbSQLite!=null)connectSQLite(dbSQLite);
    }
    
    public JMConnection(JMDBMySQL dbMySQL){
        if(dbMySQL!=null)connectMySQL(dbMySQL);
    }

    public ResultSet querySQLite(String sql){
        ResultSet ret=null;
        if(this.connectedSQLite){
            Callable<ResultSet> c=()->{
                try {
                    Statement stmt=this.conSQLite.createStatement();
                    ResultSet rs=stmt.executeQuery(sql);
                    this.errMsg="";
                    return rs; 
                } catch (SQLException ex) {
                    Logger.getLogger(JMConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.errMsg=ex.getMessage();
                    return null;
                }
                
            };
            ret= (ResultSet) new JMAsyncTask(JMFunctions.getCurrentAsyncListener(),c,JMConnection.JM_ASYNC_FETCH);
            
        }
        
        if(!this.errMsg.equals(""))JMFunctions.traceAndShow(this.errMsg);
        return ret;
    }
    
    public ResultSet queryMySQL(String sql){
        ResultSet ret=null;
        if(this.connectedMySQL){
            Callable<ResultSet> c=()->{
                try {
                    Statement stmt=this.conMySQL.createStatement();
                    ResultSet rs=stmt.executeQuery(sql);
                    this.errMsg="";
                    return rs; 
                } catch (SQLException ex) {
                    Logger.getLogger(JMConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.errMsg=ex.getMessage();
                    return null;
                }
                
            };
            ret=(ResultSet) new JMAsyncTask(JMFunctions.getCurrentAsyncListener(),c,JMConnection.JM_ASYNC_FETCH);
            
        }
        
        if(!this.errMsg.equals(""))JMFunctions.traceAndShow(this.errMsg);
        return ret;
    }

}
