package com.thowo.jmjavaframework.db;

/**
 * Created by jimi on 6/2/2017.
 */

public class JMDBMySQL {
    private String host;
    private String port;
    private String db;
    private String user;
    private String pass;

    public JMDBMySQL(String host, String port, String db, String user, String pass){
        this.host=host;
        this.port=port;
        this.db=db;
        this.user=user;
        this.pass=pass;
    }
    public String getConnectionString(){
        return "jdbc:mysql://" + host + ":" + port + "/" + db;
    }
    public  String getUser(){
        return user;
    }
    public String getPass(){
        return pass;
    }
    public void changeConnectionSetting(String host, String port, String db, String user, String pass){
        this.host=host;
        this.port=port;
        this.db=db;
        this.user=user;
        this.pass=pass;
    }
}
