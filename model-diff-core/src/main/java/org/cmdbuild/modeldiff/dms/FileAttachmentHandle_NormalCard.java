/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import java.util.Objects;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;

public class FileAttachmentHandle_NormalCard extends FileAttachmentHandle {

    /**
     * Valued only for <i>attribute <code>FILE</code></i>, to be a unique key in
     * the {@link Card} if more than one <i>attribute <code>FILE</code></i> is
     * valued.
     */
    private final String attributeName;

    /**
     * For <i>attribute <code>FILE</code></i>.
     *
     * @param classe
     * @param cardId
     * @param attributeName
     */
    public FileAttachmentHandle_NormalCard(Classe classe, String cardId, String attributeName) {
        super(classe, cardId);

        this.attributeName = attributeName;
    }

    @Override
    public FileAttachmentHandle copyWith(String newCardId) {
        return new FileAttachmentHandle_NormalCard(classe, newCardId, attributeName);
    }

    /**
     *
     * @return the <i>attribute <code>FILE</code></i> name, if this represent a
     * normal card.
     */
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public int compareTo(FileAttachmentHandle other) {
        int compare = super.compareTo(other);

        if (compare == 0) {
            // If same card, compare by attribute 
            return this.attributeName.compareTo(((FileAttachmentHandle_NormalCard) other).attributeName);
        }

        return compare;
    }

    // Override di equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileAttachmentHandle_NormalCard other = (FileAttachmentHandle_NormalCard) o;
        return super.equals(o)
                // If same card, compare by attribute 
                && Objects.equals(attributeName, other.attributeName);
    }

    // Override di hashCode
    @Override
    public int hashCode() {
        return Objects.hash(getClasseName(), cardId, attributeName);
    }

    @Override
    public String toString() {
        return "FileAttachmentHandle_NormalCard{classId=< %s >, cardId=< %s >, attributeName=< %s >}".formatted(getClasseName(), cardId, attributeName);
    }

} // end FileAttachmentHandle_NormalCard class
