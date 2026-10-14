/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;

public interface UserClassService {

    ExtendedClass getExtendedClass(String classId, ClassQueryFeatures... features);

    ExtendedClass getExtendedClass(Classe classe, ClassQueryFeatures... features);

    ExtendedClass createClass(ExtendedClassDefinition definition);

    /**
     * Used by <code>model-diff-schema</code> to handle <code>reserved</code>
     * and <code>protected</code> {@link Classe}s.
     *
     * @param definition
     * @return
     */
    ExtendedClass createClass_Reserved(ExtendedClassDefinition definition);

    ExtendedClass updateClass(ExtendedClassDefinition definition);

    /**
     * Used by <code>model-diff-schema</code> to handle <code>reserved</code>
     * and <code>protected</code> {@link Classe}s.
     *
     * @param definition
     * @param features
     * @return
     */
    ExtendedClass updateClass_Reserved(ExtendedClassDefinition definition);

    void invalidateCache();
    
    void invalidateCache_Class(String classeName);    

    boolean userCanModify(String classId);

    boolean userCanRead(Classe classe);

    boolean isActiveAndUserCanRead(String classId);

    void deleteClass(String classId);

    List<Classe> getAllUserClasses();

    Classe getUserClass(String classId);

    @Nullable
    Classe getUserClassOrNull(String classId);

    Attribute getUserAttribute(String classId, String attrId);

    List<Attribute> getUserAttributes(String classId);

    List<Attribute> getActiveUserAttributes(String classId);

    Attribute createAttribute(Attribute attribute);

    /**
     * Used by <code>model-diff-schema</code> to handle {@link Attribute} in
     * <code>reserved</code> and <code>protected</code> {@link Classe}s.
     *
     * @param attribute
     * @return
     */
    Attribute createAttribute_Reserved(Attribute attribute);

    Attribute updateAttribute(Attribute attribute);

    /**
     * Used by <code>model-diff-schema</code> to handle {@link Attribute} in
     * <code>reserved</code> and <code>protected</code> {@link Classe}s.
     * 
     * @param attribute
     * @return 
     */
    Attribute updateAttribute_Reserved(Attribute attribute);

    void deleteAttribute(String classId, String attrId);

    void updateAttributes(List<Attribute> attributes);

    Optional<Map.Entry<LookupType, List<LookupValue>>> getAllLookupValues(Attribute attribute);
    
    Optional<Map.Entry<LookupType, List<LookupValue>>> getAllLookupValues(Attribute attribute, boolean onlyActiveValues);

    default void checkUserCanModify(String classId, String message, Object... args) {
        checkArgument(userCanModify(classId), message, args);
    }

    default List<Classe> getActiveUserClasses() {
        return getAllUserClasses().stream().filter(Classe::isActive).collect(toList());
    }

    enum ClassQueryFeatures {
        CQ_FILTER_DEVICE, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES
    }

}
