/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.diagram;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableSet;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.singleton;
import java.util.Set;
import jakarta.activation.DataSource;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import static org.cmdbuild.utils.dot.DotUtils.dotToImage;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.springframework.stereotype.Component;

@Component
public class DiagramServiceImpl implements DiagramService {

    private final DaoService dao;

    public DiagramServiceImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public DataSource renderDatabaseDiagram(Collection<String> classes) {
        String dot = new DatabaseRenderer().includeOnlyClasses(classes).buildDatabaseDiagram();
//        return dotToImage(dot, Engine.FDP, Format.PNG);
        return dotToImage(dot, Engine.NEATO, Format.PNG);
    }

    private class DatabaseRenderer {

        private boolean includeInheritedRelations = false;
        private Set<String> includeOnlyClasses = singleton(BASE_CLASS_NAME);

        public DatabaseRenderer includeOnlyClasses(Collection<String> classes) {
            this.includeOnlyClasses = ImmutableSet.copyOf(classes);
            return this;
        }

        public String buildDatabaseDiagram() {

            Set<Classe> classes = set();
            Set<Domain> domains = set();

            includeOnlyClasses.stream().map(dao::getClasse).forEach(classes::add);
            includeOnlyClasses.stream().map(dao::getClasse).flatMap(c -> c.getAncestors().stream().map(dao::getClasse)).forEach(classes::add);
            includeOnlyClasses.stream().map(dao::getClasse).flatMap(c -> dao.getClasseHierarchy(c).getDescendants().stream()).forEach(classes::add);

            dao.getAllDomains().stream().filter(d -> dao.getClassesForDomain(d).stream().map(Classe::getName).anyMatch(includeOnlyClasses::contains)).forEach(domains::add);
            domains.stream().map(Domain::getSourceClass).forEach(classes::add);
            domains.stream().map(Domain::getTargetClass).forEach(classes::add);

            dao.getAllClasses().stream().filter(not(classes::contains))
                    .filter(c -> c.getAllAttributes().stream().filter(a -> a.isOfType(FOREIGNKEY)).anyMatch(a -> includeOnlyClasses.contains(a.getType().as(ForeignKeyAttributeType.class).getForeignKeyDestinationClassName())))
                    .forEach(classes::add);
            includeOnlyClasses.stream().map(dao::getClasse).flatMap(c -> c.getAllAttributes().stream()).filter(a -> a.isOfType(FOREIGNKEY)).map(a -> a.getType().as(ForeignKeyAttributeType.class).getForeignKeyDestinationClassName())
                    .distinct().map(dao::getClasse).forEach(classes::add);

            StringBuilder dot = new StringBuilder();

            dot.append("digraph \"CMDBuild Data Model\" {\n\n")
                    .append("\toverlap=false;\n")
                    .append("\tsplines=curved;\n\n");

            classes.forEach(c -> dot.append(format("\t\"class_%s\" [ shape = \"none\", label = <<table border=\"0\" cellspacing=\"0\"><tr><td border=\"1\" bgcolor=\"%s\">%s</td></tr><tr><td border=\"1\">%s</td></tr></table>> ];\n",
                    c.getName(),
                    c.isSimpleClass() ? "gold" : (c.isSuperclass() ? "azure3" : "chartreuse"),
                    c.isSimpleClass() ? "simple" : (c.isSuperclass() ? "super" : "standard"),
                    c.getName())));

            dot.append("\n");

            classes.stream().filter(Classe::hasParent).forEach(c
                    -> dot.append(format("\t\"class_%s\" -> \"class_%s\" [ color = \"dimgray\", style = \"dashed\" ];\n", c.getName(), c.getParent())));

            dot.append("\n");

            domains.forEach(d -> dot.append(format("\t\"domain_%s\" [ shape = \"none\", label = <<table border=\"0\" cellspacing=\"0\"><tr><td border=\"1\" bgcolor=\"%s\">%s</td></tr><tr><td border=\"1\">%s</td></tr></table>> ];\n",
                    d.getName(),
                    "deepskyblue",
                    "domain",
                    d.getName())));

            dot.append("\n");

            domains.stream().forEach(d -> {

                dot.append(format("\t\"domain_%s\" -> \"class_%s\" [ label = \"%s\", color = \"deepskyblue\", arrowtype = \"%s\" ];\n", d.getName(), d.getSourceClass().getName(), d.getSourceCardinality(), d.getSourceClass().hasReferenceForDomain(d, RD_DIRECT) ? "odot" : "none"));
                dot.append(format("\t\"domain_%s\" -> \"class_%s\" [ label = \"%s\", color = \"deepskyblue\", arrowtype = \"%s\" ];\n", d.getName(), d.getTargetClass().getName(), d.getTargetCardinality(), d.getTargetClass().hasReferenceForDomain(d, RD_INVERSE) ? "odot" : "none"));

                if (includeInheritedRelations) {
                    dao.getClasseHierarchy(d.getSourceClass()).getDescendants().stream().filter(not(d::isDisabledSourceDescendant)).filter(classes::contains).forEach(c
                            -> dot.append(format("\t\"domain_%s\" -> \"class_%s\" [ label = \"%s\", color = \"cyan1\", arrowtype = \"%s\" ];\n", d.getName(), c.getName(), d.getSourceCardinality(), c.hasReferenceForDomain(d, RD_DIRECT) ? "odot" : "none")));
                    dao.getClasseHierarchy(d.getTargetClass()).getDescendants().stream().filter(not(d::isDisabledTargetDescendant)).filter(classes::contains).forEach(c
                            -> dot.append(format("\t\"domain_%s\" -> \"class_%s\" [ label = \"%s\", color = \"cyan1\", arrowtype = \"%s\" ];\n", d.getName(), c.getName(), d.getTargetCardinality(), c.hasReferenceForDomain(d, RD_INVERSE) ? "odot" : "none")));
                }

            });

            dot.append("\n");

            classes.stream().filter(classes::contains).flatMap(c -> c.getAllAttributes().stream()).filter(a -> a.isOfType(FOREIGNKEY))
                    .filter(a -> classes.stream().map(Classe::getName).anyMatch(equalTo(a.getType().as(ForeignKeyAttributeType.class).getForeignKeyDestinationClassName())))
                    .forEach(a -> dot.append(format("\t\"class_%s\" -> \"class_%s\" [ color = \"dodgerblue\", arrowtype = \"dot\" ];\n", a.getType().as(ForeignKeyAttributeType.class).getForeignKeyDestinationClassName(), a.getOwner().getName())));

            dot.append("\n}\n");

            return dot.toString();
        }
    }

}
