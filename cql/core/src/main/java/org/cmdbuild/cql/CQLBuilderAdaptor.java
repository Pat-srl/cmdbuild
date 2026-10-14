package org.cmdbuild.cql;

/**
 * Adaptor class for CQLBuilderListener interface. All method are empty.
 */
public class CQLBuilderAdaptor implements CQLBuilderListener {

	@Override
	public void addFromClass(String className, String classAs) {
	}

	@Override
	public void addFromClass(int classId, String classAs) {
	}

	@Override
	public void addGroupByElement(String classDomainReference, String attributeName) {
	}

	@Override
	public void addOrderByElement(String classDomainReference, String attributeName, OrderByType type) {
	}

	@Override
	public void addSelectAttribute(String attributeName, String attributeAs, String classNameOrReference) {
	}

	@Override
	public void defaultGroupBy() {
	}

	@Override
	public void defaultLimit() {
	}

	@Override
	public void defaultOffset() {
	}

	@Override
	public void defaultOrderBy() {
	}

	@Override
	public void defaultSelect() {
	}

	@Override
	public void defaultWhere() {
	}

	@Override
	public void endDomain() {
	}

	@Override
	public void endDomainMeta() {
	}

	@Override
	public void endDomainObjects() {
	}

	@Override
	public void endDomainRef() {
	}

	@Override
	public void endExpression() {
	}

	@Override
	public void endField() {
	}

	@Override
	public void endFrom() {
	}

	@Override
	public void endFromDomain() {
	}

	@Override
	public void endGroup() {
	}

	@Override
	public void endGroupBy() {
	}

	@Override
	public void endOrderBy() {
	}

	@Override
	public void endSelect() {
	}

	@Override
	public void endSelectFromClass() {
	}

	@Override
	public void endSelectFromDomain() {
	}

	@Override
	public void endSelectFromDomainMeta() {
	}

	@Override
	public void endSelectFromDomainObjects() {
	}

	@Override
	public void endSelectFunction() {
	}

	@Override
	public void endValue() {
	}

	@Override
	public void endWhere() {
	}

	@Override
	public void globalEnd() {
	}

	@Override
	public void globalStart() {
	}

	@Override
	public void selectAll() {
	}

	@Override
	public void setLimit(long limit) {
	}

	@Override
	public void setLimit(FieldInputValue limit) {
	}

	@Override
	public void setOffset(long offset) {
	}

	@Override
	public void setOffset(FieldInputValue offset) {
	}

	@Override
	public void startComplexField(WhereType type, boolean isNot, String classOrDomainNameOrRef, String[] fieldPath, FieldOperator operator) {
	}

	@Override
	public void startDomain(WhereType type, String scopeReference, String domainName, DomainDirection direction, boolean isNot) {
	}

	@Override
	public void startDomain(WhereType type, String scopeReference, long domainId, DomainDirection direction, boolean isNot) {
	}

	@Override
	public void startDomainMeta() {
	}

	@Override
	public void startDomainObjects() {
	}

	@Override
	public void startDomainRef(WhereType type, String domainReference, boolean isNot) {
	}

	@Override
	public void startExpression() {
	}

	@Override
	public void startFrom(boolean history) {
	}

	@Override
	public void startFromDomain(String scopeReference, String domainName, String domainAs, DomainDirection direction) {
	}

	@Override
	public void startFromDomain(String scopeReference, long domainId, String domainas, DomainDirection direction) {
	}

	@Override
	public void startGroup(WhereType type, boolean isNot) {
	}

	@Override
	public void startGroupBy() {
	}

	@Override
	public void startLookupField(WhereType type, boolean isNot, String classOrDomainNameOrRef, String fieldId, LookupOperator[] lookupPath, FieldOperator operator) {
	}

	@Override
	public void startOrderBy() {
	}

	@Override
	public void startSelect() {
	}

	@Override
	public void startSelectFromClass(String classNameOrReference) {
	}

	@Override
	public void startSelectFromDomain(String domainNameOrReference) {
	}

	@Override
	public void startSelectFromDomainMeta() {
	}

	@Override
	public void startSelectFromDomainObjects() {
	}

	@Override
	public void startSelectFunction(String functionName, String functionAs) {
	}

	@Override
	public void startSimpleField(WhereType type, boolean isNot, String classOrDomainNameOrRef, String fieldId, FieldOperator operator) {
	}

	@Override
	public void startValue(FieldValueType type) {
	}

	@Override
	public void startWhere() {
	}

	@Override
	public void value(Object o) {
	}

}
