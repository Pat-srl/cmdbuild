/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.stub;

/**
 *
 * @author afelice
 */
public class KnownModelItem {

    private final String id;
    private String name;
    private String value;

    public KnownModelItem(String id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
