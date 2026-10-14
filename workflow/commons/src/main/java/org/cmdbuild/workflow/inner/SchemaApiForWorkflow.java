package org.cmdbuild.workflow.inner;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cmdbuild.workflow.type.LookupType;

/**
 * API to query the database structure.
 */
public interface SchemaApiForWorkflow {

    ClassInfo findClass(String className);

    ClassInfo findClass(int classId);

    LookupType selectLookupById(long id);

    LookupType selectLookupByCode(String type, String code);

    LookupType selectLookupByDescription(String type, String description);

    LookupType selectLookupByCodeCreateIfMissing(String type, String code);

    LookupType updateLookup(String type, String code, @Nullable String description);

    /**
     * Temporary object till we find a decent solution
     */
    static class ClassInfo {

        private final String name;
        private final long id;

        public ClassInfo(String name, long id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public long getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ClassInfo)) {
                return false;
            }
            ClassInfo other = ClassInfo.class.cast(obj);
            return new EqualsBuilder() //
                    .append(this.name, other.name) //
                    .append(this.id, other.id) //
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder() //
                    .append(name) //
                    .append(id) //
                    .toHashCode();
        }

        @Override
        public String toString() {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }

    }

}
