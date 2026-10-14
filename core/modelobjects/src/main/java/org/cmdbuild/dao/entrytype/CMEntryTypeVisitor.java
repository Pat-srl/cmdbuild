package org.cmdbuild.dao.entrytype;

import org.cmdbuild.dao.function.StoredFunction;

public interface CMEntryTypeVisitor {

    void visit(Classe type);

    void visit(Domain type);

    void visit(CMFunctionCall type);

    default void visit(StoredFunction type) {
        throw new UnsupportedOperationException();
    }

}
