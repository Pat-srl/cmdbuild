/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.getDomain;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.getReferencedType;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isDirectReferenceAttribute;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isForeignKeyAttribute;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isForeignKeyTo;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isInverseReferenceAttribute;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isLookup;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isReferenceAttribute;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isReferenceTo;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaItemDeltaBuilder.buildSchemaItemAttributesDataNode_Unique;
import org.cmdbuild.modeldiff.schema.ClasseConfiguration;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.sync.ClasseSync;
import org.cmdbuild.sync.DmsHandler;
import org.cmdbuild.sync.LookupHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link Classe} stuff, composed of {@link Attribute} items.
 *
 * <p>
 * Knows how to build a <i>schema node</i> for a {@link Classe} and its
 * contained {@link Attribute}s.
 *
 * @author afelice
 */
public class CmClasseSchemaNode extends CmSchemaItemDataNode {

    /**
     * Contains the {@link Classe}s this one depends on:
     * <ol>
     * <li>the parent, if any;
     * <li>the <i>foreign keys targets</i>.
     * </ol>
     *
     * <p>
     * May be overwritten when calculating dependency order.
     */
    Set<String> dependentOn = set();

    public CmClasseSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);

        String parent = getParent();
        if (parent != null) {
            this.dependentOn.add(parent);
        }
    }

    /**
     *
     * @param component if adding a <i>foreign key</i> {@link Attribute}, add
     * the target {@link Classe} as <i>dependent on</i> for this {@link Classe}.
     */
    @Override
    public void addComponent(CmSchemaItemAttributesDataNode component) {
        super.addComponent(component);

        // (Possibly) Add as dependency of this Classe:
        addDependentOn(component);
    }

    /**
     *
     * @return name of parent; <code>null</code> if none.
     */
    public String getParent() {
        return toStringOrNull(getModelObj().getAttributesSerialization().get(ATTR_PARENT_SERIALIZATION));
    }

    public void addDependentOn(CmSchemaItemAttributesDataNode attribNode) {
        // Nothing to add: this Classe depends only on parent Classe
    }

    /**
     * Can be used for a strict dependency checking.
     * 
     * <b>Was previously</b> used when dependency of this {@link Classe} were:
     * <ol>
     * <li>target of a foreign key {@link Attribute};
     * <li>target of direct reference [@link Attribute} ({@link Domain}s <code>N:1</code>); note: <i>inverse references</i> {@link Attribute} ({@link Domain} <code>1:N</code>),
     *  that are stored in the <b>source</b> {@link Classe}, are treaded elsewhere.
     * 
     * @param attribNode 
     */
    public void addDependentOn_withFK(CmSchemaItemAttributesDataNode attribNode) {
        Map<String, Object> attribCmdbSerialization = attribNode.getModelObj().getAttributesSerialization();

        if (isForeignKeyAttribute(attribCmdbSerialization)) {
            dependentOn.add(getReferencedType(attribCmdbSerialization));
        } 
        // @todo AFE siccome le REFERENCE sono gestite dopo i domini, questa dipendenza non serve più
//        else if (isDirectReferenceAttribute(attribCmdbSerialization)) {
//            dependentOn.add(getReferencedType(attribCmdbSerialization));
//        }
        // note: inverse references are handled in the calling code
    }    
    
    public void addDependentOn(String classeName) {
        dependentOn.add(classeName);
    }

    public List<String> getDependentOn() {
        return list(dependentOn);
    }

    /**
     * Removes a <i>dependent on</i> {@link Classe} when calculating dependency
     * order and this is an already existing, untouched, {@link Classe}: it
     * don't concur to dependency calculation.
     *
     * @param dependentOnClassName
     */
    public void removeDependentOn(String dependentOnClassName) {
        this.dependentOn.remove(dependentOnClassName);
    }

    /**
     * Adds to the node:
     * <ol>
     * <li>the serialization for the {@link Classe};
     * <li>for each contained {@link Attribute}, a
     * {@link CmSchemaItemAttributesDataNode} with attribute's serialization;
     * </ol>
     *
     * @param aClasse
     * @param lookupDataHandler
     * @param dmsDataHandler
     * @param classeSync
     * @param attributeSerializer
     * @return
     */
    static public CmClasseSchemaNode from(Classe aClasse,
            LookupHandler lookupDataHandler, DmsHandler dmsDataHandler,
            ClasseSync classeSync, AttributeTypeConversionService attributeSerializer) {

        ExtendedClass extendedClass = classeSync.readExtended(aClasse.getName());
        Map<String, Object> classeSerialization = classeSync.serializeClasseProps(extendedClass);

        // Store class serialization in a CmSchemaItemAttributesDataNode
        CmClasseSchemaNode result = new CmClasseSchemaNode(new CmSchemaItemAttributesData(aClasse.getName(), classeSerialization));

        // Add attributes as components
        aClasse.getAllAttributes().stream().
                map(curAttrib -> buildSchemaItemAttributesDataNode_Unique(aClasse.getName(), curAttrib.getName(), attributeSerializer.serializeAttributeType(curAttrib, true)))
                .forEach(curAttribNode -> result.addComponent(curAttribNode));

        // @todo AFE add lookups
        return result;
    }

    /**
     * Node used for <i>topological sorting</i>.
     *
     * @param classe
     * @return
     */
    static public CmClasseSchemaNode buildClasseSchemaNode(ClasseConfiguration classe) {
        CmClasseSchemaNode result = new CmClasseSchemaNode(
                CmSchemaItemAttributesData.from(classe.name,
                        classe.getCmdbSerialization() // Classe properties
                )
        );

        classe.attributes.forEach(attribConfiguration
                -> result.addComponent(
                        new CmSchemaItemAttributesDataNode(
                                CmSchemaItemAttributesData.from(attribConfiguration.getName(), attribConfiguration.getCmdbSerialization()) // Attribute properties
                        ))
        );

        return result;
    }

    /**
     * Used converting from {@link GeneratedDiffSchema_Classe} to a
     * {@link CmClasseSchemaNode}.
     *
     * @param props
     * @return
     */
    static public String fetchParent(Map<String, Object> props) {
        return (String) props.get(ATTR_PARENT_SERIALIZATION);
    }

    /**
     * Used when <i>removing</i> a {@link Classe}: all
     * <i>foreign keys</i> to that has to be deactivated.
     *
     * @param referredClasseNames
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getReferencesTo(Set<String> referredClasseNames) {
        return getComponents().stream().filter(attribDataNode -> isReferenceTo(attribDataNode, referredClasseNames)).collect(toList());
    }
    
    /**
     * Used when <i>removing</i> a {@link Classe}: all
     * <i>references</i>, <i>foreign keys</i> to that has to be deactivated.
     *
     * @param referredClasseNames
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getFKsTo(Set<String> referredClasseNames) {
        return getComponents().stream().filter(attribDataNode -> isForeignKeyTo(attribDataNode, referredClasseNames)).collect(toList());
    }    

    /**
     * Used while deactivating {@link Domain}s.
     *
     * @param domainNames
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getAllReferences(Set<String> domainNames) {
        return getComponents().stream().filter(attribDataNode -> 
                (isDirectReferenceAttribute(attribDataNode) || isInverseReferenceAttribute(attribDataNode)) &&
                domainNames.contains(getDomain(attribDataNode.getModelObj().getAttributesSerialization()))).collect(toList());
    }

    /**
     * Used while inserting/updating {@link Classe} {@link Attribute}s, to skip
     * <code>REFERENCE</code> ones and make than later on, after new
     * {@link Domain}s inserted.
       * 
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getWithoutReferences() {
        return getComponents().stream().filter(attribDataNode -> 
                !isReferenceAttribute(attribDataNode)).collect(toList());
    }

    /**
     * Used while inserting/updating {@link Classe} <code>REFERENCE</code>
     * {@link Attribute}s, after new {@link Domain}s inserted.
     * 
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getOnlyReferences() {
        return getComponents().stream().filter(attribDataNode -> 
                isReferenceAttribute(attribDataNode)).collect(toList());
    }
    
    /**
     * Used before making <i>topological sort</i> while inserting
     * {@link Classe}s.
     *
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getAllInverseReferences() {
        return getComponents().stream().filter(attribDataNode -> isInverseReferenceAttribute(attribDataNode)).collect(toList());
    }

    /**
     * Used before making <i>topological sort</i> while inserting
     * {@link Classe}s.
     *
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getAllDirectReferences() {
        return getComponents().stream().filter(attribDataNode -> isDirectReferenceAttribute(attribDataNode)).collect(toList());
    }    

    /**
     * Used before making <i>topological sort</i> while inserting
     * {@link Classe}s.
     *
     * @return
     */    
    public List<CmSchemaItemAttributesDataNode> getForeignKeys() {
        return getComponents().stream().filter(attribDataNode -> isForeignKeyAttribute(attribDataNode)).collect(toList());
    }    
    
    /**
     * Used when <i>removing</i> a {@link LookupType}: all related
     * <i>lookup attributes</i> has to be deactivated.
     *
     * @param lookupTypeNames
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getLookups(Set<String> lookupTypeNames) {
        return getComponents().stream().filter(attribDataNode -> isLookup(attribDataNode, lookupTypeNames)).collect(toList());
    }

}
