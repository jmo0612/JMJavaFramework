/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.db;

import com.thowo.jmjavaframework.JMDate;
import com.thowo.jmjavaframework.JMFunctions;
import com.thowo.jmjavaframework.lang.JMConstMessage;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimi
 */
public class JMResultSet {
    private ResultSet rs;
    
    public JMResultSet(ResultSet rs){
        this.rs=rs;
        this.first();
    }
    public JMResultSet(ResultSet rs, Boolean showNullError){
        this.rs=rs;
        if(showNullError && this.rs==null)JMFunctions.traceAndShow(JMFunctions.getMessege(JMConstMessage.MSG_DB+JMConstMessage.MSG_DB_FETCHED_NULL));
        this.first();
    }
    
    public ResultSet getSQLResultSet(){
        return this.rs;
    }

    public boolean first(){
        try {
            return this.rs.first();
        } catch (SQLException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
            return false;
        }
    }

    public boolean next(){
        try {
            return this.rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
            return false;
        }
    }
    public boolean last(){
        try {
            return this.rs.last();
        } catch (SQLException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
            return false;
        }
    }
    public boolean isLast(){
        try {
            return this.rs.isLast();
        } catch (SQLException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
            return false;
        }
    }

    public int getCount(){
        try {
            this.rs.last();
            return this.rs.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
            return 0;
        }
    }

    public int getColCount(){
        try {
            return this.rs.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
            JMFunctions.trace(e.getMessage());
            return 0;
        }
    }
    
    public String getString(String fieldName){
        if(this.rs==null)return "";
        try {
            //this.rs.first();
            return this.rs.getString(fieldName);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return "";
        }
    }
    
    public String getString(int field){
        if(this.rs==null)return "";
        try {
            //this.rs.first();
            return this.rs.getString(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return "";
        }
    }
    
    public Double getDouble(String fieldName){
        if(this.rs==null)return 0.0;
        try {
            //this.rs.first();
            return this.rs.getDouble(fieldName);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return 0.0;
        }
    }
    public Double getDouble(int field){
        if(this.rs==null)return 0.0;
        try {
            //this.rs.first();
            //if(this.rs.)
            return this.rs.getDouble(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return 0.0;
        }
    }
    public int getInt(String fieldName){
        if(this.rs==null)return 0;
        try {
            //this.rs.first();
            return this.rs.getInt(fieldName);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return 0;
        }
    }
    public int getInt(int field){
        if(this.rs==null)return 0;
        try {
            //this.rs.first();
            return this.rs.getInt(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return 0;
        }
    }
    
    public boolean getBool(String fieldName){
        if(this.rs==null)return false;
        try {
            //this.rs.first();
            return this.rs.getBoolean(fieldName);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return false;
        }
    }
    public boolean getBool(int field){
        if(this.rs==null)return false;
        try {
            //this.rs.first();
            return this.rs.getBoolean(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return false;
        }
    }
    public Blob getBlob(String fieldName){
        if(this.rs==null)return null;
        try {
            //this.rs.first();
            return this.rs.getBlob(fieldName);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return null;
        }
    }
    
    public Blob getBlob(int field){
        if(this.rs==null)return null;
        try {
            //this.rs.first();
            return this.rs.getBlob(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return null;
        }
    }
    
    public JMDate getDate(String fieldName, Boolean showDateError){
        if(this.rs==null)return null;
        try {
            //this.rs.first();
            JMDate ret=new JMDate(rs.getDate(fieldName),showDateError);
            if(ret==null)return null;
            return ret;
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return null;
        }
    }
    public JMDate getDate(int field, Boolean showDateError){
        if(this.rs==null)return null;
        try {
            //this.rs.first();
            JMDate ret=new JMDate(new Date(rs.getTimestamp(field+1).getTime()),showDateError);
            if(ret==null)return null;
            return ret;
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return null;
        }
    }
    public int getColumnType(int field){
        if(this.rs==null)return -1;
        try {
            //this.rs.first();
            return this.rs.getMetaData().getColumnType(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return -1;
        }
    }
    public String getColumnName(int field){
        if(this.rs==null)return "";
        try {
            //this.rs.first();
            return this.rs.getMetaData().getColumnName(field+1);
        } catch (SQLException ex) {
            Logger.getLogger(JMResultSet.class.getName()).log(Level.SEVERE, null, ex);
            JMFunctions.traceAndShow(ex.getMessage());
            return "";
        }
    }
}
