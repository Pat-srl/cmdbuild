/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

/**
 * To <b>temporarily</b> disable some processing.
 *
 * @author afelice
 */
public class FeatureToggles {

    /**
     * Feature toggle to <b>temporarily</b> disable {@link Domain} (and
     * {@link Attribute} with references).
     */
    public boolean handleDomainsToggle = true;

    /**
     * Feature toggle to <b>temporarily</b> disable {@link Attribute}s
     * <b>inside</b> {@link Domain}s.
     */
    public boolean handleDomainAttributesToggle = true;

    /**
     * Feature toggle to <b>temporarily</b> disable {@link Lookup} (and
     * {@link Attribute} with lookups).
     */
    public boolean handleLookupsMergeToggle = true;

    /**
     * Feature toggle to <b>temporarily</b> disable {@link DmsCategories} (and
     * {@link Attribute} with files).
     */
    public boolean handleDmsCategoriesToggle = true;

    /**
     * Feature toggle to <b>temporarily</b> disable (if <code>false</code>) the
     * <code>_icon</code> handling in *apply schema*.
     *
     * @see
     * <a href="http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/8341">#8341
     * - backend: support to synchronization of CMDBuild model - handle Classe
     * icon</a>
     */
    public boolean handleClasseIcons = true;

    /**
     * Feature toggle to <b>temporarily</b> disable (if <code>false</code>) the
     * <code>contextMenuItems</code> - <code>component</code> handling in *collect
     * schema*.
     *
     * @see
     * <a href="http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/8342">#8342
     * - backend: support to synchronization of CMDBuild model - handle Classe
     * JSComponent inside Classe contextMenuItems</a>
     */
    public boolean handleContextMenuItems_JSComponent_CollectToggle = true;

    /**
     * Feature toggle to <b>temporarily</b> disable (if <code>false</code>) the
     * <code>contextMenuItems</code> - <code>component</code> handling in *apply
     * schema*.
     *
     * @see
     * <a href="http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/8342">#8342
     * - backend: support to synchronization of CMDBuild model - handle Classe
     * JSComponent inside Classe contextMenuItems</a>
     */
    public boolean handleContextMenuItems_JSComponent_MergeToggle = true;

    /**
     * Feature toggle to <b>temporarily</b> disable (if <code>false</code>) the
     * {@link Process} handling *apply schema*.
     */
    public boolean handleProcess = true;

    /**
     * Feature toggle to <b>temporarily</b> disable (if <code>false</code>) the
     * {@link Process} <code>XPDL files</code> handling in *collect* and *apply
     * schema*.
     */
    public boolean handleProcessXpdl = true;

    static public FeatureToggles allEnabled() {
        FeatureToggles result = new FeatureToggles();
        result.handleDomainsToggle = true;
        result.handleDomainAttributesToggle = true;
        result.handleLookupsMergeToggle = true;
        result.handleDmsCategoriesToggle = true;
        result.handleClasseIcons = true;
        result.handleContextMenuItems_JSComponent_CollectToggle = true;
        result.handleContextMenuItems_JSComponent_MergeToggle = true;
        result.handleProcess = true;
        result.handleProcessXpdl = true;

        return result;
    }

    static public FeatureToggles allDisabled() {
        FeatureToggles result = new FeatureToggles();
        result.handleDomainsToggle = false;
        result.handleDomainAttributesToggle = false;
        result.handleLookupsMergeToggle = false;
        result.handleDmsCategoriesToggle = false;
        result.handleClasseIcons = false;
        result.handleContextMenuItems_JSComponent_CollectToggle = false;
        result.handleContextMenuItems_JSComponent_MergeToggle = false;
        result.handleProcess = false;
        result.handleProcessXpdl = false;

        return result;
    }

    static public FeatureToggles copyOf(FeatureToggles featureToggles) {
        FeatureToggles result = new FeatureToggles();
        result.handleDomainsToggle = featureToggles.handleDomainsToggle;
        result.handleDomainAttributesToggle = featureToggles.handleDomainAttributesToggle;
        result.handleLookupsMergeToggle = featureToggles.handleLookupsMergeToggle;
        result.handleDmsCategoriesToggle = featureToggles.handleDmsCategoriesToggle;
        result.handleClasseIcons = featureToggles.handleClasseIcons;
        result.handleContextMenuItems_JSComponent_CollectToggle = featureToggles.handleContextMenuItems_JSComponent_CollectToggle;
        result.handleContextMenuItems_JSComponent_MergeToggle = featureToggles.handleContextMenuItems_JSComponent_MergeToggle;
        result.handleProcess = featureToggles.handleProcess;
        result.handleProcessXpdl = featureToggles.handleProcessXpdl;

        return result;
    }

    /**
     *
     * @param confFeatureToggles can enable or disable <i>collect</i>/<i>compare</i>/<i>applyDiff</i>, in <i>property</i> of {@link Classe} items, for:
     * <dl>
     * <dt><code>handleClasseIcons</code><dd><code>"icons"</code>;
     * <dt><code>handleContextMenuItems_JSComponent_CollectToggle</code><dd><code>"contextMenuItems"</code> with <code>"type" : "component"</code> (JS components) <i>collect</i>.
     * <dt><code>handleContextMenuItems_JSComponent_MergeToggle</code><dd><code>"contextMenuItems"</code> with <code>"type" : "component"</code> (JS components) <i>merge</i>.
     * </dl>
     */
    public void merge(FeatureToggles confFeatureToggles) {
        if (confFeatureToggles != null) {
            handleClasseIcons = confFeatureToggles.handleClasseIcons;
            handleContextMenuItems_JSComponent_CollectToggle = confFeatureToggles.handleContextMenuItems_JSComponent_CollectToggle;
            handleContextMenuItems_JSComponent_MergeToggle = confFeatureToggles.handleContextMenuItems_JSComponent_MergeToggle;
            handleProcess = confFeatureToggles.handleProcess;
            handleProcessXpdl = confFeatureToggles.handleProcessXpdl;
        }
    }
}
