/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface EtlRecordInfo {

    long getRecordIndex();

    long getRecordLineNumber();

    List<Entry<String, String>> getRecordData();

    Map<String, Object> getRawRecord();
}
