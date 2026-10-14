/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

public interface StreamProgressEvent {

    String getStreamId();

    String getStreamDescription();

    /**
     * @return progress, between 0.0d and 1.0d
     */
    double getProgress();

    long getCount();

    long getTotal();

    long getElapsedTime();

    long getBeginTimestamp();

    String getProgressDescription();

    String getProgressDescriptionDetailed();

    String getProgressDescriptionEta();

    default long getCurrentTimestamp() {
        return getBeginTimestamp() + getElapsedTime();
    }

}
