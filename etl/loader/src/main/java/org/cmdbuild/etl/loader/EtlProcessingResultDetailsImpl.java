/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;

public class EtlProcessingResultDetailsImpl implements EtlProcessingResultDetails {

    private final List<Long> createdRecords, modifiedRecords, deletedRecords;

    public EtlProcessingResultDetailsImpl(Collection<Long> createdRecords, Collection<Long> modifiedRecords, Collection<Long> deletedRecords) {
        this.createdRecords = ImmutableList.copyOf(createdRecords);
        this.modifiedRecords = ImmutableList.copyOf(modifiedRecords);
        this.deletedRecords = ImmutableList.copyOf(deletedRecords);
    }

    @Override
    public List<Long> getCreatedRecords() {
        return createdRecords;
    }

    @Override
    public List<Long> getModifiedRecords() {
        return modifiedRecords;
    }

    @Override
    public List<Long> getDeletedRecords() {
        return deletedRecords;
    }

}
