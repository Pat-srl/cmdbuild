package org.cmdbuild.api.fluent;

public interface FluentApi {

	NewCard newCard(String className);

	ExistingCard existingCard(CardDescriptor descriptor);

	ExistingCard existingCard(String className, long id);

	NewRelation newRelation(String domainName);

	ExistingRelation existingRelation(String domainName);

	QueryClass queryClass(String className);

	FunctionCall callFunction(String functionName);

	CreateReport createReport(String title, String format);

	ActiveQueryRelations queryRelations(CardDescriptor descriptor);

	ActiveQueryRelations queryRelations(String className, long id);

	NewProcessInstance newProcessInstance(String processClassName);

	ExistingProcessInstance existingProcessInstance(String processClassName, long processId);

	QueryAllLookup queryLookup(String type);

}
