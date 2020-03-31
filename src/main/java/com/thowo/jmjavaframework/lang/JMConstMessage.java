/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.lang;

/**
 *
 * @author jimi
 */
public class JMConstMessage {
    //============================MSG===============================
    public static final String MSG_UI="001";
    public static final String MSG_ASYNC="002";
    public static final String MSG_DB="003";
    public static final String MSG_DATE="004";
    public static final String MSG_CURRENCY="005";
    public static final String MSG_ELSE="006";
    
    //==============================MSG_UI_TYPES============================
    public static final String MSG_UI_OK="001";
    public static final String MSG_UI_CANCEL="002";
    public static final String MSG_UI_YES="003";
    public static final String MSG_UI_NO="004";
    public static final String MSG_UI_HELP="005";
    public static final String MSG_UI_EXIT="006";
    public static final String MSG_UI_CONFIRM="007";
    public static final String MSG_UI_WARNING="008";
    public static final String MSG_UI_ERROR="009";
    
    //==============================MSG_ASYNC_STATES============================
    public static final String MSG_ASYNC_STARTED="001";
    public static final String MSG_ASYNC_PROCESSING="002";
    public static final String MSG_ASYNC_COMPLETED="003";
    public static final String MSG_ASYNC_ERROR="004";
    
    //==============================MSG_ASYNC_STATE_JOB======================
    public static final String MSG_ASYNC_STATE_CONNECT_DB="001";
    public static final String MSG_ASYNC_STATE_FETCH="002";
    public static final String MSG_ASYNC_STATE_UPDATE="003";
    public static final String MSG_ASYNC_STATE_DELETE="004";
    public static final String MSG_ASYNC_STATE_LOAD_CONFIG="005";
    
    //==============================MSG_DB_TYPES==========================
    public static final String MSG_DB_CONNECTED="001";
    public static final String MSG_DB_CONNECT_TIMED_OUT="002";
    public static final String MSG_DB_FETCH_TIMED_OUT="003";
    public static final String MSG_DB_FETCHED="004";
    public static final String MSG_DB_FETCHED_NULL="005";
    
    //==============================MSG_DATE_TYPES========================
    public static final String MSG_DATE_DAY="001";
    public static final String MSG_DATE_MONTH="002";
    public static final String MSG_DATE_HOUR="003";
    public static final String MSG_DATE_INPUTFORMAT="004";
    public static final String MSG_DATE_TIME_INPUTFORMAT="005";
    
    //=============================MSG_DATE_TYPE__DISPLAYS===================
    public static final String MSG_DATE_TYPE_COMPLETE="001";
    public static final String MSG_DATE_TYPE_SHORT="002";

    //=============================MSG_CURRENCY_TYPE================================
    public static final String MSG_CURRENCY_NAME="001";
    public static final String MSG_CURRENCY_SYMBOL="002";
    public static final String MSG_CURRENCY_COMMA="003";
    public static final String MSG_CURRENCY_SEPARATOR="004";

    //=============================MSG_ELSE================================
    public static final String MSG_ELSE_DATE_EMPTY="001";
    public static final String MSG_ELSE_DATE_INVALID="002";
}
