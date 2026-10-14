/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.common;

import org.cmdbuild.dao.entrytype.Classe;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

import static org.mockito.Matchers.argThat;

/**
 *
 * @author ldare
 */
public class ClasseMatcher extends ArgumentMatcher<Classe> {

    private final Classe expClasse;

    public ClasseMatcher(Classe expClasse) {
        this.expClasse = expClasse;
    }

    @Override
    public boolean matches(Object obj) {
        if (expClasse == null || obj == null) {
            return false;
        }
        Classe actualClasse = (Classe) obj;
        return Objects.equals(expClasse.getName(), actualClasse.getName());
    }

    @Override
    public String toString() {
        return expClasse == null
                ? "null Classe"
                : "Classe of name: " + this.expClasse.getName();
    }

    public static Classe matchClasse(Classe expClasse) {
        return argThat(new ClasseMatcher(expClasse));
    }
}
