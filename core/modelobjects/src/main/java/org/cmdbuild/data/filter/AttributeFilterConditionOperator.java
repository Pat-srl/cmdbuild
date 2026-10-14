/*
 * To change this license header,
 choose License Headers in Project Properties.
 * To change this template file,
 choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

public enum AttributeFilterConditionOperator {
    EQUAL,
    NOTEQUAL,
    ISNULL,
    ISNOTNULL,
    GREATER,
    LESS,
    BETWEEN,
    LIKE,
    CONTAIN,
    NOTCONTAIN,
    DESCRIPTION_CONTAINS,
    DESCRIPTION_NOTCONTAIN,
    DESCRIPTION_BEGIN,
    DESCRIPTION_NOTBEGIN,
    DESCRIPTION_END,
    DESCRIPTION_NOTEND,
    FULLTEXT,
    BEGIN,
    NOTBEGIN,
    END,
    NOTEND,
    IN,
    OVERLAP,
    NOTOVERLAP,
    NET_CONTAINED,
    NET_CONTAINEDOREQUAL,
    NET_CONTAINS,
    NET_CONTAINSOREQUAL,
    NET_RELATION,
    FALSE,
    TRUE

}
