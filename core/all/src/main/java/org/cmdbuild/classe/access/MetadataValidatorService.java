/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import org.cmdbuild.dao.entrytype.ClassMetadata;

public interface MetadataValidatorService {

    void validateMetadata(String classId, ClassMetadata metadata);

}
