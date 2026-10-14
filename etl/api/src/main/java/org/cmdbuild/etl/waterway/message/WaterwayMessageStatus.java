/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

public enum WaterwayMessageStatus {
    WMS_DRAFT, WMS_QUEUED, WMS_PROCESSING, WMS_STANDBY, WMS_PROCESSED, WMS_COMPLETED, WMS_FORWARDED, WMS_FAULT_TOLERANT_ERROR, WMS_ERROR, WMS_FAILED

}
