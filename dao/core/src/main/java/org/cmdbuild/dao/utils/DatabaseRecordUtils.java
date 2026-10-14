/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.DatabaseRecordBuilder;
import org.cmdbuild.dao.beans.RelationImpl;

public class DatabaseRecordUtils {

    public static <T extends DatabaseRecord, B extends DatabaseRecordBuilder<T, B>> DatabaseRecordBuilder<T, B> copyOf(T record) {
        return switch (record.getType().getEtType()) {
            case ET_CLASS ->
                (DatabaseRecordBuilder<T, B>) CardImpl.copyOf(record);
            case ET_DOMAIN ->
                (DatabaseRecordBuilder<T, B>) RelationImpl.copyOf((CMRelation) record);
            default ->
                throw new UnsupportedOperationException();
        };
    }
}
