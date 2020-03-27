/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jimi
 */
public class JMStringMessages {
    private static List<String> msgType;
    private static List<String> msgDefault;
    private static List<String> msg;
    
    public static void init(){
        msgType=new ArrayList();
        msgDefault=new ArrayList();
        msg=new ArrayList();
        
        //=====================DEFAULT=======================
        addMsg("DB_NO_CONNECTION","Can't connect to database");
    }
    
    private static void addMsg(String msgTypeStr, String msgDefaultStr, String msgStr){
        if(getMsgIndex(msgTypeStr)==-1){
            msgType.add(msgTypeStr);
            msgDefault.add(msgDefaultStr);
            msg.add(msgStr);
        }
    }
    
    private static void addMsg(String msgTypeStr, String msgStr){
        addMsg(msgTypeStr,msgStr,"");
    }
    
    private static Integer getMsgIndex(String msgTypeStr){
        Integer ret=-1;
        for(String tmp:msgType){
            if(tmp.equals(msgTypeStr)){
                return ++ret;
            }
        }
        return -1;
    }
    
    public static String getDefaultMessage(String msgTypeStr){
        Integer tmp=getMsgIndex(msgTypeStr);
        if(tmp!=-1){
            return msgDefault.get(tmp);
        }
        return "";
    }
    
    public static String getMessage(String msgTypeStr){
        Integer tmp=getMsgIndex(msgTypeStr);
        if(tmp!=-1){
            return msg.get(tmp);
        }
        return "";
    }
}
