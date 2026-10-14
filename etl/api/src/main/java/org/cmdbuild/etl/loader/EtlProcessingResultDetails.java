/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import java.util.List;

public interface EtlProcessingResultDetails {

    List<Long> getCreatedRecords();

    List<Long> getDeletedRecords();

    List<Long> getModifiedRecords();

}
