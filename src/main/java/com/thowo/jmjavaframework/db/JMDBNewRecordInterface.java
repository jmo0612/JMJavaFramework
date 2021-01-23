/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thowo.jmjavaframework.db;

import com.thowo.jmjavaframework.table.JMRow;
import java.util.List;

/**
 *
 * @author jimi
 */
public interface JMDBNewRecordInterface {
    JMRow newDefaultRow(String tableName,JMRow newRow, List<String> params, String id);
}
