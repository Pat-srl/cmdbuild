package org.cmdbuild.api.fluent;

import java.util.Map;

public interface Relation {

    String getDomainName();

    String getClassName1();

    long getCardId1();

    Relation setCard1(String className, long id);

    String getClassName2();

    long getCardId2();

    Relation setCard2(String className, long id);
    
    Map<String, Object> getAttributes();
    
    Relation setAttribute(String attributeName, Object attributeValue);

}
