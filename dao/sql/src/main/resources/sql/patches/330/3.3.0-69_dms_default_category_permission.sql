-- set default alfresco category permissions to default

UPDATE "LookUp" SET "AccessType" = 'default' WHERE "Type" = 'AlfrescoCategory' AND "Status" = 'A' AND "Code" = 'org.cmdbuild.LOOKUPTYPE';